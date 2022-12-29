package com.superapp.redsocial.notification.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.superapp.redsocial.notification.domain.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@DynamoDbBean
public class Notificacion {

    @SerializedName(value = "id")
    @JsonProperty("id")
    private UUID id;

    @SerializedName(value = "idPublicacion")
    @JsonProperty("idPublicacion")
    private String idPublicacion;

    @SerializedName(value = "idRelacion")
    @JsonProperty("idRelacion")
    private String idRelacion;

    @SerializedName(value = "mensaje")
    @JsonProperty("mensaje")
    private String mensaje;

    @SerializedName(value = "imagenReaccion")
    @JsonProperty("imagenReaccion")
    private String imagenReaccion;

    @SerializedName(value = "tipo")
    @JsonProperty("tipo")
    private Integer tipo;

    @SerializedName(value = "subTipo")
    @JsonProperty("subTipo")
    private Integer subTipo;

    @SerializedName(value = "estatus")
    @JsonProperty("estatus")
    private Integer estatus;

    @SerializedName(value = "fechaCreacion")
    @JsonProperty("fechaCreacion")
    private Long fechaCreacion;

    @SerializedName(value = "contador")
    @JsonProperty("contador")
    private Integer contador;

    @SerializedName(value = "usuario")
    @JsonProperty("usuario")
    private Usuario usuario;

    @SerializedName(value = "usuarios")
    @JsonProperty("usuarios")
    private List<String> usuarios;




}
