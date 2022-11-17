package com.superapp.redsocial.user.domain.storages;

import com.superapp.redsocial.user.domain.entity.ReactionData;
import com.superapp.redsocial.user.domain.repositories.ReactionCatalogRepository;

import java.util.List;


public class ReactionStorage {
    private boolean loadedReactions;
    private List<ReactionData> reactions;
    private ReactionCatalogRepository repository;
    private String[] reactionsIds;

    public ReactionStorage(ReactionCatalogRepository repository) {
        this.repository = repository;
    }

    public ReactionStorage() {
    }


    public List<ReactionData> getReactions() {
        if (reactions == null) {
            this.downloadReactions();
            if (reactions != null) {
                reactionsIds = this.reactions.parallelStream()
                        .map(ReactionData::getId)
                        .toArray(String[]::new);
            }
        }
        return this.reactions;
    }

    public boolean isLoadedReactions() {
        return loadedReactions;
    }

    public void setReactions(List<ReactionData> reactions) {
        if (reactions != null) {
            this.reactions = reactions;
            this.loadedReactions = true;
            reactionsIds = this.reactions.parallelStream()
                    .map(ReactionData::getId)
                    .toArray(String[]::new);
        }
    }

    public void downloadReactions(final ReactionCatalogRepository repository) {
        this.reactions = repository.getActiveReactions();
    }

    public String[] getReactionsIds() {
        if (reactions == null) {
            downloadReactions();
        }
        return reactionsIds;
    }

    public void downloadReactions() {
        if (repository != null) {
            this.reactions = repository.getActiveReactions();
            this.loadedReactions = true;
            if (reactions != null) {
                reactionsIds = this.reactions.parallelStream()
                        .map(ReactionData::getId)
                        .toArray(String[]::new);
            }
        }
    }

}
