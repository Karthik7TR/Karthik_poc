package com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.thomsonreuters.uscl.ereader.common.proview.feature.ProviewFeaturesListBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.proview.feature.ProviewFeaturesListBuilderFactoryImpl;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.service.title.ProviewTitleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("IntegrationTests")
@Import(CommonTestContextConfiguration.class)
public class GenerateTitleMetadataStepIntegrationTestConfiguration {
    private static final String TITLE_ID = "uscl/gen/title_metadata_integration_test";

    @Bean(name = "generateTitleMetadataTask")
    public GenerateTitleMetadataStep generateTitleMetadataTask() {
        return new GenerateTitleMetadataStep();
    }

    @Bean
    public ProviewFeaturesListBuilderFactory proviewFeaturesListBuilderFactory() {
        final ProviewTitleService proviewTitleService = mock(ProviewTitleService.class);
        when(proviewTitleService.getLatestProviewTitleVersion(TITLE_ID)).thenReturn(new Version("v1.0"));
        return new ProviewFeaturesListBuilderFactoryImpl(null, proviewTitleService);
    }
}
