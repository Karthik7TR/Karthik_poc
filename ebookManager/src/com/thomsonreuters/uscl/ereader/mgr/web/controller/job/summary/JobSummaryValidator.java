package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.JobCommand;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class JobSummaryValidator implements Validator
{
    //private static final Logger log = LogManager.getLogger(FilterFormValidator.class);
    @Override
    public boolean supports(final Class<?> clazz)
    {
        return (JobSummaryForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors)
    {
        final JobSummaryForm form = (JobSummaryForm) obj;

        if ((form.getJobCommand() == JobCommand.STOP_JOB) || (form.getJobCommand() == JobCommand.RESTART_JOB))
        {
            if (form.getJobExecutionIds().length == 0)
            {
                errors.reject("error.required.bookselection");
            }
        }
    }
}
