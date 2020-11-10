package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.assemble.service.PlaceholderDocumentService;
import com.thomsonreuters.uscl.ereader.assemble.service.PlaceholderDocumentServiceImpl;
import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataService;
import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataServiceImpl;
import com.thomsonreuters.uscl.ereader.common.proview.feature.ProviewFeaturesListBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.proview.feature.ProviewFeaturesListBuilderFactoryImpl;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.util.VersionUtilImpl;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.title.ProviewTitleService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacadeImpl;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import lombok.SneakyThrows;
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

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {GenerateTitleMetadataIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class GenerateTitleMetadataIntegrationTest {
    private static final String USCL_BOOK = "uscl/an/test";
    private static final String CW_BOOK = "cw/eg/test";
    private static final String ISBN = "0-459-27693-X";
    private static final String DISPLAY_NAME = "Book Name";
    private static final String VERSION = "1.0";
    private static final String DOC_GUID = "docGuid";
    private static final String PROVIEW_ID = "proviewId";
    private static final String COVER_IMAGE = "coverArt.png";

    @Autowired
    private GenerateTitleMetadata step;
    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step, "resourceGenerateTitleMetadata");

        when(step.getJobParameters().getString(JobParameterKey.BOOK_VERSION_SUBMITTED)).thenReturn(VERSION);
        final BookDefinition bookDefinition = step.getBookDefinition();
        bookDefinition.setProviewDisplayName(DISPLAY_NAME);
        bookDefinition.setIsbn(ISBN);
        bookDefinition.setCoverImage(COVER_IMAGE);
    }

    @Test
    public void shouldGenerateTitleMetadataForUsclBook() throws Exception {
        final BookDefinition bookDefinition = step.getBookDefinition();
        bookDefinition.setFullyQualifiedTitleId(USCL_BOOK);

        runner.test(step, "usclBookTest");
    }

    @Test
    public void shouldGenerateTitleMetadataForCwBook() throws Exception {
        final BookDefinition bookDefinition = step.getBookDefinition();
        bookDefinition.setFullyQualifiedTitleId(CW_BOOK);

        runner.test(step, "cwBookTest");
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public GenerateTitleMetadata generateTitleMetadata() {
            return new GenerateTitleMetadata();
        }
        @Bean
        public TitleMetadataService titleMetadataService() {
            return new TitleMetadataServiceImpl();
        }
        @Bean
        public BookDefinitionService bookDefinitionService() {
            BookDefinitionService bookDefinitionService = mock(BookDefinitionService.class);
            when(bookDefinitionService.getSplitPartsForEbook(any())).thenReturn(2);
            return bookDefinitionService;
        }
        @Bean
        public ProviewFeaturesListBuilderFactory proviewFeaturesListBuilderFactory() {
            ProviewTitleService proviewTitleService = mock(ProviewTitleService.class);
            when(proviewTitleService.getLatestProviewTitleVersion(any())).thenReturn(new Version(VERSION));
            when(proviewTitleService.getPreviousTitles(any(), any())).thenReturn(Collections.emptyList());
            when(proviewTitleService.isMajorVersionPromotedToFinal(any(), any())).thenReturn(false);
            return new ProviewFeaturesListBuilderFactoryImpl(new VersionUtilImpl(), proviewTitleService);
        }
        @Bean
        public DocMetadataService docMetadataService() {
            DocMetadataService docMetadataService = mock(DocMetadataService.class);
            Map<String, String> familyGuidMap = new HashMap<>();
            familyGuidMap.put(DOC_GUID, PROVIEW_ID);
            when(docMetadataService.findDistinctProViewFamGuidsByJobId(any())).thenReturn(familyGuidMap);
            return docMetadataService;
        }
        @Bean
        public PlaceholderDocumentService placeholderDocumentService() {
            return new PlaceholderDocumentServiceImpl();
        }
        @Bean
        public FileUtilsFacade fileUtilsFacade() {
            return new FileUtilsFacadeImpl();
        }
        @Bean
        public UuidGenerator uuidGenerator() {
            return new UuidGenerator();
        }
        @Bean
        public ImageService imageService() {
            ImageService imageService = mock(ImageService.class);
            when(imageService.getDocImageListMap(any())).thenReturn(Collections.emptyMap());
            return imageService;
        }
        @Bean
        @SneakyThrows
        public ProviewHandler proviewHandler() {
            ProviewHandler proviewHandler = mock(ProviewHandler.class);
            ProviewTitleInfo titleInfo = new ProviewTitleInfo();
            titleInfo.setTitleIdCaseSensitive(CW_BOOK);
            when(proviewHandler.getTitleIdCaseSensitiveForVersion(any(), any())).thenReturn(CW_BOOK);
            return proviewHandler;
        }
    }
}
