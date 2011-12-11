/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.displaytag.pagination.PaginatedList;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.orchestrate.core.engine.EngineConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobsummary.JobSummaryController;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobsummary.JobSummaryForm;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobsummary.JobSummaryFormValidator;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service.DashboardService;

/**
 * Unit tests for the JobSummaryController which handles the Job Execution Summary page.
 */
public class JobSummaryControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+JobSummaryForm.FORM_NAME;
    private JobSummaryController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private JobSummaryForm form;
    private DashboardService mockDashboardService;
    private JobExplorer mockJobExplorer;

    @Before
    public void setUp() {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the dashboard service
    	this.mockDashboardService = EasyMock.createMock(DashboardService.class);
    	this.form = new JobSummaryForm();
    	JobSummaryController.initializeForm(form, new MockHttpSession());
    	Long[] idArray = { 10l, 20l, 30l, 40l };
    	EasyMock.expect(mockDashboardService.findJobExecutionIds(EngineConstants.JOB_DEFINITION_EBOOK,
    			form.getStartTime(), form.getBatchStatus())).andReturn(Arrays.asList(idArray));
    	EasyMock.expect(mockDashboardService.findJobExecutionByPrimaryKey(Arrays.asList(idArray))).andReturn(new ArrayList<JobExecution>(0));
    	EasyMock.replay(mockDashboardService);
    	
    	// Mock up the Spring Bach JobExplorer
    	this.mockJobExplorer = EasyMock.createMock(JobExplorer.class);
    	EasyMock.expect(mockJobExplorer.getJobNames()).andReturn(new ArrayList<String>(0));
    	EasyMock.replay(mockJobExplorer);

    	// Set up the controller
    	this.controller = new JobSummaryController();
    	controller.setDashboardService(mockDashboardService);
    	controller.setEnvironmentName("junitTestEnv");
    	controller.setJobExplorer(mockJobExplorer);
    	controller.setValidator(new JobSummaryFormValidator());
    }
        
    /**
     * Test the GET to the Job Execution Summary page.
     */
    @Test
    public void testGetJobSummary() throws Exception {

    	request.setRequestURI("/"+WebConstants.URL_JOB_SUMMARY);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("status", form.getStatus());
    	request.setParameter("startDate", form.getStartDate());
    	

    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        // Verify the returned view name
        assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
        
        // Check the state of the model
        validateModel(mav.getModel());
        EasyMock.verify(mockDashboardService);
        EasyMock.verify(mockJobExplorer);
    }
    
    /**
     * Test the happy path POST of a new set of search criteria.
     */
    @Test
    public void testPostJobSummary() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_SUMMARY);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("status", form.getStatus());
    	request.setParameter("startDate", form.getStartDate());
    	request.setParameter("itemsPerPage", "20");

    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
    	Map<String,Object> model = mav.getModel();
    	BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
    	assertNotNull(bindingResult);
    	assertFalse(bindingResult.hasErrors());
    	Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
    	validateModel(model);
    	EasyMock.verify(mockDashboardService);
    	EasyMock.verify(mockJobExplorer);
    }
    
    /**
     * Test the POST of a new set of search criteria that contain validation errors
     */
    @Test
    public void testPostJobSummaryValiationError() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_SUMMARY);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("jobName", "testJobName");
    	request.setParameter("status", "COMPLETED");
    	request.setParameter("startDate", "mm/dd/yy");  // Invalid date
    	request.setParameter("itemsPerPage", "20x");

    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
    	Map<String,Object> model = mav.getModel();
    	BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
    	assertNotNull(bindingResult);
    	assertTrue(bindingResult.hasErrors());
    	Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
    	assertNull(model.get(WebConstants.KEY_PAGINATED_LIST));
    }

    private void validateModel(Map<String,Object> model) {
    	HttpSession httpSession = request.getSession();
        assertNotNull(model.get(WebConstants.KEY_ENVIRONMENT));
        assertTrue(model.get(WebConstants.KEY_JOB_NAMES) instanceof List<?>);
        assertTrue(model.get(WebConstants.KEY_PAGINATED_LIST) instanceof PaginatedList);
        assertNotNull(httpSession.getAttribute(WebConstants.KEY_PAGINATED_LIST));  // Make sure the paginated list is on the session
    }
}
