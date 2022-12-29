package com.superapp.redsocial.notification.domain.utils;

public enum Type {
    REACTIONS (1),
    COMMENTS (2),
    FRIEND_REQUEST(3);
    private final Integer value;

    Type(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
