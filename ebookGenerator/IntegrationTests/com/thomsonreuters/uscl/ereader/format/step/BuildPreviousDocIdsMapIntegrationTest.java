package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.format.service.MinorVersionMappingFileSaver;
import com.thomsonreuters.uscl.ereader.format.service.TitleXmlUnifiedConverter;
import com.thomsonreuters.uscl.ereader.format.service.TocXmlUnifiedConverter;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BuildPreviousDocIdsMapIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class BuildPreviousDocIdsMapIntegrationTest {
    private static final String PREVIOUS_VERSION_TITLE_XML = "/previous-version-title.xml";

    @Autowired
    private BuildPreviousDocumentIdsMappingStep step;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Autowired
    private ProviewClient proviewClient;

    @Autowired
    private DocMetadataService docMetadataService;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step, "resourceBuildPrevDocIdsMap");
        when(docMetadataService.findDistinctProViewFamGuidsByJobId(any())).thenReturn(getFamilyGuidMap());
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
        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_PREVIOUS_DOCUMENT_IDS, Boolean.TRUE);
    }

    @Test
    public void shouldBuildMapFile() throws Exception {
        final String testPath = "buildMapFile";
        whenProViewClientGetTitleInfo(testPath);
        runner.test(step, testPath);
        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_PREVIOUS_DOCUMENT_IDS, Boolean.TRUE);
    }

    @Test
    public void shouldNotBuildMapFile() throws Exception {
        final String testPath = "sameDocIds";
        whenProViewClientGetTitleInfo(testPath);
        runner.testWithSourceOnly(step, testPath);
        verify(step.getJobExecutionContext(), never()).put(JobExecutionKey.WITH_PREVIOUS_DOCUMENT_IDS, Boolean.TRUE);
    }

    private Map<String, String> getFamilyGuidMap() {
        return new HashMap<String, String>() {
            @Override
            public String get(Object key) {
                return String.valueOf(key);
            }
        };
    }

    private void whenProViewClientGetTitleInfo(final String testPath) throws ProviewException, IOException {
        when(proviewClient.getTitleInfo(any(), any())).thenReturn(getPreviousTitleXmlContent(testPath));
    }

    private String getPreviousTitleXmlContent(final String testPath) throws IOException {
        return FileUtils.readFileToString(new File(
                runner.getResourceRootDir(), testPath + PREVIOUS_VERSION_TITLE_XML));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public BuildPreviousDocumentIdsMappingStep buildThesaurusStep() {
            return new BuildPreviousDocumentIdsMappingStep();
        }

        @Bean
        public DocMetadataService docMetadataService() {
            return Mockito.mock(DocMetadataService.class);
        }

        @Bean
        public TitleXmlUnifiedConverter titleXmlUnifiedConverter() {
            return new TitleXmlUnifiedConverter();
        }

        @Bean
        public TocXmlUnifiedConverter tocXmlUnifiedConverter() {
            return new TocXmlUnifiedConverter();
        }

        @Bean
        public MinorVersionMappingFileSaver minorVersionMappingFileSaver() {
            return new MinorVersionMappingFileSaver();
        }
    }
}
