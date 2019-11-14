package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static java.util.Collections.emptyList;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_BOOK_DEFINITION;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_ERR_MESSAGE;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_GROUP_CURRENT_PREVIEW;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_GROUP_NEXT_PREVIEW;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_ISBN;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_IS_COMPLETE;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_IS_NEW_ISBN;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_NEW_MAJOR_VERSION_NUMBER;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_NEW_MINOR_VERSION_NUMBER;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_NEW_OVERWRITE_VERSION_NUMBER;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_OVERWRITE_ALLOWED;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_PILOT_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_PUBLISHING_CUTOFF_DATE_EQUAL_OR_GREATER_THAN_TODAY;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_PUBLISHING_CUT_OFF_DATE;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_SUPER_PUBLISHER_PUBLISHERPLUS;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_USE_PUBLISHING_CUT_OFF_DATE;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_VERSION_NUMBER;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.MVC_ERROR_BOOK_DELETED;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.TITLE;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.VIEW_BOOK_GENERATE_BULK_PREVIEW;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.VIEW_BOOK_GENERATE_PREVIEW;
import static org.apache.commons.io.FilenameUtils.removeExtension;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateEbookController;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.form.GenerateHelperService;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.SneakyThrows;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public final class GenerateEbookControllerTest {
    private static Long BOOK_DEFINITION_ID = 127L;
    private MockMvc mockMvc;

    @InjectMocks
    private GenerateEbookController controller;

    @Mock
    private BookDefinitionService mockBookDefinitionService;
    @Mock
    private MessageSourceAccessor mockMessageSourceAccessor;
    @Mock
    private ProviewHandler mockProviewHandler;
    @Mock
    private GroupService mockGroupService;
    @Mock
    private JobRequestService mockJobRequestService;
    @Mock
    private PublishingStatsService mockPublishingStatsService;
    @Mock
    private ManagerService mockManagerService;
    @Mock
    private OutageService mockOutageService;
    @Mock
    private MiscConfigSyncService mockMiscConfigService;
    @Mock
    private XppBundleArchiveService mockXppBundleArchiveService;
    @Mock
    private GenerateHelperService generateFormService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * Test the GET of one book selected to generator preview
     *
     * @throws Exception
     */
    @Test
    @SneakyThrows
    public void shouldGenerateEbookPreviewGET() {
        final BookDefinition book = givenBook();
        final GroupDefinition group = new GroupDefinition();

        given(mockBookDefinitionService.findBookDefinitionByEbookDefId(eq(BOOK_DEFINITION_ID))).willReturn(book);

        given(mockOutageService.getAllPlannedOutagesToDisplay()).willReturn(emptyList());
        given(mockMiscConfigService.getMiscConfig()).willReturn(new MiscConfig());
        given(mockGroupService.getLastGroup(eq(book))).willReturn(group);
        given(mockGroupService.createGroupDefinition(eq(book), eq("v1"), (List<String>) isNull())).willReturn(group);
        given(mockPublishingStatsService.hasIsbnBeenPublished((String) isNull())).willReturn(false);

        mockMvc
            .perform(
                get("/" + MVC_BOOK_SINGLE_GENERATE_PREVIEW).param("id", BOOK_DEFINITION_ID.toString())
                    .param("newVersion", GenerateBookForm.Version.OVERWRITE.toString()))
            .andExpect(status().is(200))
            .andExpect(forwardedUrl(VIEW_BOOK_GENERATE_PREVIEW))
            .andExpect(
                model().attributeExists(
                    TITLE,
                    KEY_ISBN,
                    KEY_BOOK_DEFINITION,
                    KEY_PUBLISHING_CUT_OFF_DATE,
                    KEY_USE_PUBLISHING_CUT_OFF_DATE,
                    KEY_PUBLISHING_CUTOFF_DATE_EQUAL_OR_GREATER_THAN_TODAY,
                    KEY_IS_COMPLETE,
                    KEY_PILOT_BOOK_STATUS,
                    KEY_VERSION_NUMBER,
                    KEY_NEW_OVERWRITE_VERSION_NUMBER,
                    KEY_NEW_MAJOR_VERSION_NUMBER,
                    KEY_NEW_MINOR_VERSION_NUMBER,
                    KEY_OVERWRITE_ALLOWED,
                    GenerateBookForm.FORM_NAME,
                    KEY_GROUP_CURRENT_PREVIEW,
                    KEY_GROUP_NEXT_PREVIEW,
                    KEY_IS_NEW_ISBN))
            .andExpect(model().attributeDoesNotExist(KEY_ERR_MESSAGE));
    }

    @Test
    @SneakyThrows
    public void shouldRedirectToDeletedErrorPage() {
        final BookDefinition book = new BookDefinition();
        book.setIsDeletedFlag(true);
        given(mockBookDefinitionService.findBookDefinitionByEbookDefId(eq(BOOK_DEFINITION_ID))).willReturn(book);

        mockMvc.perform(get("/" + MVC_BOOK_SINGLE_GENERATE_PREVIEW).param("id", BOOK_DEFINITION_ID.toString()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrlPattern(removeExtension(MVC_ERROR_BOOK_DELETED) + ".*"));
    }

    @Test
    @SneakyThrows
    public void shouldGenerateEbookPreviewSplitBookGET() {
        final BookDefinition book = givenBook();
        book.setIsSplitBook(true);
        book.setIsSplitTypeAuto(false);
        final SplitDocument doc = new SplitDocument();
        doc.setBookDefinition(new BookDefinition());
        book.setSplitDocuments(Arrays.asList(doc));

        final GroupDefinition group = new GroupDefinition();

        given(mockBookDefinitionService.findBookDefinitionByEbookDefId(eq(BOOK_DEFINITION_ID))).willReturn(book);
        given(mockOutageService.getAllPlannedOutagesToDisplay()).willReturn(new ArrayList<PlannedOutage>());
        given(mockGroupService.getLastGroup(eq(book))).willReturn(group);
        given(mockGroupService.createGroupDefinition(any(), anyString(), anyList())).willReturn(group);
        given(mockPublishingStatsService.hasIsbnBeenPublished((String) isNull())).willReturn(false);

        mockMvc
            .perform(
                get("/" + MVC_BOOK_SINGLE_GENERATE_PREVIEW).param("id", BOOK_DEFINITION_ID.toString())
                    .param("newVersion", GenerateBookForm.Version.OVERWRITE.toString()))
            .andExpect(status().is(200))
            .andExpect(forwardedUrl(VIEW_BOOK_GENERATE_PREVIEW))
            .andExpect(model().attribute(KEY_IS_NEW_ISBN, equalTo("Y")));
        verify(mockGroupService, times(2)).createGroupDefinition(any(), any(), any());
    }

    @Test
    @SneakyThrows
    public void shouldGenerateEbookPreviewPOST() {
        //given
        final String version = "1";
        final BookDefinition book = givenBook();

        given(generateFormService.getVersion(any())).willReturn(version);
        given(mockBookDefinitionService.findBookDefinitionByEbookDefId(eq(BOOK_DEFINITION_ID))).willReturn(book);
        given(generateFormService.getError(eq(book), any())).willReturn(ofNullable(null));
        given(mockJobRequestService.saveQueuedJobRequest(eq(book), eq(version), anyInt(), any()))
            .willReturn(BOOK_DEFINITION_ID);
        doNothing().when(mockBookDefinitionService).updatePublishedStatus(eq(book.getEbookDefinitionId()), eq(true));
        given(generateFormService.getMessage(eq("mesg.job.enqueued.success"), eq(null), any())).willReturn("");

        mockMvc
            .perform(
                post("/" + MVC_BOOK_SINGLE_GENERATE_PREVIEW).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("command", Command.GENERATE.toString())
                    .param("id", BOOK_DEFINITION_ID.toString())
                    .param("isHighPriorityJob", "true"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrlPattern(removeExtension(MVC_BOOK_SINGLE_GENERATE_PREVIEW) + ".*"));
        verify(mockJobRequestService).saveQueuedJobRequest(eq(book), eq(version), anyInt(), any());
    }

    @Test
    @SneakyThrows
    public void shouldDisableActionButtons() {
        given(mockBookDefinitionService.findBookDefinitionByEbookDefId(eq(BOOK_DEFINITION_ID))).willReturn(null);

        mockMvc
            .perform(
                get("/" + MVC_BOOK_SINGLE_GENERATE_PREVIEW).param("id", BOOK_DEFINITION_ID.toString())
                    .param("newVersion", GenerateBookForm.Version.OVERWRITE.toString()))
            .andExpect(status().is(200))
            .andExpect(forwardedUrl(VIEW_BOOK_GENERATE_PREVIEW))
            .andExpect(model().attributeExists(KEY_SUPER_PUBLISHER_PUBLISHERPLUS));
    }

    @Test
    @SneakyThrows
    public void shouldShowErrorMessage() {
        //given
        final String message = "some error message";
        final BookDefinition book = givenBook();
        book.setSourceType(SourceType.XPP);
        book.setPrintComponents(emptyList());

        given(mockBookDefinitionService.findBookDefinitionByEbookDefId(eq(book.getEbookDefinitionId())))
            .willReturn(book);
        given(generateFormService.getError(eq(book), any())).willReturn(of(message));

        mockMvc
            .perform(
                post("/" + MVC_BOOK_SINGLE_GENERATE_PREVIEW).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("command", Command.GENERATE.toString())
                    .param("id", BOOK_DEFINITION_ID.toString())
                    .param("isHighPriorityJob", "true"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrlPattern(removeExtension(MVC_BOOK_SINGLE_GENERATE_PREVIEW) + ".*"))
            .andExpect(flash().attribute(KEY_ERR_MESSAGE, equalTo(message)));
    }

    @Test
    @SneakyThrows
    public void shouldGenerateEbookPreviewNoBooks() {
        mockMvc.perform(get("/" + MVC_BOOK_SINGLE_GENERATE_PREVIEW)).andExpect(status().is(400));
    }

    @Test
    @SneakyThrows
    public void shouldGenerateBulkEbookPreview() {
        final BookDefinition book = givenBook();
        book.setFullyQualifiedTitleId("");
        book.setProviewDisplayName("");
        book.setIsDeletedFlag(false);

        given(mockBookDefinitionService.findBookDefinitionByEbookDefId(eq(book.getEbookDefinitionId())))
            .willReturn(book);

        mockMvc
            .perform(
                get("/" + MVC_BOOK_BULK_GENERATE_PREVIEW).param("id", new String[] {BOOK_DEFINITION_ID.toString()}))
            .andExpect(status().is(200))
            .andExpect(forwardedUrl(VIEW_BOOK_GENERATE_BULK_PREVIEW));
    }

    private BookDefinition givenBook() {
        final DocumentTypeCode docType = new DocumentTypeCode();
        docType.setUsePublishCutoffDateFlag(true);
        final BookDefinition book = new BookDefinition();
        book.setEbookDefinitionId(BOOK_DEFINITION_ID);
        book.setIsDeletedFlag(false);
        book.setPublishedOnceFlag(true);
        book.setFullyQualifiedTitleId("");
        book.setProviewDisplayName("");
        book.setIsbn("isbn");
        book.setPublishCutoffDate(new DateTime().toDateMidnight().toDate());
        book.setDocumentTypeCodes(docType);
        book.setGroupName("test group");
        book.setEbookDefinitionCompleteFlag(true);
        return book;
    }
}
