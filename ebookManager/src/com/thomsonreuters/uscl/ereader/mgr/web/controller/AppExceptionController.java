/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

/**
 * Handles the web container throwing a bubbled-up application exception and displays
 * the exception stack trace on its own page.
 */
@Controller
public class AppExceptionController {
	// private static final Logger log = LogManager.getLogger(AppExceptionController.class);
	
	@RequestMapping(value=WebConstants.MVC_APP_EXCEPTION, method = RequestMethod.GET)
	public ModelAndView handleException(HttpServletRequest request, HttpServletResponse response)
							  			throws Exception {
		return new ModelAndView(WebConstants.VIEW_APP_EXCEPTION);
	}
}
