package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;

@Controller
public class HomeController {
	private static final Logger log = Logger.getLogger(HomeController.class);
	
	private String environmentName;
	
	public HomeController(String envName) {
		this.environmentName = envName;
	}

	@RequestMapping(value=WebConstants.URI_HOME, method = RequestMethod.GET)
	public ModelAndView home(Model model) throws Exception {
		log.debug(">>> environment=" + environmentName);
		model.addAttribute("environmentName", environmentName);
		return new ModelAndView(WebConstants.VIEW_HOME);
	}
}
