package com.thomsonreuters.uscl.ereader.gather.service;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.URISyntaxException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TocValidationServiceTest.Config.class})
@ActiveProfiles("IntegrationTests")
public class TocValidationServiceTest {
    private static final String RESOURCE_DIR_NAME = "resourceTocValidation";
    private static final String TOC_FILE_NAME = "toc!uscl-an-book_nvtest_004.xml";
    private static final String ERROR_MESSAGE = "Table of Contents has 4 items with empty name.";
    private static final String ERROR_TOC_ROOT_1 = "[toc root] -> [missing name]";
    private static final String ERROR_TOC_ROOT_2 = "[toc root] -> Chapter 2 -> Chapter 2.1 -> [missing name]";
    private static final String ERROR_TOC_ROOT_3 = "[toc root] -> Chapter 3 -> [missing name]";
    private static final String ERROR_TOC_ROOT_4 = "[toc root] -> Chapter 3 -> [missing name] -> Chapter 3.2.1 -> [missing name]";

    @Autowired
    private TocValidationService step;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void missingTocNameTest() throws URISyntaxException {
        thrown.expect(EBookException.class);
        thrown.expectMessage(ERROR_MESSAGE);
        thrown.expectMessage(ERROR_TOC_ROOT_1);
        thrown.expectMessage(ERROR_TOC_ROOT_2);
        thrown.expectMessage(ERROR_TOC_ROOT_3);
        thrown.expectMessage(ERROR_TOC_ROOT_4);

        step.validateToc(getTocFile("missingTocName"));
    }

    @Test
    public void validationSuccessTest() throws URISyntaxException {
        step.validateToc(getTocFile("validationSuccess"));
    }

    private File getTocFile(final String test) throws URISyntaxException {
        File resourceRootDir = new File(step.getClass().getResource(RESOURCE_DIR_NAME).toURI());
        return resourceRootDir.toPath().resolve(test).resolve(TOC_FILE_NAME).toFile();
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Autowired
        private JsoupService jsoupService;

        @Bean
        public TocValidationService tocValidationService() {
            return new TocValidationService(jsoupService);
        }
    }
}
