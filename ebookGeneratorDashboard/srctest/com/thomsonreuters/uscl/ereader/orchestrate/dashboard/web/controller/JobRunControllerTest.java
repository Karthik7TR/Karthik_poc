package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;
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
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book.CreateBookController;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book.CreateBookForm;

/**
 * Unit tests for the JobInstanceController which handles the Job Instance page.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "dashboard-test-context.xml" } )
public class JobRunControllerTest {
	public static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+CreateBookForm.FORM_NAME;
    @Autowired
    private CreateBookController controller;
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
    	request.setParameter("bookCode", "testBookCode");
    	request.setParameter("threadPriority", String.valueOf(Thread.NORM_PRIORITY));
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
    	request.setParameter("bookCode", "testBookCode");
    	request.setParameter("bookVersion", "testVersion");
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
