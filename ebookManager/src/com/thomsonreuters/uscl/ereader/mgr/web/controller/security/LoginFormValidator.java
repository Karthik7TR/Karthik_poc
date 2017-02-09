package com.thomsonreuters.uscl.ereader.mgr.web.controller.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates the data entered on the login form page.
 */
public class LoginFormValidator implements Validator
{
    private static final Logger log = LogManager.getLogger(LoginFormValidator.class);
    private static final String CODE_REQUIRED_FIELD = "error.required.field";
    //private MessageSourceAccessor messageSourceAccessor;

    @Override
    public boolean supports(final Class<?> clazz)
    {
        return (LoginForm.class.equals(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors)
    {
        log.debug("Validating form: " + obj.getClass().getName());
        final LoginForm form = (LoginForm) obj;

        if (StringUtils.isBlank(form.getUsername()))
        {
            final String[] args = {"Username"};
            errors.reject(CODE_REQUIRED_FIELD, args, "Username is required");
        }
        if (StringUtils.isBlank(form.getPassword()))
        {
            final String[] args = {"Password"};
            errors.reject(CODE_REQUIRED_FIELD, args, "Password is required");
        }
    }

//    @Required
//    public void setMessageSourceAccessor(MessageSourceAccessor accessor) {
//    	this.messageSourceAccessor = accessor;
//    }
}
