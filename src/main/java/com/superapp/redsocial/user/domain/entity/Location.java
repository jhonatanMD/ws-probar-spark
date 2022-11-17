package com.superapp.redsocial.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @JsonProperty(value = "id")
    String id;
    @JsonProperty(value = "latitud")
    String lat;
    @JsonProperty(value = "longitud")
    String lng;
    @JsonProperty(value = "nombre")
    String name;
    @JsonProperty(value = "direccion")
    String address;
    @JsonProperty(value = "imagen")
    String image;
    @JsonProperty(value = "idMapa")
    String idMap;
}
