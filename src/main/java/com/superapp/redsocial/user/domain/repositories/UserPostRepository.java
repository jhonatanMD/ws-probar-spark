package com.superapp.redsocial.user.domain.repositories;


import com.superapp.redsocial.core.shared.paginator.Paginator;
import com.superapp.redsocial.core.shared.search.SearchQuery;
import com.superapp.redsocial.user.domain.entity.Post;
import com.superapp.redsocial.user.domain.entity.ResponsePost;


public interface UserPostRepository {
    /**
     * Get all posts
     *
     * @param query Query data for search
     *
     * @return Data found
     * @deprecated
     */
    Paginator<Post> getAll(SearchQuery query) throws Exception;


    /**
     * Retrieve user wall
     *
     * @param query Query data for search
     *
     * @return Data found
     */
    Paginator<Post> getUserWall(SearchQuery query) throws Exception;

    Paginator<Post> getUserWallNotFriend(SearchQuery query) throws Exception;

    Post getMyUnverifiedPost(SearchQuery query) throws Exception;

    Boolean  isPublicWall(String userId,String meId) throws Exception;

    Boolean  isMyFriend(String userId,String meId) throws Exception;

    Paginator<Post> getMyHiddenPost(SearchQuery query,int hidden) throws Exception;

    String  getIdbySicu(String userSicu) throws Exception;

    ResponsePost getMyHiddenPostMM(SearchQuery query) throws Exception;
    ResponsePost getMyHiddenPostTxt(SearchQuery query) throws Exception;
    ResponsePost getMyHiddenPostEtq(SearchQuery query) throws Exception;

    ResponsePost getUserWallMM(SearchQuery query) throws Exception;
    ResponsePost getUserWallTxt(SearchQuery query) throws Exception;
    ResponsePost getUserWallEtq(SearchQuery query) throws Exception;

   ResponsePost getUserWallNotFriendMM(SearchQuery query) throws Exception;
    ResponsePost getUserWallNotFriendTxt(SearchQuery query) throws Exception;
    ResponsePost getUserWallNotFriendEtq(SearchQuery query) throws Exception;

}
