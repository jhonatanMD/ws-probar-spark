package com.superapp.redsocial.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reaction {
    @JsonProperty("idReaccion")
    private String reactionId;
    @JsonProperty("total")
    private int count;

    public Reaction() {
    }

    public Reaction(String reactionId, int count) {
        this.reactionId = reactionId;
        this.count = count;
    }

    public String getReactionId() {
        return reactionId;
    }

    public void setReactionId(String reactionId) {
        this.reactionId = reactionId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
