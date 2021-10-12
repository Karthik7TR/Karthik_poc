package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;
import com.thomsonreuters.uscl.ereader.frontmatter.service.FrontMatterPreviewService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.PdfFileNameValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleListService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.book.BookDefinitionLockService;
import lombok.SneakyThrows;
import org.easymock.EasyMock;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.USCL_PUBLISHER_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EditBookDefinitionControllerTest.Config.class})
public final class EditBookDefinitionControllerTest {
    private static final String BINDING_RESULT_KEY =
        BindingResult.class.getName() + "." + EditBookDefinitionForm.FORM_NAME;
    private static final long BOOK_DEFINITION_ID = 1;
    private static final String CI_CONTENT = "cicontent";
    private static final String SEQUENCE_NUM_1 = "1";
    private static final String SELECTED_FRONT_MATTER_PREVIEW_PAGE = "1";
    private static final String TOC_LABEL_1 = "Label 1";
    private static final String SECTION_HEADING_1 = "Heading 1";
    private static final String SECTION_TEXT_1 = "Section text 1";
    private static final String PDF_LINK_TEXT_1 = "PDF Link text 1";
    private static final String PDF_FILENAME_1 = "PDF Filename 1";
    private static final String TITLE_ID = "uscl/an/test";
    private static final String EXPECTED_HTML = "html";
    private static final String VERSION_1_5 = "v1.5";
    private static final String VERSION_1_0 = "v1.0";
    private static final String FULLY_QUALIFIED_TITLE_ID = "uscl/an/abcd";
    private static List<KeywordTypeCode> KEYWORD_CODES = new ArrayList<>();
    private final KeywordTypeCode subject = new KeywordTypeCode();

    private BookDefinitionLock bookDefinitionLock;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    @Autowired
    private EditBookDefinitionController controller;
    @Autowired
    private BookDefinitionService mockBookDefinitionService;
    @Autowired
    private KeywordTypeCodeSevice keywordTypeCodeSevice;
    @Autowired
    private DocumentTypeCodeService mockDocumentTypeCodeService;
    @Autowired
    private JobRequestService mockJobRequestService;
    @Autowired
    private EditBookDefinitionService mockEditBookDefinitionService;
    @Autowired
    private EBookAuditService mockAuditService;
    @Autowired
    private BookDefinitionLockService mockLockService;
    @Autowired
    private MiscConfigSyncService mockMiscConfigService;
    @Autowired
    private PrintComponentsCompareController mockPrintComponentsCompareController;
    @Autowired
    private FrontMatterPreviewService mockFrontMatterPreviewService;
    @Autowired
    private ProviewTitleListService proviewTitleListService;
    @Autowired
    private PdfFileNameValidator mockPdfFileNameValidator;

    private EbookName bookName;
    private DocumentTypeCode documentTypeCode;
    private PublisherCode publisherCode;
    private List<String> buckets;
    private Map<String, List<DocumentTypeCode>> documentTypesByPublishers;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        final List<String> frontMatterThemes = new ArrayList<>();
        frontMatterThemes.add("WestLaw Next");

        EasyMock.reset(
                mockBookDefinitionService,
                keywordTypeCodeSevice,
                mockDocumentTypeCodeService,
                mockJobRequestService,
                mockEditBookDefinitionService,
                mockAuditService,
                mockLockService,
                mockMiscConfigService,
                mockPrintComponentsCompareController,
                mockFrontMatterPreviewService
        );
        EasyMock.expect(mockEditBookDefinitionService.getFrontMatterThemes()).andReturn(frontMatterThemes);
        subject.setId(4L);

        bookName = new EbookName();
        bookName.setBookNameText("Book Name");
        bookName.setEbookNameId(Integer.parseInt("1"));
        bookName.setSequenceNum(1);

        documentTypeCode = new DocumentTypeCode();
        documentTypeCode.setId(Long.parseLong("1"));
        documentTypeCode.setAbbreviation(WebConstants.DOCUMENT_TYPE_ANALYTICAL_ABBR);
        documentTypeCode.setName(WebConstants.DOCUMENT_TYPE_ANALYTICAL);

        publisherCode = new PublisherCode();
        publisherCode.setId(1L);
        publisherCode.setName("uscl");

        bookDefinitionLock = new BookDefinitionLock();
        bookDefinitionLock.setCheckoutTimestamp(new Date());
        bookDefinitionLock.setEbookDefinitionLockId(1L);
        bookDefinitionLock.setFullName("name");
        bookDefinitionLock.setUsername("username");

        buckets = Collections.singletonList(Bucket.BOOKS.toString());
        documentTypesByPublishers = new HashMap<>();
    }

    /**
     * Test the GET to the Create Book Definition page
     */
    @Test
    public void testCreateBookDefintionGet() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_CREATE);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(keywordTypeCodeSevice
            .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_US))
            .andReturn(subject);
        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_CANADA))
                .andReturn(subject);
        EasyMock.replay(keywordTypeCodeSevice);

        setupDropdownMenuAndKeywords(1);

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.replay(mockMiscConfigService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_CREATE, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            checkInitialValuesDynamicContent(model);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockMiscConfigService);
    }

    /**
     * Test the POST to the Create Book Definition page when there are
     * validation errors
     */
    @Test
    public void testCreateBookDefintionPostFailed() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_CREATE);
        request.setMethod(HttpMethod.POST.name());

        setupDropdownMenuAndKeywords(1);

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.replay(mockMiscConfigService);

        EasyMock.expect(keywordTypeCodeSevice
            .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_US))
            .andReturn(subject);
        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_CANADA))
                .andReturn(subject);
        EasyMock.replay(keywordTypeCodeSevice);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_CREATE, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            checkInitialValuesDynamicContent(model);

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertTrue(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockMiscConfigService);
    }

    /**
     * Test the POST to the Create Book Definition page when titleId is complete
     * and Definition in incomplete state
     */
    @Test
    public void testCreateBookDefintionPostIncompleteSuccess() {
        final String titleId = FULLY_QUALIFIED_TITLE_ID;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_CREATE);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("contentTypeId", "1");
        request.setParameter("pubAbbr", "abcd");
        request.setParameter("publisher", "uscl");
        request.setParameter("titleId", titleId);
        request.setParameter("groupsEnabled", "false");
        request.setParameter("bucket", Bucket.BOOKS.toString());

        setupDropdownMenuAndKeywords(2);

        final BookDefinition expectedBook = createBookDef(titleId);
        EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class)))
            .andReturn(expectedBook);
        setupMockServices(null, 1, false);

        mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
        EasyMock.replay(mockAuditService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(keywordTypeCodeSevice);
        EasyMock.verify(mockAuditService);
    }

    @Test
    public void testFrontMatterPreviewPost() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_FRONT_MATTER_PREVIEW);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("titleId", TITLE_ID);
        request.setParameter("selectedFrontMatterPreviewPage", SELECTED_FRONT_MATTER_PREVIEW_PAGE);
        request.setParameter("frontMatters[0].sequenceNum", SEQUENCE_NUM_1);
        request.setParameter("frontMatters[0].pageTocLabel", TOC_LABEL_1);
        request.setParameter("frontMatters[0].frontMatterSections[0].sequenceNum", SEQUENCE_NUM_1);
        request.setParameter("frontMatters[0].frontMatterSections[0].sectionHeading", SECTION_HEADING_1);
        request.setParameter("frontMatters[0].frontMatterSections[0].sectionText", SECTION_TEXT_1);
        request.setParameter("frontMatters[0].frontMatterSections[0].pdfs[0].sequenceNum", SEQUENCE_NUM_1);
        request.setParameter("frontMatters[0].frontMatterSections[0].pdfs[0].pdfLinkText", PDF_LINK_TEXT_1);
        request.setParameter("frontMatters[0].frontMatterSections[0].pdfs[0].pdfFilename", PDF_FILENAME_1);
        initCreateFrontMatterService();

        handlerAdapter.handle(request, response, controller);

        assertionsAfterSuccessfulResponse();
    }

    @Test
    public void testFrontMatterPreviewWithNullsPost() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_FRONT_MATTER_PREVIEW);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("titleId", TITLE_ID);
        request.setParameter("selectedFrontMatterPreviewPage", SELECTED_FRONT_MATTER_PREVIEW_PAGE);
        request.setParameter("frontMatters[1].sequenceNum", SEQUENCE_NUM_1);
        request.setParameter("frontMatters[1].pageTocLabel", TOC_LABEL_1);
        request.setParameter("frontMatters[1].frontMatterSections[1].sequenceNum", SEQUENCE_NUM_1);
        request.setParameter("frontMatters[1].frontMatterSections[1].sectionHeading", SECTION_HEADING_1);
        request.setParameter("frontMatters[1].frontMatterSections[1].sectionText", SECTION_TEXT_1);
        request.setParameter("frontMatters[1].frontMatterSections[1].pdfs[1].sequenceNum", SEQUENCE_NUM_1);
        request.setParameter("frontMatters[1].frontMatterSections[1].pdfs[1].pdfLinkText", PDF_LINK_TEXT_1);
        request.setParameter("frontMatters[1].frontMatterSections[1].pdfs[1].pdfFilename", PDF_FILENAME_1);
        initCreateFrontMatterService();

        handlerAdapter.handle(request, response, controller);

        assertionsAfterSuccessfulResponse();
    }

    @Test
    public void testFrontMatterPreviewPostFailureNoId() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_FRONT_MATTER_PREVIEW);
        request.setMethod(HttpMethod.POST.name());

        handlerAdapter.handle(request, response, controller);

        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        Assert.assertEquals("Additional front matter page id is missing in request", response.getContentAsString());
    }

    @Test
    public void testFrontMatterPreviewPostFailureIncorrectId() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_FRONT_MATTER_PREVIEW);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("selectedFrontMatterPreviewPage", "5");
        String expectedErrorMessage = "Could not retrieve additional front matter page with id: 5";
        EasyMock.expect(mockFrontMatterPreviewService
                .getAdditionalFrontPagePreview(EasyMock.anyObject(BookDefinition.class), EasyMock.anyLong()))
                .andThrow(new EBookFrontMatterGenerationException(expectedErrorMessage));
        EasyMock.replay(mockFrontMatterPreviewService);

        handlerAdapter.handle(request, response, controller);

        Assert.assertEquals(expectedErrorMessage, response.getContentAsString());
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        EasyMock.verify(mockFrontMatterPreviewService);
    }


    /**
     * Test the POST to the Create Book Definition page when titleId is complete
     * but no other required fields are complete while marked as complete
     */
    @Test
    public void testCreateBookDefintionPostCompleteStateFailed() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_CREATE);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("contentTypeId", "1");
        request.setParameter("pubAbbr", "abcd");
        request.setParameter("publisher", "uscl");
        request.setParameter("titleId", FULLY_QUALIFIED_TITLE_ID);
        request.setParameter("bucket", Bucket.BOOKS.toString());
        request.setParameter("isComplete", "true");

        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_US))
                .andReturn(subject);
        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_CANADA))
                .andReturn(subject);
        setupDropdownMenuAndKeywords(1);
        setupMockServices(null, 1, true);

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.replay(mockMiscConfigService);
        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_CREATE, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertTrue(bindingResult.hasErrors());

            checkInitialValuesDynamicContent(model);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(keywordTypeCodeSevice);
        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockMiscConfigService);
    }

    /**
     * Test the POST to the Create Book Definition page when titleId is complete
     * and Definition in incomplete state
     */
    @Test
    public void testCreateBookDefintionPostCompleteStateSuccess() {
        final String titleId = FULLY_QUALIFIED_TITLE_ID;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_CREATE);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("contentTypeId", "1");
        request.setParameter("ProviewDisplayName", "Name in Proview");
        request.setParameter("pubAbbr", "abcd");
        request.setParameter("publisher", "uscl");
        request.setParameter("titleId", titleId);
        request.setParameter("frontMatterTitle.bookNameText", "title name");
        request.setParameter("frontMatterTitle.sequenceNum", "1");
        request.setParameter("copyright", "Somethings");
        request.setParameter("materialId", "123456789012345678");
        request.setParameter("sourceType", "TOC");
        request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
        request.setParameter("docCollectionName", "sdfdsfdsf");
        request.setParameter("tocCollectionName", "sdfdsfdsf");
        request.setParameter("isbn", "978-193-5-18235-1");
        request.setParameter("groupsEnabled", "false");
        request.setParameter("isComplete", "true");
        request.setParameter("validateForm", "false");
        request.setParameter("printPageNumbers", "true");
        request.setParameter("bucket", Bucket.BOOKS.toString());

        setupDropdownMenuAndKeywords(2);

        final BookDefinition expectedBook = createBookDef(titleId);
        EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class)))
            .andReturn(expectedBook);
        setupMockServices(null, 1, true);

        mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
        EasyMock.replay(mockAuditService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView TODO: fix
            // View view = mav.getView();
            // assertEquals(RedirectView.class, view.getClass());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockAuditService);
        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test the GET to the Edit Book Definition page
     */
    @Test
    public void testEditBookDefinitionGet() throws Exception {
        when(proviewTitleListService.getPreviousVersions(any())).thenReturn(Collections.singletonList(VERSION_1_5));

        Map<String, Object> model = testEditBookDefinitionGet(null);

        List<String> versions = (List<String>) model.get(WebConstants.KEY_PREVIOUS_VERSIONS);
        assertEquals(1, versions.size());
        assertEquals(VERSION_1_5, versions.get(0));
        Mockito.reset(proviewTitleListService);
    }

    @Test
    public void testEditBookDefinitionGetNoProview() throws Exception {
        when(proviewTitleListService.getPreviousVersions(any())).thenThrow(new RuntimeException());

        Map<String, Object> model = testEditBookDefinitionGet(VERSION_1_0);

        List<String> versions = (List<String>) model.get(WebConstants.KEY_PREVIOUS_VERSIONS);
        assertEquals(1, versions.size());
        assertEquals(VERSION_1_0, versions.get(0));
        Mockito.reset(proviewTitleListService);
    }

    @Test
    public void testEditBookDefinitionGetNoProviewNoPreviousVersion() throws Exception {
        when(proviewTitleListService.getPreviousVersions(any())).thenThrow(new RuntimeException());

        Map<String, Object> model = testEditBookDefinitionGet(null);

        List<String> versions = (List<String>) model.get(WebConstants.KEY_PREVIOUS_VERSIONS);
        assertEquals(0, versions.size());
        Mockito.reset(proviewTitleListService);
    }

    private Map<String, Object> testEditBookDefinitionGet(final String versionWithPreviousDocIds) throws Exception {
        final BookDefinition book = createBookDef(FULLY_QUALIFIED_TITLE_ID);
        book.setVersionWithPreviousDocIds(versionWithPreviousDocIds);
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_EDIT);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        setupDropdownMenuAndKeywords(2);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        EasyMock.expect(mockJobRequestService.isBookInJobRequest(BOOK_DEFINITION_ID)).andReturn(false);
        EasyMock.replay(mockJobRequestService);

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(mockMiscConfigService);

        bookDefinitionLock.setEbookDefinition(book);
        EasyMock.expect(mockLockService.findActiveBookLock(book)).andReturn(null);
        mockLockService.lockBookDefinition(book, null, null);
        EasyMock.replay(mockLockService);

        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_US))
                .andReturn(subject);
        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_CANADA))
                .andReturn(subject);
        EasyMock.replay(keywordTypeCodeSevice);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        // Verify the returned view name
        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_EDIT, mav.getViewName());

        // Check the state of the model
        final Map<String, Object> model = mav.getModel();
        checkInitialValuesDynamicContentForPublished(model);

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockJobRequestService);
        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockLockService);
        EasyMock.verify(mockMiscConfigService);

        return model;
    }

    /**
     * Test the GET to the Edit Book Definition page for invalid book
     */
    @Test
    public void testEditBookDefintionGetInvalidBook() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_EDIT);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        setupDropdownMenuAndKeywords(2);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(mockMiscConfigService);

        EasyMock.expect(keywordTypeCodeSevice
            .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_US))
            .andReturn(subject);
        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_CANADA))
                .andReturn(subject);
        EasyMock.replay(keywordTypeCodeSevice);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_EDIT, mav.getViewName());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockMiscConfigService);
    }

    /**
     * Test the GET to the Edit Book Definition page for deleted book
     */
    @Test
    public void testEditBookDefintionGetDeletedBook() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_EDIT);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        final BookDefinition book = new BookDefinition();
        book.setEbookDefinitionId(BOOK_DEFINITION_ID);
        book.setIsDeletedFlag(true);

        setupDropdownMenuAndKeywords(2);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test the GET to the Edit Book Definition page when locked
     */
    @Test
    public void testEditBookDefintionLocked() {
        final String fullyQualifiedTitleId = FULLY_QUALIFIED_TITLE_ID;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_EDIT);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);

        setupDropdownMenuAndKeywords(2);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        EasyMock.expect(mockLockService.findActiveBookLock(book)).andReturn(bookDefinitionLock);
        EasyMock.replay(mockLockService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_ERROR_LOCKED, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final BookDefinition actualBook = (BookDefinition) model.get(WebConstants.KEY_BOOK_DEFINITION);
            final BookDefinitionLock actualLock = (BookDefinitionLock) model.get(WebConstants.KEY_BOOK_DEFINITION_LOCK);

            Assert.assertEquals(book, actualBook);
            Assert.assertEquals(bookDefinitionLock, actualLock);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockLockService);
    }

    /**
     * Test the POST to the Edit Book Definition page
     */
    @Test
    public void testEditBookDefintionPOST() {
        final String fullyQualifiedTitleId = FULLY_QUALIFIED_TITLE_ID;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_EDIT);
        request.setParameter("ProviewDisplayName", "Name in Proview");
        request.setParameter("contentTypeId", "1");
        request.setParameter("pubAbbr", "abcd");
        request.setParameter("publisher", "uscl");
        request.setParameter("titleId", fullyQualifiedTitleId);
        request.setParameter("frontMatterTitle.bookNameText", "title name");
        request.setParameter("frontMatterTitle.sequenceNum", "1");
        request.setParameter("copyright", "Somethings");
        request.setParameter("materialId", "123456789012345678");
        request.setParameter("sourceType", "TOC");
        request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
        request.setParameter("docCollectionName", "sdfdsfdsf");
        request.setParameter("tocCollectionName", "sdfdsfdsf");
        request.setParameter("isbn", "978-193-5-18235-1");
        request.setParameter("isComplete", "true");
        request.setParameter("groupsEnabled", "false");
        request.setParameter("validateForm", "false");
        request.setParameter("bookdefinitionId", Long.toString(BOOK_DEFINITION_ID));
        request.setParameter("bucket", Bucket.BOOKS.toString());
        request.setMethod(HttpMethod.POST.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);
        EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class)))
            .andReturn(book);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID))
            .andReturn(book)
            .times(2);
        EasyMock.replay(mockBookDefinitionService);

        EasyMock.expect(mockLockService.findActiveBookLock(book)).andReturn(null);
        mockLockService.removeLock(book);
        EasyMock.replay(mockLockService);

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(mockMiscConfigService);

        final DocumentTypeCode code = new DocumentTypeCode();
        code.setId(Long.parseLong("1"));
        code.setAbbreviation("an");
        code.setName("Analytical");
        EasyMock.expect(mockDocumentTypeCodeService.getDocumentTypeCodeById(BOOK_DEFINITION_ID)).andReturn(code);
        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
        EasyMock.replay(keywordTypeCodeSevice);
        EasyMock.replay(mockDocumentTypeCodeService);

        setupDropdownMenuAndKeywords(2);

        mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
        EasyMock.replay(mockAuditService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockAuditService);
        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(keywordTypeCodeSevice);
        EasyMock.verify(mockLockService);
        EasyMock.verify(mockMiscConfigService);
    }

    /**
     * Test the POST to the Edit Book Definition Saving in Complete state with
     * no name line
     */
    @Test
    public void testEditBookDefintionPOSTFailed() {
        final String fullyQualifiedTitleId = FULLY_QUALIFIED_TITLE_ID;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_EDIT);
        request.setParameter("contentTypeId", "1");
        request.setParameter("pubAbbr", "abcd");
        request.setParameter("publisher", "uscl");
        request.setParameter("titleId", fullyQualifiedTitleId);
        request.setParameter("copyright", "Somethings");
        request.setParameter("materialId", "123456789012345678");
        request.setParameter("sourceType", "TOC");
        request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
        request.setParameter("tocCollectionName", "sdfdsfdsf");
        request.setParameter("isbn", "978-193-5-18235-1");
        request.setParameter("isComplete", "true");
        request.setParameter("validateForm", "false");
        request.setParameter("bookdefinitionId", Long.toString(BOOK_DEFINITION_ID));
        request.setParameter("bucket", Bucket.BOOKS.toString());
        request.setMethod(HttpMethod.POST.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);
        book.setPublishedOnceFlag(true);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID))
            .andReturn(book)
            .times(2);
        EasyMock.replay(mockBookDefinitionService);
        final DocumentTypeCode code = new DocumentTypeCode();
        code.setId(Long.parseLong("1"));
        code.setAbbreviation("an");
        code.setName("Analytical");
        EasyMock.expect(mockDocumentTypeCodeService.getDocumentTypeCodeById(BOOK_DEFINITION_ID)).andReturn(code);
        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
        EasyMock.expect(keywordTypeCodeSevice
            .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_US))
            .andReturn(subject);
        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_CANADA))
                .andReturn(subject);
        EasyMock.replay(keywordTypeCodeSevice);
        EasyMock.replay(mockDocumentTypeCodeService);

        EasyMock.expect(mockJobRequestService.isBookInJobRequest(BOOK_DEFINITION_ID)).andReturn(false);
        EasyMock.replay(mockJobRequestService);

        EasyMock.expect(mockLockService.findActiveBookLock(book)).andReturn(null);
        EasyMock.replay(mockLockService);

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.expectLastCall().times(3);
        EasyMock.replay(mockMiscConfigService);

        setupDropdownMenuAndKeywords(1);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertTrue(bindingResult.hasErrors());
            assertTrue((boolean) model.get(WebConstants.KEY_IS_PUBLISHED));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(keywordTypeCodeSevice);
        EasyMock.verify(mockJobRequestService);
        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockLockService);
        EasyMock.verify(mockMiscConfigService);
    }

    /**
     * Test the POST to the Edit Book Definition page when book is locked by
     * another user
     */
    @Test
    public void testEditBookDefintionLockedPOST() {
        final String fullyQualifiedTitleId = FULLY_QUALIFIED_TITLE_ID;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_EDIT);
        request.setParameter("ProviewDisplayName", "Name in Proview");
        request.setParameter("contentTypeId", "1");
        request.setParameter("pubAbbr", "abcd");
        request.setParameter("publisher", "uscl");
        request.setParameter("titleId", fullyQualifiedTitleId);
        request.setParameter("frontMatterTitle.bookNameText", "title name");
        request.setParameter("frontMatterTitle.sequenceNum", "1");
        request.setParameter("copyright", "Somethings");
        request.setParameter("materialId", "123456789012345678");
        request.setParameter("sourceType", "TOC");
        request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
        request.setParameter("docCollectionName", "sdfdsfdsf");
        request.setParameter("tocCollectionName", "sdfdsfdsf");
        request.setParameter("isbn", "978-193-5-18235-1");
        request.setParameter("isComplete", "true");
        request.setParameter("groupsEnabled", "false");
        request.setParameter("validateForm", "false");
        request.setParameter("bookdefinitionId", Long.toString(BOOK_DEFINITION_ID));
        request.setParameter("bucket", Bucket.BOOKS.toString());
        request.setMethod(HttpMethod.POST.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID))
            .andReturn(book)
            .times(2);
        EasyMock.replay(mockBookDefinitionService);

        final DocumentTypeCode code = new DocumentTypeCode();
        code.setId(Long.parseLong("1"));
        code.setAbbreviation("an");
        code.setName("Analytical");
        EasyMock.expect(mockDocumentTypeCodeService.getDocumentTypeCodeById(BOOK_DEFINITION_ID)).andReturn(code);
        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
        EasyMock.replay(keywordTypeCodeSevice);
        EasyMock.replay(mockDocumentTypeCodeService);

        EasyMock.expect(mockLockService.findActiveBookLock(book)).andReturn(bookDefinitionLock);
        EasyMock.replay(mockLockService);

        setupDropdownMenuAndKeywords(2);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_ERROR_LOCKED, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final BookDefinition actualBook = (BookDefinition) model.get(WebConstants.KEY_BOOK_DEFINITION);
            final BookDefinitionLock actualLock = (BookDefinitionLock) model.get(WebConstants.KEY_BOOK_DEFINITION_LOCK);

            Assert.assertEquals(book, actualBook);
            Assert.assertEquals(bookDefinitionLock, actualLock);

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockLockService);
        EasyMock.verify(keywordTypeCodeSevice);
    }

    /**
     * Test the GET to the Copy Book Definition page
     */
    @Test
    public void testCopyBookDefintionGet() {
        final String fullyQualifiedTitleId = FULLY_QUALIFIED_TITLE_ID;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_COPY);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.replay(mockMiscConfigService);

        mockPrintComponentsCompareController.setPrintComponentHistoryAttributes(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.replay(mockPrintComponentsCompareController);

        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_US))
                .andReturn(subject);
        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_CANADA))
                .andReturn(subject);
        EasyMock.replay(keywordTypeCodeSevice);
        setupDropdownMenuAndKeywords(2);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_COPY, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            checkInitialValuesDynamicContent(model);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockMiscConfigService);
        EasyMock.verify(mockPrintComponentsCompareController);
    }

    /**
     * Test the GET to the Copy Book Definition page for deleted book
     */
    @Test
    public void testCopyBookDefintionGetDeletedBook() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_COPY);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        final BookDefinition book = new BookDefinition();
        book.setEbookDefinitionId(BOOK_DEFINITION_ID);
        book.setIsDeletedFlag(true);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        setupDropdownMenuAndKeywords(2);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test the POST to the Copy Book Definition page when there are validation
     * errors
     */
    @Test
    public void testCopyBookDefintionPostFailed() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_COPY);
        request.setMethod(HttpMethod.POST.name());

        setupDropdownMenuAndKeywords(1);

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.replay(mockMiscConfigService);

        EasyMock.expect(keywordTypeCodeSevice
            .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_US))
            .andReturn(subject);
        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_CANADA))
                .andReturn(subject);
        EasyMock.replay(keywordTypeCodeSevice);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_COPY, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            checkInitialValuesDynamicContent(model);

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertTrue(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockMiscConfigService);
    }

    /**
     * Test the POST to the Copy Book Definition page when titleId is complete
     * and Definition in incomplete state
     */
    @Test
    public void testCopyBookDefintionPostIncompleteSuccess() {
        final String titleId = FULLY_QUALIFIED_TITLE_ID;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_COPY);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("contentTypeId", "1");
        request.setParameter("pubAbbr", "abcd");
        request.setParameter("publisher", "uscl");
        request.setParameter("titleId", titleId);
        request.setParameter("groupsEnabled", "false");
        request.setParameter("bucket", Bucket.BOOKS.toString());

        final BookDefinition expectedBook = createBookDef(titleId);
        EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class)))
            .andReturn(expectedBook);
        setupMockServices(null, 1, false);

        mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
        EasyMock.replay(mockAuditService);

        setupDropdownMenuAndKeywords(2);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockAuditService);
        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(keywordTypeCodeSevice);
    }

    /**
     * Test the POST to the Copy Book Definition page when titleId is complete
     * but no other required fields are complete while marked as complete
     */
    @Test
    public void testCopyBookDefintionPostCompleteStateFailed() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_COPY);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("contentTypeId", "1");
        request.setParameter("pubAbbr", "abcd");
        request.setParameter("publisher", "uscl");
        request.setParameter("titleId", FULLY_QUALIFIED_TITLE_ID);
        request.setParameter("isComplete", "true");
        request.setParameter("bucket", Bucket.BOOKS.toString());

        EasyMock.expect(keywordTypeCodeSevice
            .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_US))
            .andReturn(subject);
        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_CANADA))
                .andReturn(subject);
        setupMockServices(null, 1, true);
        setupDropdownMenuAndKeywords(1);

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.replay(mockMiscConfigService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_COPY, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertTrue(bindingResult.hasErrors());

            checkInitialValuesDynamicContent(model);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(keywordTypeCodeSevice);
        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockMiscConfigService);
    }

    /**
     * Test the POST to the Copy Book Definition page when titleId is complete
     * and Definition in incomplete state
     */
    @Test
    public void testCopyBookDefintionPostCompleteStateSuccess() {
        final String titleId = FULLY_QUALIFIED_TITLE_ID;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_COPY);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("contentTypeId", "1");
        request.setParameter("ProviewDisplayName", "Name in Proview");
        request.setParameter("pubAbbr", "abcd");
        request.setParameter("publisher", "uscl");
        request.setParameter("titleId", titleId);
        request.setParameter("frontMatterTitle.bookNameText", "title name");
        request.setParameter("frontMatterTitle.sequenceNum", "1");
        request.setParameter("copyright", "Somethings");
        request.setParameter("materialId", "123456789012345678");
        request.setParameter("sourceType", "TOC");
        request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
        request.setParameter("docCollectionName", "sdfdsfdsf");
        request.setParameter("tocCollectionName", "sdfdsfdsf");
        request.setParameter("isbn", "978-193-5-18235-1");
        request.setParameter("isComplete", "true");
        request.setParameter("groupsEnabled", "false");
        request.setParameter("validateForm", "false");
        request.setParameter("bucket", Bucket.BOOKS.toString());

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.replay(mockMiscConfigService);

        EasyMock.expect(keywordTypeCodeSevice.getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_US)).andReturn(subject);
        EasyMock.expect(keywordTypeCodeSevice
                .getKeywordTypeCodeByName(WebConstants.KEY_SUBJECT_MATTER_CANADA))
                .andReturn(subject);
        final BookDefinition expectedBook = createBookDef(titleId);
        EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class)))
            .andReturn(expectedBook);
        setupMockServices(null, 1, true);

        mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
        EasyMock.replay(mockAuditService);

        setupDropdownMenuAndKeywords(2);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView TODO: fix
            // View view = mav.getView();
            // assertEquals(RedirectView.class, view.getClass());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockAuditService);
        EasyMock.verify(mockBookDefinitionService);
    }

    @Test
    public void testUploadPdf() {
        JUnitCore.runClasses(AdditionalTests.class);
    }

    private void checkInitialValuesDynamicContentForPublished(final Map<String, Object> model) {
        final boolean isPublished = Boolean.parseBoolean(model.get(WebConstants.KEY_IS_PUBLISHED).toString());
        assertEquals(false, isPublished);

        checkInitialValuesDynamicContent(model);
    }

    private void checkInitialValuesDynamicContent(final Map<String, Object> model) {
        final int numAuthors = Integer.valueOf(model.get(WebConstants.KEY_NUMBER_OF_AUTHORS).toString());
        final int splitDocs = Integer.valueOf(model.get(WebConstants.KEY_NUMBER_OF_SPLIT_DOCUMENTS).toString());
        assertEquals(0, numAuthors);
        assertEquals(0, splitDocs);
        assertNotNull(model.get(WebConstants.KEY_FORM));
        checkMinPublicationCutoffDate((String) model.get(WebConstants.KEY_MIN_PUBLICATION_CUTOFF_DATE));
    }

    private void checkMinPublicationCutoffDate(final String minPublicationCutoffDate) {
        assertNotNull(minPublicationCutoffDate);
        LocalDate expected = LocalDate.now().plusDays(1);
        LocalDate actual = LocalDate.parse(minPublicationCutoffDate, CoreConstants.DATE_FORMATTER);
        assertEquals(expected, actual);
    }

    private void setupMockServices(final BookDefinition book, final int times, final boolean isComplete) {
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class)))
            .andReturn(book)
            .times(times);
        EasyMock.replay(mockBookDefinitionService);

        final DocumentTypeCode code = new DocumentTypeCode();
        code.setId(Long.parseLong("1"));
        code.setAbbreviation("an");
        code.setName("Analytical");
        EasyMock.expect(mockDocumentTypeCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class)))
            .andReturn(code);
        if (isComplete) {
            EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        }
        EasyMock.replay(keywordTypeCodeSevice);
        EasyMock.replay(mockDocumentTypeCodeService);
    }

    private BookDefinition createBookDef(final String fullyQualifiedTitleId) {
        final BookDefinition book = new BookDefinition();
        book.setEbookDefinitionId(BOOK_DEFINITION_ID);
        book.setFullyQualifiedTitleId(fullyQualifiedTitleId);
        book.setDocumentTypeCodes(documentTypeCode);
        book.setPublisherCodes(publisherCode);
        final List<EbookName> names = new ArrayList<>();
        names.add(bookName);
        book.setEbookNames(names);
        book.setCopyright("something");
        book.setSourceType(SourceType.NORT);
        book.setIsDeletedFlag(false);
        book.setEbookDefinitionCompleteFlag(false);
        book.setAutoUpdateSupportFlag(true);
        book.setSearchIndexFlag(true);
        book.setPublishedOnceFlag(false);
        book.setOnePassSsoLinkFlag(true);
        book.setKeyciteToplineFlag(true);
        book.setIsAuthorDisplayVertical(true);
        book.setEnableCopyFeatureFlag(false);
        book.setIsSplitBook(false);
        book.setIsSplitTypeAuto(true);
        book.setPrintSetNumber("1234");
        book.setPrintPageNumbers(true);
        return book;
    }

    private void setupDropdownMenuAndKeywords(final int keywordCodeTimes) {
        EasyMock.expect(mockEditBookDefinitionService.getStates()).andReturn(null);
        EasyMock.expect(mockEditBookDefinitionService.getDocumentTypes()).andReturn(null);
        EasyMock.expect(mockEditBookDefinitionService.getDocumentTypesByPublishers())
                .andReturn(documentTypesByPublishers);
        EasyMock.expect(mockEditBookDefinitionService.getJurisdictions()).andReturn(null);
        EasyMock.expect(mockEditBookDefinitionService.getKeywordCodes())
            .andReturn(new ArrayList<>())
            .times(keywordCodeTimes);
        EasyMock.expect(mockEditBookDefinitionService.getPublishers()).andReturn(null);
        EasyMock.expect(mockEditBookDefinitionService.getPubTypes()).andReturn(null);
        EasyMock.expect(mockEditBookDefinitionService.getBuckets()).andReturn(buckets);
        EasyMock.replay(mockEditBookDefinitionService);
    }

    @SneakyThrows
    private void assertionsAfterSuccessfulResponse() {
        Assert.assertEquals(EXPECTED_HTML, response.getContentAsString());
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
        EasyMock.verify(mockFrontMatterPreviewService);
    }

    @SneakyThrows
    private void initCreateFrontMatterService() {
        EasyMock.expect(mockFrontMatterPreviewService
                .getAdditionalFrontPagePreview(getBookDefinitionForFmPreview(), Long.valueOf(SELECTED_FRONT_MATTER_PREVIEW_PAGE)))
                .andReturn(EXPECTED_HTML);
        EasyMock.replay(mockFrontMatterPreviewService);
    }

    @NotNull
    private BookDefinition getBookDefinitionForFmPreview() {
        final BookDefinition expectedBookDefinition = new BookDefinition();
        final PublisherCode publisherCode = new PublisherCode();
        publisherCode.setName(USCL_PUBLISHER_NAME);
        expectedBookDefinition.setPublisherCodes(publisherCode);
        final FrontMatterPage frontMatterPage = new FrontMatterPage();
        frontMatterPage.setSequenceNum(Integer.valueOf(SEQUENCE_NUM_1));
        frontMatterPage.setPageTocLabel(TOC_LABEL_1);
        final FrontMatterSection frontMatterSection = new FrontMatterSection();
        frontMatterSection.setSequenceNum(Integer.valueOf(SEQUENCE_NUM_1));
        frontMatterSection.setSectionHeading(SECTION_HEADING_1);
        frontMatterSection.setSectionText(SECTION_TEXT_1);
        final FrontMatterPdf frontMatterPdf = new FrontMatterPdf();
        frontMatterPdf.setSequenceNum(Integer.valueOf(SEQUENCE_NUM_1));
        frontMatterPdf.setPdfLinkText(PDF_LINK_TEXT_1);
        frontMatterPdf.setPdfFilename(PDF_FILENAME_1);
        frontMatterSection.setPdfs(Collections.singletonList(frontMatterPdf));
        frontMatterPage.setFrontMatterSections(Collections.singletonList(frontMatterSection));
        expectedBookDefinition.setFrontMatterPages(Collections.singletonList(frontMatterPage));
        return expectedBookDefinition;
    }

    @Configuration
    @Import(TestConfig.class)
    public static class Config {
        @Bean
        public String environmentName() {
            return CI_CONTENT;
        }
    }

    @RunWith(PowerMockRunner.class)
    @PowerMockIgnore("javax.management.*")
    @PrepareForTest(EditBookDefinitionController.class)
    public static class AdditionalTests {
        private static final String WRONG_PDF_FILE_EXTENSION_ERROR_MESSAGE = "Please upload file of type PDF";
        private static final String CHARACTERS_NOT_ALLOWED_ERROR_MESSAGE
                = "PDF name contains forbidden characters. Allowed characters are: A-Z, a-z, 0-9, _, -, !";
        private static String PDF_FILE_NAME = "fileName.pdf";
        private static String PDF_FILE_NAME_WITH_SPECIAL_CHARACTERS = "fileName_$%#@.pdf";
        private static String IMG_NAME = "picture.img";

        @InjectMocks
        private EditBookDefinitionController editBookDefinitionController;
        @Mock
        private File rootDir;
        @Mock
        private File file;
        @Mock
        private NasFileSystem nasFileSystem;
        @Mock
        private PdfFileNameValidator pdfFileNameValidator;
        private MockMultipartFile multipartFile;

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(editBookDefinitionController);
            multipartFile = new MockMultipartFile(PDF_FILE_NAME, new byte[]{});
        }

        @SneakyThrows
        @Test
        public void testUploadPdf() {
            when(pdfFileNameValidator.isFileExtensionNotPdf(Mockito.anyString())).thenReturn(false);
            when(pdfFileNameValidator.isFileNameContainsForbiddenCharacters(Mockito.anyString())).thenReturn(false);
            PowerMockito.when(nasFileSystem.getFrontMatterUsclPdfDirectory()).thenReturn(rootDir);
            PowerMockito.whenNew(File.class).withArguments(rootDir, PDF_FILE_NAME)
                    .thenReturn(file);

            editBookDefinitionController.uploadPdf(multipartFile, PDF_FILE_NAME, CoreConstants.USCL_PUBLISHER_NAME);

            PowerMockito.verifyNew(File.class).withArguments(rootDir, PDF_FILE_NAME);
        }

        @SneakyThrows
        @Test
        public void testUploadCwPdf() {
            when(pdfFileNameValidator.isFileExtensionNotPdf(Mockito.anyString())).thenReturn(false);
            when(pdfFileNameValidator.isFileNameContainsForbiddenCharacters(Mockito.anyString())).thenReturn(false);
            PowerMockito.when(nasFileSystem.getFrontMatterCwPdfDirectory()).thenReturn(rootDir);
            PowerMockito.whenNew(File.class).withArguments(rootDir, PDF_FILE_NAME)
                    .thenReturn(file);

            editBookDefinitionController.uploadPdf(multipartFile, PDF_FILE_NAME, CoreConstants.CW_PUBLISHER_NAME);

            PowerMockito.verifyNew(File.class).withArguments(rootDir, PDF_FILE_NAME);
        }

        @Test
        public void testUploadPdfWrongFileExtension() {
            when(pdfFileNameValidator.isFileExtensionNotPdf(Mockito.anyString())).thenReturn(true);
            when(pdfFileNameValidator.getWrongPdfFileExtensionErrorMessage()).thenReturn(WRONG_PDF_FILE_EXTENSION_ERROR_MESSAGE);
            ResponseEntity<?> response = editBookDefinitionController.uploadPdf(multipartFile, IMG_NAME,
                    CoreConstants.USCL_PUBLISHER_NAME);

            assertEquals(WRONG_PDF_FILE_EXTENSION_ERROR_MESSAGE, response.getBody());
        }

        @Test
        public void testUploadPdfSpecialCharacters() {
            when(pdfFileNameValidator.isFileExtensionNotPdf(Mockito.anyString())).thenReturn(false);
            when(pdfFileNameValidator.isFileNameContainsForbiddenCharacters(Mockito.anyString())).thenReturn(true);
            when(pdfFileNameValidator.getForbiddenCharactersErrorMessage()).thenReturn(CHARACTERS_NOT_ALLOWED_ERROR_MESSAGE);
            ResponseEntity<?> response = editBookDefinitionController.uploadPdf(multipartFile,
                    PDF_FILE_NAME_WITH_SPECIAL_CHARACTERS, CoreConstants.USCL_PUBLISHER_NAME);

            assertEquals(CHARACTERS_NOT_ALLOWED_ERROR_MESSAGE, response.getBody());
        }
    }
}
