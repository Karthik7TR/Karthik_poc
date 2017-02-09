package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.assertNotNull;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.security.LoginController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.security.LoginForm;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Tests for login/logouot and authentication.
 */
public final class LoginControllerTest
{
    private LoginController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    private UserPreferenceService mockPreferenceService;
    private OutageService mockOutageService;
    private MiscConfigSyncService mockMiscConfigSyncService;

    @Before
    public void setUp()
    {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        mockPreferenceService = EasyMock.createMock(UserPreferenceService.class);
        mockOutageService = EasyMock.createMock(OutageService.class);
        mockMiscConfigSyncService = EasyMock.createMock(MiscConfigSyncService.class);

        controller = new LoginController();
        controller.setEnvironmentName("workstation");
        controller.setUserPreferenceService(mockPreferenceService);
        controller.setOutageService(mockOutageService);
        controller.setMiscConfigSyncService(mockMiscConfigSyncService);
    }

    @Test
    public void testInboundGet() throws Exception
    {
        request.setRequestURI("/" + WebConstants.MVC_SEC_LOGIN);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(mockMiscConfigSyncService.getProviewHost()).andReturn(InetAddress.getLocalHost());
        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());

        EasyMock.replay(mockMiscConfigSyncService);
        EasyMock.replay(mockOutageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_SEC_LOGIN, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        Assert.assertNotNull(model.get(LoginForm.FORM_NAME));

        EasyMock.verify(mockMiscConfigSyncService);
        EasyMock.verify(mockOutageService);
    }

    @Test
    public void testHandleLoginFormPostSuccess() throws Exception
    {
        request.setRequestURI("/" + WebConstants.MVC_SEC_LOGIN);
        request.setMethod(HttpMethod.POST.name());
        final String username = "fooUser";
        final String password = "barPassword";
        request.setParameter("username", "fooUser");
        request.setParameter("password", "barPassword");

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.replay(mockOutageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_SEC_LOGIN_AUTO, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        final LoginForm form = (LoginForm) model.get(LoginForm.FORM_NAME);
        Assert.assertNotNull(form);
        Assert.assertEquals(username, form.getJ_username());
        Assert.assertEquals(password, form.getJ_password());

        EasyMock.verify(mockOutageService);
    }

    @Test
    public void testhandleAuthenticationFailure() throws Exception
    {
        request.setRequestURI("/" + WebConstants.MVC_SEC_LOGIN_FAIL);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.replay(mockOutageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_SEC_LOGIN, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        Assert.assertNotNull(model.get(WebConstants.KEY_INFO_MESSAGES));
        final LoginForm form = (LoginForm) model.get(LoginForm.FORM_NAME);
        Assert.assertNotNull(form);

        EasyMock.verify(mockOutageService);
    }

    @Test
    public void testHandleAuthenticationSuccess() throws Exception
    {
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_SEC_AFTER_AUTHENTICATION);
        request.setMethod(HttpMethod.GET.name());

        final UserPreference preference = new UserPreference();
        preference.setStartPage("AUDIT");

        EasyMock.expect(mockPreferenceService.findByUsername(EasyMock.anyObject(String.class))).andReturn(preference);
        EasyMock.replay(mockPreferenceService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        Assert.assertTrue(mav.getView() instanceof RedirectView);
        final RedirectView view = (RedirectView) mav.getView();
        Assert.assertEquals(WebConstants.MVC_BOOK_AUDIT_LIST, view.getUrl());

        EasyMock.verify(mockPreferenceService);
    }
}
