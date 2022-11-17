package com.superapp.redsocial.user.domain.repositories;


import com.superapp.redsocial.user.domain.entity.ReactionData;

import java.util.List;


public interface ReactionCatalogRepository {
    List<ReactionData> getActiveReactions();
}
