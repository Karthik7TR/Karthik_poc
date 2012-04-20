package com.thomsonreuters.uscl.ereader.mgr.web.controller.security;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;

/**
 * Security and login/authentication URL handlers.
 * The submit of the login username/password returns here do doPost() where the fields are validated and if no errors
 * the success view is the auto-login page which auto-submits itself to the login j_security_check URL.
 */
@Controller
public class LoginController {
	
	private static final Logger log = Logger.getLogger(LoginController.class);

	/** Validator for the login form - username and password */
	private Validator validator;
	private String environmentName;
	
	@InitBinder(LoginForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	/**
	 * Handle the inbound GET to the login page.
	 * Does nothing but set up the form backing object.
	 * @param form the login form backing object
	 */
	@RequestMapping(value = WebConstants.MVC_SEC_LOGIN, method = RequestMethod.GET)
	public ModelAndView inboundGet(HttpSession httpSession, @ModelAttribute(LoginForm.FORM_NAME) LoginForm form, Model model) {
		log.debug(">>> environment="+environmentName);
		// Store the environment name in session so it can be displayed on each page
		httpSession.setAttribute(WebConstants.KEY_ENVIRONMENT_NAME, environmentName);
		return new ModelAndView(WebConstants.VIEW_SEC_LOGIN);
	}
	
	/**
	 * Handler for the submit of the authentication credentials (username/password) on the login form which initiates
	 * the spring security authentication and fetch of user details.
	 * @param form contains username and password properties
	 * @param errors binding and/or validation errors
	 */
	@RequestMapping(value = WebConstants.MVC_SEC_LOGIN, method = RequestMethod.POST) 
	public ModelAndView handleLoginFormPost(@ModelAttribute(LoginForm.FORM_NAME) @Valid LoginForm form,
			   					   BindingResult errors,
			   					   Model model) {
		log.debug(form);
		String viewName = (!errors.hasErrors()) ? WebConstants.VIEW_SEC_LOGIN_AUTO : WebConstants.VIEW_SEC_LOGIN;
		return new ModelAndView(viewName);
	}

	/**
	 * Handler for the URL invoked after a successful user authentication.
	 * A hook location for any post-authenitcation actions but in the end
	 * redirects user to the designated "home" page.
	 */
	@RequestMapping(value=WebConstants.MVC_SEC_AFTER_AUTHENTICATION, method = RequestMethod.GET)
	public ModelAndView handleAuthenticationSuccess(HttpSession httpSession) {
		if (log.isDebugEnabled()) {
			log.debug("SUCCESSFULLY AUTHENTICATED: " + CobaltUser.getAuthenticatedUser());
			log.debug("Session ID="+httpSession.getId());
		}
		
		return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_LIBRARY_LIST));
	}
	
	/**
	 * Handler for the URL invoked when user fails to authentication using the
	 * username and password that were entered.  Sends user back to login page
	 * with an failure message.
	 */
	@RequestMapping(value = WebConstants.MVC_SEC_LOGIN_FAIL) 
	public ModelAndView handleAuthenticationFailure(Model model) {
		log.debug("AUTHENTICATION FAILED!");
		InfoMessage mesg = new InfoMessage(InfoMessage.Type.FAIL, "Authentication Failed");
		List<InfoMessage> infoMessages = new ArrayList<InfoMessage>(1);
		infoMessages.add(mesg);
		model.addAttribute(WebConstants.KEY_INFO_MESSAGES, infoMessages);
		model.addAttribute(LoginForm.FORM_NAME, new LoginForm());
		return new ModelAndView(WebConstants.VIEW_SEC_LOGIN);
	}
	
	/**
	 * Handler for URL invoked immediately after a Spring Security logout.
	 * @return redirection view back to login page
	 */
	@RequestMapping(WebConstants.MVC_SEC_AFTER_LOGOUT)
	public ModelAndView handleLogout() {
		log.debug(">>>");
		// Redirect user back to the Login page
		return new ModelAndView(new RedirectView(WebConstants.MVC_SEC_LOGIN));
	}

	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	@Required
	public void setEnvironmentName(String name) {
		this.environmentName = name;
	}
}
