package com.superapp.redsocial.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PostReactionData {

    public static PostReactionData empty() {
        return new PostReactionData(0, List.of());
    }

    @JsonProperty("total")
    private int total;
    @JsonProperty("clasificacion")
    private List<Reaction> reactions;

    public PostReactionData(int total, List<Reaction> reactions) {
        this.total = total;
        this.reactions = reactions;
    }

    public PostReactionData() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }
}
