package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class StubController {
	//private static final Logger log = Logger.getLogger(StubController.class);

	@RequestMapping(value="/stub.mvc", method = RequestMethod.GET)
	public ModelAndView home(Model model) throws Exception {
		return new ModelAndView("_stubPage"); // DEBUG
	}
}
