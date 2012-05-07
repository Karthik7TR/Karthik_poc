/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

public class JobThrottleConfigFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = Logger.getLogger(JobThrottleConfigFormValidator.class);

	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (JobThrottleConfigForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	//log.debug(">>>");
		JobThrottleConfigForm form = (JobThrottleConfigForm) obj;
		if ((form.isThrottleStepActive() && StringUtils.isBlank(form.getThrottleStepName()))) {
			errors.reject("throttle.admin.step.name.blank");
		}
		
		if (form.getThrotttleStepMaxJobs() > form.getCoreThreadPoolSize()) {
			errors.reject("throttle.admin.step.max.jobs");
		}
	}
}
