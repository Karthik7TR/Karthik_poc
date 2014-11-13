/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.EBConstants;

/**
 * Test for HomeController
 */
public class HomeControllerTest {
    private HomeController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
  
    @Before
    public void setUp() throws Exception {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new HomeController();
    	controller.setEnvironmentName("workstation");
    }
    @Test
    public void testInboundGet() throws Exception {
    	request.setRequestURI("/"+EBConstants.URI_HOME);
    	request.setMethod(HttpMethod.GET.name());
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	Assert.assertNotNull(mav);
    	Assert.assertEquals(EBConstants.VIEW_HOME, mav.getViewName());
    }

}
