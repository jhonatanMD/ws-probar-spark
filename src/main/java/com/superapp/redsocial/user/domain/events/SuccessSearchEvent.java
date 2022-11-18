package com.superapp.redsocial.user.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.superapp.redsocial.core.domain.constants.HttpCodes;
import com.superapp.redsocial.core.domain.events.SuccessfulEvent;
import com.superapp.redsocial.core.shared.paginator.PageInformation;
import com.superapp.redsocial.user.domain.entity.Post;
import com.superapp.redsocial.user.domain.entity.ResponsePost;

import java.util.List;


public class SuccessSearchEvent extends SuccessfulEvent {


    @JsonProperty("publicacionesMultimedia")
    private final ResponsePost postMultimedia;
    @JsonProperty("publicacionesTexto")
    private final ResponsePost postText;
    @JsonProperty("publicacionesEtiquedatas")
    private final ResponsePost postTags;

    public SuccessSearchEvent(ResponsePost postMultimedia,ResponsePost postText,ResponsePost postTags, String message) {
        super(HttpCodes.SUCCESS.code(), message);
        this.postMultimedia = postMultimedia;
        this.postText = postText;
        this.postTags = postTags;
    }

    public SuccessSearchEvent(ResponsePost postMultimedia,ResponsePost postText,ResponsePost postTags) {
        super(HttpCodes.SUCCESS.code(), "Successful operation");
        this.postMultimedia = postMultimedia;
        this.postText = postText;
        this.postTags = postTags;
    }


}
