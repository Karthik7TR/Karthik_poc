/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.generatorswitch;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;


@Component("killSwitchFormValidator")
public class StopGeneratorFormValidator implements Validator {
	//private static final Logger log = Logger.getLogger(KillSwitchFormValidator.class);

	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (StopGeneratorForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	
    	StopGeneratorForm form = (StopGeneratorForm) obj;

    	String code = form.getCode();
    	if(StringUtils.isBlank(code)) {
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "error.required");
    	}else if(!code.equals(WebConstants.CONFIRM_CODE_KILL_SWITCH)) {
    		errors.rejectValue("code", "error.invalid", new Object[] {"Code"}, "Invalid code");
    	}

	}
	
}
