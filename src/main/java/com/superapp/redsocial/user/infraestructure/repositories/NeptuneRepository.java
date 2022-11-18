package com.superapp.redsocial.user.infraestructure.repositories;

import com.superapp.core.neptune.application.redsocial.RsClusterManager;
import com.superapp.redsocial.core.domain.constants.RsEdges;
import com.superapp.redsocial.core.domain.constants.RsPostStatus;
import com.superapp.redsocial.core.domain.exceptions.UnAuthorizedException;
import com.superapp.redsocial.core.shared.paginator.Paginator;
import com.superapp.redsocial.core.shared.search.SearchQuery;
import com.superapp.redsocial.user.domain.constants.PostStatus;
import com.superapp.redsocial.user.domain.entity.Post;
import com.superapp.redsocial.user.domain.entity.Privacy;
import com.superapp.redsocial.user.domain.entity.ResponsePost;
import com.superapp.redsocial.user.domain.entity.TypesPrivacy;
import com.superapp.redsocial.user.domain.repositories.UserPostRepository;
import com.superapp.redsocial.user.infraestructure.builders.EnhancedPostBuilder;
import com.superapp.redsocial.user.infraestructure.builders.PostBuilder;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.WithOptions;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Map;
import java.util.stream.Collectors;

import static com.superapp.redsocial.core.shared.utils.UCollections.extInt;
import static com.superapp.redsocial.core.shared.utils.UCollections.extStr;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.has;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.unfold;


public class NeptuneRepository implements UserPostRepository {

    public NeptuneRepository() {

    }

    /**
     * @param query Query data for search
     * @return Paginator of posts
     * @throws Exception throw exceptions
     * @deprecated
     */
    @Override
    public Paginator<Post> getAll(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();
            //AuthorizerProxy.verify(authorizer, g, query);
            return new Paginator<>(g.V().hasLabel("Post").as("post")
                    .has("status", PostStatus.POSTED.value())
                    .where(__.inE(RsEdges.POSTED.value()).as("edgePosted").otherV().as("me").has(T.id, query.getEntityId()))
                    .order().by("createdAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .inE(RsEdges.POSTED.V()).as("postedEdge")
                    .otherV().as("author")
                    .select("post", "author").by(__.valueMap().with(WithOptions.tokens))
                    .toList().parallelStream()
                    .map(v -> PostBuilder.fromFoldMap(v, g, query))
                    .map(m -> PostBuilder.postCompleter(m, g))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    /**
     * Retrieve user wall
     *
     * @param query Query data for search
     * @return Data found
     * @apiNote 0.02
     */
    @Override
    public Paginator<Post> getUserWall(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();

            //AuthorizerProxy.verify(authorizer, g, query);

            return new Paginator<>(g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").has("status", PostStatus.POSTED.value()).as("post").dedup()
                    .order().by("createdAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .bothE(RsEdges.POSTED.V()).has("status", 1).otherV().as("author")
                    .select("post", "author").by(__.valueMap().with(WithOptions.tokens)).toList().parallelStream()
                    .map(v -> EnhancedPostBuilder.build(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public Paginator<Post> getUserWallNotFriend(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();
            //AuthorizerProxy.verify(authorizer, g, query);

            return new Paginator<>(g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").has("status", PostStatus.POSTED.value())
                    .not(has("privacy", TypesPrivacy.PRIVATE.getValue())).as("post").dedup()
                    .order().by("createdAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .bothE(RsEdges.POSTED.V()).has("status", 1).otherV().as("author")
                    .select("post", "author").by(__.valueMap().with(WithOptions.tokens)).toList().parallelStream()
                    .map(v -> EnhancedPostBuilder.build(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public Post getMyUnverifiedPost(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();
            var post=g.V(query.getEntityId()).as("author").bothE(RsEdges.POSTED.V()).has("status", 1)
                    .otherV().hasLabel("Post").has("status", RsPostStatus.FEATURED.V())
                    .as("post").select("post", "author")
                    .by(__.valueMap().with(WithOptions.tokens)).toList()
                    .parallelStream().map(v -> EnhancedPostBuilder.build(v, g, query))
                    .collect(Collectors.toList());
//            System.out.println("---->"+post);
            if(post.isEmpty())
                return null;
            return post.get(0);
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public Boolean isPublicWall(String userId, String meId) throws Exception {
        try {
            Privacy isPublic = new Privacy();
            final var g = RsClusterManager.R.readerRsSource();
            GraphTraversal<Vertex, Map<Object, Object>> user = g.V(userId).valueMap().by(unfold()).with(WithOptions.tokens);
            if (user.hasNext()) {
                var c = user.next();
                var privacy = extInt(c, "privacy", 1);
                var sicu = extStr(c, "sicu");
                if (privacy == 1 || sicu.equals(meId)) {
                    isPublic.setPrivacy(true);
                } else {
                    getInEV(g, userId, (e, v) -> {
                        if (extInt(e, "status") == 1) {
                            isPublic.setPrivacy(true);
                        } else {
                            isPublic.setPrivacy(true);
                        }
                    }, "FRIENDS", null, new String[]{"image", "phoneNumber", "name"}, meId);
                }
            }
            return isPublic.getPrivacy();
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public Boolean isMyFriend(String userId, String meId) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();
            final Boolean[] isMyFriend2 = {false};
            String idUser = g.V().has("sicu", meId).next().id().toString();
            if (idUser.equals(userId)) {
                isMyFriend2[0] = true;
            } else {
                getOutEV(g, idUser, (e, v) -> {
                    if (extStr(e, T.label).equals("FRIENDS")) {
                        if (extInt(e, "status") == 1) {
                            isMyFriend2[0] = true;
                        }
                    }
                }, "FRIENDS", new String[]{}, new String[]{"image", "phoneNumber", "name"}, userId);
            }
            return isMyFriend2[0];
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }


    @Override
    public Paginator<Post> getMyHiddenPost(SearchQuery query, int hidden) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();
            return new Paginator<>(g.V(query.getEntityId()).as("author").outE("POSTED")
                    .otherV().hasLabel("Post").has("status",RsPostStatus.HIDDEN.V())
                    .order().by("updatedAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .as("post")
                    .select("post", "author")
                    .by(__.valueMap().with(WithOptions.tokens)).toList()
                    .parallelStream().map(v -> EnhancedPostBuilder.buildHidden(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());

        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public String getIdbySicu(String userSicu) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();
            return g.V().has("sicu",userSicu).hasLabel("User").next().id().toString();

        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public ResponsePost getMyHiddenPostMM(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();

            var count = g.V(query.getEntityId()).as("author").outE("POSTED")
                    .otherV().hasLabel("Post").has("status",RsPostStatus.HIDDEN.V())
                    .has("type",0)
                    .where(__.outE("HAS_MM"))
                    .dedup()
                    .count()
                    .next();

            var page =  new Paginator<>(g.V(query.getEntityId()).as("author").outE("POSTED")
                    .otherV().hasLabel("Post").has("status",RsPostStatus.HIDDEN.V())
                    .has("type",0)
                    .where(__.outE("HAS_MM"))
                    .order().by("updatedAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .as("post")
                    .select("post", "author")
                    .by(__.valueMap().with(WithOptions.tokens)).toList()
                    .parallelStream().map(v -> EnhancedPostBuilder.buildHidden(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());

            return new ResponsePost(page.getPageInformation(),count,page.get());
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public ResponsePost getMyHiddenPostTxt(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();

            var count = g.V(query.getEntityId()).as("author").outE("POSTED")
                    .otherV().hasLabel("Post").has("status",RsPostStatus.HIDDEN.V())
                    .has("type",0)
                    .where(__.not(__.outE("HAS_MM")))
                    .dedup()
                    .count()
                    .next();


            var page = new Paginator<>(g.V(query.getEntityId()).as("author").outE("POSTED")
                    .otherV().hasLabel("Post").has("status",RsPostStatus.HIDDEN.V())
                    .has("type",0)
                    .where(__.not(__.outE("HAS_MM")))
                    .order().by("updatedAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .as("post")
                    .select("post", "author")
                    .by(__.valueMap().with(WithOptions.tokens)).toList()
                    .parallelStream().map(v -> EnhancedPostBuilder.buildHidden(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());

             return new ResponsePost(page.getPageInformation(),count,page.get());

        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public ResponsePost getMyHiddenPostEtq(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();

            var count = g.V(query.getEntityId()).as("author").outE("POSTED")
                     .otherV().hasLabel("Post").not(has("type",0)).has("status",RsPostStatus.HIDDEN.V())
                    .dedup()
                    .count()
                    .next();

            var page =  new Paginator<>(g.V(query.getEntityId()).as("author").outE("POSTED")
                    .otherV().hasLabel("Post").not(has("type",0)).has("status",RsPostStatus.HIDDEN.V())
                    .order().by("updatedAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .as("post")
                    .select("post", "author")
                    .by(__.valueMap().with(WithOptions.tokens)).toList()
                    .parallelStream().map(v -> EnhancedPostBuilder.buildHidden(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());

            return new ResponsePost(page.getPageInformation(),count,page.get());
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public ResponsePost getUserWallMM(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();

            //AuthorizerProxy.verify(authorizer, g, query);

            var count = g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").has("status", PostStatus.POSTED.value())
                    .has("type",0)
                    .where(__.outE("HAS_MM"))
                    .as("post").dedup()
                    .bothE(RsEdges.POSTED.V()).has("status", 1)
                    .dedup()
                    .count()
                    .next();


            var page = new Paginator<>(g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").has("status", PostStatus.POSTED.value())
                    .has("type",0)
                    .where(__.outE("HAS_MM"))
                    .as("post").dedup()
                    .order().by("createdAt", Order.desc)
                    .range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .bothE(RsEdges.POSTED.V()).has("status", 1).otherV().as("author")
                    .select("post", "author").by(__.valueMap().with(WithOptions.tokens)).toList().parallelStream()
                    .map(v -> EnhancedPostBuilder.build(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());

            return new ResponsePost(page.getPageInformation(),count,page.get());
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public ResponsePost getUserWallTxt(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();

            //AuthorizerProxy.verify(authorizer, g, query);

            var count = g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").has("status", PostStatus.POSTED.value())
                    .has("type",0)
                    .where(__.not(__.outE("HAS_MM")))
                    .as("post")
                    .dedup()
                    .bothE(RsEdges.POSTED.V()).has("status", 1)
                    .dedup()
                    .count()
                    .next();


            var page =  new Paginator<>(g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").has("status", PostStatus.POSTED.value())
                    .has("type",0)
                    .where(__.not(__.outE("HAS_MM")))
                    .as("post").dedup()
                    .order().by("createdAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .bothE(RsEdges.POSTED.V()).has("status", 1).otherV().as("author")
                    .select("post", "author").by(__.valueMap().with(WithOptions.tokens)).toList().parallelStream()
                    .map(v -> EnhancedPostBuilder.build(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());

            return new ResponsePost(page.getPageInformation(),count,page.get());
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public ResponsePost getUserWallEtq(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();

            //AuthorizerProxy.verify(authorizer, g, query);

            var count = g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").not(has("type",0))
                    .has("status", PostStatus.POSTED.value()).as("post")
                    .dedup()
                    .bothE(RsEdges.POSTED.V()).has("status", 1)
                    .dedup()
                    .count()
                    .next();

            var page = new Paginator<>(g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").not(has("type",0)).has("status", PostStatus.POSTED.value()).as("post").dedup()
                    .order().by("createdAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .bothE(RsEdges.POSTED.V()).has("status", 1).otherV().as("author")
                    .select("post", "author").by(__.valueMap().with(WithOptions.tokens)).toList().parallelStream()
                    .map(v -> EnhancedPostBuilder.build(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());

            return new ResponsePost(page.getPageInformation(),count,page.get());
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public ResponsePost getUserWallNotFriendMM(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();
            //AuthorizerProxy.verify(authorizer, g, query);

            var count = g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").has("status", PostStatus.POSTED.value())
                    .has("type",0)
                    .where(__.outE("HAS_MM"))
                    .not(has("privacy", TypesPrivacy.PRIVATE.getValue())).as("post").dedup()
                    .order().by("createdAt", Order.desc)
                    .bothE(RsEdges.POSTED.V())
                    .has("status", 1)
                    .dedup()
                    .count()
                    .next();

            var page = new Paginator<>(g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").has("status", PostStatus.POSTED.value())
                    .has("type",0)
                    .where(__.outE("HAS_MM"))
                    .not(has("privacy", TypesPrivacy.PRIVATE.getValue())).as("post").dedup()
                    .order().by("createdAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .bothE(RsEdges.POSTED.V()).has("status", 1).otherV().as("author")
                    .select("post", "author").by(__.valueMap().with(WithOptions.tokens))
                    .toList().parallelStream()
                    .map(v -> EnhancedPostBuilder.build(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());

            return new ResponsePost(page.getPageInformation(),count,page.get());
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public ResponsePost getUserWallNotFriendTxt(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();
            //AuthorizerProxy.verify(authorizer, g, query);

            var count = g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").has("status", PostStatus.POSTED.value())
                    .has("type",0)
                    .where(__.not(__.outE("HAS_MM")))
                    .not(has("privacy", TypesPrivacy.PRIVATE.getValue())).as("post")
                    .dedup()
                    .bothE(RsEdges.POSTED.V())
                    .has("status", 1)
                    .dedup()
                    .count()
                    .next();



            var page = new Paginator<>(g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").has("status", PostStatus.POSTED.value())
                    .has("type",0)
                    .where(__.not(__.outE("HAS_MM")))
                    .not(has("privacy", TypesPrivacy.PRIVATE.getValue())).as("post").dedup()
                    .order().by("createdAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .bothE(RsEdges.POSTED.V()).has("status", 1).otherV().as("author")
                    .select("post", "author").by(__.valueMap().with(WithOptions.tokens)).toList().parallelStream()
                    .map(v -> EnhancedPostBuilder.build(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());

            return new ResponsePost(page.getPageInformation(),count,page.get());
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }

    @Override
    public ResponsePost getUserWallNotFriendEtq(SearchQuery query) throws Exception {
        try {
            final var g = RsClusterManager.R.readerRsSource();
            //AuthorizerProxy.verify(authorizer, g, query);

            var count = g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").not(has("type",0))
                    .has("status", PostStatus.POSTED.value())
                    .not(has("privacy", TypesPrivacy.PRIVATE.getValue())).as("post")
                    .dedup()
                    .bothE(RsEdges.POSTED.V()).has("status", 1)
                    .dedup()
                    .count()
                    .next();

            var page =  new Paginator<>(g.V(query.getEntityId())
                    .bothE(RsEdges.POSTED.V(), "POSTED_TO", "TAGGED_TO").has("status", PostStatus.APPROVED.value())
                    .otherV().hasLabel("Post").not(has("type",0)).has("status", PostStatus.POSTED.value())
                    .not(has("privacy", TypesPrivacy.PRIVATE.getValue())).as("post").dedup()
                    .order().by("createdAt", Order.desc).range(query.getPageIdentifier().low(), query.getPageIdentifier().high() + 1)
                    .bothE(RsEdges.POSTED.V()).has("status", 1).otherV().as("author")
                    .select("post", "author").by(__.valueMap().with(WithOptions.tokens)).toList().parallelStream()
                    .map(v -> EnhancedPostBuilder.build(v, g, query))
                    .collect(Collectors.toList())
                    , query.getPageIdentifier());

            return new ResponsePost(page.getPageInformation(),count,page.get());
        } finally {
            RsClusterManager.R.closeRsReaderClient();
        }
    }


    public static void getInEV(GraphTraversalSource g, String originNodeId, EachMapRecord each, String label, String[] otherLabels, String[] propsKey, String idUsuario) throws Exception {
        final GraphTraversal<Vertex, Map<String, Object>> transversal = g.V(originNodeId).inE("FRIENDS").as("b").otherV().has("sicu", idUsuario).as("c").select("b", "c").by(__.valueMap().with(WithOptions.tokens));
        if (!transversal.hasNext()) {
//            throw new UnAuthorizedException();
        }
        while (transversal.hasNext()) {
            Map<String, Object> r = transversal.next();
            each.each((Map<String, Object>) r.get("b"), (Map<String, Object>) r.get("c"));
        }
    }


    public interface EachMapRecord {
        void each(Map<String, Object> edge, Map<String, Object> vertex) throws UnAuthorizedException;
    }

    public static void getOutEV(GraphTraversalSource g, String originNodeId, EachMapRecord each, String label, String[] otherLabels, String[] propsKey, String idUsuario) throws Exception {
        final GraphTraversal<Vertex, Map<String, Object>> transversal = g.V(originNodeId).outE().hasLabel(label, otherLabels).as("b").otherV().hasId(idUsuario).as("c").select("b", "c").by(__.valueMap().with(WithOptions.tokens));
        while (transversal.hasNext()) {
            Map<String, Object> r = transversal.next();
            each.each((Map<String, Object>) r.get("b"), (Map<String, Object>) r.get("c"));
        }
    }

}
