package com.superapp.redsocial.user.domain.constants;


public enum AudioValues {
    IMAGE(-1),
    WITH_NOT_AUDIO(0),
    WITH_AUDIO(1);

    private final int value;


    AudioValues(int value) {
        this.value = value;
    }


    public int V() {
        return value;
    }


}
