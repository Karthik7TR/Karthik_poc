/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.support;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

@Component("supportFormValidator")
public class SupportFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = LogManager.getLogger(SupportFormValidator.class);
	private static final int MAXIMUM_CHARACTER_512 = 512;
	private static final int MAXIMUM_CHARACTER_1024 = 1024;

	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (SupportForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	
    	SupportForm form = (SupportForm) obj;
    	
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "linkDescription", "error.required");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "linkAddress", "error.required");
    	checkMaxLength(errors, MAXIMUM_CHARACTER_512, form.getLinkDescription(), "linkDescription", new Object[] {"Link Description", MAXIMUM_CHARACTER_512});
    	checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getLinkAddress(), "linkAddress", new Object[] {"Link Address", MAXIMUM_CHARACTER_1024});
    	
    	UrlValidator urlValidator = new UrlValidator();
    	if(!urlValidator.isValid(form.getLinkAddress())) {
    		errors.rejectValue("linkAddress", "error.invalid.url");
    	}
	}
}
