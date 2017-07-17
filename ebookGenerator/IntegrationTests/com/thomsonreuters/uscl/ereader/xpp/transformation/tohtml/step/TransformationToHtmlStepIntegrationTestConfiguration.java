package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("IntegrationTests")
@Import(CommonTestContextConfiguration.class)
public class TransformationToHtmlStepIntegrationTestConfiguration
{
    @Bean(name = "transformToHtmlTask")
    public TransformationToHtmlStep transformToHtmlTask()
    {
        return new TransformationToHtmlStep();
    }
}
