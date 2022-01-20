package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.TestNasFileSystemImpl;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.service.DateProvider;
import com.thomsonreuters.uscl.ereader.core.service.DateService;
import com.thomsonreuters.uscl.ereader.core.service.DateServiceImpl;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.frontmatter.service.FrontMatterPreviewService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionFormValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.IsbnValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.IssnValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.PdfFileNameValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleListService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.book.BookDefinitionLockService;
import com.thomsonreuters.uscl.ereader.request.service.PrintComponentHistoryService;
import org.easymock.EasyMock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

@Configuration
public class TestConfig {
    @Bean
    public EditBookDefinitionFormValidator editBookDefinitionFormValidator() {
        return new EditBookDefinitionFormValidator();
    }
    @Bean
    public EditBookDefinitionController editBookDefinitionController() {
        return new EditBookDefinitionController();
    }
    @Bean
    public IsbnValidator isbnValidator() {
        return new IsbnValidator();
    }
    @Bean
    public IssnValidator issnValidator() {
        return new IssnValidator();
    }
    @Bean
    public PdfFileNameValidator pdfFileNameValidator() {
        return new PdfFileNameValidator();
    }
    @Bean
    public BookDefinitionService bookDefinitionService() {
        return EasyMock.createMock(BookDefinitionService.class);
    }
    @Bean
    public KeywordTypeCodeSevice keywordTypeCodeSevice() {
        return EasyMock.createMock(KeywordTypeCodeSevice.class);
    }
    @Bean
    public DocumentTypeCodeService documentTypeCodeService() {
        return EasyMock.createMock(DocumentTypeCodeService.class);
    }
    @Bean
    public EditBookDefinitionService editBookDefinitionService() {
        return EasyMock.createMock(EditBookDefinitionService.class);
    }
    @Bean
    public JobRequestService jobRequestService() {
        return EasyMock.createMock(JobRequestService.class);
    }
    @Bean
    public EBookAuditService eBookAuditService() {
        return EasyMock.createMock(EBookAuditService.class);
    }
    @Bean
    public BookDefinitionLockService bookDefinitionLockService() {
        return EasyMock.createMock(BookDefinitionLockService.class);
    }
    @Bean
    public FrontMatterPreviewService frontMatterPreviewService() {
        return EasyMock.createMock(FrontMatterPreviewService.class);
    }
    @Bean
    public MiscConfigSyncService miscConfigSyncService() {
        return EasyMock.createMock(MiscConfigSyncService.class);
    }
    @Bean
    public PrintComponentsCompareController printComponentsCompareController() {
        return EasyMock.createMock(PrintComponentsCompareController.class);
    }
    @Bean
    public PrintComponentHistoryService printComponentHistoryService() {
        return EasyMock.createMock(PrintComponentHistoryService.class);
    }
    @Bean
    public String environmentName() {
        return CoreConstants.PROD_ENVIRONMENT_NAME;
    }
    @Bean
    public NasFileSystem nasFileSystem() {
        return new TestNasFileSystemImpl();
    }

    @Bean
    public ProviewTitleListService proviewTitleListService() {
        return Mockito.mock(ProviewTitleListService.class);
    }

    @Bean
    public DateProvider easyMockDateProvider() {
        return EasyMock.createMock(DateProvider.class);
    }

    @Bean
    public DateService easyMockDateService() {
        return new DateServiceImpl(easyMockDateProvider());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() throws Exception {
        final PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setProperties(getProperties());
        return configurer;
    }

    private static Properties getProperties() throws URISyntaxException {
        Properties properties = new Properties();
        properties.setProperty("codes.workbench.root.dir", getRootDir());
        return properties;
    }

    private static String getRootDir() throws URISyntaxException {
        final URL url = TestConfig.class.getResource("test.xml");
        final File dir = new File(url.toURI());
        return dir.getAbsolutePath();
    }
}
