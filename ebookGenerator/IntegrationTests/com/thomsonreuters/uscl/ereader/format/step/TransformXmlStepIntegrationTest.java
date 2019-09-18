package com.thomsonreuters.uscl.ereader.format.step;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.GenerateDocumentDataBlockService;
import com.thomsonreuters.uscl.ereader.format.service.TransformerService;
import com.thomsonreuters.uscl.ereader.format.service.TransformerServiceImpl;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TransformXmlStepIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public final class TransformXmlStepIntegrationTest {
    private static final String COLLECTION_NAME = "w_codesstatxnvdp";
    private static final String DOC_TYPE = "6A";
    private static final String staticContentDir = "/apps/ebookbuilder/staticContent/";

    @Autowired
    private TransformXML step;

    @Autowired
    private StepIntegrationTestRunner runner;

    private File resourceDir;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step);
        resourceDir = new File(TransformXmlStepIntegrationTest.class.getResource("resourceTransformXml").toURI());

        final ExecutionContext context = step.getJobExecutionContext();

        when(context.getString(JobExecutionKey.STATIC_CONTENT_DIR)).thenReturn(staticContentDir);
        when(context.containsKey(JobExecutionKey.STATIC_CONTENT_DIR)).thenReturn(Boolean.TRUE);

        when(context.getInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT)).thenReturn(1);
        when(context.containsKey(JobExecutionKey.EBOOK_STATS_DOC_COUNT)).thenReturn(Boolean.TRUE);
    }

    @Test
    public void shouldTransform() throws Exception {
        final BookDefinition bookDefinition = step.getBookDefinition();
        bookDefinition.setFullyQualifiedTitleId("uscl/an/octxcoa");
        bookDefinition.setIncludeAnnotations(true);
        bookDefinition.setIncludeNotesOfDecisions(false);

        runner.test(step, new File(resourceDir, "annotationsTest"));
    }

    public static void whenJobExecutionPropertyString(final ExecutionContext jobExecutionContext, final String name, final String value) {
        when(jobExecutionContext.getString(name)).thenReturn(value);
        when(jobExecutionContext.containsKey(name)).thenReturn(Boolean.TRUE);
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public TransformXML transformXML() {
            return new TransformXML();
        }

        @Bean
        public TransformerService transformerService() throws EBookFormatException {
            final TransformerServiceImpl service = new TransformerServiceImpl();
            service.setdocMetadataService(getDocMetadataService());
            service.setGenerateDocumentDataBlockService(getGenerateDocumentDataBlockService());
            service.setfileHandlingHelper(getFileHandlingHelper());
            return service;
        }

        @Bean
        public PublishingStatsService publishingStatsService() {
            return Mockito.mock(PublishingStatsService.class);
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
