package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.service.support.SupportPageLinkService;
import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;
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

public final class SupportControllerTest {
    private static final String BINDING_RESULT_KEY = BindingResult.class.getName() + "." + SupportForm.FORM_NAME;
    private static final SupportPageLink SUPPORT_PAGE_LINK = new SupportPageLink();
    private static final Long SUPPORT_PAGE_LINK_ID = 1L;
    private SupportController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private SupportPageLinkService mockService;
    private OutageService mockOutageService;
    private SupportFormValidator validator;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up the Code service
        mockService = EasyMock.createMock(SupportPageLinkService.class);
        mockOutageService = EasyMock.createMock(OutageService.class);
        validator = new SupportFormValidator();

        // Set up the controller
        controller = new SupportController(mockService, mockOutageService, validator);

        SUPPORT_PAGE_LINK.setId(SUPPORT_PAGE_LINK_ID);
    }

    /**
     * Test the GET to the List page
     */
    @Test
    public void testViewAdminSupportLinkList() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_SUPPORT_VIEW);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(mockService.findAllSupportPageLink()).andReturn(new ArrayList<SupportPageLink>());
        EasyMock.replay(mockService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_SUPPORT_VIEW, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final List<SupportPageLink> supportPageLink = (List<SupportPageLink>) model.get(WebConstants.KEY_SUPPORT);
            assertEquals(0, supportPageLink.size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockService);
    }

    /**
     * Test the GET to the List page
     */
    @Test
    public void testViewSupportLinkList() {
        request.setRequestURI("/" + WebConstants.MVC_SUPPORT_PAGE_VIEW);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(mockService.findAllSupportPageLink()).andReturn(new ArrayList<SupportPageLink>());
        EasyMock.replay(mockService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_SUPPORT_PAGE_VIEW, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final List<SupportPageLink> supportPageLink = (List<SupportPageLink>) model.get(WebConstants.KEY_SUPPORT);
            assertEquals(0, supportPageLink.size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockService);
    }

    /**
     * Test the GET to the Create Page
     */
    @Test
    public void testCreateSupportPageLinkGet() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_SUPPORT_CREATE);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_SUPPORT_CREATE, mav.getViewName());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST to the Create Page Success
     */
    @Test
    public void testCreateSupportPageLinkPost() {
        final String description = "test";
        final String url = "http://www.google.com";
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_SUPPORT_CREATE);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("linkDescription", description);
        request.setParameter("linkAddress", url);

        final SupportPageLink spl = new SupportPageLink();
        spl.setLinkAddress(url);
        spl.setLinkDescription(description);

        mockService.save(spl);
        EasyMock.replay(mockService);

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
        EasyMock.verify(mockService);
    }

    /**
     * Test the POST to the Create Page Fail
     */
    @Test
    public void testCreateSupportPageLinkPostFail() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_SUPPORT_CREATE);
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_SUPPORT_CREATE, mav.getViewName());

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
    public void testEditSupportPageLinkGet() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_SUPPORT_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", SUPPORT_PAGE_LINK_ID.toString());

        EasyMock.expect(mockService.findByPrimaryKey(SUPPORT_PAGE_LINK_ID)).andReturn(SUPPORT_PAGE_LINK);
        EasyMock.replay(mockService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_SUPPORT_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final SupportPageLink actual = (SupportPageLink) model.get(WebConstants.KEY_SUPPORT);

            Assert.assertEquals(SUPPORT_PAGE_LINK, actual);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST to the Edit Page Success
     */
    @Test
    public void testEditSupportPageLinkPost() {
        final String description = "description";
        final String address = "http://www.google.com";
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_SUPPORT_EDIT);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("supportPageLinkId", SUPPORT_PAGE_LINK_ID.toString());
        request.setParameter("linkAddress", address);
        request.setParameter("linkDescription", description);

        SUPPORT_PAGE_LINK.setLinkAddress(address);
        SUPPORT_PAGE_LINK.setLinkDescription(description);

        mockService.save(SUPPORT_PAGE_LINK);
        EasyMock.replay(mockService);

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
        EasyMock.verify(mockService);
    }

    /**
     * Test the POST to the Edit Page Fail
     */
    @Test
    public void testEditSupportPageLinkPostFail() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_SUPPORT_EDIT);
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_SUPPORT_EDIT, mav.getViewName());

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
    public void testEditSupportGetException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_SUPPORT_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", "1");
        EasyMock.expect(mockService.findByPrimaryKey(EasyMock.anyLong())).andThrow(new IllegalArgumentException());
        EasyMock.replay(mockService);

        handlerAdapter.handle(request, response, controller);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteSupportGetException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_SUPPORT_DELETE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", "1");
        EasyMock.expect(mockService.findByPrimaryKey(EasyMock.anyLong())).andThrow(new IllegalArgumentException());
        EasyMock.replay(mockService);

        handlerAdapter.handle(request, response, controller);
    }
}
