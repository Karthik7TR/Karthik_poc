/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.history;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm;

public class JobHistoryController {
	
	private BookDefinitionService bookDefinitionService;
	
	/**
	 * 
	 * @param titleId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_JOB_HISTORY, method = RequestMethod.GET)
	public ModelAndView getJobHistory(@RequestParam Long id,
			@ModelAttribute(GenerateBookForm.FORM_NAME) GenerateBookForm form,
			Model model) throws Exception {

		BookDefinition book = bookDefinitionService
				.findBookDefinitionByEbookDefId(id);

		if (book != null) {

			String cutOffDate = null;

		

			model.addAttribute(WebConstants.TITLE, book.getProviewDisplayName());
			model.addAttribute(WebConstants.KEY_ISBN, book.getIsbn());
			model.addAttribute(WebConstants.KEY_MATERIAL_ID,
					book.getMaterialId());
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, book);
			model.addAttribute(WebConstants.KEY_PUBLISHING_CUT_OFF_DATE,
					cutOffDate);
			model.addAttribute(WebConstants.KEY_USE_PUBLISHING_CUT_OFF_DATE,
					book.getDocumentTypeCodes().getUsePublishCutoffDateFlag());

			

		}

		return new ModelAndView(WebConstants.VIEW_JOB_HISTORY);
	}
	
	public BookDefinitionService getBookDefinitionService() {
		return bookDefinitionService;
	}

	public void setBookDefinitionService(BookDefinitionService bookDefinitionService) {
		this.bookDefinitionService = bookDefinitionService;
	}

}
