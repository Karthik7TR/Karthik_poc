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

import java.util.ArrayList;
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

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunner;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book.CreateBookController;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book.CreateBookForm;

/**
 * Unit tests for the JobInstanceController which handles the Job Instance page.
 */
public class CreateBookControllerTest {
	public static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+CreateBookForm.FORM_NAME;
	public static final String TEST_BOOK_ID = "testBookId";
    private CreateBookController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private CoreService mockCoreService;

    @Before
    public void setUp() {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	this.mockCoreService = EasyMock.createMock(CoreService.class);
    	JobRunner jobRunner = EasyMock.createMock(JobRunner.class);
    	MessageSource messageSource = EasyMock.createMock(MessageSource.class);
    	
    	List<BookDefinition> bookDefs = new ArrayList<BookDefinition>();
    	BookDefinition bookDef = new BookDefinition();
    	bookDef.setBookDefinitionKey(new BookDefinitionKey("bogusId"));
    	bookDefs.add(bookDef);
    	EasyMock.expect(mockCoreService.findAllBookDefinitions()).andReturn(bookDefs);
    	EasyMock.replay(mockCoreService);
    	
    	this.controller = new CreateBookController();
    	controller.setCoreService(mockCoreService);
    	controller.setJobRunner(jobRunner);
    	controller.setEnvironmentName("junitEnvironment");
    	controller.setMessageSourceAccessor(new MessageSourceAccessor(messageSource));
    }
        
    /**
     * Test the GET to the create book page.
     */
    @Test
    public void testGetPage() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_CREATE_BOOK);
    	request.setMethod(HttpMethod.GET.name());

    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        // Verify the returned view name
        assertEquals(WebConstants.VIEW_CREATE_BOOK, mav.getViewName());
        
        // Check the state of the model
        validateModel(mav.getModel());
        
        EasyMock.verify(mockCoreService);
    }
    
    /**
     * Test the happy path POST of a create book request
     */
    @Test
    public void testPostPage() throws Exception {
    	request.setRequestURI("/"+WebConstants.URL_CREATE_BOOK);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("bookId", TEST_BOOK_ID);
    	request.setParameter("highPriorityJob", Boolean.TRUE.toString());

    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
    	Map<String,Object> model = mav.getModel();
    	BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
    	assertNotNull(bindingResult);
    	assertFalse(bindingResult.hasErrors());
    	Assert.assertEquals(WebConstants.VIEW_CREATE_BOOK, mav.getViewName());
    	validateModel(model);
    	EasyMock.verify(mockCoreService);
    }
    
    /**
     * Test the POST of a new set of search criteria that contain validation errors
     */
    @Test
    public void testPostJobRunWithBindingError() {
    	request.setRequestURI("/"+WebConstants.URL_CREATE_BOOK);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("bookId", TEST_BOOK_ID);
    	request.setParameter("highPriorityJob", "xxx");	// expects true|false
    	try {
    		handlerAdapter.handle(request, response, controller);
    		Assert.fail("Should have thrown a binding exception.");
    	} catch (Exception e) {
    		assertTrue(e instanceof BindException);
    		assertTrue(true); // expected
    	}
    }
    
    @Test
    public void testCreateBookForm() {
    	CreateBookForm form = new CreateBookForm();
    	String titleId = "bogusTitleId";
    	form.setTitleId(titleId);
    	BookDefinitionKey key = form.getBookDefinitionKey();
    	Assert.assertEquals(titleId, key.getTitleId());
    }

    private static void validateModel(Map<String,Object> model) {
        assertNotNull(model.get(WebConstants.KEY_ENVIRONMENT));
        assertTrue(model.get(WebConstants.KEY_BOOK_OPTIONS) instanceof List<?>);
    }
}
