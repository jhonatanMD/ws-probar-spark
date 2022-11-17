package com.superapp.redsocial.shared.infraestructure.authorizer;

import com.superapp.core.neptune.application.redsocial.authorizer.NeptuneAuthorizationManager;
import com.superapp.core.neptune.application.redsocial.authorizer.authorizer.AuthorizationData;
import com.superapp.core.neptune.application.redsocial.authorizer.exceptions.ForbiddenAuthException;
import com.superapp.core.neptune.application.redsocial.authorizer.exceptions.ResourceNotFoundAuthException;
import com.superapp.redsocial.core.domain.exceptions.UnAuthorizedException;
import com.superapp.redsocial.core.domain.exceptions.UserNotFoundException;
import com.superapp.redsocial.core.shared.search.SearchQuery;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;


public class AuthorizerProxy {
    public static void verify(final NeptuneAuthorizationManager authorizationManager, final GraphTraversalSource g, final SearchQuery query) throws Exception {
        try {
            authorizationManager.setGremlinSource(g);
            authorizationManager.verify(AuthorizationData.of(query.getOwnQueryId(), query.getIdentityId()));
        } catch (ResourceNotFoundAuthException e) {
            throw new UserNotFoundException(query.getOwnQueryId());
        } catch (ForbiddenAuthException e) {
            throw new UnAuthorizedException();
        }
    }
}
