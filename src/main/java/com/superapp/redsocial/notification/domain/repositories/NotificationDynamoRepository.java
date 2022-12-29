package com.superapp.redsocial.notification.domain.repositories;

import com.superapp.redsocial.notification.domain.entity.Notificacion;
import com.superapp.redsocial.notification.domain.entity.data.Notification;

public interface NotificationDynamoRepository {

    Notification setDataNotification(Notification notificacion, String idUserReceptor,
                                     String idUserEmisor, String target, Integer TypeNotification);

}
