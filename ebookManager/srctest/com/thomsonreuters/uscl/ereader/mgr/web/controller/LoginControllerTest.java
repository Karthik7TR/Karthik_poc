/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.assertNotNull;

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

public class LoginControllerTest {
    private LoginController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
  
   
    @Before
    public void setUp() throws Exception {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new LoginController();
    }
    
    @Test
    public void testAfterAuthentication() throws Exception {
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_SEC_AFTER_AUTHENTICATION);
    	request.setMethod(HttpMethod.GET.name());

    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	assertNotNull(mav);
    	Assert.assertTrue(mav.getView() instanceof RedirectView);
    	RedirectView view = (RedirectView) mav.getView();
    	Assert.assertEquals(WebConstants.MVC_BOOK_LIBRARY_LIST, view.getUrl());
    }
}
