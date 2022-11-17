package com.superapp.redsocial.user.infraestructure.builders;

import com.superapp.redsocial.user.domain.entity.ReactionData;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;


public class ReactionDataBuilder {
    public static ReactionData of(final Map<String, AttributeValue> data) {
        return new ReactionData(
                data.get("Id").s(),
                data.get("Active").bool(),
                data.get("Animation").s(),
                data.get("Color").s(),
                data.get("Image").s(),
                data.get("Type").s()
        );
    }
}
