package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

@Controller
public class EditBookDefinitionController {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionController.class);

	private CoreService coreService;
	
	/**
	 * Handle the in-bound GET to the Book Definition create view page.
	 * @param titleId the primary key of the book to be edited as a required query string parameter.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_CREATE_GET, method = RequestMethod.GET)
	public ModelAndView createBookDefintionGet(@ModelAttribute(EditBookDefinitionForm.FORM_NAME) EditBookDefinitionForm form,
				Model model) {
		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_CREATE);
	}
	
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_CREATE_POST, method = RequestMethod.POST)
	public ModelAndView createBookDefintionPost(@ModelAttribute(EditBookDefinitionForm.FORM_NAME) EditBookDefinitionForm form,
				Model model) {
		
		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_CREATE);
	}
	
	/**
	 * Handle the in-bound GET to the Book Definition create view page.
	 * @param titleId the primary key of the book to be edited as a required query string parameter.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_EDIT_GET, method = RequestMethod.GET)
	public ModelAndView editBookDefintionGet(@RequestParam String titleId,
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) EditBookDefinitionForm form,
				Model model) {

		
		// Lookup the book by its primary key
		BookDefinitionKey bookDefKey = new BookDefinitionKey(titleId);
		BookDefinition bookDef = coreService.findBookDefinition(bookDefKey);
		
		form.initialize(bookDef);
		
		model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
	}
	
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_EDIT_POST, method = RequestMethod.POST)
	public ModelAndView editBookDefintionPost(@RequestParam String titleId,
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) EditBookDefinitionForm form,
				Model model) {
		
		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
	}

	@Required
	public void setCoreService(CoreService service) {
		this.coreService = service;
	}
}
