/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

public class FilterFormValidator extends BaseFormValidator implements Validator {
	
	//private static final Logger log = LogManager.getLogger(FilterFormValidator.class);
	@Override
    public boolean supports(Class<?> clazz) {
		return (FilterForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	FilterForm form = (FilterForm) obj;
    	// Do not validate form if we are simply resetting its values to the defaults
    	if (FilterForm.FilterCommand.RESET.equals(form.getFilterCommand())) {
    		return;
    	}
    	
    	Date fromDate = form.getFromDate();
    	Date toDate = form.getToDate();

		if (StringUtils.isNotBlank(form.getFromDateString())) {
			validateDate(form.getFromDateString(), fromDate, "FROM", errors);
		}
		if (StringUtils.isNotBlank(form.getToDateString())) {
			validateDate(form.getToDateString(), toDate, "TO", errors);
		}

		validateDateRange(fromDate, toDate, errors);
	}
}
