package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("jobExecutionFormValidator")
@Slf4j
public class JobExecutionFormValidator implements Validator {

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
