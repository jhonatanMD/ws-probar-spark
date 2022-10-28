package redsocial.multimedia.infraestructure.api;


import com.superapp.redsocial.core.infraestructure.jackson.JsonTransformer;
import com.superapp.redsocial.core.infraestructure.spark.response.RsSpark;
import com.superapp.redsocial.core.shared.domain.entities.TransactionRequest;
import com.superapp.redsocial.core.shared.utils.UGeneric;
import redsocial.multimedia.aplication.create.GetMultimedia;
import redsocial.multimedia.domain.entities.PayloadMultimedia;
import redsocial.multimedia.domain.event.SuccessSearchEvent;
import redsocial.multimedia.infraestructure.repositories.NeptuneRepository;

import static spark.Spark.before;
import static spark.Spark.post;

public class Resources {

    private static GetMultimedia getMultimedia = new GetMultimedia(new NeptuneRepository());

    public static void apiResources() {

        before(((request, response) -> response.type("application/json")));
        post("/crop/video/compartir", (req, res) -> {
            try {
                final TransactionRequest<PayloadMultimedia> request = JsonTransformer.toModel(req.body(), UGeneric.type(TransactionRequest.class, PayloadMultimedia.class));
                var response = getMultimedia.getMultimedia(request.getTransaction());
                return RsSpark.buildSuccess(new SuccessSearchEvent(response), res);
            } catch (Exception ex) {
                return RsSpark.buildError(ex, res);
            }
        }, new JsonTransformer());
    }



}
