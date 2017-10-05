package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("jobExecutionFormValidator")
public class JobExecutionFormValidator implements Validator {
    private static final Logger log = LogManager.getLogger(JobExecutionFormValidator.class);

    @Override
    public boolean supports(final Class<?> clazz) {
        return (JobExecutionForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        log.debug(">>>");
        // Do not validate inputs if there were binding errors since you cannot
        // validate garbage (like "abc" entered instead of a valid integer).
        if (errors.hasErrors()) {
            return;
        }
        final JobExecutionForm form = (JobExecutionForm) obj;

        if (form.getJobExecutionId() == null) {
            final String[] args = {"Job Execution ID"};
            errors.reject("error.required.field", args, "execution id is required");
        }
    }
}
