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
public class DataNotification {

    @JsonProperty("idPost")
    @SerializedName(value = "idPost")
    private String idPost;

    @JsonProperty("idComment")
    @SerializedName(value = "idComment")
    private String idComment;

    @JsonProperty("idResponse")
    @SerializedName(value = "idResponse")
    private String idResponse;

    @JsonProperty("idRelation")
    @SerializedName(value = "idRelation")
    private String idRelation;

    @JsonProperty("idStory")
    @SerializedName(value = "idStory")
    private String idStory;

    @JsonProperty("idGroup")
    @SerializedName(value = "idGroup")
    private String idGroup;
}
