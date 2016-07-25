/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

/**
 * In-bound (get method) controller for Error pages.
 * This is where the user is sent to if they try and access a page that will cause an error
 */
@Controller
public class ErrorController {
	//private static final Logger log = LogManager.getLogger(ErrorController.class);
	
	@RequestMapping(value=WebConstants.MVC_ERROR_BOOK_DELETED, method = RequestMethod.GET)
	public ModelAndView getBookDeleted() {
		return new ModelAndView(WebConstants.VIEW_ERROR_BOOK_DELETED);
	}
	
	@RequestMapping(value=WebConstants.MVC_ERROR_BOOK_DEFINITION, method = RequestMethod.GET)
	public ModelAndView getBookDefinitionError() {
		return new ModelAndView(WebConstants.VIEW_ERROR_BOOK_DEFINTION);
	}
}
