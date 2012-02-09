/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

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
import com.thomsonreuters.uscl.ereader.mgr.web.controller.security.SecurityController;

public class SecurityControllerTest {
	private static URL CAS_URL;
    private SecurityController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    static {
    	try {
    		CAS_URL = new URL("https://someHost/ebookCas");
    	} catch (Exception e) {
    		Assert.fail(e.getMessage());
    	}
    }
   
    @Before
    public void setUp() throws Exception {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new SecurityController();
    	controller.setCasUrl(CAS_URL);
    }
    
    @Test
    public void testAfterLogout() throws Exception {
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_AFTER_LOGOUT);
    	request.setMethod(HttpMethod.GET.name());

    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	assertNotNull(mav);
    	Assert.assertTrue(mav.getView() instanceof RedirectView);
    	RedirectView view = (RedirectView) mav.getView();
    	Assert.assertEquals(CAS_URL+"/logout", view.getUrl());
    }
}
