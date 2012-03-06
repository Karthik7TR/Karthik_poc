/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.book.service.BookService;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionFormValidator;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

public class EditBookDefinitionControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+EditBookDefinitionForm.FORM_NAME;
    private EditBookDefinitionController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private CoreService mockCoreService;
    private BookService mockBookService;
    private EditBookDefinitionFormValidator validator;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the dashboard service
    	this.mockCoreService = EasyMock.createMock(CoreService.class);
    	this.mockBookService = EasyMock.createMock(BookService.class);
    	
    	// Set up the controller
    	this.controller = new EditBookDefinitionController();
    	controller.setBookService(mockBookService);
    	controller.setCoreService(mockCoreService);
    	
    	validator = new EditBookDefinitionFormValidator();
    	validator.setCoreService(mockCoreService);
    	controller.setValidator(validator);
    	
    	
	}

	/**
     * Test the GET to the Create Book Definition page
     */
	@Test
	public void testCreateBookDefintionGet() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_CREATE);
    	request.setMethod(HttpMethod.GET.name());
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_CREATE, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        checkInitialValuesDynamicContentForCreate(model);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	/**
     * Test the POST to the Create Book Definition page when there are validation errors
     */
	@Test
	public void testCreateBookDefintionPostFailed() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_CREATE);
    	request.setMethod(HttpMethod.POST.name());

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_CREATE, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        checkInitialValuesDynamicContentForCreate(model);
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	assertTrue(bindingResult.hasErrors());
	       
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
     * Test the POST to the Create Book Definition page when titleId is complete and Definition in incomplete state
     */
	@Test
	public void testCreateBookDefintionPostIncompleteStateSuccess() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("contentType", WebConstants.KEY_ANALYTICAL_ABBR);
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", "uscl/an/abcd");

    	EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null);
    	EasyMock.replay(mockCoreService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
			// Verify mav is a RedirectView
			View view = mav.getView();
	        assertEquals(RedirectView.class, view.getClass());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	Assert.assertFalse(bindingResult.hasErrors());
	       
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
     * Test the POST to the Create Book Definition page when titleId is complete but no other 
     * required fields are complete while marked as complete
     */
	@Test
	public void testCreateBookDefintionPostCompleteStateFailed() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("contentType", WebConstants.KEY_ANALYTICAL_ABBR);
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", "uscl/an/abcd");
    	request.setParameter("isComplete", "true");

    	EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null);
    	EasyMock.replay(mockCoreService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
			// Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_CREATE, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	assertTrue(bindingResult.hasErrors());
	    	
	    	checkInitialValuesDynamicContentForCreate(model);
	       
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
     * Test the POST to the Create Book Definition page when titleId is complete and Definition in incomplete state
     */
	@Test
	public void testCreateBookDefintionPostCompleteStateSuccess() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("contentType", WebConstants.KEY_ANALYTICAL_ABBR);
    	request.setParameter("ProviewDisplayName", "Name in Proview");
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", "uscl/an/abcd");
    	request.setParameter("nameLines[0].bookNameText", "title name");
    	request.setParameter("nameLines[0].sequenceNum", "1");
    	request.setParameter("copyright", "Somethings");
    	request.setParameter("materialId", "123456789012345678");
    	request.setParameter("isTOC", "true");
    	request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
    	request.setParameter("tocCollectionName", "sdfdsfdsf");
    	request.setParameter("isbn", "978-193-5-18235-1");
    	request.setParameter("isComplete", "true");
    	request.setParameter("validateForm", "false");

    	EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null);
    	EasyMock.replay(mockCoreService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
			// Verify mav is a RedirectView TODO: fix
			//View view = mav.getView();
	        //assertEquals(RedirectView.class, view.getClass());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	Assert.assertFalse(bindingResult.hasErrors());
	       
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockCoreService);
	}

	private void checkInitialValuesDynamicContentForCreate(Map<String,Object> model) {
		int numNameLines = Integer.valueOf(model.get(WebConstants.KEY_NUMBER_OF_NAME_LINES).toString());
        int numAuthors = Integer.valueOf(model.get(WebConstants.KEY_NUMBER_OF_AUTHORS).toString());
        int numFrontMatter = Integer.valueOf(model.get(WebConstants.KEY_NUMBER_OF_FRONT_MATTERS).toString());
        boolean isPublished = Boolean.parseBoolean(model.get(WebConstants.KEY_IS_PUBLISHED).toString());
        assertEquals(0, numNameLines);
        assertEquals(0, numAuthors);
        assertEquals(0, numFrontMatter);
        assertEquals(false, isPublished);
	}
	
	/**
     * Test the GET to the Edit Book Definition page
     */
	@Test
	public void testEditBookDefintionGet() {
		String fullyQualifiedTitleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_EDIT);
		request.setParameter("titleId", fullyQualifiedTitleId);
		request.setParameter("bookdefinitionId", "1");
    	request.setMethod(HttpMethod.GET.name());
    	
    	BookDefinition book = new BookDefinition();
    	BookDefinitionKey key = new BookDefinitionKey(fullyQualifiedTitleId);
    	book.setBookDefinitionKey(key);
    	book.setContentType("Analytical");
    	book.setBookName("Book");
    	book.setCopyright("something");
    	
    	EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(book);
    	EasyMock.expect(mockBookService.getAuthors(EasyMock.anyInt())).andReturn(new ArrayList<Author>());
    	EasyMock.expect(mockBookService.getKeywordsTypesAndValues()).andReturn(new ArrayList<KeywordTypeCode>());
    	EasyMock.replay(mockCoreService);
    	EasyMock.replay(mockBookService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_EDIT, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        checkInitialValuesDynamicContentForEdit(model);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockCoreService);
		EasyMock.verify(mockBookService);
	}
	
	/**
     * Test the GET to the Edit Book Definition page for invalid book
     */
	@Test
	public void testEditBookDefintionGetInvalidBook() {
		String fullyQualifiedTitleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_EDIT);
		request.setParameter("titleId", fullyQualifiedTitleId);
		request.setParameter("bookdefinitionId", "1");
    	request.setMethod(HttpMethod.GET.name());
    	
    	
    	EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null);
    	EasyMock.replay(mockCoreService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_EDIT, mav.getViewName());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
     * Test the POST to the Edit Book Definition page
     */
	@Test
	public void testEditBookDefintionPOST() {
		String fullyQualifiedTitleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_EDIT);
		request.setParameter("ProviewDisplayName", "Name in Proview");
		request.setParameter("contentType", WebConstants.KEY_ANALYTICAL_ABBR);
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", fullyQualifiedTitleId);
    	request.setParameter("nameLines[0].bookNameText", "title name");
    	request.setParameter("nameLines[0].sequenceNum", "1");
    	request.setParameter("copyright", "Somethings");
    	request.setParameter("materialId", "123456789012345678");
    	request.setParameter("isTOC", "true");
    	request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
    	request.setParameter("tocCollectionName", "sdfdsfdsf");
    	request.setParameter("isbn", "978-193-5-18235-1");
    	request.setParameter("isComplete", "true");
    	request.setParameter("validateForm", "false");
		request.setParameter("bookdefinitionId", "1");
    	request.setMethod(HttpMethod.POST.name());
    	
    	BookDefinition book = new BookDefinition();
    	BookDefinitionKey key = new BookDefinitionKey(fullyQualifiedTitleId);
    	book.setBookDefinitionKey(key);
    	book.setContentType("Analytical");
    	book.setBookName("Book");
    	book.setCopyright("something");
    	
    	EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(book).times(2);
    	EasyMock.expect(mockBookService.getKeywordsTypesAndValues()).andReturn(new ArrayList<KeywordTypeCode>());
    	EasyMock.replay(mockCoreService);
    	EasyMock.replay(mockBookService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
			// Verify mav is a RedirectView
			View view = mav.getView();
	        assertEquals(RedirectView.class, view.getClass());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	Assert.assertFalse(bindingResult.hasErrors());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockCoreService);
		EasyMock.verify(mockBookService);
	}
	
	/**
     * Test the POST to the Edit Book Definition
     * Saving in Complete state with no name line
     */
	@Test
	public void testEditBookDefintionPOSTFailed() {
		String fullyQualifiedTitleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_EDIT);
		request.setParameter("contentType", WebConstants.KEY_ANALYTICAL_ABBR);
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", fullyQualifiedTitleId);
    	request.setParameter("copyright", "Somethings");
    	request.setParameter("materialId", "123456789012345678");
    	request.setParameter("isTOC", "true");
    	request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
    	request.setParameter("tocCollectionName", "sdfdsfdsf");
    	request.setParameter("isbn", "978-193-5-18235-1");
    	request.setParameter("isComplete", "true");
    	request.setParameter("validateForm", "false");
		request.setParameter("bookdefinitionId", "1");
    	request.setMethod(HttpMethod.POST.name());
    	
    	BookDefinition book = new BookDefinition();
    	BookDefinitionKey key = new BookDefinitionKey(fullyQualifiedTitleId);
    	book.setBookDefinitionKey(key);
    	book.setContentType("Analytical");
    	book.setBookName("Book");
    	book.setCopyright("something");
    	
    	EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(book).times(2);
    	EasyMock.expect(mockBookService.getKeywordsTypesAndValues()).andReturn(new ArrayList<KeywordTypeCode>());
    	EasyMock.replay(mockCoreService);
    	EasyMock.replay(mockBookService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_EDIT, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	assertTrue(bindingResult.hasErrors());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockCoreService);
		EasyMock.verify(mockBookService);
	}
	
	private void checkInitialValuesDynamicContentForEdit(Map<String,Object> model) {
		int numNameLines = Integer.valueOf(model.get(WebConstants.KEY_NUMBER_OF_NAME_LINES).toString());
        int numAuthors = Integer.valueOf(model.get(WebConstants.KEY_NUMBER_OF_AUTHORS).toString());
        int numFrontMatter = Integer.valueOf(model.get(WebConstants.KEY_NUMBER_OF_FRONT_MATTERS).toString());
        boolean isPublished = Boolean.parseBoolean(model.get(WebConstants.KEY_IS_PUBLISHED).toString());
        assertEquals(1, numNameLines);
        assertEquals(0, numAuthors);
        assertEquals(0, numFrontMatter);
        assertEquals(false, isPublished);
	}
	
}
