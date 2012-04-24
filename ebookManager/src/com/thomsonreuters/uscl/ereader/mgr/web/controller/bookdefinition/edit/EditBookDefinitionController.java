/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.text.ParseException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionLockService;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class EditBookDefinitionController {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionController.class);
	protected BookDefinitionService bookDefinitionService;
	protected JobRequestService jobRequestService;
	protected EditBookDefinitionService editBookDefinitionService;
	private EBookAuditService auditService;
	private BookDefinitionLockService bookLockService;
	protected Validator validator;

	@InitBinder(EditBookDefinitionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setAutoGrowNestedPaths(false);
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.setValidator(validator);
	}
	
	
	/**
	 * Handle the in-bound GET to the Book Definition create view page.
	 * @param titleId the primary key of the book to be edited as a required query string parameter.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_CREATE, method = RequestMethod.GET)
	public ModelAndView createBookDefintionGet(
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) {
		
		initializeModel(model, form);

		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_CREATE);
	}
	
	/**
	 * Handle the in-bound POST to the Book Definition create view page.
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_CREATE, method = RequestMethod.POST)
	public ModelAndView createBookDefintionPost(
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) throws ParseException {
		
		if(!bindingResult.hasErrors()) {
			BookDefinition book = new BookDefinition();
			form.loadBookDefinition(book);
			book = bookDefinitionService.saveBookDefinition(book);
			
			// Save in Audit
			EbookAudit audit = new EbookAudit();
			audit.loadBookDefinition(book, EbookAudit.AUDIT_TYPE.CREATE, UserUtils.getAuthenticatedUserName(), form.getComment());
			auditService.saveEBookAudit(audit);
			
			// Redirect user
			String queryString = String.format("?%s=%s", WebConstants.KEY_ID, book.getEbookDefinitionId());
			return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_VIEW_GET+queryString));
		}
		
		initializeModel(model, form);
				
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_CREATE);
	}
	
	/**
	 * Handle the in-bound GET to the Book Definition edit view page.
	 * @param titleId the primary key of the book to be edited as a required query string parameter.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_EDIT, method = RequestMethod.GET)
	public ModelAndView editBookDefintionGet(
				@RequestParam Long id,
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) throws Exception {
		
		String username = UserUtils.getAuthenticatedUserName();
		
		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
		
		// model used in VIEW_BOOK_DEFINITION_LOCKED and VIEW_BOOK_DEFINITION_EDIT
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);

		// Check if Book Definition is being edited by another user
		BookDefinitionLock lock = bookLockService.findBookLockByBookDefinition(bookDef);
		
		if(lock != null && !lock.getUsername().equalsIgnoreCase(username)) {
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_LOCK, lock);
			return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_LOCKED);
		} else {
			form.initialize(bookDef);
			checkJobRequestAndPublishStatus(bookDef, model);
			initializeModel(model, form);
			
			if(bookDef != null) {
				// Lock book definition
				bookLockService.lockBookDefinition(bookDef, username, UserUtils.getAuthenticatedUserFullName());
			}
			
			return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
		}
	}
	/**
	 * Handle the in-bound POST to the Book Definition edit view page.
	 * @param titleId
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_EDIT, method = RequestMethod.POST)
	public ModelAndView editBookDefintionPost(
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) throws Exception {
		
		Long bookDefinitionId = form.getBookdefinitionId();
		String username = UserUtils.getAuthenticatedUserName();
		
		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId);
		
		// model used in VIEW_BOOK_DEFINITION_LOCKED and VIEW_BOOK_DEFINITION_EDIT
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		
		// Check if Book Definition is being edited by another user
		BookDefinitionLock lock = bookLockService.findBookLockByBookDefinition(bookDef);
		
		if(lock != null && !lock.getUsername().equalsIgnoreCase(username)) {
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_LOCK, lock);
			return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_LOCKED);
		}
		
		if(!bindingResult.hasErrors()) {
			BookDefinition book = new BookDefinition();
			form.loadBookDefinition(book);
			book = bookDefinitionService.saveBookDefinition(book);
			
			// Save in Audit
			EbookAudit audit = new EbookAudit();
			audit.loadBookDefinition(book, EbookAudit.AUDIT_TYPE.EDIT, UserUtils.getAuthenticatedUserName(), form.getComment());
			auditService.saveEBookAudit(audit);
			
			// Remove lock from BookDefinition
			bookLockService.removeLock(book);
			
			// Redirect user
			String queryString = String.format("?%s=%s", WebConstants.KEY_ID, bookDefinitionId);
			return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_VIEW_GET+queryString));
		}
		
		checkJobRequestAndPublishStatus(bookDef, model);
		initializeModel(model, form);

		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
	}
	
	/**
	 * AJAX call to remove lock on book definition
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_UNLOCK, method = RequestMethod.GET)
	public void unlockBookDefinition(@RequestParam Long id) {
		String username = UserUtils.getAuthenticatedUserName();
		
		BookDefinition book = bookDefinitionService.findBookDefinitionByEbookDefId(id);
		
		if(book != null) {
			// Check if current user is the one with the lock
			BookDefinitionLock lock = bookLockService.findBookLockByBookDefinition(book);
			if(lock != null && lock.getUsername().equalsIgnoreCase(username)) {
				bookLockService.removeLock(book);
			}
		}
	}
	
	/**
	 * Handle the in-bound GET to the Book Definition copy view page.
	 * @param titleId the primary key of the book to be edited as a required query string parameter.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_COPY, method = RequestMethod.GET)
	public ModelAndView copyBookDefintionGet(
				@RequestParam Long id,
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) throws Exception {
		
		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
		form.copyBookDefinition(bookDef);
		initializeModel(model, form);

		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_COPY);
	}
	
	/**
	 * Handle the in-bound POST to the Book Definition copy view page.
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_COPY, method = RequestMethod.POST)
	public ModelAndView copyBookDefintionPost(
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) throws ParseException {
		
		if(!bindingResult.hasErrors()) {
			BookDefinition book = new BookDefinition();
			form.loadBookDefinition(book);
			book = bookDefinitionService.saveBookDefinition(book);
			
			// Save in Audit
			EbookAudit audit = new EbookAudit();
			audit.loadBookDefinition(book, EbookAudit.AUDIT_TYPE.CREATE, UserUtils.getAuthenticatedUserName(), form.getComment());
			auditService.saveEBookAudit(audit);
			
			// Redirect user
			String queryString = String.format("?%s=%s", WebConstants.KEY_ID, book.getEbookDefinitionId());
			return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_VIEW_GET+queryString));
		}
		
		initializeModel(model, form);
				
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_COPY);
	}
	
	private void checkJobRequestAndPublishStatus(BookDefinition bookDef, Model model) throws Exception {
		boolean isInJobRequest = false;
		boolean isPublished = false;
		
		// Check if book is scheduled or queued
		if (bookDef != null) {
			isPublished = bookDef.getPublishedOnceFlag();
			isInJobRequest =  jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId());
		}
		
		model.addAttribute(WebConstants.KEY_IS_IN_JOB_REQUEST, isInJobRequest);
		model.addAttribute(WebConstants.KEY_IS_PUBLISHED, isPublished);
	}
	
	
	
	/**
	 * Initializes the model for the Create/Edit Book Definition View
	 * @param model
	 * @param form
	 */
	private void initializeModel(Model model, EditBookDefinitionForm form) {
		// Get Collection sizes to display on form
		model.addAttribute(WebConstants.KEY_NUMBER_OF_AUTHORS,form.getAuthorInfo().size());
		model.addAttribute(WebConstants.KEY_NUMBER_OF_FRONT_MATTERS,form.getFrontMatters().size());
		
		// Set drop down lists
		model.addAttribute(WebConstants.KEY_STATES, editBookDefinitionService.getStates());
		model.addAttribute(WebConstants.KEY_CONTENT_TYPES, editBookDefinitionService.getDocumentTypes());
		model.addAttribute(WebConstants.KEY_PUB_TYPES, editBookDefinitionService.getPubTypes());
		model.addAttribute(WebConstants.KEY_JURISDICTIONS, editBookDefinitionService.getJurisdictions());
		model.addAttribute(WebConstants.KEY_PUBLISHERS, editBookDefinitionService.getPublishers());
		model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, editBookDefinitionService.getKeywordCodes());
	}

	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
	}
	
	@Required
	public void setEditBookDefinitionService(EditBookDefinitionService service) {
		this.editBookDefinitionService = service;
	}
	
	@Required
	public void setJobRequestService(JobRequestService service) {
		this.jobRequestService = service;
	}
	
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	
	@Required
	public void setAuditService(EBookAuditService service) {
		this.auditService = service;
	}
	
	@Required
	public void setBookLockService(BookDefinitionLockService service) {
		this.bookLockService = service;
	}

}
