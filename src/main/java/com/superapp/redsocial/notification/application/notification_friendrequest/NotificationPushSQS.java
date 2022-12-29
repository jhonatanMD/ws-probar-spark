package com.superapp.redsocial.notification.application.notification_friendrequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superapp.core.sqs.SQSUtil;
import com.superapp.redsocial.core.shared.utils.Env;
import com.superapp.redsocial.notification.domain.entity.Emisor;
import com.superapp.redsocial.notification.domain.entity.Notificacion;
import com.superapp.redsocial.notification.domain.entity.Parametro;
import com.superapp.redsocial.notification.domain.entity.PushNotification;
import com.superapp.redsocial.notification.domain.events.ResposeData;
import com.superapp.redsocial.notification.domain.utils.ConstansPush;
import com.superapp.redsocial.notification.domain.utils.SALogger;
import com.superapp.redsocial.notification.domain.utils.SubType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.superapp.core.sqs.SQSUtil.sendMessageWithSQSName;

public class NotificationPushSQS {

    public void sendMessageSQS(Notificacion notificacion, String sicu, String name){

        SALogger.info("NotificationPushSQS", "sendMessageSQS",
                "El sicu del usuario: " + name + " es: " + sicu);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        List<Parametro> listParameter = new ArrayList<>();
        listParameter.add(new Parametro("usuario", name));
        listParameter.add(new Parametro("flow", ConstansPush.FLOW.value()));
        listParameter.add(new Parametro("upax", gson.toJson(notificacion)));

        Emisor emisor = new Emisor(listParameter);

        PushNotification pushNotification = new PushNotification();
        pushNotification.setIdPlantilla(notificacion.getSubTipo() == SubType.SENT_FRIEND_REQUEST.value() ?
                ConstansPush.ID_PLANTILLA_AMISTAD.value() :
                ConstansPush.ID_PLANTILLA_AMISTAD_ACEPTADA.value());
        pushNotification.setEmisor(emisor);

        Map<String, String> map = new HashMap<>();
        map.put("x-sicu", sicu);
        map.put("x-id-interaccion", ConstansPush.X_ID_INTEREACCION.value());

        var push = gson.toJson(pushNotification);
        ResposeData resposeData = new ResposeData();
        resposeData.setAwsSecretManagerKey(Env.var("SCToken").get());
        resposeData.setRequestBody(push);
        resposeData.setHeaders(map);

        var message = gson.toJson(resposeData);
        System.out.println(message);
        sendMessageWithSQSName(message, Env.var("PushNotificationSQS").get(), 0, 1);
        SALogger.info("SQSUtil", "sendMessageWithSQSName", message);

    }
}
