package com.superapp.redsocial.notification.domain.utils;

public enum Status {
    SENT(0),
    VIEWED(1),
    DELETE(2);
    private final Integer value;

    Status(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}

