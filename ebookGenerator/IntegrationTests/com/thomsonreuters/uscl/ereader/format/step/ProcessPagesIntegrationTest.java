package com.thomsonreuters.uscl.ereader.format.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DOCS_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DOCS_METADATA_DIR;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter;
import com.thomsonreuters.uscl.ereader.format.service.CiteQueryService;
import com.thomsonreuters.uscl.ereader.format.service.InternalLinkResolverService;
import com.thomsonreuters.uscl.ereader.format.service.LinksResolverService;
import com.thomsonreuters.uscl.ereader.format.service.ReorderFootnotesService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.PaceMetadataService;
import com.thomsonreuters.uscl.ereader.gather.parsinghandler.DocMetaDataXMLParser;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ProcessPagesIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public final class ProcessPagesIntegrationTest {
    private static final String STD_PUB_NAME = "GAST";
    private static final String PUBLICATION_NAME = "GA ST";
    private static final String TITLE_ID = "titleId";
    private static final long JOB_INSTANCE_ID = 1L;
    private static final String COLLECTION_NAME = "collectionName";
    private static final String SOURCE = "source";
    @Autowired
    private ProcessPages step;
    @Autowired
    private StepIntegrationTestRunner runner;
    @Autowired
    private DocMetadataService docMetadataService;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step, "resourceProcessPages");
    }

    @Test
    public void shouldTransformPages() throws Exception {
        when(step.getJobExecutionContext().get(JobExecutionKey.WITH_PAGE_NUMBERS)).thenReturn(Boolean.TRUE);
        runner.test(step, "transformTest");
    }

    @Test
    public void shouldTransformPagesWithSectionbreaks() throws Exception {
        final String resourceTestDir = "transformWithSectionbreaksTest";
        when(step.getJobExecutionContext().get(JobExecutionKey.WITH_PAGE_NUMBERS)).thenReturn(Boolean.TRUE);
        setUpDocMetadata(resourceTestDir);
        runner.test(step, resourceTestDir);
    }

    @Test
    public void shouldCopyPages() throws Exception {
        runner.test(step, "copyTest");
    }

    private void setUpDocMetadata(final String resourceTestDir) {
        Set<DocMetadata> docsMetadata = parseDocsMetadata(resourceTestDir);
        DocumentMetadataAuthority documentMetadataAuthority = new DocumentMetadataAuthority(docsMetadata);
        when(docMetadataService.findAllDocMetadataForTitleByJobId(any())).thenReturn(documentMetadataAuthority);
    }

    private Set<DocMetadata> parseDocsMetadata(final String resourceTestDir) {
        return Stream.of(Objects.requireNonNull(getMetadataDir(resourceTestDir).listFiles()))
                .map(this::parseMetadataFile)
                .collect(Collectors.toSet());
    }

    private DocMetadata parseMetadataFile(final File metadataFile) {
        final DocMetaDataXMLParser xmlParser = DocMetaDataXMLParser.create();
        try {
            return xmlParser.parseDocument(TITLE_ID, JOB_INSTANCE_ID, COLLECTION_NAME, metadataFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private File getMetadataDir(final String resourceTestDir) {
        return runner.getTestDir(resourceTestDir).toPath()
                .resolve(SOURCE)
                .resolve(GATHER_DIR.getName())
                .resolve(GATHER_DOCS_DIR.getName())
                .resolve(GATHER_DOCS_METADATA_DIR.getName()).toFile();
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Value("${mud.domain}")
        private String mudDomain;

        @Value("${mud.parameter.rs}")
        private String mudParameterRs;

        @Value("${mud.parameter.vr}")
        private String mudParameterVr;

        @Bean
        public ProcessPages processPages() {
            return new ProcessPages();
        }

        @Bean
        public ReorderFootnotesService reorderFootnotesService() {
            return new ReorderFootnotesService();
        }

        @Bean
        public CiteQueryService CiteQueryService() {
            return new CiteQueryService();
        }

        @Bean
        @SneakyThrows
        public CiteQueryAdapter citeQueryAdapter() {
            final CiteQueryAdapter adapter = new CiteQueryAdapter();

            adapter.setHostname(mudDomain);
            adapter.setRs(mudParameterRs);
            adapter.setVr(mudParameterVr);

            return adapter;
        }

        @Bean
        public LinksResolverService linksResolverService() {
            return new LinksResolverService();
        }

        @Bean
        public InternalLinkResolverService internalLinkResolverService() {
            return new InternalLinkResolverService();
        }

        @Bean
        public PaceMetadataService paceMetadataService() {
            PaceMetadataService paceMetadataService = Mockito.mock(PaceMetadataService.class);

            PaceMetadata paceMetadata = new PaceMetadata();
            paceMetadata.setStdPubName(STD_PUB_NAME);
            paceMetadata.setPublicationName(PUBLICATION_NAME);

            when(paceMetadataService.findAllPaceMetadataForPubCode(any())).thenReturn(Collections.singletonList(paceMetadata));
            return paceMetadataService;
        }

        @Bean
        public DocMetadataService docMetadataService() {
            return mock(DocMetadataService.class);
        }
    }
}
