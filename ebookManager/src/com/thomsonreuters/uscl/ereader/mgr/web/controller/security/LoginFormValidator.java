package com.thomsonreuters.uscl.ereader.mgr.web.controller.security;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates the data entered on the login form page.
 */
public class LoginFormValidator implements Validator {
	
	private static final Logger log = Logger.getLogger(LoginFormValidator.class);
	private static final String CODE_REQUIRED_FIELD = "error.required.field";
	//private MessageSourceAccessor messageSourceAccessor;
	
	@SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
        return (LoginForm.class.equals(clazz));
    }

    public void validate(Object obj, Errors errors) {
    	log.debug("Validating form: " + obj.getClass().getName());
    	LoginForm form = (LoginForm) obj;
    	
    	if (StringUtils.isBlank(form.getUsername())) {
    		String[] args = { "Username" };
    		errors.reject(CODE_REQUIRED_FIELD, args, "Username is required");
    	}
    	if (StringUtils.isBlank(form.getPassword())) {
    		String[] args = { "Password" };
    		errors.reject(CODE_REQUIRED_FIELD, args, "Password is required");
    	}
    }
    
//    @Required
//    public void setMessageSourceAccessor(MessageSourceAccessor accessor) {
//    	this.messageSourceAccessor = accessor;
//    }
}
