package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobsummary;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("jobSummaryFormValidator")
public class JobSummaryFormValidator implements Validator {
	
	//private static final Logger log = Logger.getLogger(JobSummaryFormValidator.class);
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (JobSummaryForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	// Do not validate inputs if there were binding errors since you cannot validate garbage (like "abc" entered instead of a valid integer).
    	if (errors.hasErrors()) {
    		return;
    	}
    	JobSummaryForm form = (JobSummaryForm) obj;
    	
		if (StringUtils.isBlank(form.getStartDate())) {
			String[] args = { "Start Date" };
			errors.reject("error.required.field", args, "start date is required");
		} else {
			if (form.getStartTime() == null) {
				errors.reject("error.start.date.invalid");	
			} else if (form.getStartTime().after(new Date())) {
				errors.reject("error.start.date.too.late");
			}
		}
	}
}
