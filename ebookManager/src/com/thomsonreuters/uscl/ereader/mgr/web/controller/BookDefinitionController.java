package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;


@Controller
public class BookDefinitionController {
	//private static final Logger log = Logger.getLogger(BookDefinitionController.class);

	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_VIEW, method = RequestMethod.GET)
	public ModelAndView bookDefinitionView(Model model) throws Exception {
// TODO: implement this		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_VIEW);
	}
}
