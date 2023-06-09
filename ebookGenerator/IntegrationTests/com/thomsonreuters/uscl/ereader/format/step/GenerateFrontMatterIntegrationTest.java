package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.service.PdfToImgConverter;
import com.thomsonreuters.uscl.ereader.frontmatter.service.BaseFrontMatterService;
import com.thomsonreuters.uscl.ereader.frontmatter.service.BaseFrontMatterServiceImpl;
import com.thomsonreuters.uscl.ereader.frontmatter.service.CreateFrontMatterService;
import com.thomsonreuters.uscl.ereader.frontmatter.service.CreateFrontMatterServiceImpl;
import com.thomsonreuters.uscl.ereader.frontmatter.service.PdfImagesService;
import com.thomsonreuters.uscl.ereader.frontmatter.service.PdfImagesServiceImpl;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.jetbrains.annotations.NotNull;
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
import java.util.Arrays;
import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {GenerateFrontMatterIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class GenerateFrontMatterIntegrationTest {
    private static final String RESOURCE_DIR_NAME = "resourceGenerateFrontMatter";
    private static final String USCL_BOOK_TEST = "usclBookTest";
    private static final String CW_BOOK_TEST = "cwBookTest";
    private static final String USCL_BOOK = "uscl/an/test";
    private static final String CW_BOOK = "cw/eg/test";
    private static final String FRONT_MATTER_THEME = "WestLaw Next";
    private static final String ISBN = "0-459-27693-X";
    private static final String ISSN = "2049-3630";
    private static final String MAIN_TITLE = "Main title";
    private static final String SUB_TITLE = "Sub title";
    private static final String SERIES = "Series";

    @Autowired
    private GenerateFrontMatterHTMLPages step;
    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step, RESOURCE_DIR_NAME);

        final BookDefinition bookDefinition = step.getBookDefinition();
        bookDefinition.setEbookNames(getBookNames());
        bookDefinition.setFrontMatterTheme(FRONT_MATTER_THEME);
        bookDefinition.setIsbn(ISBN);
        bookDefinition.setIssn(ISSN);
    }

    private Collection<EbookName> getBookNames() {
        EbookName mainTitle = getBookName(MAIN_TITLE, 1);
        EbookName subTitle = getBookName(SUB_TITLE, 2);
        EbookName series = getBookName(SERIES, 3);
        return Arrays.asList(mainTitle, subTitle, series);
    }

    @NotNull
    private EbookName getBookName(final String name, final int num) {
        EbookName ebookName = new EbookName();
        ebookName.setBookNameText(name);
        ebookName.setSequenceNum(num);
        return ebookName;
    }

    @Test
    public void shouldGenerateFrontMatterPagesForUsclBook() throws Exception {
        final BookDefinition bookDefinition = step.getBookDefinition();
        bookDefinition.setFullyQualifiedTitleId(USCL_BOOK);

        runner.testWithExpectedOnly(step, USCL_BOOK_TEST);
    }

//    @Test
//    public void shouldGenerateFrontMatterPagesForCwBook() throws Exception {
//        final BookDefinition bookDefinition = step.getBookDefinition();
//        bookDefinition.setFullyQualifiedTitleId(CW_BOOK);
//        bookDefinition.setTitlePageImageIncluded(true);
//
//        runner.testWithExpectedOnly(step, CW_BOOK_TEST);
//    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Autowired
        private PublishingStatsService publishingStatsService;
        @Autowired
        private GatherFileSystem gatherFileSystem;
        @Autowired
        private FormatFileSystem formatFileSystem;
        @Autowired
        private NasFileSystem nasFileSystem;
        @Bean
        public GenerateFrontMatterHTMLPages generateFrontMatterHTMLPages() {
            return new GenerateFrontMatterHTMLPages(createFrontMatterService(), publishingStatsService, pagesAnalyzeService(), gatherFileSystem, formatFileSystem);
        }
        @Bean
        public BaseFrontMatterService baseFrontMatterService() {
            return new BaseFrontMatterServiceImpl();
        }
        @Bean
        public CreateFrontMatterService createFrontMatterService() {
            return new CreateFrontMatterServiceImpl(baseFrontMatterService(), pdfImagesService());
        }
        @Bean
        public PdfToImgConverter pdfToImgConverter() {
            return new PdfToImgConverter();
        }
        @Bean
        public PagesAnalyzeService pagesAnalyzeService() {
            return new PagesAnalyzeService();
        }
        @Bean
        public PdfImagesService pdfImagesService() {
            return new PdfImagesServiceImpl(nasFileSystem, pdfToImgConverter());
        }
    }
}
