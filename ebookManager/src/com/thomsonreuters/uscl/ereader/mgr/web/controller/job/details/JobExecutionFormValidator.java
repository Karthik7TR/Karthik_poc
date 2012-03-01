package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("jobExecutionFormValidator")
public class JobExecutionFormValidator implements Validator {
	
	private static final Logger log = Logger.getLogger(JobExecutionFormValidator.class);
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (JobExecutionForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
		log.debug(">>>");
    	// Do not validate inputs if there were binding errors since you cannot validate garbage (like "abc" entered instead of a valid integer).
    	if (errors.hasErrors()) {
    		return;
    	}
    	JobExecutionForm form = (JobExecutionForm) obj;
    	
		if (form.getJobExecutionId() == null) {
			String[] args = { "Job Execution ID" };
			errors.reject("error.required.field", args, "execution id is required");
		}
	}
}
