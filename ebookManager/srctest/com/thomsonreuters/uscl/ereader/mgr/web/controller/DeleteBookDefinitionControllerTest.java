/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
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

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionLockService;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete.DeleteBookDefinitionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete.DeleteBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete.DeleteBookDefinitionFormValidator;

public class DeleteBookDefinitionControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+DeleteBookDefinitionForm.FORM_NAME;
	private BookDefinition BOOK_DEFINITION = new BookDefinition();
	private static final Long BOOK_DEFINITION_ID = 1L;
	private BookDefinitionLock BOOK_DEFINITION_LOCK;
    private DeleteBookDefinitionController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    
    private BookDefinitionService mockBookDefinitionService;
    private JobRequestService mockJobRequestService;
    private EBookAuditService mockAuditService;
    private BookDefinitionLockService mockLockService;
    
    private DeleteBookDefinitionFormValidator validator;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the dashboard service
    	this.mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
    	this.mockJobRequestService = EasyMock.createMock(JobRequestService.class);
    	this.mockAuditService = EasyMock.createMock(EBookAuditService.class);
    	this.mockLockService = EasyMock.createMock(BookDefinitionLockService.class);
    	
    	// Set up the controller
    	this.controller = new DeleteBookDefinitionController();
    	controller.setBookDefinitionService(mockBookDefinitionService);
    	controller.setJobRequestService(mockJobRequestService);
    	controller.setAuditService(mockAuditService);
    	controller.setBookDefinitionLockService(mockLockService);
    	
    	validator = new DeleteBookDefinitionFormValidator();
    	validator.setBookDefinitionLockService(mockLockService);
    	validator.setJobRequestService(mockJobRequestService);
    	controller.setValidator(validator);	
    	
    	BOOK_DEFINITION_LOCK = new BookDefinitionLock();
    	BOOK_DEFINITION_LOCK.setCheckoutTimestamp(new Date());
    	BOOK_DEFINITION_LOCK.setEbookDefinitionLockId(1L);
    	BOOK_DEFINITION_LOCK.setFullName("name");
    	BOOK_DEFINITION_LOCK.setUsername("username");
    	
    	
    	BOOK_DEFINITION.setEbookDefinitionId(BOOK_DEFINITION_ID);
    	BOOK_DEFINITION.setEbookDefinitionId(BOOK_DEFINITION_ID);
    	BOOK_DEFINITION.setFullyQualifiedTitleId("something");
    	BOOK_DEFINITION.setCopyright("something");
    	BOOK_DEFINITION.setSourceType(SourceType.NORT);
    	BOOK_DEFINITION.setIsDeletedFlag(false);
    	BOOK_DEFINITION.setEbookDefinitionCompleteFlag(false);
    	BOOK_DEFINITION.setAutoUpdateSupportFlag(true);
    	BOOK_DEFINITION.setSearchIndexFlag(true);
    	BOOK_DEFINITION.setPublishedOnceFlag(false);
    	BOOK_DEFINITION.setOnePassSsoLinkFlag(true);
    	BOOK_DEFINITION.setKeyciteToplineFlag(true);
    	BOOK_DEFINITION.setIsAuthorDisplayVertical(true);
    	BOOK_DEFINITION.setEnableCopyFeatureFlag(false);
    	BOOK_DEFINITION.setDocumentTypeCodes(new DocumentTypeCode());
    	BOOK_DEFINITION.setPublisherCodes(new PublisherCode());
	}

	/**
     * Test the GET to the Delete Book Definition page
     */
	@Test
	public void testDeleteBookDefintionGet() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_DELETE);
		request.setParameter("id", BOOK_DEFINITION_ID.toString());
    	request.setMethod(HttpMethod.GET.name());

    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(BOOK_DEFINITION);
    	EasyMock.expect(mockJobRequestService.isBookInJobRequest(BOOK_DEFINITION_ID)).andReturn(false);
    	EasyMock.expect(mockLockService.findBookLockByBookDefinition(BOOK_DEFINITION)).andReturn(null);
    	EasyMock.replay(mockBookDefinitionService);
    	EasyMock.replay(mockJobRequestService);
    	EasyMock.replay(mockLockService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_DELETE, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        
	        boolean isInQueue =Boolean.valueOf(model.get(WebConstants.KEY_IS_IN_JOB_REQUEST).toString());
	        BookDefinitionLock lock = (BookDefinitionLock) model.get(WebConstants.KEY_BOOK_DEFINITION_LOCK);
	        
	        Assert.assertEquals(false, isInQueue);
	        Assert.assertEquals(lock, null);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockBookDefinitionService);
    	EasyMock.verify(mockJobRequestService);
    	EasyMock.verify(mockLockService);
	}

	/**
     * Test the POST to the Delete Book Definition page when there are validation errors
     */
	@Test
	public void testDeleteBookDefintionPostFailed() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_DELETE);
		request.setParameter("id", BOOK_DEFINITION_ID.toString());
		request.setParameter("Action", DeleteBookDefinitionForm.Action.DELETE.toString());
    	request.setMethod(HttpMethod.POST.name());
    	
    	EasyMock.expect(mockJobRequestService.findJobRequestByBookDefinitionId(BOOK_DEFINITION_ID)).andReturn(null);
    	EasyMock.expect(mockLockService.findBookLockByBookDefinition(BOOK_DEFINITION)).andReturn(null);
    	EasyMock.replay(mockJobRequestService);
    	EasyMock.replay(mockLockService);

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_DELETE, mav.getViewName());
	        
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
		
		EasyMock.verify(mockJobRequestService);
    	EasyMock.verify(mockLockService);
	}
	
	/**
     * Test the POST to the Delete Book Definition page when success
     * Permanent delete from database.
     */
	@Test
	public void testDeleteBookDefintionPostSuccessPermanent() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_DELETE);
		request.setParameter("id", BOOK_DEFINITION_ID.toString());
		request.setParameter("comment", "Delete");
		request.setParameter("code", "DELETE BOOK");
		request.setParameter("Action", DeleteBookDefinitionForm.Action.DELETE.toString());
    	request.setMethod(HttpMethod.POST.name());
    	
    	BOOK_DEFINITION.setPublishedOnceFlag(false);

    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(BOOK_DEFINITION);
    	mockBookDefinitionService.removeBookDefinition(BOOK_DEFINITION_ID);
    	EasyMock.expect(mockJobRequestService.findJobRequestByBookDefinitionId(BOOK_DEFINITION_ID)).andReturn(null);
    	EasyMock.expect(mockLockService.findBookLockByBookDefinition(BOOK_DEFINITION)).andReturn(null);
    	EasyMock.replay(mockBookDefinitionService);
    	EasyMock.replay(mockJobRequestService);
    	EasyMock.replay(mockLockService);
    	
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
		
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockJobRequestService);
    	EasyMock.verify(mockLockService);
	}
	
	/**
     * Test the POST to the Delete Book Definition page when success
     * Soft delete.
     */
	@Test
	public void testDeleteBookDefintionPostSuccessSoft() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_DELETE);
		request.setParameter("id", BOOK_DEFINITION_ID.toString());
		request.setParameter("comment", "Delete");
		request.setParameter("code", "DELETE BOOK");
		request.setParameter("Action", DeleteBookDefinitionForm.Action.DELETE.toString());
    	request.setMethod(HttpMethod.POST.name());
    	
    	BOOK_DEFINITION.setPublishedOnceFlag(true);

    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(BOOK_DEFINITION);
    	mockBookDefinitionService.updateDeletedStatus(BOOK_DEFINITION_ID, true);
    	EasyMock.expect(mockJobRequestService.findJobRequestByBookDefinitionId(BOOK_DEFINITION_ID)).andReturn(null);
    	EasyMock.expect(mockLockService.findBookLockByBookDefinition(BOOK_DEFINITION)).andReturn(null);
    	EasyMock.replay(mockBookDefinitionService);
    	EasyMock.replay(mockJobRequestService);
    	EasyMock.replay(mockLockService);
    	
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
		
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockJobRequestService);
    	EasyMock.verify(mockLockService);
	}
	
	
	/**
     * Test the GET to the Restore Book Definition page
     */
	@Test
	public void testRestoreBookDefintionGet() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_RESTORE);
		request.setParameter("id", BOOK_DEFINITION_ID.toString());
    	request.setMethod(HttpMethod.GET.name());

    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(BOOK_DEFINITION);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_RESTORE, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        
	        BookDefinition book = (BookDefinition) model.get(WebConstants.KEY_BOOK_DEFINITION);
	        
	        Assert.assertEquals(BOOK_DEFINITION, book);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockBookDefinitionService);
	}

	/**
     * Test the POST to the Restore Book Definition page when there are validation errors
     */
	@Test
	public void testRestoreBookDefintionPostFailed() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_RESTORE);
		request.setParameter("id", BOOK_DEFINITION_ID.toString());
		request.setParameter("Action", DeleteBookDefinitionForm.Action.RESTORE.toString());
    	request.setMethod(HttpMethod.POST.name());

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_RESTORE, mav.getViewName());
	        
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
	}
	
	/**
     * Test the POST to the Restore Book Definition page when success
     */
	@Test
	public void testRestoreBookDefintionPostSuccessPermanent() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_RESTORE);
		request.setParameter("id", BOOK_DEFINITION_ID.toString());
		request.setParameter("comment", "Restore");
		request.setParameter("Action", DeleteBookDefinitionForm.Action.RESTORE.toString());
    	request.setMethod(HttpMethod.POST.name());
    	
    	BOOK_DEFINITION.setPublishedOnceFlag(false);

    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(BOOK_DEFINITION);
    	mockBookDefinitionService.updateDeletedStatus(BOOK_DEFINITION_ID, false);
    	EasyMock.replay(mockBookDefinitionService);
    	
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
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
}
