/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobexecution.JobExecutionController;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobexecution.JobExecutionForm;

/**
 * Unit tests for the JobExecutionController which handles the Job Execution Details page.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "dashboard-test-context.xml" } )
public class JobExecutionControllerTest {

	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+JobExecutionForm.FORM_NAME;
    @Autowired
    private JobExecutionController controller;
    @Resource(name="engineContextUrl")
    private URL engineContextUrl;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
    	assertNotNull(controller);
    	assertNotNull(engineContextUrl);
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    }
        
    /**
     * Test the GET to the details page.
     */
    @Test
    public void testGetJobExecutionDetails() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_EXECUTION_DETAILS_GET);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, "1234");
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        // Verify the returned view name
        Assert.assertEquals(WebConstants.VIEW_JOB_EXECUTION_DETAILS, mav.getViewName());
        
        // Check the state of the model
        validateModel(mav.getModel());
    }
    
    /**
     * Test a happy path POST of a new job execution ID, done when the user wants
     * to view a new job execution.
     */
    @Test
    public void testPostJobExecutionDetails() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_EXECUTION_DETAILS_POST);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, "5678");
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
    	Map<String,Object> model = mav.getModel();
    	BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
    	assertNotNull(bindingResult);
    	assertFalse(bindingResult.hasErrors());
    	Assert.assertEquals(WebConstants.VIEW_JOB_EXECUTION_DETAILS, mav.getViewName());
    	validateModel(model);
    }
    
    /**
     * Test a failed submit/post of the search form, pretend the user enters garbage into
     * the job Execution ID text input field. 
     */
    @Test
    public void testPostJobExecutionDetailsValidationError() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_EXECUTION_DETAILS_POST);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, "abcd");  // INVALID id
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
    	Map<String,Object> model = mav.getModel();
    	BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
    	assertNotNull(bindingResult);
    	assertTrue(bindingResult.hasErrors());
    	Assert.assertEquals(WebConstants.VIEW_JOB_EXECUTION_DETAILS, mav.getViewName());
    	assertNull(model.get(WebConstants.KEY_JOB_EXECUTION));
    }

    /**
     * Test the press of the Restart job button.
     */
    @Test
    public void testRestartJob() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_RESTART);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, "1234");
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
    	assertTrue(mav.getView() instanceof RedirectView);
    	// Verify the returned view
    	Assert.assertTrue((((RedirectView)mav.getView()).getUrl().toString()+"/"+WebConstants.URL_JOB_RESTART).startsWith(engineContextUrl.toString()));
    }

    /**
     * Test the press of the Stop job button.
     */
    @Test
    public void testStopJob() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_STOP);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, "1234");
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
    	assertTrue(mav.getView() instanceof RedirectView);
    	// Verify the returned view
    	Assert.assertTrue((((RedirectView)mav.getView()).getUrl().toString()+"/"+WebConstants.URL_JOB_STOP).startsWith(engineContextUrl.toString()));
    }
    
    private static void validateModel(Map<String,Object> model) {
        // Check the state of the model
        assertNotNull(model.get(WebConstants.KEY_ENVIRONMENT));
        assertTrue(model.get(WebConstants.KEY_JOB_EXECUTION) instanceof JobExecution);
        assertTrue(model.get(WebConstants.KEY_VDO) instanceof JobExecutionVdo);
    }
}
