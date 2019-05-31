package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.doctypemetric;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("docTypeMetricFormValidator")
public class DocTypeMetricFormValidator extends BaseFormValidator implements Validator {

    private final String MESSAGE_PROPERTY_NAME = "error.required";

    @Override
    public boolean supports(final Class<?> clazz) {
        return DocTypeMetricForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", MESSAGE_PROPERTY_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "thresholdValue", MESSAGE_PROPERTY_NAME);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "thresholdPercent", MESSAGE_PROPERTY_NAME);
    }
}
