package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;


@Controller
public class HomeController {
	//private static final Logger log = Logger.getLogger(HomeController.class);

	@RequestMapping(value="/home.mvc", method = RequestMethod.GET)
	public ModelAndView home(Model model) throws Exception {
		return new ModelAndView(WebConstants.VIEW_HOME);
	}
}
