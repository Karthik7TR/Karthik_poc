/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book.CreateBookForm;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.stepexecution.StepExecutionController;

/**
 * Unit tests for the JobInstanceController which handles the Job Instance page.
 */
public class StepExecutionControllerTest {
	public static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+CreateBookForm.FORM_NAME;
	private static final long JOB_INST_ID = 444;
	private static final long JOB_EXEC_ID = 555;
	private static final long STEP_EXEC_ID = 666;
    private StepExecutionController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private JobExplorer mockJobExplorer;
    private JobExecution mockJobExecution;

    @Before
    public void setUp() {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the Spring Bach JobExplorer
    	mockJobExplorer = EasyMock.createMock(JobExplorer.class);
    	mockJobExecution = new JobExecution(JOB_EXEC_ID);
    	EasyMock.expect(mockJobExplorer.getJobInstance(JOB_INST_ID)).andReturn(new JobInstance(JOB_INST_ID, new JobParameters(), JobRunRequest.JOB_NAME_CREATE_EBOOK));
    	EasyMock.expect(mockJobExplorer.getStepExecution(JOB_EXEC_ID, STEP_EXEC_ID)).andReturn(new StepExecution("theStep", mockJobExecution));
    	EasyMock.replay(mockJobExplorer);
    	
    	this.controller = new StepExecutionController();
    	controller.setEnvironmentName("junitTestEnv");
    	controller.setJobExplorer(mockJobExplorer);
    }
        
    /**
     * Test the GET to the job run page.
     */
    @Test
    public void testGet() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_STEP_EXECUTION_DETAILS);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, String.valueOf(JOB_EXEC_ID));
    	request.setParameter(WebConstants.KEY_JOB_INSTANCE_ID, String.valueOf(JOB_INST_ID));
    	request.setParameter(WebConstants.KEY_STEP_EXECUTION_ID, String.valueOf(STEP_EXEC_ID));
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        // Verify the returned view name
        assertEquals(WebConstants.VIEW_STEP_EXECUTION_DETAILS, mav.getViewName());
        
        // Check the state of the model
        validateModel(mav.getModel());
    }    

    private static void validateModel(Map<String,Object> model) {
        assertNotNull(model.get(WebConstants.KEY_ENVIRONMENT));
        assertTrue(model.get(WebConstants.KEY_JOB_INSTANCE) instanceof JobInstance);
        assertTrue(model.get(WebConstants.KEY_STEP_EXECUTION) instanceof StepExecution);
        assertNotNull(model.get(WebConstants.KEY_STEP_EXECUTION_CONTEXT_MAP_ENTRIES));
    }
}
