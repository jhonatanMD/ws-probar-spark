package com.superapp.redsocial.shared.infraestructure.builders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputFn<T> {
    public static <T> InputFn<T> of(T data, Map<String, Object> meta) {
        return new InputFn<>(data, meta);
    }

    private T data;
    private Map<String, Object> meta;
}
