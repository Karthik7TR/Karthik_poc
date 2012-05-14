/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionLockService;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.frontmatter.service.CreateFrontMatterService;
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
	private CreateFrontMatterService frontMatterService;
	protected Validator validator;

	@InitBinder(EditBookDefinitionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setAutoGrowNestedPaths(false);
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(WebConstants.DATE_TIME_FORMAT_PATTERN);
		dateFormat.setLenient(false);
		// true passed to CustomDateEditor constructor means convert empty String to null
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));

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
	public ModelAndView createBookDefintionPost(HttpSession httpSession,
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) throws Exception {
		
		setUpFrontMatterPreviewModel(httpSession, form, bindingResult);
		
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
		
		boolean isPublished = false;
		String username = UserUtils.getAuthenticatedUserName();
		
		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
		
		// model used in VIEW_BOOK_DEFINITION_LOCKED and VIEW_BOOK_DEFINITION_EDIT
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);

		// Check if user needs to be shown an error
		if(bookDef != null) {
			// Check if book is soft deleted
			if(bookDef.isDeletedFlag()) {
				return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
			}
			
			// Check if book is being edited by another user
			BookDefinitionLock lock = bookLockService.findBookLockByBookDefinition(bookDef);
			if(lock != null && !lock.getUsername().equalsIgnoreCase(username)) {
				model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_LOCK, lock);
				return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_ERROR_LOCKED);
			}
			
			// Check if book is in queue to be generated
			if(jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId())) {
				return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_ERROR_QUEUED);
			}
			
			// Lock book definition
			bookLockService.lockBookDefinition(bookDef, username, UserUtils.getAuthenticatedUserFullName());
			
			isPublished = bookDef.getPublishedOnceFlag();
		}
		
		form.initialize(bookDef, editBookDefinitionService.getKeywordCodes());
		model.addAttribute(WebConstants.KEY_IS_PUBLISHED, isPublished);
		initializeModel(model, form);
		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
	}
	
	private void setUpFrontMatterPreviewModel(HttpSession httpSession, EditBookDefinitionForm form,
											  BindingResult bindingResult) throws Exception {
		// The one error is the message indicating that the form was validated, any more than this indicates other problems
		Long frontMatterPreviewPageId = form.getSelectedFrontMatterPreviewPage();
		if ((frontMatterPreviewPageId != null) && (bindingResult.getErrorCount() == 1)) {
			BookDefinition fmBookDef = createFrontMatterPreviewBookDefinitionFromForm(form);
			String html = frontMatterService.getAdditionalFrontPage(fmBookDef, frontMatterPreviewPageId);
			//model.addAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML, html);
			// Place the preview content on the session so that it can be fetched and used when the popup preview window is opened
			httpSession.setAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML, html);
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
	public ModelAndView editBookDefintionPost(HttpSession httpSession,
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) throws Exception {

		setUpFrontMatterPreviewModel(httpSession, form, bindingResult);

		boolean isPublished = false;
		Long bookDefinitionId = form.getBookdefinitionId();
		String username = UserUtils.getAuthenticatedUserName();
		
		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId);
		
		// model used in VIEW_BOOK_DEFINITION_LOCKED and VIEW_BOOK_DEFINITION_EDIT
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		
		// Check if user needs to be shown an error
		if(bookDef != null) {
			// Check if book is soft deleted
			if(bookDef.isDeletedFlag()) {
				return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
			}
			
			// Check if book is being edited by another user
			BookDefinitionLock lock = bookLockService.findBookLockByBookDefinition(bookDef);
			if(lock != null && !lock.getUsername().equalsIgnoreCase(username)) {
				model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_LOCK, lock);
				return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_ERROR_LOCKED);
			}
			
			// Check if book is in queue to be generated
			if(jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId())) {
				return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_ERROR_QUEUED);
			}

			isPublished = bookDef.getPublishedOnceFlag();
		} else {
			// Book Definition has been deleted from the database when user saved the book.
			// Show error page
			return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
		}
		
		
		if(!bindingResult.hasErrors()) {
			form.loadBookDefinition(bookDef);
			bookDef = bookDefinitionService.saveBookDefinition(bookDef);
			
			// Save in Audit
			EbookAudit audit = new EbookAudit();
			audit.loadBookDefinition(bookDef, EbookAudit.AUDIT_TYPE.EDIT, UserUtils.getAuthenticatedUserName(), form.getComment());
			auditService.saveEBookAudit(audit);
			
			// Remove lock from BookDefinition
			bookLockService.removeLock(bookDef);
			
			// Redirect user
			String queryString = String.format("?%s=%s", WebConstants.KEY_ID, bookDefinitionId);
			return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_VIEW_GET+queryString));
		}

		model.addAttribute(WebConstants.KEY_IS_PUBLISHED, isPublished);
		initializeModel(model, form);
		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
	}
	
	/**
	 * AJAX call to remove lock on book definition
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_UNLOCK, method = RequestMethod.POST)
	public @ResponseBody String unlockBookDefinition(@RequestParam Long id) {
		String username = UserUtils.getAuthenticatedUserName();
		
		BookDefinition book = bookDefinitionService.findBookDefinitionByEbookDefId(id);
		
		if(book != null) {
			// Check if current user is the one with the lock
			BookDefinitionLock lock = bookLockService.findBookLockByBookDefinition(book);
			if(lock != null && lock.getUsername().equalsIgnoreCase(username)) {
				bookLockService.removeLock(book);
			}
		}
		return "success";
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
		
		if(bookDef.isDeletedFlag()) {
			return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
		} else {
			form.copyBookDefinition(bookDef, editBookDefinitionService.getKeywordCodes());
			initializeModel(model, form);
			return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_COPY);
		}
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
	public ModelAndView copyBookDefintionPost(HttpSession httpSession,
				@ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid EditBookDefinitionForm form,
				BindingResult bindingResult,
				Model model) throws Exception {
		
		setUpFrontMatterPreviewModel(httpSession, form, bindingResult);
		
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
	 * Initializes the model for the Create/Edit Book Definition View
	 * @param model
	 * @param form
	 */
	private void initializeModel(Model model, EditBookDefinitionForm form) {
		// Get Collection sizes to display on form
		model.addAttribute(WebConstants.KEY_NUMBER_OF_AUTHORS,form.getAuthorInfo().size());
		model.addAttribute(WebConstants.KEY_NUMBER_OF_FRONT_MATTERS,form.getFrontMatters().size());
		model.addAttribute(WebConstants.KEY_NUMBER_OF_EXCLUDE_DOCUMENTS, form.getExcludeDocuments().size());
		
		// Set drop down lists
		model.addAttribute(WebConstants.KEY_STATES, editBookDefinitionService.getStates());
		model.addAttribute(WebConstants.KEY_CONTENT_TYPES, editBookDefinitionService.getDocumentTypes());
		model.addAttribute(WebConstants.KEY_PUB_TYPES, editBookDefinitionService.getPubTypes());
		model.addAttribute(WebConstants.KEY_JURISDICTIONS, editBookDefinitionService.getJurisdictions());
		model.addAttribute(WebConstants.KEY_PUBLISHERS, editBookDefinitionService.getPublishers());
		model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, editBookDefinitionService.getKeywordCodes());
	}
	
	/**
	 * Create a book definition suitable for providing to the front matter preview service. 
	 * @param form assumed to have been previously validated correctly
	 * @return 
	 */
	public static BookDefinition createFrontMatterPreviewBookDefinitionFromForm(EditBookDefinitionForm form) throws ParseException {
		BookDefinition book = new BookDefinition();
		form.loadBookDefinition(book);
		List<FrontMatterPage> pages = book.getFrontMatterPages();
		for (FrontMatterPage page : pages) {
			Long pk = new Long(page.getSequenceNum());
			page.setId(pk);
		}
		return book;
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
	
	@Required
	public void setFrontMatterService(CreateFrontMatterService service) {
		this.frontMatterService = service;
	}
}
