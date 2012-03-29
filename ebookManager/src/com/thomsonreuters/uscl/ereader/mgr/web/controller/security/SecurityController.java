/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.security;

import org.springframework.stereotype.Controller;

/**
 * DEPRECATED CAS related controller.
 */
@Controller
@Deprecated
public class SecurityController {
//	private static final Logger log = Logger.getLogger(SecurityController.class);
//	/**
//	 * The context root URL for the Central Authentication Service (CAS) server web application,
//	 * like "https://someHost/ebookCas"
//	 */
//	private URL casUrl;
//	
//	/**
//	 * Invoked after the j_security_check_logout URL is invoked.
//	 * This is configured in the application spring security spring bean file as as logout success URL.
//	 */
//	@RequestMapping(value=WebConstants.MVC_AFTER_LOGOUT, method = RequestMethod.GET)
//	public ModelAndView afterLogout(HttpSession httpSession) throws Exception {
//		log.debug(">>>");
//		httpSession.invalidate();
//		String casLogoutUrl = casUrl + "/logout";
//		RedirectView view = new RedirectView(casLogoutUrl);
//		return new ModelAndView(view);
//	}
//	
//	/**
//	 * Invoked when user attempts to access a resource for which they do not have privilege, i.e. they are
//	 * not in the proper group/role.
//	 */
//	@RequestMapping(value=WebConstants.MVC_ACCESS_DENIED, method = RequestMethod.GET)
//	public ModelAndView onAccessDenied(HttpSession httpSession) throws Exception {
//		log.debug(">>>");
//		return new ModelAndView(WebConstants.VIEW_ACCESS_DENIED);
//	}
//	
//	@Required
//	public void setCasUrl(URL url) {
//		this.casUrl = url;
//	}
}
