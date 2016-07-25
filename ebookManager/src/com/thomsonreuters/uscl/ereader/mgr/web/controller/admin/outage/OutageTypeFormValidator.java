/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

@Component("outageFormValidator")
public class OutageTypeFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = LogManager.getLogger(OutageTypeFormValidator.class);
	private static final int MAXIMUM_CHARACTER_128 = 128;

	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (OutageTypeForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	
		OutageTypeForm form = (OutageTypeForm) obj;

    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "system", "error.required.field", new Object[] {"System"});
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subSystem", "error.required.field", new Object[] {"Sub-system"});
    	
    	checkMaxLength(errors, MAXIMUM_CHARACTER_128, form.getSystem(), "system", new Object[] {"System", MAXIMUM_CHARACTER_128});
    	checkMaxLength(errors, MAXIMUM_CHARACTER_128, form.getSubSystem(), "subSystem", new Object[] {"Sub-system", MAXIMUM_CHARACTER_128});
	}
}
