package redsocial.multimedia.domain.repositories;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import redsocial.multimedia.domain.entities.PayloadMultimedia;
import redsocial.multimedia.domain.entities.ResponseMultimedia;

public interface MultimediaRepository {


    ResponseMultimedia make(PayloadMultimedia multimedia, final GraphTraversalSource g);
}
