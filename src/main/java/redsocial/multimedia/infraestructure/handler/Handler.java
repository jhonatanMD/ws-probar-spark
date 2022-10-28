package redsocial.multimedia.infraestructure.handler;


import com.superapp.core.neptune.application.redsocial.RsClusterManager;
import com.superapp.redsocial.core.configs.RsCore;
import com.superapp.redsocial.core.configs.RsFeatures;
import com.superapp.redsocial.core.infraestructure.neptune.SSMCredentialsManager;
import com.superapp.redsocial.core.infraestructure.spark.CoreHandler;
import com.superapp.redsocial.core.shared.domain.constants.EnvKeys;
import com.superapp.redsocial.core.shared.logger.RSLogger;
import com.superapp.redsocial.core.shared.utils.Env;
import redsocial.multimedia.infraestructure.api.Resources;

import javax.servlet.ServletContext;

/**
 * Handler
 * <p>
 * Handler
 *
 * @author Noe Cruz | contactozurckz@gmail.com | linkedin.com/in/zurckz
 * @version 1.0.0
 * @since 09-06-2021
 */
public class Handler extends CoreHandler {

    public Handler() {
        super(Resources::apiResources);
    }

    @Override
    protected void onStartup(ServletContext servletContext) {
        setupCredentials();
        RsCore.rsFeature(RsFeatures.FILTER_REQUEST_TRACKING, false);
    }

    /**
     * Setup credentials and initialize resources
     */
    private static void setupCredentials() {
        try {
            // 🔑 Get and setup neptune credentials into data store manager.
            Env.var(EnvKeys.RS_NEPTUNE_CONFIG.V())
                    .ifPresent(v -> Env.var(EnvKeys.SEARCH_NEPTUNE_CONFIG.V())
                            .ifPresent(s -> SSMCredentialsManager.download(v, s)));

            // 🔄 Setup credentials downloader for retry.
            RsClusterManager.INSTANCE
                    .setupDownloader(SSMCredentialsManager.downloader);

            // 🗝 Setup credentials to Neptune Cluster manager
            RsClusterManager.INSTANCE.setupConfigurationData(RsClusterManager
                    .ClusterManagerConfigData
                    .ofRawMap(SSMCredentialsManager.get()));

            // 🚨 Only initialize Read Node of RedSocial Cluster
            RsClusterManager.INSTANCE.initAll();
        } catch (Exception e) {
            RSLogger.exception(e);
        }
    }
}
