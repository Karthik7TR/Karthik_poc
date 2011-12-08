/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.codes.security.authentication.LdapUserInfo;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunner;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.SelectOption;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service.DashboardService;

@Controller
public class CreateBookController {
	private static final Logger log = Logger.getLogger(CreateBookController.class);
	
	@Resource(name="environmentName")
	private String environmentName;
	@Autowired
	private JobRunner jobRunner;
	@Autowired
	private DashboardService service;
	@Autowired
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

		String bookCode = form.getBookCode();
		String bookTitle = service.getBookTitle(bookCode);
		JobRunRequest jobRunRequest = JobRunRequest.create(bookCode, bookTitle, userName, userEmail);
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
		List<SelectOption> bookCodeOptions = new ArrayList<SelectOption>();
		Map<String,String> bookMap = service.getBookCodes();
		Set<Entry<String,String>> entrySet = bookMap.entrySet();
		for (Entry<String,String> book : entrySet) {
			bookCodeOptions.add(new SelectOption(book.getValue(), book.getKey()));
		}
		model.addAttribute(WebConstants.KEY_BOOK_CODE_OPTIONS, bookCodeOptions);
		model.addAttribute(WebConstants.KEY_ENVIRONMENT, environmentName);
	}
}
