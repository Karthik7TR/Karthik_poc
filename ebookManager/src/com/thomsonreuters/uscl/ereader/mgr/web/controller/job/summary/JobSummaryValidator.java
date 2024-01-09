package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.JobCommand;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("jobSummaryValidator")
public class JobSummaryValidator implements Validator {
    @Override
    public boolean supports(final Class<?> clazz) {
        return (JobSummaryForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final JobSummaryForm form = (JobSummaryForm) obj;

        if ((form.getJobCommand() == JobCommand.STOP_JOB) || (form.getJobCommand() == JobCommand.RESTART_JOB)) {
            if (form.getJobExecutionIds().length == 0) {
                errors.reject("error.required.bookselection");
            }
        }
    }
}
