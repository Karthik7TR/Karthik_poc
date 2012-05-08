/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.util.ArrayList;
import java.util.List;

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

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.smoketest.SmokeTestController;
import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;
import com.thomsonreuters.uscl.ereader.smoketest.service.SmokeTestService;

/**
 * Tests for login/logouot and authentication.
 */
public class SmokeTestControllerTest {
	private SmokeTest SMOKE_TEST;
	private List<SmokeTest> SMOKE_TEST_LIST;
	private List<String> APP_NAMES;
	
	private SmokeTestService mockService;
	
    private SmokeTestController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
  
    @Before
    public void setUp() throws Exception {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	mockService = EasyMock.createMock(SmokeTestService.class);
    	
    	controller = new SmokeTestController();
    	controller.setEnvironmentName("workstation");
    	controller.setProviewDomain("ci");
    	controller.setImageVertical("image");
    	controller.setSmokeTestService(mockService);
    	
    	SMOKE_TEST = new SmokeTest();
    	SMOKE_TEST.setName("name");
    	SMOKE_TEST.setIsRunning(false);
    	SMOKE_TEST.setAddress("123");
    	
    	SMOKE_TEST_LIST = new ArrayList<SmokeTest>();
    	SMOKE_TEST_LIST.add(SMOKE_TEST);
    	
    	APP_NAMES = new ArrayList<String>();
    	APP_NAMES.add("1");
    	APP_NAMES.add("2");
    	
    	EasyMock.expect(mockService.getApplicationStatus(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(SMOKE_TEST);
    	EasyMock.expect(mockService.getApplicationStatus(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(SMOKE_TEST);
    	EasyMock.expect(mockService.testConnection()).andReturn(SMOKE_TEST);
    	EasyMock.expect(mockService.getRunningApplications()).andReturn(APP_NAMES);
    	EasyMock.expect(mockService.getCIServerStatuses()).andReturn(SMOKE_TEST_LIST);
    	EasyMock.expect(mockService.getCIApplicationStatuses()).andReturn(SMOKE_TEST_LIST);
    	EasyMock.expect(mockService.getTestServerStatuses()).andReturn(SMOKE_TEST_LIST);
    	EasyMock.expect(mockService.getTestApplicationStatuses()).andReturn(SMOKE_TEST_LIST);
    	EasyMock.expect(mockService.getQAServerStatuses()).andReturn(SMOKE_TEST_LIST);
    	EasyMock.expect(mockService.getQAApplicationStatuses()).andReturn(SMOKE_TEST_LIST);
    	EasyMock.expect(mockService.getLowerEnvDatabaseServerStatuses()).andReturn(SMOKE_TEST_LIST);
    	EasyMock.expect(mockService.getProdServerStatuses()).andReturn(SMOKE_TEST_LIST);
    	EasyMock.expect(mockService.getProdApplicationStatuses()).andReturn(SMOKE_TEST_LIST);
    	EasyMock.expect(mockService.getProdDatabaseServerStatuses()).andReturn(SMOKE_TEST_LIST);
    	EasyMock.replay(mockService);
    	
    }
    
    @Test
    public void testInboundGet() throws Exception {
    	request.setRequestURI("/"+WebConstants.MVC_SMOKE_TEST);
    	request.setMethod(HttpMethod.GET.name());
    	
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	Assert.assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_SMOKE_TEST, mav.getViewName());
    	
    	EasyMock.verify(mockService);
    }
}
