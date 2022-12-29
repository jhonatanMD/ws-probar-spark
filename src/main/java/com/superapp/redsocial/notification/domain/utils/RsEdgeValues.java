package com.superapp.redsocial.notification.domain.utils;

public enum RsEdgeValues {
    FRIENDS("FRIENDS");
    private final String value;


    RsEdgeValues(String value) {
        this.value = value;
    }


    public String value() {
        return value;
    }

}
