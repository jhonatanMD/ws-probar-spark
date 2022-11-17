package com.superapp.redsocial.user.infraestructure.builders;

import com.superapp.redsocial.core.shared.logger.RSLogger;
import com.superapp.redsocial.core.shared.search.SearchQuery;
import com.superapp.redsocial.core.shared.utils.UCollections;
import com.superapp.redsocial.user.domain.constants.PostTypes;
import com.superapp.redsocial.user.domain.entity.Post;
import com.superapp.redsocial.user.domain.entity.PostReactionData;
import com.superapp.redsocial.user.domain.entity.Reaction;
import com.superapp.redsocial.user.domain.entity.User;
import com.superapp.redsocial.user.infraestructure.api.resources.Resources;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.BulkSet;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.WithOptions;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.superapp.redsocial.core.shared.utils.UCollections.extInt;
import static com.superapp.redsocial.core.shared.utils.UCollections.extStr;


public class PostBuilder {

    /**
     * Post Data Completer
     *
     * @param p Post to complete data
     *
     * @return Post instance
     * @deprecated
     */
    public static Post postCompleter(Post p, final GraphTraversalSource g) {

        // TODO: Before call this function prepare 10 clients for dispatch this methods then in another thread close 10 clients

        try {
            final var data = g.V(p.getId())
                    .outE("HAS_MM")
                    .otherV().as("multimedia")
                    .order().by("order", Order.asc)
                    .aggregate("multimedia")
                    .by(__.valueMap().with(WithOptions.tokens))
                    .fold()
                    .V(p.getId())
                    .outE().hasLabel("HAS_LOCATION")
                    .otherV().as("location")
                    .aggregate("location")
                    .by(__.valueMap().with(WithOptions.tokens))
                    .cap("multimedia", "location");

            if (data.hasNext()) {
                final var dataOfPosted = (Map<?, ?>) data.next();
                final BulkSet<?> multimedia = (BulkSet<?>) dataOfPosted.get("multimedia");
                final BulkSet<?> locations = (BulkSet<?>) dataOfPosted.get("location");
                if (multimedia != null && (!multimedia.isEmpty()))
                    p.setMultimedia(multimedia
                            .parallelStream()
                            .map(MultimediaBuilder::fromFoldMapObject)
                            .collect(Collectors.toList()));
                if (locations != null && (!locations.isEmpty()))
                    p.setLocation(locations.parallelStream()
                            .map(LocationBuilder::fromFoldMapObject)
                            .collect(Collectors.toList()).get(0)
                    );
            }

        } catch (Exception e) {
            RSLogger.exception(e);
        }
        return p;
    }


    public static PostReactionData buildTopReactions(final List<?> reactions) {
        try {
            if (reactions == null) return PostReactionData.empty();
            final var foundReactions = reactions.stream().filter(Objects::nonNull).map(r -> {
                try {
                    final var splitData = r.toString().split(":");
                    if (splitData.length != 2 || Integer.parseInt(splitData[1]) == 0)
                        return null;
                    return new Reaction(splitData[0], Integer.parseInt(splitData[1]));
                } catch (Exception e) {
                    RSLogger.exception(e);
                    return null;
                }
            }).filter(Objects::nonNull).sorted((r, l) -> l.getCount() - r.getCount()).collect(Collectors.toList());
            return new PostReactionData(foundReactions.stream()
                    .mapToInt(Reaction::getCount)
                    .sum(), foundReactions);
        } catch (Exception e) {
            RSLogger.exception(e);
            return PostReactionData.empty();
        }
    }

    static Predicate<Integer> isPostedInUserWall = i -> i == PostTypes.USER_POST_IN_USER_WALL.V();

    static BiFunction<GraphTraversalSource, Post, User> getDestinationUser = (g, i) -> {
        if (!isPostedInUserWall.test(i.getType())) return null;
        return g.V(i.getId()).out("POSTED_TO").elementMap().toList().parallelStream()
                .map(UserBuilder::fromFoldMap).findFirst()
                .orElse(null);
    };

    /**
     * @param m     Map
     * @param g     Gremlin source
     * @param query Query String
     *
     * @return Post object
     * @deprecated
     */
    public static Post fromFoldMap(final Map<String, Object> m, final GraphTraversalSource g, final SearchQuery query) {
        final Map<?, ?> postData = (Map<?, ?>) m.get("post");
        final Map<?, ?> userData = (Map<?, ?>) m.get("author");
        final var postId = extStr(postData, T.id);


        final var myReactionId = g.V().hasLabel("User").has("sicu", query.getIdentityId()).as("user")
                .outE(Resources.reactionStorage.getReactionsIds()).as("reaction")
                .otherV().hasId(postId)
                .select("reaction")
                .by(__.unfold().valueMap().with(WithOptions.tokens)).toList()
                .stream().map(r -> extStr((Map<?, ?>) r, T.label)).findFirst()
                .orElse(null);

        final var p = new Post(
                UCollections.extStr(postData, T.id),
                UCollections.extInt(postData, "status"),
                myReactionId,
                UCollections.extStr(postData, "idFeeling"),
                UCollections.extStr(postData, "content"),
                UCollections.extInt(postData, "countComment", 0),
                buildTopReactions((List<?>) postData.getOrDefault("countReact", null)),
                UCollections.extLong(postData, "createdAt"),
                UserBuilder.fromFoldMap(userData),
                null,
                null,
                null,
                extInt(postData, "privacy", 1),
                UCollections.extInt(postData, "type", 0),
                null,
                null,
                UCollections.extLong(postData, "updatedAt"),0,0

        );
        if (p.getType() == PostTypes.USER_POST_IN_USER_WALL.V()) {
            p.setDestinationUser(getDestinationUser.apply(g, p));
        }
        return p;
    }
}
