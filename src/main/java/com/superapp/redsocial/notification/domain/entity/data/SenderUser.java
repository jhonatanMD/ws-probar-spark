package com.superapp.redsocial.notification.domain.entity.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@DynamoDbBean
public class SenderUser {

    @JsonProperty("id")
    @SerializedName(value = "id")
    private String id;

    @JsonProperty("image")
    @SerializedName(value = "image")
    private String image;

    @JsonProperty("name")
    @SerializedName(value = "name")
    private String name;
}
