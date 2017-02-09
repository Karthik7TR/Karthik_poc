package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class QueueFormValidator implements Validator
{
    //private static final Logger log = LogManager.getLogger(FilterFormValidator.class);
    @Override
    public boolean supports(final Class<?> clazz)
    {
        return (QueueForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors)
    {
//		JobRunRequestForm form = (JobRunRequestForm) obj;

//		if (form.getTomIq() > 80) {
//			errors.reject("error.invalid.iq");
//		}
    }
}
