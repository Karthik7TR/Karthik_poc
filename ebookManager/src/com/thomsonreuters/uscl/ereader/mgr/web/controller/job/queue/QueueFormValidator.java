package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class QueueFormValidator implements Validator {
	
	//private static final Logger log = Logger.getLogger(FilterFormValidator.class);
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (QueueForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
//		JobRunRequestForm form = (JobRunRequestForm) obj;
		
//		if (form.getTomIq() > 80) {
//			errors.reject("error.invalid.iq");
//		}
	}
}
