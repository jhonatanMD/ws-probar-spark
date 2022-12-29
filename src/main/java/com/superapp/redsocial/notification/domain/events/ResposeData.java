package com.superapp.redsocial.notification.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResposeData {


    @JsonProperty("awsSecretManagerKey")
    @SerializedName("awsSecretManagerKey")
    private String awsSecretManagerKey;

    @JsonProperty("requestBody")
    @SerializedName("requestBody")
    private String requestBody;

    @JsonProperty("headers")
    @SerializedName("headers")
    private Map<String, String> headers;
}
