package com.thomsonreuters.uscl.ereader.xpp.transformation.pplinks.step;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("IntegrationTests")
@Import(CommonTestContextConfiguration.class)
public class PocketPartLinksStepIntegrationTestConfiguration {
    @Bean(name = "pocketPartLinksTask")
    public PocketPartLinksStep pocketPartLinksTask() {
        return new PocketPartLinksStep();
    }
}
