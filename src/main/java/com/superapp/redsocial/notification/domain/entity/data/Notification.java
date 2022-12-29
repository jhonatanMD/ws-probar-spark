package com.superapp.redsocial.notification.domain.entity.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@DynamoDbBean
public class Notification {

    @SerializedName(value = "idNotification")
    @JsonProperty("idNotification")
    private String idNotification;

    @SerializedName(value = "createDate")
    @JsonProperty("createDate")
    private Long createDate;

    @SerializedName(value = "updateDate")
    @JsonProperty("updateDate")
    private Long updateDate;

    @SerializedName(value = "category")
    @JsonProperty("category")
    private String category;

    @SerializedName(value = "type")
    @JsonProperty("type")
    private int type;

    @SerializedName(value = "subType")
    @JsonProperty("subType")
    private int subType;

    @SerializedName(value = "status")
    @JsonProperty("status")
    private int status;

    @SerializedName(value = "dataNotification")
    @JsonProperty("dataNotification")
    private DataNotification dataNotification;

    @SerializedName(value = "eventNotification")
    @JsonProperty("eventNotification")
    private EventNotification eventNotification;

    @SerializedName(value = "senderUser")
    @JsonProperty("senderUser")
    private SenderUser senderUser;

    @SerializedName(value = "receiverUser")
    @JsonProperty("receiverUser")
    private ReceiverUser receiverUser;

    @SerializedName(value = "listUser")
    @JsonProperty("listUser")
    private List<String> listUser;

    @SerializedName(value = "historicalContent")
    @JsonProperty("historicalContent")
    private List<EventNotification> historicalContent;

    @SerializedName(value = "count")
    @JsonProperty("count")
    private int count;




}
