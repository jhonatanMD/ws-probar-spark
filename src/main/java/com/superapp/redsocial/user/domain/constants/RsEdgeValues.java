package com.superapp.redsocial.user.domain.constants;


public enum RsEdgeValues {
    FRIENDS("FRIENDS"),
    POSTED("POSTED"),
    HAS_MULTIMEDIA("HAS_MM"),
    HAS_LOCATION("HAS_LOCATION");
    private final String value;


    RsEdgeValues(String value) {
        this.value = value;
    }


    public String value() {
        return value;
    }


}
