package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataService;
import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataServiceImpl;
import com.thomsonreuters.uscl.ereader.assemble.step.CoverArtUtil;
import com.thomsonreuters.uscl.ereader.assemble.step.CreateDirectoriesAndMoveResources;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.service.PdfToImgConverter;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacadeImpl;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
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

import static com.thomsonreuters.uscl.ereader.StepTestUtil.whenJobExecutionPropertyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CreateDirectoriesAndMoveResourcesIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class CreateDirectoriesAndMoveResourcesIntegrationTest {
    private static final String ISBN = "0-459-27693-X";
    private static final String PROVIEW_DISPLAY_NAME = "Book Name";
    private static final String FULLY_QUALIFIED_TITLE_ID = "uscl/an/test";
    private static final String FRONT_MATTER_THEME = "WestLaw Next";
    private static final String COVER_IMAGE = "coverArt.png";
    private static final String BOOK_VERSION = "1.0";
    private static final String DOC_UUID = "docUuid";
    private static final String PROVIEW_ID = "proviewId";
    @Autowired
    private CreateDirectoriesAndMoveResources step;
    @Autowired
    private FormatFileSystem formatFileSystem;
    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws Exception {
        runner.setUp(step, "resourceCreateDirectoriesAndMoveResources");
        final BookDefinition bookDefinition = step.getBookDefinition();
        bookDefinition.setProviewDisplayName(PROVIEW_DISPLAY_NAME);
        bookDefinition.setFullyQualifiedTitleId(FULLY_QUALIFIED_TITLE_ID);
        bookDefinition.setFrontMatterTheme(FRONT_MATTER_THEME);
        bookDefinition.setIsbn(ISBN);
        bookDefinition.setCoverImage(COVER_IMAGE);
        bookDefinition.setSplitEBookParts(2);

        when(step.getJobParameters().getString(JobParameterKey.BOOK_VERSION_SUBMITTED)).thenReturn(BOOK_VERSION);
        whenJobExecutionPropertyString(step.getJobExecutionContext(),
                JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH,
                formatFileSystem.getHtmlWrapperDirectory(step).getAbsolutePath());
    }

    @Test
    public void splitBookTest() throws Exception {
        runner.test(step, "splitBookTest");
    }

    @Test
    public void splitBookTitleImageTest() throws Exception {
        final BookDefinition bookDefinition = step.getBookDefinition();
        bookDefinition.setTitlePageImageIncluded(true);
        runner.test(step, "splitBookTitleImageTest");
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public CreateDirectoriesAndMoveResources createDirectoriesAndMoveResources() {
            return new CreateDirectoriesAndMoveResources();
        }
        @Bean
        public PdfToImgConverter pdfToImgConverter() {
            return new PdfToImgConverter();
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
        public DocMetadataService docMetadataService() {
            DocMetadataService docMetadataService = mock(DocMetadataService.class);
            Map<String, String> familyGuidMap = new HashMap<>();
            familyGuidMap.put(DOC_UUID, PROVIEW_ID);
            when(docMetadataService.findDistinctProViewFamGuidsByJobId(any())).thenReturn(familyGuidMap);
            return docMetadataService;
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
        public CoverArtUtil coverArtUtil() {
            return new CoverArtUtil();
        }
    }
}
