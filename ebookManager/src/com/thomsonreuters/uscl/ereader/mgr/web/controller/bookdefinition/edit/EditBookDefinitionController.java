/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AutoPopulatingList;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.service.BookService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

@Controller
public class EditBookDefinitionController {
	private static final Logger log = Logger.getLogger(EditBookDefinitionController.class);

	private CoreService coreService;
	private BookService bookService;
	private Validator validator;

	@InitBinder(EditBookDefinitionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setAutoGrowNestedPaths(false);
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
	
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_CREATE, method = RequestMethod.POST)
	public ModelAndView createBookDefintionPost(
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) {
		
		if(!bindingResult.hasErrors()) {
			
		}
		
		initializeModel(model, form);
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_CREATE);
	}
	
	/**
	 * Handle the in-bound GET to the Book Definition create view page.
	 * @param titleId the primary key of the book to be edited as a required query string parameter.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_EDIT, method = RequestMethod.GET)
	public ModelAndView editBookDefintionGet(
				@RequestParam String titleId,
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) {

		
		// Lookup the book by its primary key
		BookDefinitionKey bookDefKey = new BookDefinitionKey(titleId);
		BookDefinition bookDef = coreService.findBookDefinition(bookDefKey);
		
		// Load Authors
		List<Author> authors = new AutoPopulatingList<Author>(Author.class);
		authors.addAll(bookService.getAuthors(1));
		form.setAuthorInfo(authors);
		
		form.initialize(bookDef);
		initializeModel(model, form);
		
		model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
	}
	
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_EDIT, method = RequestMethod.POST)
	public ModelAndView editBookDefintionPost(
				@RequestParam String titleId,
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) {
		
		if(!bindingResult.hasErrors()) {
			
		}
		
		initializeModel(model, form);
		BookDefinitionKey bookDefKey = new BookDefinitionKey(titleId);
		BookDefinition bookDef = coreService.findBookDefinition(bookDefKey);
		
		model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
	}

	private void initializeModel(Model model, EditBookDefinitionForm form) {
		// Clear out emtpy authors and determine size
		form.removeEmptyAuthorRows();
		model.addAttribute(WebConstants.KEY_NUMBER_OF_AUTHORS,form.getAuthorInfo().size());
		
		model.addAttribute(WebConstants.KEY_STATES, EditBookDefinitionForm.getStates());
		model.addAttribute(WebConstants.KEY_CONTENT_TYPES, EditBookDefinitionForm.getContentTypes());
		model.addAttribute(WebConstants.KEY_PUB_TYPES, EditBookDefinitionForm.getPubTypes());
		model.addAttribute(WebConstants.KEY_JURISDICTIONS, EditBookDefinitionForm.getJurisdictions());
		model.addAttribute(WebConstants.KEY_PUBLISHERS, EditBookDefinitionForm.getPublishers());
				
		model.addAttribute(WebConstants.KEY_KEYWORDS_TYPE, bookService.getKeywordsTypesAndValues());
		
		// TODO: Update condition to check if book definition has been published
		model.addAttribute(WebConstants.KEY_IS_PUBLISHED, false);
		
		log.debug(form.getKeywords());
	}

	@Required
	public void setCoreService(CoreService service) {
		this.coreService = service;
	}
	
	@Required
	public void setBookService(BookService service) {
		this.bookService = service;
	}
	
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
}
