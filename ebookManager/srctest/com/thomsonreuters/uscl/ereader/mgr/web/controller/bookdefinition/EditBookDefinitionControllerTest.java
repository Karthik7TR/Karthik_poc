package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionFormValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.book.BookDefinitionLockService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

public final class EditBookDefinitionControllerTest {
    private static final String BINDING_RESULT_KEY =
        BindingResult.class.getName() + "." + EditBookDefinitionForm.FORM_NAME;
    private static final long BOOK_DEFINITION_ID = 1;
    private static List<KeywordTypeCode> KEYWORD_CODES = new ArrayList<>();

    private BookDefinitionLock bookDefinitionLock;
    private EditBookDefinitionController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private BookDefinitionService mockBookDefinitionService;
    private CodeService mockCodeService;
    private DocumentTypeCodeService mockDocumentTypeCodeService;
    private JobRequestService mockJobRequestService;
    private EditBookDefinitionService mockEditBookDefinitionService;
    private EBookAuditService mockAuditService;
    private BookDefinitionLockService mockLockService;
    private EditBookDefinitionFormValidator validator;
    private MiscConfigSyncService mockMiscConfigService;

    private EbookName bookName;
    private DocumentTypeCode documentTypeCode;
    private PublisherCode publisherCode;

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up the services
        mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
        mockCodeService = EasyMock.createMock(CodeService.class);
        mockDocumentTypeCodeService = EasyMock.createMock(DocumentTypeCodeService.class);
        mockEditBookDefinitionService = EasyMock.createMock(EditBookDefinitionService.class);
        mockJobRequestService = EasyMock.createMock(JobRequestService.class);
        mockAuditService = EasyMock.createMock(EBookAuditService.class);
        mockLockService = EasyMock.createMock(BookDefinitionLockService.class);
        mockMiscConfigService = EasyMock.createMock(MiscConfigSyncService.class);

        final List<String> frontMatterThemes = new ArrayList<>();
        frontMatterThemes.add("WestLaw Next");

        EasyMock.expect(mockEditBookDefinitionService.getFrontMatterThemes()).andReturn(frontMatterThemes);
        validator = new EditBookDefinitionFormValidator(
            mockBookDefinitionService,
            mockCodeService,
            mockDocumentTypeCodeService,
            "workstation",
            null);

        // Set up the controller
        controller = new EditBookDefinitionController();
        org.springframework.test.util.ReflectionTestUtils
            .setField(controller, "editBookDefinitionService", mockEditBookDefinitionService);
        org.springframework.test.util.ReflectionTestUtils
            .setField(controller, "bookDefinitionService", mockBookDefinitionService);
        org.springframework.test.util.ReflectionTestUtils
            .setField(controller, "jobRequestService", mockJobRequestService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "auditService", mockAuditService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "bookLockService", mockLockService);
        org.springframework.test.util.ReflectionTestUtils
            .setField(controller, "miscConfigService", mockMiscConfigService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "validator", validator);

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
    }

    /**
     * Test the GET to the Create Book Definition page
     */
    @Test
    public void testCreateBookDefintionGet() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_CREATE);
        request.setMethod(HttpMethod.GET.name());

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
        final String titleId = "uscl/an/abcd";
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_CREATE);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("contentTypeId", "1");
        request.setParameter("pubAbbr", "abcd");
        request.setParameter("publisher", "uscl");
        request.setParameter("titleId", titleId);
        request.setParameter("groupsEnabled", "false");

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
            Assert.assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockCodeService);
        EasyMock.verify(mockAuditService);
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
        request.setParameter("titleId", "uscl/an/abcd");
        request.setParameter("isComplete", "true");

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
        EasyMock.verify(mockCodeService);
        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockMiscConfigService);
    }

    /**
     * Test the POST to the Create Book Definition page when titleId is complete
     * and Definition in incomplete state
     */
    @Test
    public void testCreateBookDefintionPostCompleteStateSuccess() {
        final String titleId = "uscl/an/abcd";
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
            Assert.assertFalse(bindingResult.hasErrors());
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
    public void testEditBookDefintionGet() {
        final String fullyQualifiedTitleId = "uscl/an/abcd";
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_EDIT);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);

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
        EasyMock.expect(mockLockService.findBookLockByBookDefinition(book)).andReturn(null);
        mockLockService.lockBookDefinition(book, null, null);
        EasyMock.replay(mockLockService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            checkInitialValuesDynamicContentForPublished(model);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockJobRequestService);
        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockLockService);
        EasyMock.verify(mockMiscConfigService);
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
        final String fullyQualifiedTitleId = "uscl/an/abcd";
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_EDIT);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);

        setupDropdownMenuAndKeywords(2);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        EasyMock.expect(mockLockService.findBookLockByBookDefinition(book)).andReturn(bookDefinitionLock);
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
        final String fullyQualifiedTitleId = "uscl/an/abcd";
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
        request.setMethod(HttpMethod.POST.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);
        EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class)))
            .andReturn(book);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID))
            .andReturn(book)
            .times(2);
        EasyMock.replay(mockBookDefinitionService);

        EasyMock.expect(mockLockService.findBookLockByBookDefinition(book)).andReturn(null);
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
        EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
        EasyMock.replay(mockCodeService);
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
            Assert.assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockAuditService);
        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockCodeService);
        EasyMock.verify(mockLockService);
        EasyMock.verify(mockMiscConfigService);
    }

    /**
     * Test the POST to the Edit Book Definition Saving in Complete state with
     * no name line
     */
    @Test
    public void testEditBookDefintionPOSTFailed() {
        final String fullyQualifiedTitleId = "uscl/an/abcd";
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
        EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
        EasyMock.replay(mockCodeService);
        EasyMock.replay(mockDocumentTypeCodeService);

        EasyMock.expect(mockJobRequestService.isBookInJobRequest(BOOK_DEFINITION_ID)).andReturn(false);
        EasyMock.replay(mockJobRequestService);

        EasyMock.expect(mockLockService.findBookLockByBookDefinition(book)).andReturn(null);
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
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockCodeService);
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
        final String fullyQualifiedTitleId = "uscl/an/abcd";
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
        EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
        EasyMock.replay(mockCodeService);
        EasyMock.replay(mockDocumentTypeCodeService);

        EasyMock.expect(mockLockService.findBookLockByBookDefinition(book)).andReturn(bookDefinitionLock);
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
            Assert.assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockLockService);
        EasyMock.verify(mockCodeService);
    }

    /**
     * Test the GET to the Copy Book Definition page
     */
    @Test
    public void testCopyBookDefintionGet() {
        final String fullyQualifiedTitleId = "uscl/an/abcd";
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_COPY);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        final MiscConfig miscConfig = new MiscConfig();
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.replay(mockMiscConfigService);

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
        final String titleId = "uscl/an/abcd";
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_COPY);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("contentTypeId", "1");
        request.setParameter("pubAbbr", "abcd");
        request.setParameter("publisher", "uscl");
        request.setParameter("titleId", titleId);
        request.setParameter("groupsEnabled", "false");

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
            Assert.assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockAuditService);
        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockCodeService);
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
        request.setParameter("titleId", "uscl/an/abcd");
        request.setParameter("isComplete", "true");

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
        EasyMock.verify(mockCodeService);
        EasyMock.verify(mockEditBookDefinitionService);
        EasyMock.verify(mockMiscConfigService);
    }

    /**
     * Test the POST to the Copy Book Definition page when titleId is complete
     * and Definition in incomplete state
     */
    @Test
    public void testCopyBookDefintionPostCompleteStateSuccess() {
        final String titleId = "uscl/an/abcd";
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
            Assert.assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockAuditService);
        EasyMock.verify(mockBookDefinitionService);
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
            EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
        }
        EasyMock.replay(mockCodeService);
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
        return book;
    }

    private void setupDropdownMenuAndKeywords(final int keywordCodeTimes) {
        EasyMock.expect(mockEditBookDefinitionService.getStates()).andReturn(null);
        EasyMock.expect(mockEditBookDefinitionService.getDocumentTypes()).andReturn(null);
        EasyMock.expect(mockEditBookDefinitionService.getJurisdictions()).andReturn(null);
        EasyMock.expect(mockEditBookDefinitionService.getKeywordCodes())
            .andReturn(new ArrayList<KeywordTypeCode>())
            .times(keywordCodeTimes);
        EasyMock.expect(mockEditBookDefinitionService.getPublishers()).andReturn(null);
        EasyMock.expect(mockEditBookDefinitionService.getPubTypes()).andReturn(null);
        EasyMock.replay(mockEditBookDefinitionService);
    }
}
