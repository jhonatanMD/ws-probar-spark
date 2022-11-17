package com.superapp.redsocial.user.domain.constants;


public enum PostTypes {
    USER_POST(0),
    USER_POST_IN_USER_WALL(1),
    USER_POST_WITH_TAGS(2),
    USER_POST_IN_USER_WALL_WITH_TAGS(3);

    private final int value;


    PostTypes(int value) {
        this.value = value;
    }


    public int V() {
        return value;
    }


}
