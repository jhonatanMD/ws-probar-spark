package com.superapp.redsocial.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.superapp.redsocial.core.shared.paginator.PageInformation;
import lombok.Data;

import java.util.List;

@Data
public class ResponsePost {

    @JsonProperty("paginacion")
    private final PageInformation pagination;

    @JsonProperty("cantidad")
    private final Long size;

    @JsonProperty("publicaciones")
    private final List<Post> post;

    public ResponsePost(PageInformation pagination, Long size, List<Post> post) {
        this.pagination = pagination;
        this.size = size;
        this.post = post;
    }
}
