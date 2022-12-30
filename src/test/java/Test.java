import com.superapp.core.neptune.application.redsocial.RsClusterManager;
import com.superapp.redsocial.core.infraestructure.neptune.SSMCredentialsManager;
import com.superapp.redsocial.core.shared.domain.constants.EnvKeys;
import com.superapp.redsocial.core.shared.utils.Env;
import com.superapp.redsocial.notification.application.notification_friendrequest.NotificationFriend;
import com.superapp.redsocial.notification.domain.events.RequestData;
import com.superapp.redsocial.notification.infraestructure.repositories.NotificationDynamoRepositoryImpl;
import com.superapp.redsocial.notification.infraestructure.repositories.NotificationNeptuneRepositoryImpl;

import java.util.Date;

public class Test {

    public static void main(String[] args) throws Exception {

        Env.var(EnvKeys.RS_NEPTUNE_CONFIG.V()).ifPresent(v ->
                Env.var(EnvKeys.SEARCH_NEPTUNE_CONFIG.V())
                        .ifPresent(s -> SSMCredentialsManager.download(v, s)));
        RsClusterManager
                .INSTANCE
                .setupDownloader(SSMCredentialsManager.downloader);
        RsClusterManager.INSTANCE.setupConfigurationData(RsClusterManager
                .ClusterManagerConfigData
                .ofRawMap(SSMCredentialsManager.get()));
        RsClusterManager.INSTANCE.initAll();

        NotificationFriend notificationComments = new NotificationFriend
                (
                new NotificationNeptuneRepositoryImpl(),
                new NotificationDynamoRepositoryImpl()
        );

        notificationComments.Notification(new RequestData("e4c2ad86-f1bb-ca5e-662f-24f5de02131f",0,new Date().getTime()));
    }
}
