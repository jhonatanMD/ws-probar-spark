package com.superapp.redsocial.user.domain.entity;

public enum TypesPrivacy {
    PRIVATE(0),
    PUBLIC(1);
    private int value;

    TypesPrivacy(int i){
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
