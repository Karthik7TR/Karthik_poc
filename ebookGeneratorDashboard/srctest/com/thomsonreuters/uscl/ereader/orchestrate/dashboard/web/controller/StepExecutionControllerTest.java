/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
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

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book.CreateBookForm;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.stepexecution.StepExecutionController;

/**
 * Unit tests for the JobInstanceController which handles the Job Instance page.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "dashboard-test-context.xml" } )
public class StepExecutionControllerTest {
	public static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+CreateBookForm.FORM_NAME;
    @Autowired
    private StepExecutionController controller;
    @Resource(name="engineContextUrl")
    private URL engineContextUrl;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
    	Assert.assertNotNull(controller);
    	Assert.assertNotNull(engineContextUrl);
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    }
        
    /**
     * Test the GET to the job run page.
     */
    @Test
    public void testGet() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_STEP_EXECUTION_DETAILS);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, "1234");
    	request.setParameter(WebConstants.KEY_JOB_INSTANCE_ID, "5678");
    	request.setParameter(WebConstants.KEY_STEP_EXECUTION_ID, "4456");
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
