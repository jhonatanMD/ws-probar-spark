package com.superapp.redsocial.user.infraestructure.api.resources;


import com.superapp.redsocial.core.infraestructure.jackson.JsonTransformer;
import com.superapp.redsocial.core.infraestructure.neptune.NeptuneId;
import com.superapp.redsocial.core.infraestructure.spark.response.RsSpark;
import com.superapp.redsocial.core.shared.paginator.RangePageIdentifier;
import com.superapp.redsocial.core.shared.utils.RsUtils;
import com.superapp.redsocial.user.application.post_searcher.PostSearcher;
import com.superapp.redsocial.user.domain.constants.SeccionValues;
import com.superapp.redsocial.user.domain.storages.ReactionStorage;
import com.superapp.redsocial.user.infraestructure.repositories.DynamoCatalogRepository;
import com.superapp.redsocial.user.infraestructure.repositories.NeptuneRepository;

import static com.superapp.redsocial.core.infraestructure.spark.validators.SpParameterValidator.*;
import static spark.Spark.get;
import static spark.Spark.path;

public class Resources {

    public final static ReactionStorage reactionStorage = new ReactionStorage(new DynamoCatalogRepository());

    public static void downloadReactions() {
        reactionStorage.downloadReactions();
    }

    public static void apiResources() {

        final var psRepository = new NeptuneRepository();
        final var psSearcher = new PostSearcher(psRepository);

        path("/perfiles", () -> get("/publicaciones/iniciales", (req, res) -> {
            try {
                validateQueryParams(req.queryParams(), "idUsuario");
                int ocultas= SeccionValues.MURO.V();

                if(strOrNullQueryParam(req, "ocultas")!=null){
                    ocultas= Integer.parseInt(strOrNullQueryParam(req, "ocultas"));
                }

                return RsSpark.buildSuccess(psSearcher.searchAll(
                        NeptuneId.of(strQueryParam(req, "idUsuario"), "id usuario"),
                        new RangePageIdentifier(strOrNullQueryParam(req, "sigPagina"), RsUtils.getPageSize()), req.headers("x-sicu"),ocultas), res);
            } catch (Exception ex) {
                return RsSpark.buildError(ex, res);
            }
        }, new JsonTransformer()));
    }


}
