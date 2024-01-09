package com.thomsonreuters.uscl.ereader.xpp.transformation.footnotes.step;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("IntegrationTests")
@Import(CommonTestContextConfiguration.class)
public class CrossPageLinkedFootnotesStepIntegrationTestConfig {
    @Bean(name = "crossPageLinkedFootnotesTask")
    public CrossPageLinkedFootnotesStep crossPageLinkedFootnotesTask() {
        return new CrossPageLinkedFootnotesStep();
    }
}
