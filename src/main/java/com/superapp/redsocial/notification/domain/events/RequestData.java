package com.superapp.redsocial.notification.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestData {
    private String target;
    private Integer type;
    private Long date;
}
