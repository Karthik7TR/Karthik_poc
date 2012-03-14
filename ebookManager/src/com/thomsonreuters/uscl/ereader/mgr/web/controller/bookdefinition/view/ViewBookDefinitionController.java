/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm.Command;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

@Controller
public class ViewBookDefinitionController {
	private static final Logger log = Logger.getLogger(ViewBookDefinitionController.class);

	private CoreService coreService;
	private JobRequestService jobRequestService;
	
	/**
	 * Handle the in-bound GET to the Book Definition read-only view page.
	 * @param titleId the primary key of the book to be viewed as a required query string parameter.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET, method = RequestMethod.GET)
	public ModelAndView viewBookDefintion(@RequestParam Long id,
				@ModelAttribute(ViewBookDefinitionForm.FORM_NAME) ViewBookDefinitionForm form,
				Model model) {

		// Lookup the book by its primary key
		BookDefinition bookDef = coreService.findBookDefinitionByEbookDefId(id);
		form.setId(id);
		
		model.addAttribute(WebConstants.KEY_IS_IN_JOB_REQUEST, jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId()));
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_VIEW);
	}

	/**
	 * Handle press of one of the functional buttons at the bottom of the
	 * Book Defintion read-only view page.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_VIEW_POST, method = RequestMethod.POST)
	public ModelAndView doPost(@ModelAttribute(ViewBookDefinitionForm.FORM_NAME) ViewBookDefinitionForm form,
							   Model model) {
		ModelAndView mav = null;
		log.debug(form);
		String queryString = String.format("?%s=%s", WebConstants.KEY_ID, form.getId());
		Command command = form.getCommand();
		switch (command) {
			case DELETE:
	// TODO: implement this
				break;
			case EDIT:
				mav = new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_EDIT+queryString));
				break;
			case GENERATE:
				mav = new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW+queryString));
	// TODO: implement this
				break;
			case AUDIT_LOG:
	// TODO: implement this
				//mav = new ModelAndView(new RedirectView(WebConstants.MVC_AUDIT_LOG_TODO+queryString));
				break;
			case JOB_HISTORY:
				//mav = new ModelAndView(new RedirectView(WebConstants.MVC_JOB_HISTORY_TODO+queryString));
	// TODO: implement this
				break;
			default:
				throw new RuntimeException("Unexpected form command: " + command);
		}
		return mav;
	}
	
	@Required
	public void setCoreService(CoreService service) {
		this.coreService = service;
	}
	
	@Required
	public void setJobRequestService(JobRequestService service) {
		this.jobRequestService = service;
	}
}
