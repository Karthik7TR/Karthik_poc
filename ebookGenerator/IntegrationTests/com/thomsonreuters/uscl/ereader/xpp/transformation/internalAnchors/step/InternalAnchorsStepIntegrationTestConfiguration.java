package com.thomsonreuters.uscl.ereader.xpp.transformation.internalAnchors.step;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("IntegrationTests")
@Import(CommonTestContextConfiguration.class)
public class InternalAnchorsStepIntegrationTestConfiguration
{
    @Bean(name = "internalAnchorsTask")
    public InternalAnchorsStep internalAnchorsTask()
    {
        return new InternalAnchorsStep();
    }
}
