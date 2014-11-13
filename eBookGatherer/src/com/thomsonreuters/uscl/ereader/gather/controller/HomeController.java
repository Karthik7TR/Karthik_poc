package com.thomsonreuters.uscl.ereader.gather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.EBConstants;


@Controller
public class HomeController {
	//private static final Logger log = Logger.getLogger(HomeController.class);
	
	private String environmentName;
	
	@RequestMapping(value=EBConstants.URI_HOME, method = RequestMethod.GET)
	public ModelAndView home(Model model) throws Exception {
		model.addAttribute("environmentName", environmentName);
		return new ModelAndView(EBConstants.VIEW_HOME);
	}
	
	@Autowired
	public void setEnvironmentName(String envName) {
		this.environmentName = envName;
	}
}
