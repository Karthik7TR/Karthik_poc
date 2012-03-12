/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.util.List;

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

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

@Controller
public class EditBookDefinitionController {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionController.class);

	private CoreService coreService;
	private CodeService codeService;
	private ProviewClient proviewClient;
	private Validator validator;

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
		
		initialize(model, form);
		model.addAttribute(WebConstants.KEY_IS_PUBLISHED, false);

		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_CREATE);
	}
	
	/**
	 * Handle the in-bound POST to the Book Definition create view page.
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_CREATE, method = RequestMethod.POST)
	public ModelAndView createBookDefintionPost(
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) {
		
		if(!bindingResult.hasErrors()) {
			// TODO: Update to go to the View Book Definition page 
			return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_LIBRARY_LIST));
		}
		
		initialize(model, form);
		model.addAttribute(WebConstants.KEY_IS_PUBLISHED, false);
				
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
		BookDefinition bookDef = coreService.findBookDefinitionByEbookDefId(id);
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
			// TODO: Update to go to the View Book Definition page 
			return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_LIBRARY_LIST));
		}
		
		// Lookup the book by its primary key
		BookDefinition bookDef = coreService.findBookDefinitionByEbookDefId(form.getBookdefinitionId());
		setupEditFormAndModel(bookDef, form, model);
		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
	}
	
	private void setupEditFormAndModel(BookDefinition bookDef, EditBookDefinitionForm form, Model model) throws Exception {
		boolean isInJobRequest;
		boolean isPublished;
		
		// Check if book is scheduled or queued
		// TODO: Update with queue checking
		if (bookDef != null) {
			isPublished = bookDef.IsPublishedOnceFlag();
			isInJobRequest = false;
			
			// Check proview if book went to final state if isPublished is false
			if(!isPublished) {
				if(proviewClient.hasTitleIdBeenPublished(bookDef.getTitleId())) {
					isPublished = true;
					//TODO: update book definition in db with isPublished to true
				}
			}
		} else {
			isInJobRequest = false;
			isPublished = false;
		}
		
		initialize(model, form);
		
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_ID, form.getBookdefinitionId());
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		model.addAttribute(WebConstants.KEY_IS_IN_JOB_REQUEST, isInJobRequest);
		model.addAttribute(WebConstants.KEY_IS_PUBLISHED, isPublished);
	}
	
	/**
	 * 
	 * @param contentTypeId
	 * @return String of Content Type abbreviation
	 */
	@RequestMapping(value=WebConstants.MVC_GET_CONTENT_TYPE_ABBR, method = RequestMethod.GET)
	public @ResponseBody DocumentTypeCode getContentTypeAbbr(@RequestParam Long contentTypeId) {
		DocumentTypeCode code = codeService.getDocumentTypeCodeById(contentTypeId);
	    return code;
	}
	
	/**
	 * Initializes the model and form values for the Create/Edit Book Definition View
	 * @param model
	 * @param form
	 */
	private void initialize(Model model, EditBookDefinitionForm form) {
		// Get Collection sizes to display on form
		model.addAttribute(WebConstants.KEY_NUMBER_OF_NAME_LINES,form.getNameLines().size());
		model.addAttribute(WebConstants.KEY_NUMBER_OF_AUTHORS,form.getAuthorInfo().size());
		model.addAttribute(WebConstants.KEY_NUMBER_OF_FRONT_MATTERS,form.getAdditionalFrontMatter().size());
		
		// Set drop down lists
		model.addAttribute(WebConstants.KEY_STATES, EditBookDefinitionForm.getStates());
		model.addAttribute(WebConstants.KEY_CONTENT_TYPES, EditBookDefinitionForm.getDocumentTypes());
		model.addAttribute(WebConstants.KEY_PUB_TYPES, EditBookDefinitionForm.getPubTypes());
		model.addAttribute(WebConstants.KEY_JURISDICTIONS, EditBookDefinitionForm.getJurisdictions());
		model.addAttribute(WebConstants.KEY_PUBLISHERS, EditBookDefinitionForm.getPublishers());
		model.addAttribute(WebConstants.KEY_KEYWORDS_TYPE, EditBookDefinitionForm.getKeywordCodes());
		
	}

	@Required
	public void setCoreService(CoreService service) {
		this.coreService = service;
	}
	
	@Required
	public void setCodeService(CodeService service) {
		this.codeService = service;
	}
	
	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
	
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
}
