/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

@Controller
public class PublishingStatsController {

	private PublishingStatsService publishingStatsService;

	@RequestMapping(value = WebConstants.MVC_STATS, method = RequestMethod.GET)
	public ModelAndView bookPublishingHistory(Model model) throws Exception {

		List<PublishingStats> publishingStatsList = publishingStatsService
				.findAllPublishingStats();
		if (publishingStatsList != null) {
			model.addAttribute(WebConstants.KEY_PAGINATED_LIST,
					publishingStatsList);
			model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE,
					publishingStatsList.size());

		}

		return new ModelAndView(WebConstants.VIEW_STATS);
	}

	@Required
	public PublishingStatsService getPublishingStatsService() {
		return publishingStatsService;
	}

	public void setPublishingStatsService(
			PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

}
