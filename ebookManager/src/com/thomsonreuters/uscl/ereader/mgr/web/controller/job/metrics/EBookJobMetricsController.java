/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.metrics;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

@Controller
public class EBookJobMetricsController {

	private PublishingStatsService publishingStatsService;
	private BookDefinitionService bookDefinitionService;

	/**
	 * 
	 * @param jobInstanceId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_BOOK_JOB_METRICS, method = RequestMethod.GET)
	public ModelAndView bookPublishingHistory(Long jobInstanceId, Model model)
			throws Exception {

		
		PublishingStats ebookPublishingStats= publishingStatsService.findPublishingStatsByJobId(jobInstanceId);
		if (ebookPublishingStats != null) {

			BookDefinition book = bookDefinitionService
					.findBookDefinitionByEbookDefId(ebookPublishingStats.getEbookDefId());
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, book);
			model.addAttribute(WebConstants.KEY_PUBLISHING_STATS, ebookPublishingStats);
		}

		return new ModelAndView(WebConstants.VIEW_BOOK_JOB_METRICS);
	}

	
	@Required
	public PublishingStatsService getPublishingStatsService() {
		return publishingStatsService;
	}

	public void setPublishingStatsService(
			PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

	
	@Required
	public BookDefinitionService getBookDefinitionService() {
		return bookDefinitionService;
	}


	public void setBookDefinitionService(BookDefinitionService bookDefinitionService) {
		this.bookDefinitionService = bookDefinitionService;
	}

	

	

}
