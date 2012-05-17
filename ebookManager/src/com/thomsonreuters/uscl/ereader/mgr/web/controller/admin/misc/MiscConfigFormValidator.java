/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

public class MiscConfigFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = Logger.getLogger(JobThrottleConfigFormValidator.class);

	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (MiscConfigForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	//log.debug(">>>");
		//MiscConfigForm form = (MiscConfigForm) obj;
	}
}
