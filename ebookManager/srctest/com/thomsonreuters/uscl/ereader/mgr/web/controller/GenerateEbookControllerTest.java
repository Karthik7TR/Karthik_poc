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
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_IS_PUBLISHED;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_NEW_MAJOR_VERSION_NUMBER;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_NEW_MINOR_VERSION_NUMBER;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_NEW_OVERWRITE_VERSION_NUMBER;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_NEW_VERSION_NUMBER;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_IS_BOOK_GENERATION_ENQUEUED;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_IS_HIGH_PRIORITY_JOB;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_NEW_VERSION_TYPE;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_OVERWRITE_ALLOWED;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_PILOT_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_PUBLISHING_CUTOFF_DATE_GREATER_THAN_TODAY;
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
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm.Version;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateEbookController;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.form.GenerateHelperService;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
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
    private static final String BOOK_TITLE_ID = "title_id";
    private static final String ID_PARAM = "id";
    private static final String NEW_VERSION_PARAM = "newVersion";

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
    private ManagerService mockManagerService;
    @Mock
    private OutageService mockOutageService;
    @Mock
    private MiscConfigSyncService mockMiscConfigService;
    @Mock
    private XppBundleArchiveService mockXppBundleArchiveService;
    @Mock
    private VersionIsbnService mockVersionIsbnService;
    @Mock
    private GenerateHelperService generateFormService;
    @Mock
    private ProviewTitleInfo latestProviewTitleInfo;
    @Mock
    private ProviewTitleInfo latestPublishedProviewTitleInfo;

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
        prepareMocksForBookGenerationPreview(book, group);

        mockMvc
            .perform(
                get("/" + MVC_BOOK_SINGLE_GENERATE_PREVIEW).param(ID_PARAM, BOOK_DEFINITION_ID.toString())
                    .param(NEW_VERSION_PARAM, GenerateBookForm.Version.OVERWRITE.toString()))
            .andExpect(status().is(200))
            .andExpect(forwardedUrl(VIEW_BOOK_GENERATE_PREVIEW))
            .andExpect(
                model().attributeExists(
                    TITLE,
                    KEY_ISBN,
                    KEY_BOOK_DEFINITION,
                    KEY_PUBLISHING_CUT_OFF_DATE,
                    KEY_USE_PUBLISHING_CUT_OFF_DATE,
                    KEY_PUBLISHING_CUTOFF_DATE_GREATER_THAN_TODAY,
                    KEY_IS_COMPLETE,
                    KEY_PILOT_BOOK_STATUS,
                    KEY_VERSION_NUMBER,
                    KEY_IS_PUBLISHED,
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

    @SneakyThrows
    @Test
    public void shouldGenerateEbookPreviewLastVersionRemoved() {
        final BookDefinition book = givenBook();
        final GroupDefinition group = new GroupDefinition();
        prepareMocksForBookGenerationPreview(book, group);

        final String latestVersion = "v2.0";
        final String latestPublishedVersion = "v1.8";
        setUpProviewTitleInfo(latestVersion, latestPublishedVersion);

        mockMvc.perform(get("/" + MVC_BOOK_SINGLE_GENERATE_PREVIEW)
                .param(ID_PARAM, BOOK_DEFINITION_ID.toString())
                .param(NEW_VERSION_PARAM, GenerateBookForm.Version.OVERWRITE.toString()))
                .andExpect(status().is(200))
                .andExpect(forwardedUrl(VIEW_BOOK_GENERATE_PREVIEW))
                .andExpect(model().attribute(KEY_VERSION_NUMBER, "1.8"))
                .andExpect(model().attribute(KEY_IS_PUBLISHED, true))
                .andExpect(model().attribute(KEY_NEW_MAJOR_VERSION_NUMBER, "3.0"))
                .andExpect(model().attribute(KEY_NEW_MINOR_VERSION_NUMBER, "2.1"))
                .andExpect(model().attribute(KEY_NEW_OVERWRITE_VERSION_NUMBER, "2.0"))
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
        given(mockVersionIsbnService.isIsbnExists((String) isNull())).willReturn(false);

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
                    .param("highPriorityJob", "true")
                    .param(NEW_VERSION_PARAM, Version.MAJOR.toString()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrlPattern(removeExtension(MVC_BOOK_SINGLE_GENERATE_PREVIEW) + ".*"))
            .andExpect(flash().attribute(KEY_IS_HIGH_PRIORITY_JOB, true))
            .andExpect(flash().attribute(KEY_IS_BOOK_GENERATION_ENQUEUED, true))
            .andExpect(flash().attribute(KEY_NEW_VERSION_TYPE, Version.MAJOR.toString()))
            .andExpect(flash().attribute(KEY_NEW_VERSION_NUMBER, version));
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

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private void prepareMocksForBookGenerationPreview(final BookDefinition book, final GroupDefinition group) {
        given(mockBookDefinitionService.findBookDefinitionByEbookDefId(eq(BOOK_DEFINITION_ID)))
                .willReturn(book);
        given(mockOutageService.getAllPlannedOutagesToDisplay()).willReturn(emptyList());
        given(mockMiscConfigService.getMiscConfig()).willReturn(new MiscConfig());
        given(mockGroupService.getLastGroup(eq(book))).willReturn(group);
        given(mockGroupService.createGroupDefinition(eq(book), eq("v1"), (List<String>) isNull()))
                .willReturn(group);
        given(mockVersionIsbnService.isIsbnExists((String) isNull())).willReturn(false);
    }

    @SuppressWarnings("SameParameterValue")
    @SneakyThrows
    private void setUpProviewTitleInfo(final String latestVersion, final String latestPublishedVersion) {
        given(mockProviewHandler.getLatestProviewTitleInfo(BOOK_TITLE_ID)).willReturn(latestProviewTitleInfo);
        given(latestProviewTitleInfo.getVersion()).willReturn(latestVersion);
        given(mockProviewHandler.getLatestPublishedProviewTitleInfo(BOOK_TITLE_ID))
                .willReturn(latestPublishedProviewTitleInfo);
        given(latestPublishedProviewTitleInfo.getVersion()).willReturn(latestPublishedVersion);
    }

    private BookDefinition givenBook() {
        final DocumentTypeCode docType = new DocumentTypeCode();
        docType.setUsePublishCutoffDateFlag(true);
        final BookDefinition book = new BookDefinition();
        book.setEbookDefinitionId(BOOK_DEFINITION_ID);
        book.setIsDeletedFlag(false);
        book.setPublishedOnceFlag(true);
        book.setFullyQualifiedTitleId(BOOK_TITLE_ID);
        book.setProviewDisplayName("");
        book.setIsbn("isbn");
        book.setPublishCutoffDate(new DateTime().toDateMidnight().toDate());
        book.setDocumentTypeCodes(docType);
        book.setGroupName("test group");
        book.setEbookDefinitionCompleteFlag(true);
        return book;
    }
}
