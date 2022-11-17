package com.superapp.redsocial.user.application.post_searcher;

import com.superapp.redsocial.core.shared.paginator.Paginator;
import com.superapp.redsocial.core.shared.paginator.RangePageIdentifier;
import com.superapp.redsocial.core.shared.search.SearchQuery;
import com.superapp.redsocial.user.domain.constants.SeccionValues;
import com.superapp.redsocial.user.domain.entity.Post;
import com.superapp.redsocial.user.domain.events.SuccessSearchEvent;
import com.superapp.redsocial.user.infraestructure.repositories.NeptuneRepository;

import java.util.List;


public class PostSearcher {
    private final NeptuneRepository psRepository;

    public PostSearcher(final NeptuneRepository psRepository) {
        this.psRepository = psRepository;
    }

    public SuccessSearchEvent  searchAll(String userId, RangePageIdentifier page, String identityId,int hiddenPost) throws Exception {
        final var isPublic=psRepository.isPublicWall(userId,identityId);
        final var isMyFriend=psRepository.isMyFriend(userId,identityId);
        List<Post> posts=null;
        Paginator<Post> searchResultMulti = null;
        Paginator<Post> searchResultText = null;
        Paginator<Post> searchResultTag = null;

        var id=psRepository.getIdbySicu(identityId);
        if(userId.equals(id) && hiddenPost == SeccionValues.OCULTAS.V()) {



            searchResultMulti =   psRepository.getMyHiddenPostMM(new SearchQuery(page, userId, userId, identityId));

            searchResultText =    psRepository.getMyHiddenPostTxt(new SearchQuery(page, userId, userId, identityId));

            searchResultTag =    psRepository.getMyHiddenPostEtq(new SearchQuery(page, userId, userId, identityId));


        }else{

            if(isMyFriend ){
                    searchResultMulti = psRepository.getUserWallMM(new SearchQuery(page, userId, userId, identityId));
                    searchResultText = psRepository.getUserWallTxt(new SearchQuery(page, userId, userId, identityId));
                    searchResultTag = psRepository.getUserWallEtq(new SearchQuery(page, userId, userId, identityId));
            }
            else {
                    searchResultMulti =  psRepository.getUserWallNotFriendMM(new SearchQuery(page, userId, userId, identityId));
                    searchResultText = psRepository.getUserWallNotFriendTxt(new SearchQuery(page, userId, userId, identityId));
                    searchResultTag = psRepository.getUserWallNotFriendEtq(new SearchQuery(page, userId, userId, identityId));
            }

            if(userId.equals(identityId))
                posts.add(0, psRepository.getMyUnverifiedPost(new SearchQuery(page, userId, userId, identityId)));

        }
        return new SuccessSearchEvent(searchResultMulti.getPageInformation(), searchResultMulti.get(), searchResultText.get(), searchResultTag.get());
    }
}
