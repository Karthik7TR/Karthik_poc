package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.format.service.MinorVersionMappingFileSaver;
import com.thomsonreuters.uscl.ereader.format.service.MinorVersionMappingService;
import com.thomsonreuters.uscl.ereader.format.service.TitleXmlUnifiedConverter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
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
import java.io.IOException;
import java.net.URISyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MinorVersionMappingIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class MinorVersionMappingIntegrationTest {
    private static final String PREVIOUS_VERSION_TITLE_XML = "/previous-version-title.xml";
    private static final String DOCUMENT_GUID = "documentGuid";
    private static final String DOC_ID = "docId";
    private static final String VERSION = "v1.0";
    private static final String FULLY_QUALIFIED_TITLE_ID = "uscl/an/test_book_en";

    @Autowired
    private MinorVersionMappingStep step;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Autowired
    private ProviewClient proviewClient;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step, "resourceMinorVersionMapping");
        step.getBookDefinition().setVersionWithPreviousDocIds(VERSION);
        step.getBookDefinition().setFullyQualifiedTitleId(FULLY_QUALIFIED_TITLE_ID);
    }

    @Test
    public void shouldBuildMapFileArchibald() throws Exception {
        final String testPath = "buildMapFileArchibald";
        whenProViewClientGetTitleInfo(testPath);
        runner.test(step, testPath);
    }

    @Test
    public void shouldBuildMapFileEllis() throws Exception {
        final String testPath = "buildMapFileEllis";
        whenProViewClientGetTitleInfo(testPath);
        runner.test(step, testPath);
    }

    @Test
    public void shouldBuildMapFile() throws Exception {
        final String testPath = "buildMapFile";
        whenProViewClientGetTitleInfo(testPath);
        runner.test(step, testPath);
    }

    @Test
    public void shouldNotBuildMapWithSameDocIds() throws Exception {
        final String testPath = "hasSameDocIds";
        whenProViewClientGetTitleInfo(testPath);
        runner.test(step, testPath);
    }

    @Test
    public void shouldNotAddNodesWithSameDocIds() throws Exception {
        final String testPath = "allSameDocIdsArchibald";
        whenProViewClientGetTitleInfo(testPath);
        runner.test(step, testPath);
    }

    @Test
    public void shouldNotBuildMapFile() throws Exception {
        step.getBookDefinition().setVersionWithPreviousDocIds(null);
        runner.test(step);
    }

    private void whenProViewClientGetTitleInfo(final String testPath) throws ProviewException, IOException {
        when(proviewClient.getTitleInfo(any(), any())).thenReturn(getPreviousTitleXmlContent(testPath));
    }

    private String getPreviousTitleXmlContent(final String testPath) throws IOException {
        return FileUtils.readFileToString(new File(getTestDir(testPath), PREVIOUS_VERSION_TITLE_XML));
    }

    private File getTestDir(final String testPath) {
        return new File(runner.getResourceRootDir(), testPath);
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public MinorVersionMappingStep buildThesaurusStep() {
            return new MinorVersionMappingStep();
        }

        @Bean
        public TitleXmlUnifiedConverter titleXmlUnifiedConverter() {
            return new TitleXmlUnifiedConverter();
        }

        @Bean
        public MinorVersionMappingFileSaver minorVersionMappingFileSaver() {
            return new MinorVersionMappingFileSaver();
        }

        @Bean
        public MinorVersionMappingService minorVersionMappingService() {
            return new MinorVersionMappingService();
        }
    }
}
