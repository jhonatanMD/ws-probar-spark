package com.superapp.redsocial.notification.domain.entity.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Notifications {
    private String userId;
    private Integer counterReset;
    private Map<String,Notification> notifications;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute(value = "counterReset")
    public Integer getCounterReset() {
        return counterReset;
    }

    @DynamoDbAttribute(value = "notifications")
    public Map<String, Notification> getNotifications() {
        return notifications;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCounterReset(Integer counterReset) {
        this.counterReset = counterReset;
    }

    public void setNotifications(Map<String, Notification> notifications) {
        this.notifications = notifications;
    }
}
