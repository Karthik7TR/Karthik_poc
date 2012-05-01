/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.util.Map;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;


/**
 * Unit tests for the OperationsController which handles the URL request(s) to restart or stop a job.
 */
public class OperationsControllerTest {
	private static final Long JOB_EXEC_ID = new Long(1234);
	
    private OperationsController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
//    private ThreadPoolTaskExecutor mockTaskExecutor;
    private EngineService mockEngineService;
    private MessageSourceAccessor mockAccessor;
    private HandlerAdapter handlerAdapter;
	
    @Before
    public void setUp() throws Exception {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
//    	mockTaskExecutor = EasyMock.createMock(ThreadPoolTaskExecutor.class);
    	mockEngineService = EasyMock.createMock(EngineService.class);
    	mockAccessor = EasyMock.createMock(MessageSourceAccessor.class);
    	
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	controller = new OperationsController();
    	controller.setEngineService(mockEngineService);
    	controller.setMessageSourceAccessor(mockAccessor);
    }
    @Test
    public void testRestartJob() throws Exception {
    	request.setRequestURI("/service/restart/job/"+JOB_EXEC_ID);
    	request.setMethod(HttpMethod.GET.name());
    	Long restartedJobExecId = new Long(JOB_EXEC_ID.longValue() + 1l);
    	EasyMock.expect(mockEngineService.restartJob(JOB_EXEC_ID)).andReturn(restartedJobExecId);
    	EasyMock.replay(mockEngineService);
    	
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	Assert.assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_OPERATION_RESPONSE, mav.getViewName());
    	
    	Map<String,Object> model = mav.getModel();
    	JobOperationResponse opResponseActual = (JobOperationResponse) model.get(WebConstants.KEY_JOB_OPERATION_RESPONSE);
    	Assert.assertEquals(restartedJobExecId, opResponseActual.getJobExecutionId());
    	
    	EasyMock.verify(mockEngineService);
    }

	@Test
	public void futureTest() {
		Assert.assertTrue(true);
	}
}
