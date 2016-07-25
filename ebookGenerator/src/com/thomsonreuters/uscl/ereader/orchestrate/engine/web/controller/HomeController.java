/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;

@Controller
public class HomeController {
	//private static final Logger log = LogManager.getLogger(HomeController.class);
	
	private String environmentName;
	
	public HomeController(String envName) {
		this.environmentName = envName;
	}

	@RequestMapping(value=WebConstants.URI_HOME, method = RequestMethod.GET)
	public ModelAndView home(Model model) throws Exception {
		//log.debug(">>> environment=" + environmentName);
		model.addAttribute("environmentName", environmentName);
		return new ModelAndView(WebConstants.VIEW_HOME);
	}
}
