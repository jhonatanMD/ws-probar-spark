package com.superapp.redsocial.notification.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Union {
    private String id;
    private Notificacion notificacion;
    private String sicu;
    private String name;
}
