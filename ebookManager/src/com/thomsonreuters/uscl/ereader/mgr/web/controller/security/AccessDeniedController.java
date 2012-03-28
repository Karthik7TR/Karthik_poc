package com.thomsonreuters.uscl.ereader.mgr.web.controller.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

/**
 * In-bound (get method) controller for the Access Denied page.
 */
@Controller
public class AccessDeniedController {
	
	//private static final Logger log = Logger.getLogger(AccessDeniedController.class);
	
	@RequestMapping(WebConstants.MVC_SEC_ACCESS_DENIED)
	public ModelAndView inboundGet() {
		return new ModelAndView(WebConstants.VIEW_SEC_ACCESS_DENIED);
	}
}
