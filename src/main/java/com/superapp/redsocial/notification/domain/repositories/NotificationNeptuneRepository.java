package com.superapp.redsocial.notification.domain.repositories;

import com.superapp.redsocial.notification.domain.entity.Union;
import com.superapp.redsocial.notification.domain.events.RequestData;

public interface NotificationNeptuneRepository {
    Union getDataRelationship(RequestData requestData, String message) throws Exception;
}
