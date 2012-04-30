/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Required;
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
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionLockService;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class DeleteBookDefinitionController {
	//private static final Logger log = Logger.getLogger(DeleteBookDefinitionController.class);

	private BookDefinitionService bookDefinitionService;
	private EBookAuditService auditService;
	private JobRequestService jobRequestService;
	private BookDefinitionLockService bookLockService;
	protected Validator validator;

	@InitBinder(DeleteBookDefinitionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	/**
	 * Handle the in-bound GET to the Book Definition delete page
	 * @param surrogate key
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_DELETE, method = RequestMethod.GET)
	public ModelAndView getDeleteBookDefintion(@RequestParam Long id,
				@ModelAttribute(DeleteBookDefinitionForm.FORM_NAME) DeleteBookDefinitionForm form,
				Model model) {
		
		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
		
		form.setAction(DeleteBookDefinitionForm.Action.DELETE);
		setupModel(model, bookDef);
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_DELETE);
	}

	/**
	 * Handle the in-bound POST to the Book Definition delete page.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_DELETE, method = RequestMethod.POST)
	public ModelAndView postDeleteBookDefinition(@ModelAttribute(DeleteBookDefinitionForm.FORM_NAME) @Valid DeleteBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) {
		
		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(form.getId());
			
		if(!bindingResult.hasErrors()) {
			String url;
			
			// Soft delete Book Definition if book has been published
			if(bookDef.getPublishedOnceFlag()) {
				// Set flag as deleted
				bookDefinitionService.updateDeletedStatus(bookDef.getEbookDefinitionId(), true);
				
				String queryString = String.format("?%s=%s", WebConstants.KEY_ID, form.getId());
				url = WebConstants.MVC_BOOK_DEFINITION_VIEW_GET+queryString;
				
			} else {
				// Otherwise Delete Book Definition from the database.
				bookDefinitionService.removeBookDefinition(bookDef.getEbookDefinitionId());
				
				url = WebConstants.MVC_BOOK_LIBRARY_LIST;
			}
			
			// Save in Audit
			EbookAudit audit = new EbookAudit();
			audit.loadBookDefinition(bookDef, EbookAudit.AUDIT_TYPE.DELETE, UserUtils.getAuthenticatedUserName(), form.getComment());
			auditService.saveEBookAudit(audit);
			
			return new ModelAndView(new RedirectView(url));
		}
		
		setupModel(model, bookDef);
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_DELETE);
	}
	
	/**
	 * Handle the in-bound GET to the Restore Book Definition page
	 * @param surrogate key
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_RESTORE, method = RequestMethod.GET)
	public ModelAndView getRestoreBookDefintion(@RequestParam Long id,
				@ModelAttribute(DeleteBookDefinitionForm.FORM_NAME) DeleteBookDefinitionForm form,
				Model model) {
		
		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		form.setAction(DeleteBookDefinitionForm.Action.RESTORE);
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_RESTORE);
	}

	/**
	 * Handle the in-bound POST to the Restore Book Definition page.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_RESTORE, method = RequestMethod.POST)
	public ModelAndView postRestoreBookDefinition(@ModelAttribute(DeleteBookDefinitionForm.FORM_NAME) @Valid DeleteBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) {

		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(form.getId());
			
		if(!bindingResult.hasErrors()) {
			// Save in Audit
			EbookAudit audit = new EbookAudit();
			audit.loadBookDefinition(bookDef, EbookAudit.AUDIT_TYPE.RESTORE, UserUtils.getAuthenticatedUserName(), form.getComment());
			auditService.saveEBookAudit(audit);
			
			// Set flag as not deleted
			bookDefinitionService.updateDeletedStatus(bookDef.getEbookDefinitionId(), false);
			
			String queryString = String.format("?%s=%s", WebConstants.KEY_ID, form.getId());
			return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_VIEW_GET+queryString));
		}
		
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_RESTORE);
	}
	
	private void setupModel(Model model, BookDefinition bookDef) {
		if(bookDef != null){
			model.addAttribute(WebConstants.KEY_IS_IN_JOB_REQUEST, jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId()));
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_LOCK, bookLockService.findBookLockByBookDefinition(bookDef));
		}
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
	}
 	
	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
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
	public void setJobRequestService(JobRequestService service) {
		this.jobRequestService = service;
	}
	
	@Required
	public void setBookDefinitionLockService(BookDefinitionLockService service) {
		this.bookLockService = service;
	}
}
