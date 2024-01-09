package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("outageTypeFormValidator")
public class OutageTypeFormValidator extends BaseFormValidator implements Validator {
    private static final int MAXIMUM_CHARACTER_128 = 128;

    @Override
    public boolean supports(final Class clazz) {
        return (OutageTypeForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final OutageTypeForm form = (OutageTypeForm) obj;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "system", "error.required.field", new Object[] {"System"});
        ValidationUtils
            .rejectIfEmptyOrWhitespace(errors, "subSystem", "error.required.field", new Object[] {"Sub-system"});

        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_128,
            form.getSystem(),
            "system",
            new Object[] {"System", MAXIMUM_CHARACTER_128});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_128,
            form.getSubSystem(),
            "subSystem",
            new Object[] {"Sub-system", MAXIMUM_CHARACTER_128});
    }
}
