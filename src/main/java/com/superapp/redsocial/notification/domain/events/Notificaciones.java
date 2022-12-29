package com.superapp.redsocial.notification.domain.events;

import com.superapp.redsocial.notification.domain.entity.Notificacion;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Notificaciones {
    private String userId;
    private Integer counterReset;
    private Map<String, Notificacion> notificationFriendRequest;


    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "UserId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDbAttribute(value = "CounterReset")
    public Integer getCounterReset() {
        return counterReset;
    }

    public void setCounterReset(Integer counterReset) {
        this.counterReset = counterReset;
    }

    @DynamoDbAttribute(value = "Notificaciones")
    public Map<String, Notificacion> getNotificationFriendRequest() {
        return notificationFriendRequest;
    }

    public void setNotificationFriendRequest(Map<String, Notificacion> notificationFriendRequest) {
        this.notificationFriendRequest = notificationFriendRequest;
    }
}
