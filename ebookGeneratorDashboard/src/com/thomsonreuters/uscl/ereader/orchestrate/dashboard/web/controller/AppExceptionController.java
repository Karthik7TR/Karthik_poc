package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppExceptionController {
	// private static final Logger log = Logger.getLogger(AppExceptionController.class);
	
	@RequestMapping(value="/appException.mvc", method = RequestMethod.GET)
	public ModelAndView handleException(HttpServletRequest request, HttpServletResponse response)
							  throws Exception {
		return new ModelAndView("appException");
	}
	
}
