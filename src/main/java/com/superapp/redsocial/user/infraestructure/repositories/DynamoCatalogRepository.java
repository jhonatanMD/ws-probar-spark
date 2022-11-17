package com.superapp.redsocial.user.infraestructure.repositories;

import com.superapp.redsocial.user.domain.entity.ReactionData;
import com.superapp.redsocial.user.domain.repositories.ReactionCatalogRepository;
import com.superapp.redsocial.user.infraestructure.builders.ReactionDataBuilder;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import superapp.core.aws.dynamo.AWSDynamo;

import java.util.List;
import java.util.stream.Collectors;


public class DynamoCatalogRepository implements ReactionCatalogRepository {

    @Override
    public List<ReactionData> getActiveReactions() {
        final var request = ScanRequest.builder().tableName("Reacciones").build();
        return AWSDynamo.INSTANCE.getClient().scan(request)
                .items().parallelStream().map(ReactionDataBuilder::of).collect(Collectors.toList());
    }

}
