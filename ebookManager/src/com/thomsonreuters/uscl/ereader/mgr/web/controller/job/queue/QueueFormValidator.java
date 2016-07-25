/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class QueueFormValidator implements Validator {
	
	//private static final Logger log = LogManager.getLogger(FilterFormValidator.class);
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (QueueForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
//		JobRunRequestForm form = (JobRunRequestForm) obj;
		
//		if (form.getTomIq() > 80) {
//			errors.reject("error.invalid.iq");
//		}
	}
}
