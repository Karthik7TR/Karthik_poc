package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class AdminController {
	//private static final Logger log = Logger.getLogger(AdminController.class);

	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 * Only Super users allowed
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_ADMIN_MAIN, method = RequestMethod.GET)
	public ModelAndView admin() throws Exception {

		return new ModelAndView(WebConstants.VIEW_ADMIN_MAIN);
	}
	
}
