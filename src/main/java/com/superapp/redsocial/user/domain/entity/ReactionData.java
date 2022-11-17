package com.superapp.redsocial.user.domain.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;


@DynamoDbBean
public class ReactionData {
    private String id;
    private boolean active;
    private String animation;
    private String color;
    private String image;
    private String type;

    public ReactionData() {
    }

    public ReactionData(String id, boolean active, String animation, String color, String image, String type) {
        this.id = id;
        this.active = active;
        this.animation = animation;
        this.color = color;
        this.image = image;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAnimation() {
        return animation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ReactionData{" +
                "id='" + id + '\'' +
                ", active=" + active +
                ", animation='" + animation + '\'' +
                ", color='" + color + '\'' +
                ", image='" + image + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
