package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobinstance.JobInstanceController;

/**
 * Unit tests for the JobInstanceController which handles the Job Instance page.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "controller-test-context.xml" } )
public class JobInstanceControllerTest {

    @Autowired
    private JobInstanceController controller;
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
     * Test the GET to the job instance page.
     */
    @Test
    public void testGetJobInstanceDetails() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_JOB_INSTANCE_DETAILS);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(WebConstants.KEY_JOB_INSTANCE_ID, "1234");
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        // Verify the returned view name
        assertEquals(WebConstants.VIEW_JOB_INSTANCE_DETAILS, mav.getViewName());
        
        // Check the state of the model
        validateModel(mav.getModel());
    }

    private static void validateModel(Map<String,Object> model) {
        assertNotNull(model.get(WebConstants.KEY_ENVIRONMENT));
        assertNotNull(model.get(WebConstants.KEY_JOB_INSTANCE));
        assertNotNull(model.get(WebConstants.KEY_STEP_EXECUTIONS));
    }
}
