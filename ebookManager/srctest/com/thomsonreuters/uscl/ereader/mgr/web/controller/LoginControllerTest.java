/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

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

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.security.LoginController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.security.LoginForm;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;

/**
 * Tests for login/logouot and authentication.
 */
public class LoginControllerTest {
    private LoginController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    
    private UserPreferenceService mockPreferenceService;
  
    @Before
    public void setUp() throws Exception {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	mockPreferenceService = EasyMock.createMock(UserPreferenceService.class);
    	
    	controller = new LoginController();
    	controller.setEnvironmentName("workstation");
    	controller.setProviewDomain("ci");
    	controller.setUserPreferenceService(mockPreferenceService);
    }
    @Test
    public void testInboundGet() throws Exception {
    	request.setRequestURI("/"+WebConstants.MVC_SEC_LOGIN);
    	request.setMethod(HttpMethod.GET.name());
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	Assert.assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_SEC_LOGIN, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	Assert.assertNotNull(model.get(LoginForm.FORM_NAME));
    }

    @Test
    public void testHandleLoginFormPostSuccess() throws Exception {
    	request.setRequestURI("/"+WebConstants.MVC_SEC_LOGIN);
    	request.setMethod(HttpMethod.POST.name());
    	String username = "fooUser";
    	String password = "barPassword";
    	request.setParameter("username", "fooUser");
    	request.setParameter("password", "barPassword");
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	Assert.assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_SEC_LOGIN_AUTO, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	LoginForm form = (LoginForm) model.get(LoginForm.FORM_NAME);
    	Assert.assertNotNull(form);
    	Assert.assertEquals(username, form.getJ_username());
    	Assert.assertEquals(password, form.getJ_password());
    }    

    @Test
    public void testhandleAuthenticationFailure() throws Exception {
    	request.setRequestURI("/"+WebConstants.MVC_SEC_LOGIN_FAIL);
    	request.setMethod(HttpMethod.GET.name());
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	Assert.assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_SEC_LOGIN, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	Assert.assertNotNull(model.get(WebConstants.KEY_INFO_MESSAGES));
    	LoginForm form = (LoginForm) model.get(LoginForm.FORM_NAME);
    	Assert.assertNotNull(form);
    }

    @Test
    public void testHandleAuthenticationSuccess() throws Exception {
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_SEC_AFTER_AUTHENTICATION);
    	request.setMethod(HttpMethod.GET.name());
    	
    	UserPreference preference = new UserPreference();
    	preference.setStartPage("AUDIT");
    	
    	EasyMock.expect(mockPreferenceService.findByUsername(EasyMock.anyObject(String.class))).andReturn(preference);
    	EasyMock.replay(mockPreferenceService);

    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	assertNotNull(mav);
    	Assert.assertTrue(mav.getView() instanceof RedirectView);
    	RedirectView view = (RedirectView) mav.getView();
    	Assert.assertEquals(WebConstants.MVC_BOOK_AUDIT_LIST, view.getUrl());
    	
    	EasyMock.verify(mockPreferenceService);
    }
}
