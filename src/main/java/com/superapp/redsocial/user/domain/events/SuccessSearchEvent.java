package com.superapp.redsocial.user.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.superapp.redsocial.core.domain.constants.HttpCodes;
import com.superapp.redsocial.core.domain.events.SuccessfulEvent;
import com.superapp.redsocial.core.shared.paginator.PageInformation;
import com.superapp.redsocial.user.domain.entity.Post;

import java.util.List;


public class SuccessSearchEvent extends SuccessfulEvent {

    @JsonProperty("paginacion")
    private final PageInformation pagination;
    @JsonProperty("publicacionesMultimedia")
    private final List<Post> postMultimedia;
    @JsonProperty("publicacionesTexto")
    private final List<Post> postText;
    @JsonProperty("publicacionesEtiquedatas")
    private final List<Post> postTags;

    public SuccessSearchEvent(PageInformation pagination, List<Post> postMultimedia,List<Post> postText,List<Post> postTags, String message) {
        super(HttpCodes.SUCCESS.code(), message);
        this.pagination = pagination;
        this.postMultimedia = postMultimedia;
        this.postText = postText;
        this.postTags = postTags;
    }

    public SuccessSearchEvent(PageInformation pagination,List<Post> postMultimedia,List<Post> postText,List<Post> postTags) {
        super(HttpCodes.SUCCESS.code(), "Successful operation");
        this.pagination = pagination;
        this.postMultimedia = postMultimedia;
        this.postText = postText;
        this.postTags = postTags;
    }


}
