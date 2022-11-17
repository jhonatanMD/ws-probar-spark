package com.superapp.redsocial.user.infraestructure.authorizers;

import com.superapp.core.neptune.application.redsocial.authorizer.NeptuneFilterAuthorization;
import com.superapp.core.neptune.application.redsocial.authorizer.authorizer.AuthorizationData;
import com.superapp.core.neptune.application.redsocial.authorizer.exceptions.ForbiddenAuthException;
import com.superapp.core.neptune.application.redsocial.authorizer.exceptions.ResourceNotFoundAuthException;


public class OwnOrFriendWallFilter extends NeptuneFilterAuthorization {
    public static OwnOrFriendWallFilter build() {
        return new OwnOrFriendWallFilter();
    }

    @Override
    public boolean verify(AuthorizationData authorizationData) throws Exception {
        final var user = this.graphTraversalSource
                .V(authorizationData.getUserId()).elementMap("sicu");

        if (!user.hasNext()) throw new ResourceNotFoundAuthException("User not found");
        final var userData = user.next();

        if (!userData.getOrDefault("sicu", "").equals(authorizationData.getIdentityId())) {
            final var isMyFriend = this.graphTraversalSource
                    .V().hasLabel("User").has("sicu", authorizationData.getIdentityId())
                    .outE("FRIENDS")
                    .has("status", 1)
                    .otherV().hasId(authorizationData.getUserId());

            if (!isMyFriend.hasNext())
                throw new ForbiddenAuthException(
                        String.format("Forbidden operation with data provided. The user %s (identityId) does not owns the user wall and does not have a friend with user (%s(userId)) provided.",
                                authorizationData.getIdentityId(), authorizationData.getUserId()));
        }
        return super.verify(authorizationData);
    }
}
