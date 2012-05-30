/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

@Component("outageFormValidator")
public class OutageFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = Logger.getLogger(OutageFormValidator.class);
	private static final int MAXIMUM_CHARACTER_2048 = 2048;

	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (OutageForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	
    	OutageForm form = (OutageForm) obj;

    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "outageTypeId", "error.required.field", new Object[] {"Outage Type"});
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startTimeString", "error.required.field", new Object[] {"Start Date/Time"});
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endTimeString", "error.required.field", new Object[] {"End Date/Time"});
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reason", "error.required.field", new Object[] {"Reason"});
    	
    	checkMaxLength(errors, MAXIMUM_CHARACTER_2048, form.getReason(), "reason", new Object[] {"Reason", MAXIMUM_CHARACTER_2048});
    	checkMaxLength(errors, MAXIMUM_CHARACTER_2048, form.getServersImpacted(), "serversImpacted", new Object[] {"Servers Impacted", MAXIMUM_CHARACTER_2048});
    	checkMaxLength(errors, MAXIMUM_CHARACTER_2048, form.getSystemImpactDescription(), "systemImpactDescription", new Object[] {"System Impact Description", MAXIMUM_CHARACTER_2048});
    	
    	Date fromDate = form.getStartTime();
    	Date toDate = form.getEndTime();

		if (StringUtils.isNotBlank(form.getStartTimeString())) {
			validateDate(form.getStartTimeString(), fromDate, "Start", errors);
		}
		if (StringUtils.isNotBlank(form.getEndTimeString())) {
			validateDate(form.getEndTimeString(), toDate, "End", errors);
		}

		checkDateRange(fromDate, toDate, errors);
	}
	
	private void checkDateRange(Date fromDate, Date toDate, Errors errors) {
		if (fromDate != null) {
			if (toDate != null) {
				if (fromDate.after(toDate)) {
					errors.reject("error.start.date.after.end.date");	
				}
				if (toDate.before(fromDate)) {
					errors.reject("error.end.date.before.start.date");	
				}
			}
		}
	}
}
