package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionLockService;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete.DeleteBookDefinitionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete.DeleteBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete.DeleteBookDefinitionFormValidator;
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

public final class DeleteBookDefinitionControllerTest {
    private static final String BINDING_RESULT_KEY =
        BindingResult.class.getName() + "." + DeleteBookDefinitionForm.FORM_NAME;
    private BookDefinition bookDefinition = new BookDefinition();
    private static final Long BOOK_DEFINITION_ID = 1L;
    private BookDefinitionLock bookDefinitionLock;
    private DeleteBookDefinitionController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    private BookDefinitionService mockBookDefinitionService;
    private JobRequestService mockJobRequestService;
    private EBookAuditService mockAuditService;
    private BookDefinitionLockService mockLockService;

    private DeleteBookDefinitionFormValidator validator;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up the dashboard service
        mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
        mockJobRequestService = EasyMock.createMock(JobRequestService.class);
        mockAuditService = EasyMock.createMock(EBookAuditService.class);
        mockLockService = EasyMock.createMock(BookDefinitionLockService.class);
        validator = new DeleteBookDefinitionFormValidator(mockJobRequestService, mockLockService);

        // Set up the controller
        controller = new DeleteBookDefinitionController(
            mockBookDefinitionService,
            mockAuditService,
            mockJobRequestService,
            mockLockService,
            validator);

        bookDefinitionLock = new BookDefinitionLock();
        bookDefinitionLock.setCheckoutTimestamp(new Date());
        bookDefinitionLock.setEbookDefinitionLockId(1L);
        bookDefinitionLock.setFullName("name");
        bookDefinitionLock.setUsername("username");

        bookDefinition.setEbookDefinitionId(BOOK_DEFINITION_ID);
        bookDefinition.setEbookDefinitionId(BOOK_DEFINITION_ID);
        bookDefinition.setFullyQualifiedTitleId("something");
        bookDefinition.setCopyright("something");
        bookDefinition.setSourceType(SourceType.NORT);
        bookDefinition.setIsDeletedFlag(false);
        bookDefinition.setEbookDefinitionCompleteFlag(false);
        bookDefinition.setAutoUpdateSupportFlag(true);
        bookDefinition.setSearchIndexFlag(true);
        bookDefinition.setPublishedOnceFlag(false);
        bookDefinition.setOnePassSsoLinkFlag(true);
        bookDefinition.setKeyciteToplineFlag(true);
        bookDefinition.setIsAuthorDisplayVertical(true);
        bookDefinition.setEnableCopyFeatureFlag(false);
        bookDefinition.setDocumentTypeCodes(new DocumentTypeCode());
        bookDefinition.setPublisherCodes(new PublisherCode());
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setIsSplitTypeAuto(true);
    }

    /**
     * Test the GET to the Delete Book Definition page
     */
    @Test
    public void testDeleteBookDefintionGet() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_DELETE);
        request.setParameter("id", BOOK_DEFINITION_ID.toString());
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID))
            .andReturn(bookDefinition);
        EasyMock.expect(mockJobRequestService.isBookInJobRequest(BOOK_DEFINITION_ID)).andReturn(false);
        EasyMock.expect(mockLockService.findBookLockByBookDefinition(bookDefinition)).andReturn(null);
        EasyMock.replay(mockBookDefinitionService);
        EasyMock.replay(mockJobRequestService);
        EasyMock.replay(mockLockService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_DELETE, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            final boolean isInQueue = Boolean.valueOf(model.get(WebConstants.KEY_IS_IN_JOB_REQUEST).toString());
            final BookDefinitionLock lock = (BookDefinitionLock) model.get(WebConstants.KEY_BOOK_DEFINITION_LOCK);

            Assert.assertEquals(false, isInQueue);
            Assert.assertEquals(lock, null);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockJobRequestService);
        EasyMock.verify(mockLockService);
    }

    /**
     * Test the POST to the Delete Book Definition page when there are validation errors
     */
    @Test
    public void testDeleteBookDefintionPostFailed() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_DELETE);
        request.setParameter("id", BOOK_DEFINITION_ID.toString());
        request.setParameter("Action", DeleteBookDefinitionForm.Action.DELETE.toString());
        request.setMethod(HttpMethod.POST.name());

        EasyMock.expect(mockJobRequestService.findJobRequestByBookDefinitionId(BOOK_DEFINITION_ID)).andReturn(null);
        EasyMock.expect(mockLockService.findBookLockByBookDefinition(bookDefinition)).andReturn(null);
        EasyMock.replay(mockJobRequestService);
        EasyMock.replay(mockLockService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_DELETE, mav.getViewName());

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

        EasyMock.verify(mockJobRequestService);
        EasyMock.verify(mockLockService);
    }

    /**
     * Test the POST to the Delete Book Definition page when success
     * Permanent delete from database.
     */
    @Test
    public void testDeleteBookDefintionPostSuccessPermanent() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_DELETE);
        request.setParameter("id", BOOK_DEFINITION_ID.toString());
        request.setParameter("comment", "Delete");
        request.setParameter("code", "DELETE BOOK");
        request.setParameter("Action", DeleteBookDefinitionForm.Action.DELETE.toString());
        request.setMethod(HttpMethod.POST.name());

        bookDefinition.setPublishedOnceFlag(false);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID))
            .andReturn(bookDefinition);
        mockBookDefinitionService.removeBookDefinition(BOOK_DEFINITION_ID);
        EasyMock.expect(mockJobRequestService.findJobRequestByBookDefinitionId(BOOK_DEFINITION_ID)).andReturn(null);
        EasyMock.expect(mockLockService.findBookLockByBookDefinition(bookDefinition)).andReturn(null);
        EasyMock.replay(mockBookDefinitionService);
        EasyMock.replay(mockJobRequestService);
        EasyMock.replay(mockLockService);

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
        EasyMock.verify(mockJobRequestService);
        EasyMock.verify(mockLockService);
    }

    /**
     * Test the POST to the Delete Book Definition page when success
     * Soft delete.
     */
    @Test
    public void testDeleteBookDefintionPostSuccessSoft() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_DELETE);
        request.setParameter("id", BOOK_DEFINITION_ID.toString());
        request.setParameter("comment", "Delete");
        request.setParameter("code", "DELETE BOOK");
        request.setParameter("Action", DeleteBookDefinitionForm.Action.DELETE.toString());
        request.setMethod(HttpMethod.POST.name());

        bookDefinition.setPublishedOnceFlag(true);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID))
            .andReturn(bookDefinition);
        mockBookDefinitionService.updateDeletedStatus(BOOK_DEFINITION_ID, true);
        EasyMock.expect(mockJobRequestService.findJobRequestByBookDefinitionId(BOOK_DEFINITION_ID)).andReturn(null);
        EasyMock.expect(mockLockService.findBookLockByBookDefinition(bookDefinition)).andReturn(null);
        EasyMock.replay(mockBookDefinitionService);
        EasyMock.replay(mockJobRequestService);
        EasyMock.replay(mockLockService);

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
        EasyMock.verify(mockJobRequestService);
        EasyMock.verify(mockLockService);
    }

    /**
     * Test the GET to the Restore Book Definition page
     */
    @Test
    public void testRestoreBookDefintionGet() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_RESTORE);
        request.setParameter("id", BOOK_DEFINITION_ID.toString());
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID))
            .andReturn(bookDefinition);
        EasyMock.replay(mockBookDefinitionService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_RESTORE, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            final BookDefinition book = (BookDefinition) model.get(WebConstants.KEY_BOOK_DEFINITION);

            Assert.assertEquals(bookDefinition, book);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test the POST to the Restore Book Definition page when there are validation errors
     */
    @Test
    public void testRestoreBookDefintionPostFailed() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_RESTORE);
        request.setParameter("id", BOOK_DEFINITION_ID.toString());
        request.setParameter("Action", DeleteBookDefinitionForm.Action.RESTORE.toString());
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_DEFINITION_RESTORE, mav.getViewName());

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
    }

    /**
     * Test the POST to the Restore Book Definition page when success
     */
    @Test
    public void testRestoreBookDefintionPostSuccessPermanent() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_RESTORE);
        request.setParameter("id", BOOK_DEFINITION_ID.toString());
        request.setParameter("comment", "Restore");
        request.setParameter("Action", DeleteBookDefinitionForm.Action.RESTORE.toString());
        request.setMethod(HttpMethod.POST.name());

        bookDefinition.setPublishedOnceFlag(false);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID))
            .andReturn(bookDefinition);
        mockBookDefinitionService.updateDeletedStatus(BOOK_DEFINITION_ID, false);
        EasyMock.replay(mockBookDefinitionService);

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
    }
}
