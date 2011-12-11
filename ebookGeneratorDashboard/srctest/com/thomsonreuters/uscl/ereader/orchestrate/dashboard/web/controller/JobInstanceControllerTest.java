/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobinstance.JobInstanceController;

/**
 * Unit tests for the JobInstanceController which handles the Job Instance page.
 */
public class JobInstanceControllerTest {
	private static final long JOB_INST_ID = 2345;
    private JobInstanceController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private JobInstance jobInstance;
    private JobExplorer jobExplorer;

    @Before
    public void setUp() {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	this.jobExplorer = EasyMock.createMock(JobExplorer.class);
    	this.jobInstance = new JobInstance(JOB_INST_ID, new JobParameters(), "fooJob");
    	
    	this.controller = new JobInstanceController();
    	controller.setEnvironmentName("junitTestEnv");
    	controller.setJobExplorer(jobExplorer);
    }
        
    /**
     * Test the GET to the job instance page.
     */
    @Test
    public void testGetJobInstanceDetails() throws Exception {
    	EasyMock.expect(jobExplorer.getJobInstance(JOB_INST_ID)).andReturn(jobInstance);
    	EasyMock.expect(jobExplorer.getJobExecutions(jobInstance)).andReturn(new ArrayList<JobExecution>(0));
    	EasyMock.replay(jobExplorer);
    	request.setRequestURI("/"+WebConstants.URL_JOB_INSTANCE_DETAILS);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(WebConstants.KEY_JOB_INSTANCE_ID, String.valueOf(JOB_INST_ID));
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        // Verify the returned view name
        assertEquals(WebConstants.VIEW_JOB_INSTANCE_DETAILS, mav.getViewName());
        
        // Check the state of the model
        validateModel(mav.getModel());
        EasyMock.verify(jobExplorer);
    }

    private static void validateModel(Map<String,Object> model) {
        assertNotNull(model.get(WebConstants.KEY_ENVIRONMENT));
        assertTrue(model.get(WebConstants.KEY_JOB_INSTANCE) instanceof JobInstance);
        assertTrue(model.get(WebConstants.KEY_STEP_EXECUTIONS) instanceof List<?>);
    }
}
