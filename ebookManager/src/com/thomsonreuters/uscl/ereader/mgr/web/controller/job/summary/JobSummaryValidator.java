package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.JobCommand;

public class JobSummaryValidator implements Validator {
	
	//private static final Logger log = Logger.getLogger(FilterFormValidator.class);
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (JobSummaryForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
		JobSummaryForm form = (JobSummaryForm) obj;
		
		if ((form.getJobCommand() == JobCommand.STOP_JOB) ||
			(form.getJobCommand() == JobCommand.RESTART_JOB)) {
			if (form.getJobExecutionIds().length == 0) {
				errors.reject("error.required.bookselection");
			}
		}
	}
}
