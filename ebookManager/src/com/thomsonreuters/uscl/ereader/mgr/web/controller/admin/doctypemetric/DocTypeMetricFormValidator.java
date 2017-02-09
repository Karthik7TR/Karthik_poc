package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.doctypemetric;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("docTypeMetricFormValidator")
public class DocTypeMetricFormValidator extends BaseFormValidator implements Validator
{
    @Override
    public boolean supports(final Class<?> clazz)
    {
        return (DocTypeMetricForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors)
    {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "thresholdValue", "error.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "thresholdPercent", "error.required");
    }
}
