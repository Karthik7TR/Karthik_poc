package com.thomsonreuters.uscl.ereader.mgr.web.controller.promotion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

import org.apache.log4j.Logger;

@Controller
public class BookPromotionController {
	private static final Logger log = Logger.getLogger(BookPromotionController.class);

	@RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_PROMOTION, method = RequestMethod.GET)
	public ModelAndView promoteEbookPreview(@RequestParam String titleId, Model model) throws Exception {
		
		log.debug(titleId);
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_PROMOTION);
	}

	@RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_BULK_PROMOTION, method = RequestMethod.GET)
	public ModelAndView promoteBulkEbookPreview(@RequestParam String[] titleId, Model model) throws Exception {


		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_BULK_PROMOTION);
	}
}
