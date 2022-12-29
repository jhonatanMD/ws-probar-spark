package com.superapp.redsocial.notification.infraestructure.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import com.superapp.core.neptune.application.redsocial.RsClusterManager;
import com.superapp.redsocial.core.infraestructure.neptune.SSMCredentialsManager;
import com.superapp.redsocial.core.shared.domain.constants.EnvKeys;
import com.superapp.redsocial.core.shared.logger.RSLogger;
import com.superapp.redsocial.core.shared.utils.Env;
import com.superapp.redsocial.notification.application.notification_friendrequest.NotificationFriend;
import com.superapp.redsocial.notification.domain.events.RequestData;
import com.superapp.redsocial.notification.domain.utils.GsonParserUtils;
import com.superapp.redsocial.notification.infraestructure.repositories.NotificationDynamoRepositoryImpl;
import com.superapp.redsocial.notification.infraestructure.repositories.NotificationNeptuneRepositoryImpl;


import java.util.List;

public class Handler extends SQSHandler {


    NotificationFriend notificationFriend = new NotificationFriend(
            new NotificationNeptuneRepositoryImpl(), new NotificationDynamoRepositoryImpl()
    );

    protected void init() {

    }

    @Override
    protected void preHandler() {
    }

    @Override
    protected void handleRequest(List<SQSEvent.SQSMessage> request, Context context) {
        try {
            Env.var(EnvKeys.RS_NEPTUNE_CONFIG.V()).ifPresent(v ->
                    Env.var(EnvKeys.SEARCH_NEPTUNE_CONFIG.V())
                            .ifPresent(s -> SSMCredentialsManager.download(v, s)));

            RsClusterManager
                    .INSTANCE
                    .setupDownloader(SSMCredentialsManager.downloader);
            RsClusterManager.INSTANCE.setupConfigurationData(RsClusterManager
                    .ClusterManagerConfigData
                    .ofRawMap(SSMCredentialsManager.get()));
            RsClusterManager.INSTANCE.initReadRsCluster();
            request.forEach(m -> {

                try {
                    notificationFriend.Notification(GsonParserUtils.getGson().fromJson(m.getBody(), RequestData.class));
                } catch (Exception e) {
                    RSLogger.exception(e);
                }

            });
        } catch (Exception e) {
            RSLogger.exception(e);
        }

    }

    @Override
    protected void postHandler() {

    }
}
