package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.generatorswitch;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("killSwitchFormValidator")
public class StopGeneratorFormValidator implements Validator {
    //private static final Logger log = LogManager.getLogger(KillSwitchFormValidator.class);

    @Override
    public boolean supports(final Class clazz) {
        return (StopGeneratorForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final StopGeneratorForm form = (StopGeneratorForm) obj;

        final String code = form.getCode();
        if (StringUtils.isBlank(code)) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "error.required");
        } else if (!code.equals(WebConstants.CONFIRM_CODE_KILL_SWITCH)) {
            errors.rejectValue("code", "error.invalid", new Object[] {"Code"}, "Invalid code");
        }
    }
}
