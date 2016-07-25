/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import java.util.Arrays;
import java.util.Map;

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
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class EditGroupController {
	//private static final Logger log = LogManager.getLogger(EditGroupController.class);
	private BookDefinitionService bookDefinitionService;
	private GroupService groupService;
	private EBookAuditService auditService;
	
	private Validator validator;

	@InitBinder(EditGroupDefinitionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setAutoGrowNestedPaths(false);
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));

		binder.setValidator(validator);
	}
	
	/**
	 * Handle the in-bound GET to the Group Definition edit view page.
	 * @param titleId the primary key of the book as required query string parameter.
	 */
	@RequestMapping(value=WebConstants.MVC_GROUP_DEFINITION_EDIT, method = RequestMethod.GET)
	public ModelAndView editGroupDefinitionGet(
				@RequestParam("id") Long id,
				@ModelAttribute(EditGroupDefinitionForm.FORM_NAME) EditGroupDefinitionForm form,
				BindingResult bindingResult,
				Model model) throws Exception {
		
		// Lookup the book by its primary key
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);

		// Check if user needs to be shown an error
		if(bookDef != null) {
			// Check if book is soft deleted
			if(bookDef.isDeletedFlag()) {
				return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
			}
			
			try {
				String groupId = groupService.getGroupId(bookDef);
				form.setBookDefinitionId(bookDef.getEbookDefinitionId());
				form.setFullyQualifiedTitleId(bookDef.getFullyQualifiedTitleId());
				form.setGroupId(groupId);
				
				GroupDefinition group = groupService.getLastGroup(bookDef);
				setupVersion(group, form, model);
				
				// TODO: Need to refactor to account for types standard, periodicals, ereference.
				form.setGroupType("standard");
				
				Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(bookDef);
				Map<String, ProviewTitleInfo> pilotBookMap = groupService.getPilotBooksForGroup(bookDef);
				if (pilotBookMap.size()>0){
				   proviewTitleMap.putAll(pilotBookMap);
				}
				if(groupService.getPilotBooksNotFound().size()>0){
					String msg = groupService.getPilotBooksNotFound().toString();
					msg = msg.replaceAll("\\[|\\]|\\{|\\}", "");
					model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, "Pilot books are not available in Proview "+Arrays.asList(msg.split("\\s*,\\s*")));
				}
				
				setupModel(model, bookDef, proviewTitleMap.size());
				form.initialize(bookDef, proviewTitleMap, pilotBookMap, group);
			} catch (ProviewException ex) {
				setupModel(model, bookDef, 1);
				model.addAttribute(WebConstants.KEY_ERR_MESSAGE, WebConstants.ERROR_PROVIEW);
				model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, Arrays.asList(ex.getMessage().split("\\s*,\\s*")));
			} catch (Exception e) {
				model.addAttribute(WebConstants.KEY_ERR_MESSAGE, WebConstants.ERROR_PROVIEW);
			}
		}

		return new ModelAndView(WebConstants.VIEW_GROUP_DEFINITION_EDIT);
	}

	private void setupVersion(GroupDefinition group, EditGroupDefinitionForm form, Model model) {
		String status = null;
		if (group != null) {
			status = group.getStatus();
		}

		model.addAttribute(WebConstants.KEY_GROUP_STATUS_IN_PROVIEW, status);
		if ("review".equalsIgnoreCase(status)) {
			String formatedVersion = EditGroupDefinitionForm.Version.OVERWRITE.toString();
			formatedVersion = formatedVersion.charAt(0)+formatedVersion.substring(1).toLowerCase();
			model.addAttribute(WebConstants.KEY_OVERWRITE_ALLOWED, formatedVersion);
		} else {
			String formatedVersion = EditGroupDefinitionForm.Version.MAJOR.toString();
			formatedVersion = formatedVersion.charAt(0)+formatedVersion.substring(1).toLowerCase();
			model.addAttribute(WebConstants.KEY_OVERWRITE_ALLOWED, formatedVersion);
		}
	}

	/**
	 * Handle the in-bound POST to the Book Definition edit view page.
	 * 
	 * @param titleId
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@RequestMapping(value=WebConstants.MVC_GROUP_DEFINITION_EDIT, method = RequestMethod.POST)
	public ModelAndView editGroupDefinitionPost(HttpSession httpSession,
				@ModelAttribute(EditGroupDefinitionForm.FORM_NAME) @Valid EditGroupDefinitionForm form,
				BindingResult bindingResult,
				Model model) throws Exception {

		BookDefinition bookDef = null;
		try {
		// Lookup the book by its primary key
			bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(form.getBookDefinitionId());
		} catch(Exception e) {
			// Error happens when POST of form is over Tomcat post limit. // Default is set at 2 mb.
			// The processed form is empty.
			return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DEFINITION));
		}
		
		// Check if user needs to be shown an error
		if(bookDef == null) {
			return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
		}
		
		Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(bookDef);
		if (form.getIncludePilotBook()) {
			Map<String, ProviewTitleInfo> pilotBookMap = groupService.getPilotBooksForGroup(bookDef);
			proviewTitleMap.putAll(pilotBookMap);
		}
		
		if(!bindingResult.hasErrors()) {
			try {
				
				GroupDefinition groupDefinition = form.createGroupDefinition(proviewTitleMap.values());
				
				// determine group version
				GroupDefinition lastGroup = groupService.getLastGroup(groupDefinition.getGroupId());
				if(lastGroup != null) {
					if(lastGroup.getStatus().equalsIgnoreCase(GroupDefinition.REVIEW_STATUS)) {
						groupDefinition.setGroupVersion(lastGroup.getGroupVersion());
					} else {
						groupDefinition.setGroupVersion(lastGroup.getGroupVersion() + 1);
					}
				} else {
					// first group being created
					groupDefinition.setGroupVersion(1L);
				}
				groupService.createGroup(groupDefinition);

				// Update group information in book definition
				bookDef.setGroupName(groupDefinition.getName());
				bookDef.setSubGroupHeading(groupDefinition.getFirstSubgroupHeading());
				bookDef = bookDefinitionService.saveBookDefinition(bookDef);
				
				// Save in Audit
				EbookAudit audit = new EbookAudit();
				audit.loadBookDefinition(bookDef, EbookAudit.AUDIT_TYPE.GROUP, UserUtils.getAuthenticatedUserName(), form.getComment());
				auditService.saveEBookAudit(audit);
				
				// Redirect user
				String queryString = String.format("?%s=%s", WebConstants.KEY_ID, form.getBookDefinitionId());
				return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_VIEW_GET+queryString));
			} catch (Exception e) {
				model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, e.getMessage());
			}
		}
		
		GroupDefinition group = groupService.getLastGroup(bookDef);
		setupVersion(group, form, model);
		setupModel(model, bookDef, proviewTitleMap.size());
		return new ModelAndView(WebConstants.VIEW_GROUP_DEFINITION_EDIT);
	}
	
	private void setupModel(Model model, BookDefinition bookDef, Integer numOfProviewTitles) {
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		model.addAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES, numOfProviewTitles);
	}
	

	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
	}
	
	@Required
	public void setGroupService(GroupService service) {
		this.groupService = service;
	}
	
	@Required
	public void setAuditService(EBookAuditService auditService) {
		this.auditService = auditService;
	}

	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
}
