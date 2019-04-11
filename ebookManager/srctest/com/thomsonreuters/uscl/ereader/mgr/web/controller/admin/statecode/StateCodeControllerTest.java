package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCodeService;
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

public final class StateCodeControllerTest {
    private static final String BINDING_RESULT_KEY = BindingResult.class.getName() + "." + StateCodeForm.FORM_NAME;
    private static final StateCode STATE_CODE = new StateCode();
    private static final Long STATE_ID = 1L;
    private StateCodeController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private StateCodeService mockStateCodeService;
    private StateCodeFormValidator validator;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up the Code service
        mockStateCodeService = EasyMock.createMock(StateCodeService.class);
        validator = new StateCodeFormValidator(mockStateCodeService);

        // Set up the controller
        controller = new StateCodeController(mockStateCodeService, validator);

        STATE_CODE.setId(STATE_ID);
        STATE_CODE.setName("test");
    }

    /**
     * Test the GET to the List page
     */
    @Test
    public void testViewStateCodeList() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STATE_CODE_VIEW);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(mockStateCodeService.getAllStateCodes()).andReturn(new ArrayList<StateCode>());
        EasyMock.replay(mockStateCodeService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_STATE_CODE_VIEW, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final List<StateCode> codes = (List<StateCode>) model.get(WebConstants.KEY_STATE_CODE);
            assertEquals(0, codes.size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockStateCodeService);
    }

    /**
     * Test the GET to the Create Page
     */
    @Test
    public void testCreateStateCodeGet() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STATE_CODE_CREATE);
        request.setMethod(HttpMethod.GET.name());
        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_STATE_CODE_CREATE, mav.getViewName());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST to the Create Page Success
     */
    @Test
    public void testCreateStateCodePost() {
        final String name = "test";
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STATE_CODE_CREATE);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("name", name);

        final StateCode code = new StateCode();
        code.setName(name);

        EasyMock.expect(mockStateCodeService.getStateCodeByName(name)).andReturn(null);
        mockStateCodeService.saveStateCode(code);
        EasyMock.replay(mockStateCodeService);

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
        EasyMock.verify(mockStateCodeService);
    }

    /**
     * Test the POST to the Create Page Fail
     */
    @Test
    public void testCreateStateCodePostFail() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STATE_CODE_CREATE);
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_STATE_CODE_CREATE, mav.getViewName());

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
    public void testEditStateCodeGet() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STATE_CODE_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", STATE_ID.toString());

        EasyMock.expect(mockStateCodeService.getStateCodeById(STATE_ID)).andReturn(STATE_CODE);
        EasyMock.replay(mockStateCodeService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_STATE_CODE_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final StateCode actual = (StateCode) model.get(WebConstants.KEY_STATE_CODE);

            Assert.assertEquals(STATE_CODE, actual);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST to the Edit Page Success
     */
    @Test
    public void testEditStateCodePost() {
        final String name = STATE_CODE.getName();
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STATE_CODE_EDIT);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("stateId", STATE_ID.toString());
        request.setParameter("name", name);

        EasyMock.expect(mockStateCodeService.getStateCodeByName(name)).andReturn(null);
        mockStateCodeService.saveStateCode(STATE_CODE);
        EasyMock.replay(mockStateCodeService);

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
        EasyMock.verify(mockStateCodeService);
    }

    /**
     * Test the POST to the Edit Page Fail
     */
    @Test
    public void testEditStateCodePostFail() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STATE_CODE_EDIT);
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_STATE_CODE_EDIT, mav.getViewName());

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
    public void testEditStateCodeGetException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STATE_CODE_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", "1");
        EasyMock.expect(mockStateCodeService.getStateCodeById(EasyMock.anyLong())).andThrow(new IllegalArgumentException());
        EasyMock.replay(mockStateCodeService);

        handlerAdapter.handle(request, response, controller);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteStateCodeGetException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STATE_CODE_DELETE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", "1");
        EasyMock.expect(mockStateCodeService.getStateCodeById(EasyMock.anyLong())).andThrow(new IllegalArgumentException());
        EasyMock.replay(mockStateCodeService);

        handlerAdapter.handle(request, response, controller);
    }
}
