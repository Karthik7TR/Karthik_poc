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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class EditBookDefinitionController {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionController.class);
	protected BookDefinitionService bookDefinitionService;
	protected JobRequestService jobRequestService;
	protected EditBookDefinitionService editBookDefinitionService;
	private EBookAuditService auditService;
	protected Validator validator;
	protected ProviewClient proviewClient;

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

		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
		form.initialize(bookDef);
		setupEditFormAndModel(bookDef, form, model);
		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
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
		
		if(!bindingResult.hasErrors()) {
			BookDefinition book = new BookDefinition();
			form.loadBookDefinition(book);
			book = bookDefinitionService.saveBookDefinition(book);
			
			// Save in Audit
			EbookAudit audit = new EbookAudit();
			audit.loadBookDefinition(book, EbookAudit.AUDIT_TYPE.EDIT, UserUtils.getAuthenticatedUserName(), form.getComment());
			auditService.saveEBookAudit(audit);
			
			// Redirect user
			String queryString = String.format("?%s=%s", WebConstants.KEY_ID, book.getEbookDefinitionId());
			return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_VIEW_GET+queryString));
		}
		
		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(form.getBookdefinitionId());
		setupEditFormAndModel(bookDef, form, model);
		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
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
		bookDef.setEbookDefinitionId(null);
		form.initialize(bookDef);
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
	
	/**
	 * AJAX call to get the DocumentTypeCode object as JSON
	 * @param contentTypeId
	 * @return String of Content Type abbreviation
	 */
	@RequestMapping(value=WebConstants.MVC_GET_CONTENT_TYPE, method = RequestMethod.GET)
	public @ResponseBody DocumentTypeCode getContentType(@RequestParam Long contentTypeId) {
	    return editBookDefinitionService.getContentTypeById(contentTypeId);
	}
	
	private void setupEditFormAndModel(BookDefinition bookDef, EditBookDefinitionForm form, Model model) throws Exception {
		boolean isInJobRequest = false;
		boolean isPublished = false;
		
		// Check if book is scheduled or queued
		if (bookDef != null) {
			isPublished = bookDef.IsPublishedOnceFlag();
			isInJobRequest =  jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId());
			
			// Check proview if book went to final state if isPublished is false
			if(!isPublished) {
				if(proviewClient.hasTitleIdBeenPublished(bookDef.getFullyQualifiedTitleId())) {
					// Save new Publish State if Title ID is found in ProView as Final 
					isPublished = true;
					bookDef.setPublishedOnceFlag(true);
					bookDefinitionService.saveBookDefinition(bookDef);
				}
			}
		}
		
		initializeModel(model, form);
		
		model.addAttribute(WebConstants.KEY_ID, form.getBookdefinitionId());
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		model.addAttribute(WebConstants.KEY_IS_IN_JOB_REQUEST, isInJobRequest);
		model.addAttribute(WebConstants.KEY_IS_PUBLISHED, isPublished);
	}
	
	/**
	 * Initializes the model for the Create/Edit Book Definition View
	 * @param model
	 * @param form
	 */
	protected void initializeModel(Model model, EditBookDefinitionForm form) {
		// Get Collection sizes to display on form
		model.addAttribute(WebConstants.KEY_NUMBER_OF_NAME_LINES,form.getNameLines().size());
		model.addAttribute(WebConstants.KEY_NUMBER_OF_AUTHORS,form.getAuthorInfo().size());
		
		// Set drop down lists
		model.addAttribute(WebConstants.KEY_STATES, editBookDefinitionService.getStates());
		model.addAttribute(WebConstants.KEY_CONTENT_TYPES, editBookDefinitionService.getDocumentTypes());
		model.addAttribute(WebConstants.KEY_PUB_TYPES, editBookDefinitionService.getPubTypes());
		model.addAttribute(WebConstants.KEY_JURISDICTIONS, editBookDefinitionService.getJurisdictions());
		model.addAttribute(WebConstants.KEY_PUBLISHERS, editBookDefinitionService.getPublishers());
		model.addAttribute(WebConstants.KEY_KEYWORDS_TYPE, editBookDefinitionService.getKeywordCodes());
		
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
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}

}
