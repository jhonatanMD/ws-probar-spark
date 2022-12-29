package com.superapp.redsocial.notification.domain.entity;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Emisor {

    @SerializedName("parametros")
    private List<Parametro> parametros;
}
