package com.superapp.redsocial.user.infraestructure.builders;

import com.superapp.redsocial.core.domain.constants.RsEdges;
import com.superapp.redsocial.core.shared.logger.RSLogger;
import com.superapp.redsocial.core.shared.search.SearchQuery;
import com.superapp.redsocial.core.shared.utils.UCollections;
import com.superapp.redsocial.shared.infraestructure.builders.BuildStep;
import com.superapp.redsocial.shared.infraestructure.builders.InputFn;
import com.superapp.redsocial.user.domain.constants.PostTypes;
import com.superapp.redsocial.user.domain.entity.*;
import com.superapp.redsocial.user.infraestructure.api.resources.Resources;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.WithOptions;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.superapp.redsocial.core.shared.utils.UCollections.extInt;
import static com.superapp.redsocial.core.shared.utils.UCollections.extStr;


public class EnhancedPostBuilder {
    /**
     * Build top reactions of current post.
     *
     * @param reactions Raw reactions data
     * @return PostReactionData or null
     */
    public static PostReactionData buildTopReactions(final List<?> reactions) {
        try {
            if (reactions == null) return PostReactionData.empty();
            final var foundReactions = reactions.stream().filter(Objects::nonNull).map(r -> {
                try {
                    final var splitData = r.toString().split(":");
                    if (Integer.parseInt(splitData[1]) == 0)
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

    /**
     * Simple predicates. 🗜
     */
    Predicate<Integer> isPostedInUserWall = i -> i == PostTypes.USER_POST_IN_USER_WALL.V() ||
            i == PostTypes.USER_POST_IN_USER_WALL_WITH_TAGS.V();

    Predicate<Integer> isPostedWithTags = i -> i == PostTypes.USER_POST_WITH_TAGS.V() ||
            i == PostTypes.USER_POST_IN_USER_WALL_WITH_TAGS.V();
    /**
     * Build reaction of user who requested user wall
     */
    BiFunction<GraphTraversalSource, InputFn<Post>, String> getMyReaction = (g, i) -> {
        synchronized (this) {
            return g.V().hasLabel("User").has("sicu", i.getMeta().get("identityId")).as("user")
                    .outE(Resources.reactionStorage.getReactionsIds()).as("reaction")
                    .otherV().hasLabel("Post").hasId(i.getData().getId())
                    .select("reaction")
                    .by(__.unfold().valueMap().with(WithOptions.tokens)).toList()
                    .stream().map(r -> extStr((Map<?, ?>) r, T.label)).findFirst()
                    .orElse(null);
        }
    };

    /**
     * Build destination user
     */
    BiFunction<GraphTraversalSource, InputFn<Post>, User> getDestinationUser = (g, i) -> {
        synchronized (this) {
            if (!isPostedInUserWall.test(i.getData().getType())) return null;
            return g.V(i.getData().getId()).out("POSTED_TO").elementMap().toList().parallelStream()
                    .map(UserBuilder::fromFoldMap).findFirst()
                    .orElse(null);
        }
    };

    /**
     * Build tagged users
     */
    BiFunction<GraphTraversalSource, InputFn<Post>, List<TaggedUser>> getTaggedUsers = (g, i) -> {
        synchronized (this) {
            if (!isPostedWithTags.test(i.getData().getType())) return null;
            return g.V(i.getData().getId()).outE("TAGGED_TO").order().by("order", Order.asc).otherV().elementMap("type").toList()
                    .stream().map(UserBuilder::buildTaggedUser).collect(Collectors.toUnmodifiableList());
        }
    };

    BiFunction<GraphTraversalSource, InputFn<Post>, List<String>> getHashtags = (g, i) -> {
        synchronized (this) {
            return g.V(i.getData().getId()).outE("HAS").order().by("order", Order.asc).otherV().hasLabel("Hashtag").elementMap().toList()
                    .stream().map(UserBuilder::buildHashtags).collect(Collectors.toUnmodifiableList());
        }
    };


    /**
     * Build location
     */
    BiFunction<GraphTraversalSource, InputFn<Post>, Location> getLocation = (g, i) -> {
        synchronized (this) {
            return g.V(i.getData().getId()).out(RsEdges.HAS_LOCATION.value()).as("location")
                    .elementMap().toList().parallelStream().map(LocationBuilder::fromFoldMapObject)
                    .findFirst().orElse(null);
        }
    };

    /**
     * Build media
     */
    BiFunction<GraphTraversalSource, InputFn<Post>, List<Multimedia>> getMedia = (g, i) -> {
        synchronized (this) {
            return g.V(i.getData().getId()).as("post")
                    .out(RsEdges.HAS_MULTIMEDIA.value()).as("multimedia")
                    .order().by("order", Order.asc).elementMap()
                    .toList().parallelStream().map(MultimediaBuilder::fromFoldMapObject)
                    .collect(Collectors.toList());
        }
    };

    /**
     * Build post object from raw data
     *
     * @param dataFold Raw data
     * @param g        Gremlin source
     * @param query    CurrentQuery
     * @return Post object
     */
    public static Post buildHidden(final Map<String, Object> dataFold, final GraphTraversalSource g, final SearchQuery query) {
        final Map<?, ?> postData = (Map<?, ?>) dataFold.get("post");
        final Map<?, ?> userData = (Map<?, ?>) dataFold.get("author");
        Date d = new Date(UCollections.extLong(postData, "createdAt"));
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(d);
        var updated = UCollections.extLong(postData, "updatedAt", 0L);
        if (updated == 0)
            updated = UCollections.extLong(postData, "createdAt");
        Post p = new Post(
                extStr(postData, T.id),
                extInt(postData, "status"),
                extStr(postData, "idFeeling"),
                extStr(postData, "content"),
                extInt(postData, "countComment", 0),
                buildTopReactions((List<?>) postData.getOrDefault("countReact", null)),
                UCollections.extLong(postData, "createdAt"),
                UserBuilder.fromFoldMap(userData),
                extInt(postData, "privacy", 1),
                extInt(postData, "type", 0),
                updated,
                Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR)))
        );

        final var commonInputFn = InputFn.of(p, Map.of("requestedUserId", query.getEntityId(), "identityId", query.getIdentityId()));
        final var builder = new EnhancedPostBuilder();
        List.of(
                new BuildStep<>(0, commonInputFn, builder.getMyReaction, Post.setMyReaction),
                new BuildStep<>(1, commonInputFn, builder.getLocation, Post.setLocation),
                new BuildStep<>(2, commonInputFn, builder.getMedia, Post.setMedia),
                new BuildStep<>(3, commonInputFn, builder.getDestinationUser, Post.setDestinationUser),
                new BuildStep<>(4, commonInputFn, builder.getTaggedUsers, Post.setTaggedUsers),
                new BuildStep<>(5, commonInputFn, builder.getHashtags, Post.setHashtags)

        )
                .parallelStream()
                .forEach(s -> s.getConsumer().accept(s.getInput().getData(), s.getFn().apply(g, s.getInput())));

        if (p.getMultimedia() != null && !p.getMultimedia().isEmpty()) {//con multimedia
            if (p.getContent().length() > 0) {//con texto
                p.setViewType(1);
            } else {//sin texto
                if (p.getLocation() != null)//con ubicacion
                    p.setViewType(2);
                else//sin ubicacion
                    p.setViewType(3);
            }
        } else {//sin multimedia
            if (p.getContent().length() > 0 && p.getLocation() != null) {//con texto y con ubicacion
                p.setViewType(6);
            } else {
                if (p.getContent().isEmpty() || p.getContent() == null && p.getLocation() != null) { //sin texto y con ubicacion
                    p.setViewType(5);
                } else {
                    if (!p.getContent().isEmpty() && p.getLocation() != null) { //sin texto y con ubicacion
                        p.setViewType(6);
                    } else if (!p.getContent().isEmpty() && p.getLocation() != null) {
                        p.setViewType(4);
                    } else if (!p.getContent().isEmpty()) {
                        p.setViewType(4);
                    }
                }
            }
        }
        System.out.println(p);
        return p;

    }

    public static Post build(final Map<String, Object> dataFold, final GraphTraversalSource g,
                             final SearchQuery query) {
        final Map<?, ?> postData = (Map<?, ?>) dataFold.get("post");
        final Map<?, ?> userData = (Map<?, ?>) dataFold.get("author");

        System.out.println("--->" + extStr(postData, T.id));
        Post p = new Post(
                extStr(postData, T.id),
                extInt(postData, "status"),
                extStr(postData, "idFeeling"),
                extStr(postData, "content"),
                extInt(postData, "countComment", 0),
                buildTopReactions((List<?>) postData.getOrDefault("countReact", null)),
                UCollections.extLong(postData, "createdAt"),
                UserBuilder.fromFoldMap(userData),
                extInt(postData, "privacy", 1),
                extInt(postData, "type", 0),
                UCollections.extLong(postData, "updatedAt", 0L),
                0
        );
        final var commonInputFn = InputFn.of(p, Map.of("requestedUserId", query.getEntityId(), "identityId", query.getIdentityId()));
        final var builder = new EnhancedPostBuilder();
        List.of(
                new BuildStep<>(0, commonInputFn, builder.getMyReaction, Post.setMyReaction),
                new BuildStep<>(1, commonInputFn, builder.getLocation, Post.setLocation),
                new BuildStep<>(2, commonInputFn, builder.getMedia, Post.setMedia),
                new BuildStep<>(3, commonInputFn, builder.getDestinationUser, Post.setDestinationUser),
                new BuildStep<>(4, commonInputFn, builder.getTaggedUsers, Post.setTaggedUsers),
                new BuildStep<>(5, commonInputFn, builder.getHashtags, Post.setHashtags)
        )
                .parallelStream()
                .forEach(s -> s.getConsumer().accept(s.getInput().getData(), s.getFn().apply(g, s.getInput())));


        return p;

    }


}
