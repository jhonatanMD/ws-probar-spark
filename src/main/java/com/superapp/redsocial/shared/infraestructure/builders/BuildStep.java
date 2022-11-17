package com.superapp.redsocial.shared.infraestructure.builders;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;


@Data
@NoArgsConstructor
public class BuildStep<L, I, O> implements Comparable<BuildStep<?, ?, ?>> {


    public BuildStep(int order, I input, BiFunction<L, I, O> fn, BiConsumer<Object, Object> consumer) {
        this.order = order;
        this.input = input;
        this.fn = fn;
        this.consumer = consumer;
    }

    private int order;
    private I input;
    private O output;
    private Object rawOutput;
    private BiFunction<L, I, O> fn;
    private BiConsumer<Object, Object> consumer;


    @Override
    public int compareTo(BuildStep<?, ?, ?> o) {
        final int compareQuantity = o.getOrder();

        //ascending order
        return this.order - compareQuantity;

        //descending order
        //return compareQuantity - this.quantity;

    }
}
