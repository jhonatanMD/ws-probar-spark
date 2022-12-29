package com.superapp.redsocial.notification.infraestructure.repositories;

import com.superapp.redsocial.notification.domain.entity.data.Notification;
import com.superapp.redsocial.notification.domain.entity.data.Notifications;
import com.superapp.redsocial.notification.domain.repositories.NotificationDynamoRepository;
import com.superapp.redsocial.notification.domain.utils.SubType;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import superapp.core.aws.dynamo.AWSDynamo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class NotificationDynamoRepositoryImpl implements NotificationDynamoRepository {

    @Override
    public Notification setDataNotification(Notification notificationFriendRequest, String idUserReceptor, String idUserEmisor, String target, Integer typeNotification) {

        DynamoDbTable<Notifications> mappedTable = AWSDynamo.INSTANCE.prepareTableSchema("superapp.redsocial.notifications.v2", Notifications.class);
        Key key = Key.builder().partitionValue(idUserReceptor).build();
        Key key2 = Key.builder().partitionValue(idUserEmisor).build();

        // Get data of target user
        final var receiverData = Optional.ofNullable(mappedTable.getItem(key))
                .orElse(new Notifications(idUserReceptor, 0, new HashMap<>()));

        if (receiverData.getCounterReset() == null)
            receiverData.setCounterReset(0);

        if (notificationFriendRequest.getSubType() == SubType.ACCEPTED_FRIEND_REQUEST.value()) {
            final var emitN = mappedTable.getItem(key2);

            final var toRemove = emitN.getNotifications().values().stream()
                    .filter( l -> l.getDataNotification().getIdRelation() != null)
                    .filter(l -> l.getDataNotification().getIdRelation().equals(target)).findFirst();

            toRemove.ifPresent(v -> emitN.getNotifications().remove(v.getIdNotification()) );

            AWSDynamo.INSTANCE.writeBatch(mappedTable, Notifications.class, List.of(emitN));
        }

        // Add to target user the new notification
        receiverData.getNotifications().put(notificationFriendRequest.getIdNotification(), notificationFriendRequest);
        if (notificationFriendRequest.getSubType() != SubType.ACCEPTED_FRIEND_REQUEST.value()) {
            // Verify if exist 3 or more notifications with type = 3 and subtype = 0
            long count = receiverData.getNotifications().values().stream()
                    .filter(it -> it.getType() == 3 && it.getSubType() == 0)
                    .count();

            if (count > 3) {
                receiverData.getNotifications().remove(receiverData.getNotifications()
                        .values().stream().filter(n -> n.getType() == 3 && n.getSubType() == 0).min((r, l) -> (int) (r.getCreateDate() - l.getCreateDate()))
                        .get().getIdNotification());
            }
        }

        if (receiverData.getNotifications().size() > 30) {
            receiverData.getNotifications().remove(receiverData.getNotifications()
              .values().stream().min(Comparator.comparing(Notification::getCreateDate)).get().getIdNotification());
        }

        receiverData.setCounterReset(receiverData.getCounterReset() + 1);
        AWSDynamo.INSTANCE.writeBatch(mappedTable, Notifications.class, List.of(receiverData));

        return notificationFriendRequest;

    }
}


