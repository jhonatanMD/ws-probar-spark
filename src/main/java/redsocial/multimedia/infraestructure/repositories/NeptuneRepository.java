package redsocial.multimedia.infraestructure.repositories;

import com.superapp.core.microservice.utils.SALogger;
import lombok.SneakyThrows;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import redsocial.multimedia.domain.entities.PayloadMultimedia;
import redsocial.multimedia.domain.entities.ResponseMultimedia;
import redsocial.multimedia.domain.repositories.MultimediaRepository;


public class NeptuneRepository implements MultimediaRepository {

    @SneakyThrows
    @Override
    public ResponseMultimedia make(PayloadMultimedia multimedia, GraphTraversalSource g) {

            var data =  g.V(multimedia.getMultimediaId())
                    //.hasLabel("urlWm")
                    .elementMap()
                    .next();

            if(data.containsKey("urlWm") && !data.isEmpty())
                return new ResponseMultimedia(data.get("urlWm").toString());

        SALogger.info(this.getClass().getName(),"make",multimedia.getMultimediaId());

        return new ResponseMultimedia();
    }
}
