package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
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

public final class KeywordCodeControllerTest {
    private static final String BINDING_RESULT_KEY = BindingResult.class.getName() + "." + KeywordCodeForm.FORM_NAME;
    private static final KeywordTypeCode KEYWORD_CODE = new KeywordTypeCode();
    private static final Long KEYWORD_CODE_ID = 1L;
    private KeywordCodeController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private KeywordTypeCodeSevice keywordTypeCodeSevice;
    private KeywordCodeFormValidator validator;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up the Code service
        keywordTypeCodeSevice = EasyMock.createMock(KeywordTypeCodeSevice.class);
        validator = new KeywordCodeFormValidator(keywordTypeCodeSevice);

        // Set up the controller
        controller = new KeywordCodeController(keywordTypeCodeSevice, null, validator);

        KEYWORD_CODE.setId(KEYWORD_CODE_ID);
        KEYWORD_CODE.setName("test");
        KEYWORD_CODE.setIsRequired(false);
    }

    /**
     * Test the GET to the List page
     */
    @Test
    public void testViewKeywordCodeList() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
        EasyMock.replay(keywordTypeCodeSevice);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_KEYWORD_CODE_VIEW, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final List<KeywordTypeCode> codes = (List<KeywordTypeCode>) model.get(WebConstants.KEY_KEYWORD_TYPE_CODE);
            assertEquals(0, codes.size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(keywordTypeCodeSevice);
    }

    /**
     * Test the GET to the Create Page
     */
    @Test
    public void testCreateKeywordCodeGet() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_KEYWORD_CODE_CREATE);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_KEYWORD_CODE_CREATE, mav.getViewName());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST to the Create Page Success
     */
    @Test
    public void testCreateKeywordCodePost() {
        final String name = "test";
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_KEYWORD_CODE_CREATE);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("name", name);

        final KeywordTypeCode code = new KeywordTypeCode();
        code.setName(name);

        EasyMock.expect(keywordTypeCodeSevice.getKeywordTypeCodeByName(name)).andReturn(null);
        keywordTypeCodeSevice.saveKeywordTypeCode(code);
        EasyMock.replay(keywordTypeCodeSevice);

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
        EasyMock.verify(keywordTypeCodeSevice);
    }

    /**
     * Test the POST to the Create Page Fail
     */
    @Test
    public void testCreateKeywordCodePostFail() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_KEYWORD_CODE_CREATE);
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_KEYWORD_CODE_CREATE, mav.getViewName());

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

    /**
     * Test the GET to the Edit Page
     */
    @Test
    public void testEditKeywordCodeGet() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_KEYWORD_CODE_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", KEYWORD_CODE_ID.toString());

        EasyMock.expect(keywordTypeCodeSevice.getKeywordTypeCodeById(KEYWORD_CODE_ID)).andReturn(KEYWORD_CODE);
        EasyMock.replay(keywordTypeCodeSevice);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_KEYWORD_CODE_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final KeywordTypeCode actual = (KeywordTypeCode) model.get(WebConstants.KEY_KEYWORD_TYPE_CODE);

            Assert.assertEquals(KEYWORD_CODE, actual);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST to the Edit Page Success
     */
    @Test
    public void testEditKeywordCodePost() {
        final String name = KEYWORD_CODE.getName();
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_KEYWORD_CODE_EDIT);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("codeId", KEYWORD_CODE_ID.toString());
        request.setParameter("name", name);

        EasyMock.expect(keywordTypeCodeSevice.getKeywordTypeCodeByName(name)).andReturn(null);
        keywordTypeCodeSevice.saveKeywordTypeCode(KEYWORD_CODE);
        EasyMock.replay(keywordTypeCodeSevice);

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
        EasyMock.verify(keywordTypeCodeSevice);
    }

    /**
     * Test the POST to the Edit Page Fail
     */
    @Test
    public void testEditKeywordCodePostFail() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_KEYWORD_CODE_EDIT);
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_KEYWORD_CODE_EDIT, mav.getViewName());

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

    @Test(expected = IllegalArgumentException.class)
    public void testEditKeywordCodeGetException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_KEYWORD_CODE_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", "1");
        EasyMock.expect(keywordTypeCodeSevice.getKeywordTypeCodeById(EasyMock.anyLong())).andThrow(new IllegalArgumentException());
        EasyMock.replay(keywordTypeCodeSevice);

        handlerAdapter.handle(request, response, controller);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteKeywordCodeGetException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_KEYWORD_CODE_DELETE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", "1");
        EasyMock.expect(keywordTypeCodeSevice.getKeywordTypeCodeById(EasyMock.anyLong())).andThrow(new IllegalArgumentException());
        EasyMock.replay(keywordTypeCodeSevice);

        handlerAdapter.handle(request, response, controller);
    }
}
