/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
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
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.FilterForm.FilterCommand;

@Controller
public class FilterController extends BaseJobListController {
	private static final Logger log = Logger.getLogger(FilterController.class);
	private Validator validator;

	@InitBinder(FilterForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	/**
	 * Handle submit/post of a new set of filter criteria.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_SUMMARY_FILTER_POST, method = RequestMethod.POST)
	public ModelAndView doFilterPost(HttpSession httpSession,
						@ModelAttribute(FilterForm.FORM_NAME) @Valid FilterForm filterForm,
						BindingResult errors,
						Model model) throws Exception {
log.debug(filterForm);
		// Fetch the existing saved list of job execution ID's from the last successful query
		List<Long> jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);
		
		// Restore state of paging and sorting
		JobListForm jobListForm = new JobListForm();
		PageAndSort savedPageAndSort = fetchSavedPageAndSort(httpSession);
		PageAndSort pageAndSort = jobListForm.getPageAndSort();
		pageAndSort.copyProperties(savedPageAndSort);
		
		if (FilterCommand.RESET.equals(filterForm.getFilterCommand())){
			filterForm.initialize();
		}
		
		pageAndSort.setPage(1);
		if (!errors.hasErrors()) {
			JobFilter filter = new JobFilter(filterForm.getFromDate(), filterForm.getToDate(), filterForm.getBatchStatus(),
											 filterForm.getTitleId(), filterForm.getBookName());
			JobSort jobSort = createJobSort(savedPageAndSort.getSort(), savedPageAndSort.isAscendingSort());
			jobExecutionIds = jobService.findJobExecutions(filter, jobSort);
		}
		setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);
		model.addAttribute(JobListForm.FORM_NAME, jobListForm);

		return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
	}

	@Required
	public void setJobService(JobService service) {
		this.jobService = service;
	}
	@Required
	public void setValidator(FilterFormValidator validator) {
		this.validator = validator;
	}
}
