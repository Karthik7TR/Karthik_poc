/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.codes.security.authentication.LdapUserInfo;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunner;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.SelectOption;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;

/**
 * Controller for the Create Book page, the page used to run book generating Spring Batch jobs.
 */
@Controller
public class CreateBookController {
	private static final Logger log = Logger.getLogger(CreateBookController.class);
	
	private String environmentName;
	private JobRunner jobRunner;
	private CoreService coreService;
	private MessageSourceAccessor messageSourceAccessor;
	
	/**
	 * Handle in-bound GET request to display the book job launching page.
	 */
	@RequestMapping(value=WebConstants.URL_CREATE_BOOK, method = RequestMethod.GET)
	public ModelAndView doGet(@ModelAttribute CreateBookForm form,
							  Model model) throws Exception {
		populateModel(model);
		return new ModelAndView(WebConstants.VIEW_CREATE_BOOK);
	}
	
	/**
	 * Handle submit (POST) of the form that indicates which book is to be created.
	 */
	@RequestMapping(value=WebConstants.URL_CREATE_BOOK, method = RequestMethod.POST)
	public ModelAndView doPost(@ModelAttribute CreateBookForm form,
								Model model) {
		log.debug(form);
		String queuePriorityLabel = form.isHighPriorityJob() ? "high" : "normal";
		LdapUserInfo authenticatedUser = LdapUserInfo.getAuthenticatedUser();
		String userName = (authenticatedUser != null) ? authenticatedUser.getUsername() : null;
		String userEmail = (authenticatedUser != null) ? authenticatedUser.getEmail() : null;

		BookDefinitionKey bookDefKey = form.getBookDefinitionKey();
		JobRunRequest jobRunRequest = JobRunRequest.create(bookDefKey, userName, userEmail);
		try {
			if (form.isHighPriorityJob()) {
				jobRunner.enqueueHighPriorityJobRunRequest(jobRunRequest);
			} else {
				jobRunner.enqueueNormalPriorityJobRunRequest(jobRunRequest);
			}
			// Report success to user in informational message on page
			Object[] args = { queuePriorityLabel};
			String infoMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.success", args);
			model.addAttribute(WebConstants.KEY_INFO_MESSAGE, infoMessage);
		} catch (Exception e) {	// Report failure on page in error message area
			Object[] args = { queuePriorityLabel, e.getMessage()};
			String errMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.fail", args);
			log.error(errMessage, e);
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, errMessage);
		}
		populateModel(model);
		
		return new ModelAndView(WebConstants.VIEW_CREATE_BOOK);
	}

	private void populateModel(Model model) {
		/* Get all the unique books that can be created */
		List<SelectOption> bookOptions = new ArrayList<SelectOption>();
		List<BookDefinition> books = coreService.findAllBookDefinitions();
		for (BookDefinition book : books) {
			BookDefinitionKey key = book.getPrimaryKey();
			String label = String.format("%s - %s (%d)", book.getName(), key.getTitleId(), key.getMajorVersion());
			String value = key.toKeyString();
			bookOptions.add(new SelectOption(label, value));
		}
		model.addAttribute(WebConstants.KEY_BOOK_OPTIONS, bookOptions);
		model.addAttribute(WebConstants.KEY_ENVIRONMENT, environmentName);
	}
	@Required
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
	@Required
	public void setJobRunner(JobRunner jobRunner) {
		this.jobRunner = jobRunner;
	}
	@Required
	public void setCoreService(CoreService service) {
		this.coreService = service;
	}
	@Required
	public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
		this.messageSourceAccessor = messageSourceAccessor;
	}
}
