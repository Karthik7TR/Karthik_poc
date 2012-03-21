/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.history;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

@Controller
public class BookPublishingHistoryController {

	private PublishingStatsService publishingStatsService;
	private BookDefinitionService bookDefinitionService;

	


	/**
	 * 
	 * @param titleId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_BOOK_PUBLISHING_HISTORY, method = RequestMethod.GET)
	public ModelAndView bookPublishingHistory(@RequestParam Long id, Model model)
			throws Exception {

		BookDefinition book = bookDefinitionService
				.findBookDefinitionByEbookDefId(id);

		List<PublishingStats> ebookAuditList= publishingStatsService.findPublishingStatsByEbookDef(id);

		if (ebookAuditList != null) {

			
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, book);
			model.addAttribute(WebConstants.KEY_PAGINATED_LIST, ebookAuditList);
			model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, 1);

		}

		return new ModelAndView(WebConstants.VIEW_BOOK_PUBLISHING_HISTORY);
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
