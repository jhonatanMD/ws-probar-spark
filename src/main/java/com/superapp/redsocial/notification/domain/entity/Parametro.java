package com.superapp.redsocial.notification.domain.entity;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Parametro {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("valor")
    private String valor;
}
