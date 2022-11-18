package com.superapp.redsocial.user.application.post_searcher;

import com.superapp.redsocial.core.shared.paginator.RangePageIdentifier;
import com.superapp.redsocial.core.shared.search.SearchQuery;
import com.superapp.redsocial.user.domain.constants.SeccionValues;
import com.superapp.redsocial.user.domain.entity.ResponsePost;
import com.superapp.redsocial.user.domain.events.SuccessSearchEvent;
import com.superapp.redsocial.user.infraestructure.repositories.NeptuneRepository;


public class PostSearcher {
    private final NeptuneRepository psRepository;

    public PostSearcher(final NeptuneRepository psRepository) {
        this.psRepository = psRepository;
    }

    public SuccessSearchEvent  searchAll(String userId, RangePageIdentifier page, String identityId,int hiddenPost) throws Exception {
        final var isPublic=psRepository.isPublicWall(userId,identityId);
        final var isMyFriend=psRepository.isMyFriend(userId,identityId);

        ResponsePost responsePostMulti = null;
        ResponsePost responsePostText = null;
        ResponsePost responsePostTag = null;

        var id=psRepository.getIdbySicu(identityId);
        if(userId.equals(id) && hiddenPost == SeccionValues.OCULTAS.V()) {

            responsePostMulti =   psRepository.getMyHiddenPostMM(new SearchQuery(page, userId, userId, identityId));
            responsePostText =    psRepository.getMyHiddenPostTxt(new SearchQuery(page, userId, userId, identityId));
            responsePostTag =    psRepository.getMyHiddenPostEtq(new SearchQuery(page, userId, userId, identityId));

        }else{

            if(isMyFriend ){
                responsePostMulti = psRepository.getUserWallMM(new SearchQuery(page, userId, userId, identityId));
                responsePostText = psRepository.getUserWallTxt(new SearchQuery(page, userId, userId, identityId));
                responsePostTag = psRepository.getUserWallEtq(new SearchQuery(page, userId, userId, identityId));

            }
            else {
                responsePostMulti =  psRepository.getUserWallNotFriendMM(new SearchQuery(page, userId, userId, identityId));
                responsePostText = psRepository.getUserWallNotFriendTxt(new SearchQuery(page, userId, userId, identityId));
                responsePostTag = psRepository.getUserWallNotFriendEtq(new SearchQuery(page, userId, userId, identityId));
            }

            if(userId.equals(identityId)) {

                if(responsePostMulti != null && responsePostMulti.getPost() != null) {
                    responsePostMulti.getPost().add(0, psRepository.getMyUnverifiedPost(new SearchQuery(page, userId, userId, identityId)));
                }
            }

        }

        return new SuccessSearchEvent(responsePostMulti, responsePostText, responsePostTag);
    }
}
