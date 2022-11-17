package com.superapp.redsocial.user.domain.constants;


public enum SeccionValues {
    MURO(1),
    OCULTAS(2);

    private final int value;


    SeccionValues(int value) {
        this.value = value;
    }


    public int V() {
        return value;
    }


}
