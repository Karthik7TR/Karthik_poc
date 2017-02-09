package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class JobThrottleConfigFormValidator extends BaseFormValidator implements Validator
{
    //private static final Logger log = LogManager.getLogger(JobThrottleConfigFormValidator.class);

    @Override
    public boolean supports(final Class<?> clazz)
    {
        return (JobThrottleConfigForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors)
    {
        //log.debug(">>>");
        final JobThrottleConfigForm form = (JobThrottleConfigForm) obj;
        if ((form.isStepThrottleEnabled() && StringUtils.isBlank(form.getThrottleStepName())))
        {
            errors.reject("throttle.admin.step.name.blank");
        }

        if (form.getThrotttleStepMaxJobs() > form.getCoreThreadPoolSize())
        {
            errors.reject("throttle.admin.step.max.jobs");
        }
    }
}
