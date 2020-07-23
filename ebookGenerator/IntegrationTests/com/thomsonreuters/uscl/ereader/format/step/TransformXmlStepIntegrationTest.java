package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.filesystem.TestNasFileSystemImpl;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter;
import com.thomsonreuters.uscl.ereader.format.service.GenerateDocumentDataBlockService;
import com.thomsonreuters.uscl.ereader.format.service.TransformerService;
import com.thomsonreuters.uscl.ereader.format.service.TransformerServiceImpl;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.whenJobExecutionPropertyInt;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TransformXmlStepIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public final class TransformXmlStepIntegrationTest {
    private static final String COLLECTION_NAME = "w_codesstatxnvdp";
    private static final String DOC_TYPE = "6A";
    private static final int DOCS_NUMBER = 1;
    private static final String FULLY_QUALIFIED_TITLE_ID = "uscl/an/octxcoa";

    @Autowired
    private TransformXML step;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step, "resourceTransformXml");
        whenJobExecutionPropertyInt(step.getJobExecutionContext(), JobExecutionKey.EBOOK_STATS_DOC_COUNT, DOCS_NUMBER);
    }

    @Test
    public void shouldTransform() throws Exception {
        final BookDefinition bookDefinition = step.getBookDefinition();
        bookDefinition.setFullyQualifiedTitleId(FULLY_QUALIFIED_TITLE_ID);
        bookDefinition.setIncludeAnnotations(true);
        bookDefinition.setIncludeNotesOfDecisions(false);

        runner.test(step, "annotationsTest");
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

        @Value("${static.content.dir}")
        private File staticContentDirectory;

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
        public TransformXML transformXML() {
            return new TransformXML();
        }

        @Bean
        public NasFileSystem nasFileSystem() {
            NasFileSystem nasFileSystem = mock(TestNasFileSystemImpl.class);
            when(nasFileSystem.getStaticContentDirectory()).thenReturn(staticContentDirectory);
            return nasFileSystem;
        }

        @Bean
        public TransformerService transformerService() throws EBookFormatException {
            final TransformerServiceImpl service = new TransformerServiceImpl();
            service.setdocMetadataService(getDocMetadataService());
            service.setGenerateDocumentDataBlockService(getGenerateDocumentDataBlockService());
            service.setfileHandlingHelper(getFileHandlingHelper());
            return service;
        }

        private DocMetadataService getDocMetadataService() {
            final DocMetadataService docMetadataService = Mockito.mock(DocMetadataService.class);
            when(docMetadataService.findDocMetadataByPrimaryKey(any(), any(), any())).thenReturn(getDocMetadata());
            return docMetadataService;
        }

        private GenerateDocumentDataBlockService getGenerateDocumentDataBlockService() throws EBookFormatException {
            final GenerateDocumentDataBlockService generateDocumentDataBlockService = Mockito.mock(GenerateDocumentDataBlockService.class);
            when(generateDocumentDataBlockService.getDocumentDataBlockAsStream(any(), any(), any())).thenReturn(getBlockAsStream());
            return generateDocumentDataBlockService;
        }

        private FileHandlingHelper getFileHandlingHelper() {
            final FileHandlingHelper fileHandlingHelper = new FileHandlingHelper();
            final FileExtensionFilter fileExtensionFilter = new FileExtensionFilter();
            fileExtensionFilter.setAcceptedFileExtensions(new String[] {".preprocess"});
            fileHandlingHelper.setFilter(fileExtensionFilter);
            return fileHandlingHelper;
        }

        private DocMetadata getDocMetadata() {
            final DocMetadata docMetadata = new DocMetadata();
            docMetadata.setCollectionName(COLLECTION_NAME);
            docMetadata.setDocType(DOC_TYPE);
            return docMetadata;
        }

        private InputStream getBlockAsStream() {
            return new ByteArrayInputStream("<document-data><collection>collection</collection><datetime>19700101000000</datetime><versioned>versioned</versioned><doc-type></doc-type><cite></cite></document-data>".getBytes());
        }
    }
}