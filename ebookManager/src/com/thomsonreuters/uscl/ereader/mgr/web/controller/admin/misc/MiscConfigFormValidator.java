/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

public class MiscConfigFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = LogManager.getLogger(JobThrottleConfigFormValidator.class);

	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (MiscConfigForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	//log.debug(">>>");
		MiscConfigForm form = (MiscConfigForm) obj;
		try {
			InetAddress.getByName(form.getProviewHostname());
		} catch (UnknownHostException e) {
			Object[] args = { form.getProviewHostname() };
			errors.reject("error.unknown.proview.host", args, "Unknown/Invalid Proview Host");
		}
	}
}
