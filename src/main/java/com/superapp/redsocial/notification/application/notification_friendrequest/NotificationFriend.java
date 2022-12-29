package com.superapp.redsocial.notification.application.notification_friendrequest;

import com.superapp.redsocial.core.infraestructure.jackson.JsonTransformer;
import com.superapp.redsocial.core.shared.logger.RSLogger;
import com.superapp.redsocial.core.shared.utils.Env;
import com.superapp.redsocial.core.shared.utils.Strings;
import com.superapp.redsocial.notification.domain.entity.Notificacion;
import com.superapp.redsocial.notification.domain.entity.RequestSendSocket;
import com.superapp.redsocial.notification.domain.entity.Usuario;
import com.superapp.redsocial.notification.domain.entity.data.*;
import com.superapp.redsocial.notification.domain.events.RequestData;
import com.superapp.redsocial.notification.domain.repositories.NotificationDynamoRepository;
import com.superapp.redsocial.notification.domain.repositories.NotificationNeptuneRepository;
import com.superapp.redsocial.notification.domain.utils.SALogger;
import com.superapp.redsocial.notification.domain.utils.Status;
import com.superapp.redsocial.notification.domain.utils.SubType;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static com.superapp.core.sqs.SQSUtil.sendMessageWithSQSName;
import static com.superapp.redsocial.notification.domain.utils.Type.FRIEND_REQUEST;


public class NotificationFriend {
    private final NotificationNeptuneRepository notificationNeptuneRepository;
    private final NotificationDynamoRepository notificationDynamoRepository;

    public NotificationFriend(NotificationNeptuneRepository notificationNeptuneRepository,
                              NotificationDynamoRepository notificationDynamoRepository) {
        this.notificationNeptuneRepository = notificationNeptuneRepository;
        this.notificationDynamoRepository = notificationDynamoRepository;
    }


    public void Notification(RequestData requestData) throws Exception{

        SALogger.info("NotificationFriend", "Notification", requestData.toString());

        var message = new String[] { " solicitó seguirte", " aceptó tu solicitud"};
        var union = notificationNeptuneRepository.
                getDataRelationship(requestData,
                        requestData.getType() == SubType.SENT_FRIEND_REQUEST.value() ?
                                message[SubType.SENT_FRIEND_REQUEST.value()] :
                                message[SubType.ACCEPTED_FRIEND_REQUEST.value()]);

        var notificacion = union.getNotificacion();
        var idUserReceptor = union.getId();
        var idUserEmisor = notificacion.getUsuario().getId();


        var notificationData = new Notification(notificacion.getId().toString(),
                requestData.getDate(),
                0l,
                FRIEND_REQUEST.name(),
                FRIEND_REQUEST.value(),
                requestData.getType(),
                Status.SENT.value(),
                DataNotification.builder()
                        .idRelation(requestData.getTarget())
                        .build(),
                EventNotification.builder()
                        .body(union.getNotificacion().getMensaje())
                        .extractText(union.getNotificacion().getMensaje())
                        .build(),
                SenderUser.builder()
                        .id(notificacion.getUsuario().getId())
                        .name(notificacion.getUsuario().getName())
                        .image(notificacion.getUsuario().getImage())
                        .build(),
                ReceiverUser
                        .builder()
                        .id(union.getId())
                        .name(union.getName())
                        .sicu(union.getSicu())
                        .build(),
                notificacion.getUsuarios(),
                Arrays.asList(EventNotification.builder()
                        .body(union.getNotificacion().getMensaje())
                        .extractText(union.getNotificacion().getMensaje())
                        .build()),
                1);



        if(!idUserEmisor.equals(idUserReceptor)){

            var notification = notificationDynamoRepository.setDataNotification(notificationData,
                    idUserReceptor, idUserEmisor, requestData.getTarget(), requestData.getType());


            Notificacion payloadNotification = Notificacion.builder()
                    .id(UUID.fromString(notification.getIdNotification()))
                    .contador(notification.getCount())
                    .fechaCreacion(notification.getCreateDate())
                    .estatus(notification.getStatus())
                    .idRelacion(notification.getDataNotification().getIdRelation())
                    .mensaje(notification.getEventNotification().getBody())
                    .subTipo(notification.getSubType())
                    .tipo(notification.getType())
                    .usuario(new Usuario(notification.getSenderUser().getName(),notification.getSenderUser().getId(),notification.getSenderUser().getImage()))
                    .usuarios(notification.getListUser())
                    .build();


            System.out.println("Antes del cambio de Id: " + notification);




            if(payloadNotification.getSubTipo() == SubType.SENT_FRIEND_REQUEST.value()){
                NotificationPushSQS notificationPushSQS = new NotificationPushSQS();
                payloadNotification.getUsuario().setId(idUserReceptor);
                notificationPushSQS.sendMessageSQS(payloadNotification, union.getSicu(), Strings.capWords(notificacion.getUsuario().getName()));

            }if (payloadNotification.getSubTipo() == SubType.ACCEPTED_FRIEND_REQUEST.value()){
                NotificationPushSQS notificationPushSQS = new NotificationPushSQS();
                notificationPushSQS.sendMessageSQS(payloadNotification, union.getSicu(), Strings.capWords(notificacion.getUsuario().getName()));
            }

            sendNotification(RequestSendSocket.builder()
                            .userId(payloadNotification.getUsuario().getId())
                            .message(payloadNotification.getMensaje()).build());

        }
    }

    public void sendNotification(RequestSendSocket input){
        try {
            System.out.println("payload notification :\n" + input);
            sendMessageWithSQSName(JsonTransformer.mapper().writeValueAsString(input),
                    Env.var("SQS_NOTIFICATION").get(), 0, 0);
            System.out.println("payload notification :\n" + input);
        } catch (Exception e) {
            RSLogger.exception(e);
        }

    }
}
