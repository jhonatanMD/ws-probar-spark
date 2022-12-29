package com.superapp.redsocial.notification.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestSendSocket {

    private String userId;
    private String connectionId;
    private String message;
}
