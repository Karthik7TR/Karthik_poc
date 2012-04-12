/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view;

import org.apache.commons.lang.StringUtils;
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

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils.SecurityRole;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm.Command;

@Controller
public class ViewBookDefinitionController {
	private static final Logger log = Logger.getLogger(ViewBookDefinitionController.class);

	private BookDefinitionService bookDefinitionService;
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
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
		form.setId(id);
		
		if(bookDef != null){
			model.addAttribute(WebConstants.KEY_IS_IN_JOB_REQUEST, jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId()));
			formatTextAreaStrings(bookDef);
		}
		SecurityRole[] roles = { SecurityRole.ROLE_PUBLISHER, SecurityRole.ROLE_SUPERUSER, SecurityRole.ROLE_PUBLISHER_PLUS};
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		model.addAttribute(WebConstants.KEY_SUPER_PUBLISHER_PUBLISHERPLUS,
				UserUtils.isUserInRole(roles) 
					? "" : "disabled=\"disabled\"");
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
			case BOOK_JOB_HISTORY:
				mav = new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_JOB_HISTORY+queryString));
				break;
			case COPY:
				mav = new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_COPY+queryString));
				break;
			default:
				throw new RuntimeException("Unexpected form command: " + command);
		}
		return mav;
	}
	
	/**
	 * format strings to replace \n in text areas with <br>
	 * @param book
	 */
	private void formatTextAreaStrings(BookDefinition book) {
		book.setCurrency(StringUtils.replace(book.getCurrency(), "\n", "<br>"));
		book.setCopyright(StringUtils.replace(book.getCopyright(), "\n", "<br>"));
		book.setCopyrightPageText(StringUtils.replace(book.getCopyrightPageText(), "\n", "<br>"));
		book.setAdditionalTrademarkInfo(StringUtils.replace(book.getAdditionalTrademarkInfo(), "\n", "<br>"));

		for(EbookName name: book.getEbookNames()) {
			name.setBookNameText(StringUtils.replace(name.getBookNameText(), "\n", "<br>"));
		}
		
		for(Author author: book.getAuthors()) {
			author.setAuthorAddlText(StringUtils.replace(author.getAuthorAddlText(), "\n", "<br>"));
		}
		
		for(FrontMatterPage page : book.getFrontMatterPages()) {
			for(FrontMatterSection section : page.getFrontMatterSections()) {
				section.setSectionText(StringUtils.replace(section.getSectionText(), "\n", "<br>"));
				section.setSectionText(StringUtils.replace(section.getSectionText(), "\t", "&nbsp;&nbsp;&nbsp;&nbsp;"));
			}
		}
	}
	
	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
	}
	
	@Required
	public void setJobRequestService(JobRequestService service) {
		this.jobRequestService = service;
	}
}
