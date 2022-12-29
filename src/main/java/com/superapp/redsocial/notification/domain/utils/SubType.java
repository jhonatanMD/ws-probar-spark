package com.superapp.redsocial.notification.domain.utils;

public enum SubType {
    SENT_FRIEND_REQUEST(0),
    ACCEPTED_FRIEND_REQUEST(1);
    private final Integer value;

    SubType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
