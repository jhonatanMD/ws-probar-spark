import com.fasterxml.jackson.databind.SerializationFeature;
import com.superapp.core.neptune.application.redsocial.RsClusterManager;
import com.superapp.redsocial.core.configs.RsCore;
import com.superapp.redsocial.core.configs.RsFeatures;
import com.superapp.redsocial.core.infraestructure.jackson.JsonTransformer;
import com.superapp.redsocial.core.infraestructure.jackson.SerializationMapperConfig;
import com.superapp.redsocial.core.infraestructure.neptune.SSMCredentialsManager;
import com.superapp.redsocial.core.shared.domain.constants.EnvKeys;
import com.superapp.redsocial.core.shared.utils.Env;
import redsocial.multimedia.infraestructure.api.Resources;

public class MainTest {

    public static void main(String[] args) throws Exception {
        RsCore.rsFeature(RsFeatures.REQUEST_TRACKING, true);
        RsCore.rsFeature(RsFeatures.FILTER_REQUEST_TRACKING, false);
        new JsonTransformer().configureSerializerMapper(
                new SerializationMapperConfig[]{
                        new SerializationMapperConfig(
                                SerializationFeature.FAIL_ON_EMPTY_BEANS,
                                false
                        )
                }
        );
        Resources.apiResources();
        Env.var(EnvKeys.RS_NEPTUNE_CONFIG.V()).ifPresent(v ->
                Env.var(EnvKeys.SEARCH_NEPTUNE_CONFIG.V())
                        .ifPresent(s -> SSMCredentialsManager.download(v, s)));

        System.out.println("credenciales : " + SSMCredentialsManager.get());
        RsClusterManager.INSTANCE.setupDownloader(SSMCredentialsManager.downloader);
        RsClusterManager.INSTANCE.setupConfigurationData(RsClusterManager
                .ClusterManagerConfigData
                .ofRawMap(SSMCredentialsManager.get()));
        RsClusterManager.INSTANCE.initAll();
        RsCore.executeFeature(RsFeatures.HEADER_VALIDATOR);
        RsCore.executeFeature(RsFeatures.FILTER_REQUEST_TRACKING);

    }
}
