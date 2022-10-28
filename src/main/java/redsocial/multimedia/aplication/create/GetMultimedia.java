package redsocial.multimedia.aplication.create;

import com.superapp.core.microservice.utils.SALogger;
import com.superapp.core.neptune.application.redsocial.RsClusterManager;
import lombok.SneakyThrows;
import redsocial.multimedia.dao.NotificationDao;
import redsocial.multimedia.dao.impl.NotificationDaoImpl;
import redsocial.multimedia.domain.entities.PayloadMultimedia;
import redsocial.multimedia.domain.entities.ResponseMultimedia;
import redsocial.multimedia.infraestructure.repositories.NeptuneRepository;

import java.util.List;

public class GetMultimedia {

    private final NeptuneRepository repository;
    private final NotificationDao notificationDao;

    public GetMultimedia(NeptuneRepository repository) {

        this.repository = repository;
        this.notificationDao =  new NotificationDaoImpl();
    }

    @SneakyThrows
    public ResponseMultimedia getMultimedia(PayloadMultimedia multimedia){

        final var g = RsClusterManager.R.writerRsSource();

        try{
            SALogger.info(this.getClass().getName(),"getMultimedia",multimedia.getMultimediaId());
            ResponseMultimedia response = this.repository.make(multimedia,g);

            if(response.getUrlWm().trim().isEmpty()) {
                List<String> connections = notificationDao.getConnection(multimedia.getMultimediaId());
                if (!connections.isEmpty()) {
                    connections.add(multimedia.getConnection());
                    notificationDao.updateConnection(multimedia.getMultimediaId(), connections);
                } else {
                    notificationDao.addConnection(multimedia.getConnection(), multimedia.getMultimediaId());
                }
            }
            return response;

        }finally {
            RsClusterManager.R.closeLastReadWriteClient();

        }

    }


}
