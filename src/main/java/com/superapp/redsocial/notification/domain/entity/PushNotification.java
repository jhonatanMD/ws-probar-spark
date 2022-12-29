package com.superapp.redsocial.notification.domain.entity;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PushNotification {

    @SerializedName("idPlantilla")
    private String idPlantilla;

    @SerializedName("emisor")
    private Emisor emisor;
}
