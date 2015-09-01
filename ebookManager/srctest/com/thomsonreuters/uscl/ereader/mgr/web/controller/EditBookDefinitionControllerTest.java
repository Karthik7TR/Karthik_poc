/*
 * Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionLockService;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionFormValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionService;

public class EditBookDefinitionControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+EditBookDefinitionForm.FORM_NAME;
	private static final long BOOK_DEFINITION_ID = 1;
	private static List<KeywordTypeCode> KEYWORD_CODES = new ArrayList<KeywordTypeCode>();
	
	private BookDefinitionLock BOOK_DEFINITION_LOCK;
    private EditBookDefinitionController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private BookDefinitionService mockBookDefinitionService;
    private CodeService mockCodeService;
    private JobRequestService mockJobRequestService;
    private EditBookDefinitionService mockEditBookDefinitionService;
    private EBookAuditService mockAuditService;
    private BookDefinitionLockService mockLockService;
    private EditBookDefinitionFormValidator validator;
    private MiscConfigSyncService mockMiscConfigService;
    
    private EbookName bookName;
    private DocumentTypeCode documentTypeCode;
    private PublisherCode publisherCode;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the services
    	this.mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
    	this.mockCodeService = EasyMock.createMock(CodeService.class);
    	this.mockEditBookDefinitionService = EasyMock.createMock(EditBookDefinitionService.class);
    	this.mockJobRequestService = EasyMock.createMock(JobRequestService.class);
    	this.mockAuditService = EasyMock.createMock(EBookAuditService.class);
    	this.mockLockService = EasyMock.createMock(BookDefinitionLockService.class);
    	this.mockMiscConfigService = EasyMock.createMock(MiscConfigSyncService.class);
    	
    	List<String> frontMatterThemes = new ArrayList<String>();
    	frontMatterThemes.add("WestLaw Next");
    	
    	EasyMock.expect(mockEditBookDefinitionService.getFrontMatterThemes()).andReturn(frontMatterThemes);
    	
    	// Set up the controller
    	this.controller = new EditBookDefinitionController();
    	controller.setEditBookDefinitionService(mockEditBookDefinitionService);
    	controller.setBookDefinitionService(mockBookDefinitionService);
    	controller.setJobRequestService(mockJobRequestService);
    	controller.setAuditService(mockAuditService);
    	controller.setBookLockService(mockLockService);
    	controller.setMiscConfigSyncService(mockMiscConfigService);
    	
    	validator = new EditBookDefinitionFormValidator();
    	validator.setBookDefinitionService(mockBookDefinitionService);
    	validator.setCodeService(mockCodeService);
    	validator.setEnvironmentName("workstation");
    	controller.setValidator(validator);	
    	
    	bookName = new EbookName();
    	bookName.setBookNameText("Book Name");
    	bookName.setEbookNameId(Integer.parseInt("1"));
    	bookName.setSequenceNum(1);
    	
    	documentTypeCode = new DocumentTypeCode();
    	documentTypeCode.setId(Long.parseLong("1"));
    	documentTypeCode.setAbbreviation(WebConstants.DOCUMENT_TYPE_ANALYTICAL_ABBR);
    	documentTypeCode.setName(WebConstants.DOCUMENT_TYPE_ANALYTICAL);
    	
    	publisherCode = new PublisherCode();
    	publisherCode.setId(1L);
    	publisherCode.setName("uscl");
    	
    	BOOK_DEFINITION_LOCK = new BookDefinitionLock();
    	BOOK_DEFINITION_LOCK.setCheckoutTimestamp(new Date());
    	BOOK_DEFINITION_LOCK.setEbookDefinitionLockId(1L);
    	BOOK_DEFINITION_LOCK.setFullName("name");
    	BOOK_DEFINITION_LOCK.setUsername("username");
	}

	/**
     * Test the GET to the Create Book Definition page
     */
	@Test
	public void testCreateBookDefintionGet() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_CREATE);
    	request.setMethod(HttpMethod.GET.name());
    	
    	setupDropdownMenuAndKeywords(1);
    	
    	MiscConfig miscConfig = new MiscConfig();
		EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
		EasyMock.replay(mockMiscConfigService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_CREATE, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        checkInitialValuesDynamicContent(model);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockEditBookDefinitionService);
		EasyMock.verify(mockMiscConfigService);
	}

	/**
     * Test the POST to the Create Book Definition page when there are validation errors
     */
	@Test
	public void testCreateBookDefintionPostFailed() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	
    	setupDropdownMenuAndKeywords(1);
    	
    	MiscConfig miscConfig = new MiscConfig();
		EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
		EasyMock.replay(mockMiscConfigService);

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_CREATE, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        checkInitialValuesDynamicContent(model);
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	assertTrue(bindingResult.hasErrors());
	       
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockEditBookDefinitionService);
		EasyMock.verify(mockMiscConfigService);
	}
	
	/**
     * Test the POST to the Create Book Definition page when titleId is complete and Definition in incomplete state
     */
	@Test
	public void testCreateBookDefintionPostIncompleteSuccess() {
		String titleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("contentTypeId", "1");
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", titleId);

    	setupDropdownMenuAndKeywords(2);
    	
    	BookDefinition expectedBook = createBookDef(titleId);
    	EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class))).andReturn(expectedBook);
    	setupMockServices(null, 1, false);
    	
    	mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
    	EasyMock.replay(mockAuditService);
    	
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
		EasyMock.verify(mockCodeService);
		EasyMock.verify(mockAuditService);
	}
	
	/**
     * Test the POST to the Create Book Definition page when titleId is complete but no other 
     * required fields are complete while marked as complete
     */
	@Test
	public void testCreateBookDefintionPostCompleteStateFailed() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_CREATE);
    	request.setMethod(HttpMethod.POST.name());
		request.setParameter("contentTypeId", "1");
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", "uscl/an/abcd");
    	request.setParameter("isComplete", "true");

    	setupDropdownMenuAndKeywords(1);
    	setupMockServices(null, 1, true);
    	
    	MiscConfig miscConfig = new MiscConfig();
		EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
		EasyMock.replay(mockMiscConfigService);
    	
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
	    	
	    	checkInitialValuesDynamicContent(model);
	       
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockCodeService);
		EasyMock.verify(mockEditBookDefinitionService);
		EasyMock.verify(mockMiscConfigService);
	}
	
	/**
     * Test the POST to the Create Book Definition page when titleId is complete and Definition in incomplete state
     */
	@Test
	public void testCreateBookDefintionPostCompleteStateSuccess() {
		String titleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("contentTypeId", "1");
    	request.setParameter("ProviewDisplayName", "Name in Proview");
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", titleId);
    	request.setParameter("frontMatterTitle.bookNameText", "title name");
    	request.setParameter("frontMatterTitle.sequenceNum", "1");
    	request.setParameter("copyright", "Somethings");
    	request.setParameter("materialId", "123456789012345678");
    	request.setParameter("sourceType", "TOC");
    	request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
    	request.setParameter("docCollectionName", "sdfdsfdsf");
    	request.setParameter("tocCollectionName", "sdfdsfdsf");
    	request.setParameter("isbn", "978-193-5-18235-1");
    	request.setParameter("isComplete", "true");
    	request.setParameter("validateForm", "false");
    	
    	setupDropdownMenuAndKeywords(2);

    	BookDefinition expectedBook = createBookDef(titleId);
    	EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class))).andReturn(expectedBook);
    	setupMockServices(null, 1, true);
    	
    	mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
    	EasyMock.replay(mockAuditService);
    	
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
		
		EasyMock.verify(mockAuditService);
		EasyMock.verify(mockBookDefinitionService);
	}

	/**
     * Test the GET to the Edit Book Definition page
     */
	@Test
	public void testEditBookDefintionGet() {
		String fullyQualifiedTitleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_EDIT);
		request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
    	request.setMethod(HttpMethod.GET.name());
    	
    	BookDefinition book = createBookDef(fullyQualifiedTitleId);
    	
    	setupDropdownMenuAndKeywords(2);
    	
    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
		EasyMock.replay(mockBookDefinitionService);
		
		EasyMock.expect(mockJobRequestService.isBookInJobRequest(BOOK_DEFINITION_ID)).andReturn(false);
		EasyMock.replay(mockJobRequestService);
		
		MiscConfig miscConfig = new MiscConfig();
		EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
		EasyMock.expectLastCall().times(3);
		EasyMock.replay(mockMiscConfigService);
		
		
		
		BOOK_DEFINITION_LOCK.setEbookDefinition(book);
		EasyMock.expect(mockLockService.findBookLockByBookDefinition(book)).andReturn(null);
		mockLockService.lockBookDefinition(book, null, null);
		EasyMock.replay(mockLockService);
		
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_EDIT, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        checkInitialValuesDynamicContentForPublished(model);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockJobRequestService);
		EasyMock.verify(mockEditBookDefinitionService);
		EasyMock.verify(mockLockService);
		EasyMock.verify(mockMiscConfigService);
	}
	
	/**
     * Test the GET to the Edit Book Definition page for invalid book
     */
	@Test
	public void testEditBookDefintionGetInvalidBook() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_EDIT);
		request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
    	request.setMethod(HttpMethod.GET.name());
    	
    	setupDropdownMenuAndKeywords(2);
    	
    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(null);
		EasyMock.replay(mockBookDefinitionService);
		
		MiscConfig miscConfig = new MiscConfig();
		EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(mockMiscConfigService);
    	
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
		
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockEditBookDefinitionService);
		EasyMock.verify(mockMiscConfigService);
	}
	
	/**
     * Test the GET to the Edit Book Definition page for deleted book
     */
	@Test
	public void testEditBookDefintionGetDeletedBook() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_EDIT);
		request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
    	request.setMethod(HttpMethod.GET.name());
    	
    	BookDefinition book = new BookDefinition();
    	book.setEbookDefinitionId(BOOK_DEFINITION_ID);
    	book.setIsDeletedFlag(true);
    	
    	setupDropdownMenuAndKeywords(2);
    	
    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
		EasyMock.replay(mockBookDefinitionService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
			// Verify mav is a RedirectView
			View view = mav.getView();
	        assertEquals(RedirectView.class, view.getClass());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
     * Test the GET to the Edit Book Definition page when locked
     */
	@Test
	public void testEditBookDefintionLocked() {
		String fullyQualifiedTitleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_EDIT);
		request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
    	request.setMethod(HttpMethod.GET.name());
    	
    	BookDefinition book = createBookDef(fullyQualifiedTitleId);
    	
    	setupDropdownMenuAndKeywords(2);
    	
    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
		EasyMock.replay(mockBookDefinitionService);

		EasyMock.expect(mockLockService.findBookLockByBookDefinition(book)).andReturn(BOOK_DEFINITION_LOCK);
		EasyMock.replay(mockLockService);
		
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_ERROR_LOCKED, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        BookDefinition actualBook = (BookDefinition) model.get(WebConstants.KEY_BOOK_DEFINITION);
	        BookDefinitionLock actualLock = (BookDefinitionLock) model.get(WebConstants.KEY_BOOK_DEFINITION_LOCK);
	        
	        Assert.assertEquals(book, actualBook);
	        Assert.assertEquals(BOOK_DEFINITION_LOCK, actualLock);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockLockService);
	}
	
	/**
     * Test the POST to the Edit Book Definition page
     */
	@Test
	public void testEditBookDefintionPOST() {
		String fullyQualifiedTitleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_EDIT);
		request.setParameter("ProviewDisplayName", "Name in Proview");
		request.setParameter("contentTypeId", "1");
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", fullyQualifiedTitleId);
    	request.setParameter("frontMatterTitle.bookNameText", "title name");
    	request.setParameter("frontMatterTitle.sequenceNum", "1");
    	request.setParameter("copyright", "Somethings");
    	request.setParameter("materialId", "123456789012345678");
    	request.setParameter("sourceType", "TOC");
    	request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
    	request.setParameter("docCollectionName", "sdfdsfdsf");
    	request.setParameter("tocCollectionName", "sdfdsfdsf");
    	request.setParameter("isbn", "978-193-5-18235-1");
    	request.setParameter("isComplete", "true");
    	request.setParameter("validateForm", "false");
		request.setParameter("bookdefinitionId", Long.toString(BOOK_DEFINITION_ID));
    	request.setMethod(HttpMethod.POST.name());
    	
    	BookDefinition book = createBookDef(fullyQualifiedTitleId);
    	EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class))).andReturn(book);
    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book).times(2);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockLockService.findBookLockByBookDefinition(book)).andReturn(null);
    	mockLockService.removeLock(book);
		EasyMock.replay(mockLockService);
		
		MiscConfig miscConfig = new MiscConfig();
		EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(mockMiscConfigService);
    	
    	DocumentTypeCode code = new DocumentTypeCode();
		code.setId(Long.parseLong("1"));
		code.setAbbreviation("an");
		code.setName("Analytical");
		EasyMock.expect(mockCodeService.getDocumentTypeCodeById(BOOK_DEFINITION_ID)).andReturn(code);
		EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
		EasyMock.replay(mockCodeService);
		
		setupDropdownMenuAndKeywords(2);
		
		mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
    	EasyMock.replay(mockAuditService);

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
		
		EasyMock.verify(mockAuditService);
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockCodeService);
		EasyMock.verify(mockLockService);
		EasyMock.verify(mockMiscConfigService);
	}
	
	/**
     * Test the POST to the Edit Book Definition
     * Saving in Complete state with no name line
     */
	@Test
	public void testEditBookDefintionPOSTFailed() {
		String fullyQualifiedTitleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_EDIT);
		request.setParameter("contentTypeId", "1");
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", fullyQualifiedTitleId);
    	request.setParameter("copyright", "Somethings");
    	request.setParameter("materialId", "123456789012345678");
    	request.setParameter("sourceType", "TOC");
    	request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
    	request.setParameter("tocCollectionName", "sdfdsfdsf");
    	request.setParameter("isbn", "978-193-5-18235-1");
    	request.setParameter("isComplete", "true");
    	request.setParameter("validateForm", "false");
		request.setParameter("bookdefinitionId", Long.toString(BOOK_DEFINITION_ID));
    	request.setMethod(HttpMethod.POST.name());
    	
    	BookDefinition book = createBookDef(fullyQualifiedTitleId);
    	
    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book).times(2);
    	EasyMock.replay(mockBookDefinitionService);
    	DocumentTypeCode code = new DocumentTypeCode();
		code.setId(Long.parseLong("1"));
		code.setAbbreviation("an");
		code.setName("Analytical");
		EasyMock.expect(mockCodeService.getDocumentTypeCodeById(BOOK_DEFINITION_ID)).andReturn(code);
		EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
		EasyMock.replay(mockCodeService);
		
		EasyMock.expect(mockJobRequestService.isBookInJobRequest(BOOK_DEFINITION_ID)).andReturn(false);
		EasyMock.replay(mockJobRequestService);
		
		EasyMock.expect(mockLockService.findBookLockByBookDefinition(book)).andReturn(null);
		EasyMock.replay(mockLockService);
		
		MiscConfig miscConfig = new MiscConfig();
		EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(mockMiscConfigService);
		
		setupDropdownMenuAndKeywords(1);
    	
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
		
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockCodeService);
		EasyMock.verify(mockJobRequestService);
		EasyMock.verify(mockEditBookDefinitionService);
		EasyMock.verify(mockLockService);
		EasyMock.verify(mockMiscConfigService);
	}
	
	/**
     * Test the POST to the Edit Book Definition page when book is locked
     * by another user
     */
	@Test
	public void testEditBookDefintionLockedPOST() {
		String fullyQualifiedTitleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_EDIT);
		request.setParameter("ProviewDisplayName", "Name in Proview");
		request.setParameter("contentTypeId", "1");
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", fullyQualifiedTitleId);
    	request.setParameter("frontMatterTitle.bookNameText", "title name");
    	request.setParameter("frontMatterTitle.sequenceNum", "1");
    	request.setParameter("copyright", "Somethings");
    	request.setParameter("materialId", "123456789012345678");
    	request.setParameter("sourceType", "TOC");
    	request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
    	request.setParameter("docCollectionName", "sdfdsfdsf");
    	request.setParameter("tocCollectionName", "sdfdsfdsf");
    	request.setParameter("isbn", "978-193-5-18235-1");
    	request.setParameter("isComplete", "true");
    	request.setParameter("validateForm", "false");
		request.setParameter("bookdefinitionId", Long.toString(BOOK_DEFINITION_ID));
    	request.setMethod(HttpMethod.POST.name());
    	
    	BookDefinition book = createBookDef(fullyQualifiedTitleId);
    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book).times(2);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	DocumentTypeCode code = new DocumentTypeCode();
		code.setId(Long.parseLong("1"));
		code.setAbbreviation("an");
		code.setName("Analytical");
		EasyMock.expect(mockCodeService.getDocumentTypeCodeById(BOOK_DEFINITION_ID)).andReturn(code);
		EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
		EasyMock.replay(mockCodeService);
    	
    	EasyMock.expect(mockLockService.findBookLockByBookDefinition(book)).andReturn(BOOK_DEFINITION_LOCK);
		EasyMock.replay(mockLockService);
		
		setupDropdownMenuAndKeywords(2);

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_ERROR_LOCKED, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        BookDefinition actualBook = (BookDefinition) model.get(WebConstants.KEY_BOOK_DEFINITION);
	        BookDefinitionLock actualLock = (BookDefinitionLock) model.get(WebConstants.KEY_BOOK_DEFINITION_LOCK);
	        
	        Assert.assertEquals(book, actualBook);
	        Assert.assertEquals(BOOK_DEFINITION_LOCK, actualLock);
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	Assert.assertFalse(bindingResult.hasErrors());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockLockService);
		EasyMock.verify(mockCodeService);
	}
	
	
	/**
     * Test the GET to the Copy Book Definition page
     */
	@Test
	public void testCopyBookDefintionGet() {
		String fullyQualifiedTitleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_COPY);
		request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
    	request.setMethod(HttpMethod.GET.name());
    	
    	BookDefinition book = createBookDef(fullyQualifiedTitleId);
    	
    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
		EasyMock.replay(mockBookDefinitionService);
		
		MiscConfig miscConfig = new MiscConfig();
		EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
		EasyMock.replay(mockMiscConfigService);
		
		setupDropdownMenuAndKeywords(2);
		
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_COPY, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        checkInitialValuesDynamicContent(model);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockEditBookDefinitionService);
		EasyMock.verify(mockMiscConfigService);
	}
	
	/**
     * Test the GET to the Copy Book Definition page for deleted book
     */
	@Test
	public void testCopyBookDefintionGetDeletedBook() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_COPY);
		request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
    	request.setMethod(HttpMethod.GET.name());
    	
    	BookDefinition book = new BookDefinition();
    	book.setEbookDefinitionId(BOOK_DEFINITION_ID);
    	book.setIsDeletedFlag(true);
    	
    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
		EasyMock.replay(mockBookDefinitionService);
		
		setupDropdownMenuAndKeywords(2);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
			// Verify mav is a RedirectView
			View view = mav.getView();
	        assertEquals(RedirectView.class, view.getClass());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockBookDefinitionService);
	}

	/**
     * Test the POST to the Copy Book Definition page when there are validation errors
     */
	@Test
	public void testCopyBookDefintionPostFailed() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_COPY);
    	request.setMethod(HttpMethod.POST.name());
    	
    	setupDropdownMenuAndKeywords(1);
    	
    	MiscConfig miscConfig = new MiscConfig();
		EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
		EasyMock.replay(mockMiscConfigService);

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_COPY, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        checkInitialValuesDynamicContent(model);
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	assertTrue(bindingResult.hasErrors());
	       
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockEditBookDefinitionService);
		EasyMock.verify(mockMiscConfigService);
	}
	
	/**
     * Test the POST to the Copy Book Definition page when titleId is complete and Definition in incomplete state
     */
	@Test
	public void testCopyBookDefintionPostIncompleteSuccess() {
		String titleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_COPY);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("contentTypeId", "1");
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", titleId);

    	
    	BookDefinition expectedBook = createBookDef(titleId);
    	EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class))).andReturn(expectedBook);
    	setupMockServices(null, 1, false);
    	
    	mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
    	EasyMock.replay(mockAuditService);
    	
    	setupDropdownMenuAndKeywords(2);
    	
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
		EasyMock.verify(mockAuditService);
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockCodeService);
	}
	
	/**
     * Test the POST to the Copy Book Definition page when titleId is complete but no other 
     * required fields are complete while marked as complete
     */
	@Test
	public void testCopyBookDefintionPostCompleteStateFailed() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_COPY);
    	request.setMethod(HttpMethod.POST.name());
		request.setParameter("contentTypeId", "1");
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", "uscl/an/abcd");
    	request.setParameter("isComplete", "true");

    	setupMockServices(null, 1, true);
    	setupDropdownMenuAndKeywords(1);
		
		MiscConfig miscConfig = new MiscConfig();
		EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
		EasyMock.replay(mockMiscConfigService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
			// Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_DEFINITION_COPY, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	assertTrue(bindingResult.hasErrors());
	    	
	    	checkInitialValuesDynamicContent(model);
	       
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockCodeService);
		EasyMock.verify(mockEditBookDefinitionService);
		EasyMock.verify(mockMiscConfigService);
	}
	
	/**
     * Test the POST to the Copy Book Definition page when titleId is complete and Definition in incomplete state
     */
	@Test
	public void testCopyBookDefintionPostCompleteStateSuccess() {
		String titleId = "uscl/an/abcd";
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_DEFINITION_COPY);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("contentTypeId", "1");
    	request.setParameter("ProviewDisplayName", "Name in Proview");
    	request.setParameter("pubAbbr", "abcd");
    	request.setParameter("publisher", "uscl");
    	request.setParameter("titleId", titleId);
    	request.setParameter("frontMatterTitle.bookNameText", "title name");
    	request.setParameter("frontMatterTitle.sequenceNum", "1");
    	request.setParameter("copyright", "Somethings");
    	request.setParameter("materialId", "123456789012345678");
    	request.setParameter("sourceType", "TOC");
    	request.setParameter("rootTocGuid", "a12345678123456781234567812345678");
    	request.setParameter("docCollectionName", "sdfdsfdsf");
    	request.setParameter("tocCollectionName", "sdfdsfdsf");
    	request.setParameter("isbn", "978-193-5-18235-1");
    	request.setParameter("isComplete", "true");
    	request.setParameter("validateForm", "false");

    	
    	BookDefinition expectedBook = createBookDef(titleId);
    	EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class))).andReturn(expectedBook);
    	setupMockServices(null, 1, true);
    	
    	mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
    	EasyMock.replay(mockAuditService);
    	
    	setupDropdownMenuAndKeywords(2);
    	
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
		EasyMock.verify(mockAuditService);
		EasyMock.verify(mockBookDefinitionService);
	}

	
	private void checkInitialValuesDynamicContentForPublished(Map<String,Object> model) {
		boolean isPublished = Boolean.parseBoolean(model.get(WebConstants.KEY_IS_PUBLISHED).toString());
		assertEquals(false, isPublished);
		
		checkInitialValuesDynamicContent(model);
	}
	
	private void checkInitialValuesDynamicContent(Map<String,Object> model) {
        int numAuthors = Integer.valueOf(model.get(WebConstants.KEY_NUMBER_OF_AUTHORS).toString());
        int splitDocs = Integer.valueOf(model.get(WebConstants.KEY_NUMBER_OF_SPLIT_DOCUMENTS).toString());
        assertEquals(0, numAuthors);
        assertEquals(0, splitDocs);
	}
	
	
	
	private void setupMockServices(BookDefinition book, int times, boolean isComplete) {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(book).times(times);
    	EasyMock.replay(mockBookDefinitionService);
		
		DocumentTypeCode code = new DocumentTypeCode();
		code.setId(Long.parseLong("1"));
		code.setAbbreviation("an");
		code.setName("Analytical");
		EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(code);
		if(isComplete) {
			EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(KEYWORD_CODES);
		}
		EasyMock.replay(mockCodeService);
	}
	
	private BookDefinition createBookDef(String fullyQualifiedTitleId) {
		BookDefinition book = new BookDefinition();
		book.setEbookDefinitionId(BOOK_DEFINITION_ID);
    	book.setFullyQualifiedTitleId(fullyQualifiedTitleId);
    	book.setDocumentTypeCodes(documentTypeCode);
    	book.setPublisherCodes(publisherCode);
    	List<EbookName> names = new ArrayList<EbookName>();
    	names.add(bookName);
    	book.setEbookNames(names);
    	book.setCopyright("something");
    	book.setSourceType(SourceType.NORT);
    	book.setIsDeletedFlag(false);
    	book.setEbookDefinitionCompleteFlag(false);
    	book.setAutoUpdateSupportFlag(true);
    	book.setSearchIndexFlag(true);
    	book.setPublishedOnceFlag(false);
    	book.setOnePassSsoLinkFlag(true);
    	book.setKeyciteToplineFlag(true);
    	book.setIsAuthorDisplayVertical(true);
    	book.setEnableCopyFeatureFlag(false);
    	book.setIsSplitBook(false);
    	book.setIsSplitTypeAuto(true);
    	return book;
	}
	
	private void setupDropdownMenuAndKeywords(int keywordCodeTimes) {
		EasyMock.expect(mockEditBookDefinitionService.getStates()).andReturn(null);
    	EasyMock.expect(mockEditBookDefinitionService.getDocumentTypes()).andReturn(null);
    	EasyMock.expect(mockEditBookDefinitionService.getJurisdictions()).andReturn(null);
    	EasyMock.expect(mockEditBookDefinitionService.getKeywordCodes()).andReturn(new ArrayList<KeywordTypeCode>()).times(keywordCodeTimes);
    	EasyMock.expect(mockEditBookDefinitionService.getPublishers()).andReturn(null);
    	EasyMock.expect(mockEditBookDefinitionService.getPubTypes()).andReturn(null);
    	EasyMock.replay(mockEditBookDefinitionService);
	}
	
}
