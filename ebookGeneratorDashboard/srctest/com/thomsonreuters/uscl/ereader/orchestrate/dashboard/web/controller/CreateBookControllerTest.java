/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunner;
import com.thomsonreuters.uscl.ereader.orchestrate.core.engine.EngineConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book.CreateBookController;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book.CreateBookForm;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service.DashboardService;

/**
 * Unit tests for the JobInstanceController which handles the Job Instance page.
 */
public class CreateBookControllerTest {
	public static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+CreateBookForm.FORM_NAME;
	public static final String TEST_BOOK_CODE = "testBookCode";
    private CreateBookController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	DashboardService dashboardService = EasyMock.createMock(DashboardService.class);
    	JobRunner jobRunner = EasyMock.createMock(JobRunner.class);
    	MessageSource messageSource = EasyMock.createMock(MessageSource.class);
    	
    	Map<String,String> bookCodeMap = new HashMap<String,String>();
    	EasyMock.expect(dashboardService.getBookCodes()).andReturn(bookCodeMap);
    	EasyMock.expect(dashboardService.getBookTitle(TEST_BOOK_CODE)).andReturn("Test book title");
    	EasyMock.replay(dashboardService);
    	
    	this.controller = new CreateBookController();
    	controller.setDashboardService(dashboardService);
    	controller.setJobRunner(jobRunner);
    	controller.setEnvironmentName("junitEnvironment");
    	controller.setMessageSourceAccessor(new MessageSourceAccessor(messageSource));
    }
        
    /**
     * Test the GET to the job run page.
     */
    @Test
    public void testGetJobRun() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_CREATE_BOOK);
    	request.setMethod(HttpMethod.GET.name());

    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        // Verify the returned view name
        assertEquals(WebConstants.VIEW_CREATE_BOOK, mav.getViewName());
        
        // Check the state of the model
        validateModel(mav.getModel());
    }
    
    /**
     * Test the happy path POST of a job run request
     */
    @Test
    public void testPostJobSummary() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_CREATE_BOOK);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter(EngineConstants.JOB_PARAM_BOOK_CODE, TEST_BOOK_CODE);
    	request.setParameter("highPriorityJob", Boolean.TRUE.toString());

    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
    	Map<String,Object> model = mav.getModel();
    	BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
    	assertNotNull(bindingResult);
    	assertFalse(bindingResult.hasErrors());
    	Assert.assertEquals(WebConstants.VIEW_CREATE_BOOK, mav.getViewName());
    	validateModel(model);
    }
    
    /**
     * Test the POST of a new set of search criteria that contain validation errors
     */
    @Test
    public void testPostJobRunWithBindingError() {
    	request.setRequestURI("/"+WebConstants.URL_CREATE_BOOK);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter(EngineConstants.JOB_PARAM_BOOK_CODE, TEST_BOOK_CODE);
    	request.setParameter("highPriorityJob", "xxx");	// expects true|false
    	try {
    		handlerAdapter.handle(request, response, controller);
    		Assert.fail("Should have thrown a binding exception.");
    	} catch (Exception e) {
    		assertTrue(e instanceof BindException);
    		assertTrue(true); // expected
    	}
    }

    private static void validateModel(Map<String,Object> model) {
        assertNotNull(model.get(WebConstants.KEY_ENVIRONMENT));
        assertTrue(model.get(WebConstants.KEY_BOOK_CODE_OPTIONS) instanceof List<?>);
    }
}
