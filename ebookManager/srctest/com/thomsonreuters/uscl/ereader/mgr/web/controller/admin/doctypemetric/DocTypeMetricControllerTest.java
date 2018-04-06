package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.doctypemetric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
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

public final class DocTypeMetricControllerTest {
    private static final String BINDING_RESULT_KEY = BindingResult.class.getName() + "." + DocTypeMetricForm.FORM_NAME;
    private static final DocumentTypeCode DOCTYPE_CODE = new DocumentTypeCode();
    private static final Long DOCTYPE_CODE_ID = 1L;
    private DocTypeMetricController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private DocumentTypeCodeService mockDocumentTypeCodeService;
    private DocTypeMetricFormValidator validator;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up the Code service
        mockDocumentTypeCodeService = EasyMock.createMock(DocumentTypeCodeService.class);

        validator = new DocTypeMetricFormValidator();
        // Set up the controller
        controller = new DocTypeMetricController(mockDocumentTypeCodeService, validator);

        DOCTYPE_CODE.setId(DOCTYPE_CODE_ID);
        DOCTYPE_CODE.setName("test");
        DOCTYPE_CODE.setThresholdValue(Integer.valueOf(7500));
        DOCTYPE_CODE.setThresholdPercent(Integer.valueOf(10));
    }

    /**
     * Test the GET to the List page
     */
    @Test
    public void testViewDocTypeList() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_DOCTYPE_METRIC_VIEW);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(mockDocumentTypeCodeService.getAllDocumentTypeCodes()).andReturn(new ArrayList<DocumentTypeCode>());
        EasyMock.replay(mockDocumentTypeCodeService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_VIEW, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final List<DocumentTypeCode> codes = (List<DocumentTypeCode>) model.get(WebConstants.KEY_DOC_TYPE_CODE);
            assertEquals(0, codes.size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockDocumentTypeCodeService);
    }

    /**
     * Test the GET to the Edit Page
     */
    @Test
    public void testEditDocTypeGet() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_DOCTYPE_METRIC_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", DOCTYPE_CODE_ID.toString());

        EasyMock.expect(mockDocumentTypeCodeService.getDocumentTypeCodeById(DOCTYPE_CODE_ID)).andReturn(DOCTYPE_CODE);
        EasyMock.replay(mockDocumentTypeCodeService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final DocumentTypeCode actual = (DocumentTypeCode) model.get(WebConstants.KEY_DOC_TYPE_CODE);

            Assert.assertEquals(DOCTYPE_CODE, actual);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST to the Edit Page Success
     */
    @Test
    public void testEditDocTypeCodePost() {
        final String name = DOCTYPE_CODE.getName();
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_DOCTYPE_METRIC_EDIT);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("id", DOCTYPE_CODE_ID.toString());
        request.setParameter("name", name);
        request.setParameter("thresholdValue", "10");
        request.setParameter("thresholdPercent", "10");

        EasyMock.expect(mockDocumentTypeCodeService.getDocumentTypeCodeById(DOCTYPE_CODE_ID)).andReturn(DOCTYPE_CODE);
        mockDocumentTypeCodeService.saveDocumentTypeCode(DOCTYPE_CODE);
        EasyMock.replay(mockDocumentTypeCodeService);

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
        EasyMock.verify(mockDocumentTypeCodeService);
    }

    /**
     * Test the POST to the Edit Page Fail
     */
    @Test
    public void testEditKeywordCodePostFail() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_DOCTYPE_METRIC_EDIT);
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            Assert.assertTrue(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
