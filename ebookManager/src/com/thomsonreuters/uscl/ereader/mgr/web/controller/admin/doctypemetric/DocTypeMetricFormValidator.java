package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.doctypemetric;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

@Component("docTypeMetricFormValidator")
public class DocTypeMetricFormValidator extends BaseFormValidator implements Validator {
	
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (DocTypeMetricForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {   	
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.required");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "thresholdValue", "error.required");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "thresholdPercent", "error.required");
	}

}
