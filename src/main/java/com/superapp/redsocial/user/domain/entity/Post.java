package com.superapp.redsocial.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.BiConsumer;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    public Post(
            String id,
            int status,
            String feelingId,
            String content,
            int countComments,
            PostReactionData postReactionData,
            long createdAt,
            User user,
            int privacy,
            int type,
            long updatedAt,
            int year) {
        this.id = id;
        this.statusId = status;
        this.feelingId = feelingId;
        this.content = content;
        this.countComments = countComments;
        this.reactionData = postReactionData;
        this.createdAt = createdAt;
        this.author = user;
        this.privacy=privacy;
        this.type = type;
        this.updatedAt=updatedAt;
        this.year=year;
    }

    @JsonProperty("id")
    private String id;

    @JsonProperty("idEstatus")
    private int statusId;

    @JsonProperty("idReaccion")
    private String reactionId;

    @JsonProperty("idSentimiento")
    private String feelingId;

    @JsonProperty("contenido")
    private String content;

    @JsonProperty("totalComentarios")
    private int countComments;

    @JsonProperty("reacciones")
    private PostReactionData reactionData;

    @JsonProperty("fechaCreacion")
    private long createdAt;

    @JsonProperty("usuario")
    private User author;

    @JsonProperty("ubicacion")
    private Location location;

    @JsonProperty("multimedia")
    List<Multimedia> multimedia;

    @JsonProperty("usuarioDestino")
    private User destinationUser;

    @JsonProperty("privacidad")
    private int privacy;

    @JsonProperty("tipo")
    private int type;

    @JsonProperty("usuariosEtiquetados")
    private List<TaggedUser> taggedUsers;

    @JsonProperty("hashtags")
    private List<String> hashtags;

    @JsonProperty("fechaEdicion")
    private long updatedAt=0L;

    @JsonProperty("tipoVisualicion")
    private int viewType=0;

    @JsonProperty("fechaAgrupacion")
    private int year;



    public static BiConsumer<Object, Object> setMyReaction = (post, o) -> ((Post) post).setReactionId((String) o);
    public static BiConsumer<Object, Object> setLocation = (post, o) -> ((Post) post).setLocation((Location) o);
    public static BiConsumer<Object, Object> setMedia = (post, o) -> {
        final List<?> media = (List<?>) o;
        ((Post) post).setMultimedia(media != null && !media.isEmpty() ? (List<Multimedia>) media : null);
    };
    public static BiConsumer<Object, Object> setDestinationUser = (post, o) -> ((Post) post).setDestinationUser((User) o);

    public static BiConsumer<Object, Object> setTaggedUsers = (post, o) -> {
        final List<TaggedUser> users = (List<TaggedUser>) o;
        ((Post) post).setTaggedUsers(users != null && !users.isEmpty() ? users : null);
    };

    public static  BiConsumer<Object,Object> setHashtags=(post,o)->{
        final List<String> hashtags=(List<String>) o;
        ((Post)post).setHashtags(hashtags!=null && !hashtags.isEmpty()?hashtags:null);
    };

}
