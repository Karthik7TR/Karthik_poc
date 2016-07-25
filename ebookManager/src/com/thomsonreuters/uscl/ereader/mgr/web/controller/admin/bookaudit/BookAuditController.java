/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookaudit;

import java.util.List;

import javax.servlet.http.HttpSession;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort.SortProperty;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

@Controller
public class BookAuditController {
	//private static final Logger log = LogManager.getLogger(BookAuditController.class);
	private EBookAuditService auditService;
	private PublishingStatsService publishingStatsService; 
	
	private Validator validator;

	@InitBinder(AdminAuditFilterForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.setValidator(validator);
	}
	
	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 * Only Super users allowed
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_ADMIN_AUDIT_BOOK_LIST, method = RequestMethod.GET)
	public ModelAndView viewAuditList(HttpSession httpSession, Model model) throws Exception {
	
		AdminAuditFilterForm form = (AdminAuditFilterForm) httpSession.getAttribute(AdminAuditFilterForm.FORM_NAME);
		
		// Setup form is came from redirect
		if(model.containsAttribute(AdminAuditFilterForm.FORM_NAME)) {
			form = (AdminAuditFilterForm) model.asMap().get(AdminAuditFilterForm.FORM_NAME);
			// Save filter and paging state in the session
			httpSession.setAttribute(AdminAuditFilterForm.FORM_NAME, form);
			setupFilterForm(httpSession, model, form);
		} else if (form != null) {
			// Setup form from previous saved filter form
			setupFilterForm(httpSession, model, form);
		}else {
			// new form
			form = new AdminAuditFilterForm();
		}
		
		model.addAttribute(AdminAuditFilterForm.FORM_NAME, form);
		
		return new ModelAndView(WebConstants.VIEW_ADMIN_AUDIT_BOOK_LIST);
	}
	
	private void setupFilterForm(HttpSession httpSession, Model model, AdminAuditFilterForm form) {
		if(!form.isEmpty()) {
			PublishingStatsFilter filter = new PublishingStatsFilter(form.getTitleId(), form.getProviewDisplayName(), form.getIsbn());
			PublishingStatsSort sort = new PublishingStatsSort(SortProperty.JOB_SUBMIT_TIMESTAMP, false, 1, 100);
			List<PublishingStats> publishingStats = this.publishingStatsService.findPublishingStats(filter, sort);
			model.addAttribute("publishingStats", publishingStats);
		}	
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_AUDIT_BOOK_SEARCH, method = RequestMethod.POST)
	public String search(@ModelAttribute(AdminAuditFilterForm.FORM_NAME) @Valid AdminAuditFilterForm form,
			@RequestParam String submit, BindingResult errors, RedirectAttributes ra) throws Exception {
		
		if ("reset".equalsIgnoreCase(submit)) {
			form.initialize();
		}
		
		ra.addFlashAttribute(AdminAuditFilterForm.FORM_NAME, form);
		return "redirect:/" + WebConstants.MVC_ADMIN_AUDIT_BOOK_LIST;
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_AUDIT_BOOK_MODIFY_ISBN, method = RequestMethod.GET)
	public ModelAndView modifyAuditIsbn(@ModelAttribute(AdminAuditRecordForm.FORM_NAME) AdminAuditRecordForm form,
			@RequestParam("id") Long id, BindingResult bindingResult,
			Model model) throws Exception {
		
		EbookAudit audit = auditService.findEBookAuditByPrimaryKey(id);
		if(audit != null) {
			form.setTitleId(audit.getTitleId());
			form.setAuditId(id);
			form.setBookDefinitionId(audit.getEbookDefinitionId());
			form.setLastUpdated(audit.getLastUpdated());
			form.setIsbn(audit.getIsbn());
			form.setProviewDisplayName(audit.getProviewDisplayName());
			model.addAttribute("audit", audit);
			model.addAttribute(AdminAuditRecordForm.FORM_NAME, form);
		}
		
		return new ModelAndView(WebConstants.VIEW_ADMIN_AUDIT_BOOK_MODIFY_ISBN);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_AUDIT_BOOK_MODIFY_ISBN, method = RequestMethod.POST)
	public ModelAndView modifyAuditIsbnPost(@ModelAttribute(AdminAuditRecordForm.FORM_NAME) @Valid AdminAuditRecordForm form,
			BindingResult bindingResult,Model model) throws Exception {
		
		if(!bindingResult.hasErrors()) {
			EbookAudit audit = auditService.editIsbn(form.getTitleId(), form.getIsbn());

			if(audit != null) {
				// Save audit record to determine user that modified ISBN
				audit.setAuditId(null);
				audit.setAuditType(EbookAudit.AUDIT_TYPE.EDIT.toString());
				audit.setUpdatedBy(UserUtils.getAuthenticatedUserName());
				audit.setAuditNote("Modify Audit ISBN");
				auditService.saveEBookAudit(audit);
			}
						
			// Redirect user
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_AUDIT_BOOK_LIST));
		}
		return new ModelAndView(WebConstants.VIEW_ADMIN_AUDIT_BOOK_MODIFY_ISBN);
	}
	
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

	@Required
	public void setAuditService(EBookAuditService service) {
		this.auditService = service;
	}
	
	@Required
	public void setValidator(AdminAuditFilterFormValidator validator) {
		this.validator = validator;
	}
}
