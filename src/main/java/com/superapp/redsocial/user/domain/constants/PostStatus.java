package com.superapp.redsocial.user.domain.constants;


public enum PostStatus {
    __(-1),
    POSTED(1),
    CENSORED(2),
    DELETED(3),
    FEATURED(4),
    HIDDEN(6),
    APPROVED(1);


    private final int status;

    PostStatus(int status) {
        this.status = status;
    }

    public int value() {
        return status;
    }

}

