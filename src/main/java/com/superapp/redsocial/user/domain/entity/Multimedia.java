package com.superapp.redsocial.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Multimedia {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "url")
    private String url;
    @JsonProperty(value = "miniatura")
    private String thumbnail;
    @JsonProperty(value = "formato")
    private String format;
    @JsonProperty(value = "alto")
    private Integer height;
    @JsonProperty(value = "ancho")
    private Integer width;
    @JsonProperty(value="audio")
    private Integer audio;
    @JsonProperty(value="version")
    int version;
}
