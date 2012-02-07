/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;

/**
 * Unit tests for the OperationsController which handles the URL request(s) to restart or stop a job.
 */
public class OperationsControllerTest {
	public static final long JOB_EXEC_ID_VALUE = 100;
	public static URL DASHBOARD_CONTEXT_URL;
    private OperationsController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private EngineService mockEngineService;
    private MessageSourceAccessor mockMessageSourceAccessor;
    static {
    	try {
    		DASHBOARD_CONTEXT_URL = new URL("http://someHost/egDashboard");
    	} catch (Exception e) {
    		Assert.fail(e.getMessage());
    	}
    }

    @Before
    public void setUp() throws Exception {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	this.mockEngineService = EasyMock.createMock(EngineService.class);
    	this.mockMessageSourceAccessor = EasyMock.createMock(MessageSourceAccessor.class);
    	
    	this.controller = new OperationsController();
    	controller.setEngineService(mockEngineService);
    	controller.setMessageSourceAccessor(mockMessageSourceAccessor);
    	controller.setDashboardContextUrl(DASHBOARD_CONTEXT_URL);
    }
    
    @Test
    public void testDummy() throws Exception {

    }    
        
    @Ignore
    public void testRestartJobSuccess() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_RESTART);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, String.valueOf(JOB_EXEC_ID_VALUE));
    	
    	// Record the behavior we expect inside the mock objects
    	long expectedRestartedExecId = JOB_EXEC_ID_VALUE + 1;
    	EasyMock.expect(mockEngineService.restartJob(JOB_EXEC_ID_VALUE)).andReturn(new Long(expectedRestartedExecId));
    	EasyMock.replay(mockEngineService);

    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
    	Assert.assertTrue(mav.getView() instanceof RedirectView);
    	assertEquals(DASHBOARD_CONTEXT_URL.toString()+"/jobExecutionDetails.mvc?"+WebConstants.KEY_JOB_EXECUTION_ID+"="+expectedRestartedExecId,
    				(((RedirectView)mav.getView()).getUrl()));
        
        EasyMock.verify(mockEngineService);
    }
    
    @Ignore
    public void testRestartJobFailure() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_RESTART);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, String.valueOf(JOB_EXEC_ID_VALUE));
    	
    	EasyMock.expect(mockEngineService.restartJob(JOB_EXEC_ID_VALUE)).andThrow(new Exception("Bogus JUnit test exception starting job"));
    	EasyMock.replay(mockEngineService);
    	EasyMock.expect(mockMessageSourceAccessor.getMessage("label.restart")).andReturn("Restart");
    	EasyMock.replay(mockMessageSourceAccessor);

    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertEquals(WebConstants.VIEW_JOB_OPERATION_FAILURE, mav.getViewName());
        
        // Check the state of the model
    	verifyFailureModel(mav.getModel());
        
        // Verify the mock objects has been invoked in the proper way
        EasyMock.verify(mockMessageSourceAccessor);
        EasyMock.verify(mockEngineService);
    }
    
    @Ignore
    public void testStopJobSuccess() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_STOP);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, String.valueOf(JOB_EXEC_ID_VALUE));
    	
    	// Record the behavior we expect within the mock objects
    	mockEngineService.stopJob(JOB_EXEC_ID_VALUE);
    	EasyMock.replay(mockEngineService);

    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
       	Assert.assertTrue(mav.getView() instanceof RedirectView);
    	assertEquals(DASHBOARD_CONTEXT_URL.toString()+"/jobExecutionDetails.mvc?"+WebConstants.KEY_JOB_EXECUTION_ID+"="+JOB_EXEC_ID_VALUE,
    				(((RedirectView)mav.getView()).getUrl()));

        
        EasyMock.verify(mockEngineService);
    }
    
    @Ignore
    public void testStopJobFailure() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_STOP);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, String.valueOf(JOB_EXEC_ID_VALUE));
    	
    	mockEngineService.stopJob(JOB_EXEC_ID_VALUE);
    	EasyMock.expectLastCall().andThrow(new Exception("Bogus JUnit test exception stopping job"));
    	EasyMock.replay(mockEngineService);
    	EasyMock.expect(mockMessageSourceAccessor.getMessage("label.stop")).andReturn("Restart");
    	EasyMock.replay(mockMessageSourceAccessor);

    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertEquals(WebConstants.VIEW_JOB_OPERATION_FAILURE, mav.getViewName());
        
        // Check the state of the model
    	verifyFailureModel(mav.getModel());
        
        // Verify the mock objects has been invoked in the proper way
        EasyMock.verify(mockMessageSourceAccessor);
        EasyMock.verify(mockEngineService);    	
    }
    
    private void verifyFailureModel(Map<String,Object> model) {
        assertEquals(JOB_EXEC_ID_VALUE, model.get(WebConstants.KEY_JOB_EXECUTION_ID));
        assertNotNull(model.get(WebConstants.KEY_ACTION));
        assertNotNull(model.get(WebConstants.KEY_ERROR_MESSAGE));
        assertNotNull(model.get(WebConstants.KEY_STACK_TRACE));
    	
    }
}
