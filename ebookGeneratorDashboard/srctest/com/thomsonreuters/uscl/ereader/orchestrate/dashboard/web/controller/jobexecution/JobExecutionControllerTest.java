package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobexecution;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JobExecutionControllerTest {

	@Autowired
    private ApplicationContext applicationContext;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private JobExecutionController controller;

    @Before
    public void setUp() {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = applicationContext.getBean(HandlerAdapter.class);
    	//handlerAdapter = new AnnotationMethodHandlerAdapter();
    	controller = new JobExecutionController();
    }
        
    @Test
    public void testDoGetJobExecutionDetails() throws Exception {
    	
    	request.setRequestURI("/"+WebConstants.URL_JOB_EXECUTION_DETAILS_GET + "?jobExecutionId=1234");
    	request.setMethod(HttpMethod.GET.name());
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
//      MockHttpServletRequest request = new MockHttpServletRequest();
//      ModelAndView mav = TODO_controller.doGetJobExecutionDetails(request, 1965l, new JobExecutionForm(), new BindingAwareModelMap());
        assertNotNull(mav);
        // Check the returned view name
        Assert.assertEquals(WebConstants.VIEW_JOB_EXECUTION_DETAILS, mav.getViewName());
        // Check the state of the model
        Map<String,Object> model = mav.getModel();
        assertNotNull(model.get(WebConstants.KEY_ENVIRONMENT));
        assertNotNull(model.get(WebConstants.KEY_JOB_EXECUTION));
        assertNotNull(model.get(WebConstants.KEY_VDO));
    }
}
