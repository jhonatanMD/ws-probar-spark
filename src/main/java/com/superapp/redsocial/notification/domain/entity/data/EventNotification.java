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
public class EventNotification {

    @JsonProperty("body")
    @SerializedName(value = "body")
    private String body;

    @JsonProperty("imageReaction")
    @SerializedName(value = "imageReaction")
    private String imageReaction;

    @JsonProperty("extractText")
    @SerializedName(value = "extractText")
    private String extractText;
}
