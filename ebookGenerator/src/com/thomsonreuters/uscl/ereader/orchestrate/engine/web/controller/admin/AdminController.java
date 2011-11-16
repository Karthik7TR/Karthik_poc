package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller.admin;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;

@Controller
public class AdminController {
	private static final Logger log = Logger.getLogger(AdminController.class);

	@RequestMapping(value=WebConstants.URL_ADMIN_GET, method = RequestMethod.GET)
	public ModelAndView doGet(@ModelAttribute(AdminForm.FORM_NAME) AdminForm form,
							  Model model) throws Exception {
		
		// TODO: Set the property to currently selected value (use session to store initally)...
		
		form.setMaxConcurrentJobs(10);
		
		return new ModelAndView(WebConstants.VIEW_ADMIN);
	}
	
	@RequestMapping(value=WebConstants.URL_ADMIN_POST, method = RequestMethod.POST)
	public ModelAndView doPost(@ModelAttribute(AdminForm.FORM_NAME) AdminForm form,
							   Model model) throws Exception {
		log.debug(form);
		
		// TODO: set the new value(s) for the engine...
		
		return new ModelAndView(WebConstants.VIEW_HOME);
	}
}
