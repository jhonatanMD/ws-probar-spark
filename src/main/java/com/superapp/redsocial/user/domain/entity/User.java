package com.superapp.redsocial.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "nombre")
    private String fullName;
    @JsonProperty(value = "miniatura")
    private String thumbnail;
    @JsonProperty(value = "imagen")
    private String image;
    @JsonProperty(value = "tipo")
    private int type;
}
