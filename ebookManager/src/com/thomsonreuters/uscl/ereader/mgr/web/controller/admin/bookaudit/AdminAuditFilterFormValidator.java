/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookaudit;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

public class AdminAuditFilterFormValidator extends BaseFormValidator implements Validator {
	
	//private static final Logger log = LogManager.getLogger(FilterFormValidator.class);
	@Override
    public boolean supports(Class<?> clazz) {
		return (AdminAuditFilterForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
		AdminAuditFilterForm form = (AdminAuditFilterForm) obj;

	}
}
