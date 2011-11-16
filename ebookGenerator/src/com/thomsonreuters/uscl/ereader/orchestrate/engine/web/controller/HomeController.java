package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	//private static final Logger log = Logger.getLogger(HomeController.class);
	
	@Resource(name="environmentName")
	private String environmentName;

	@RequestMapping(value="/home.mvc", method = RequestMethod.GET)
	public ModelAndView home(Model model) throws Exception {
		model.addAttribute("environmentName", environmentName);
		return new ModelAndView("home");
	}
}
