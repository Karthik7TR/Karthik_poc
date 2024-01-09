package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.quality;

import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class QualityReportsAdminValidator extends BaseFormValidator implements Validator {
    private static final int MAXIMUM_CHARACTER_256 = 256;

    @Override
    public boolean supports(final Class clazz) {
        return QualityReportRecipient.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final QualityReportRecipient form = (QualityReportRecipient) obj;
        final String email = form.getEmail();
        final EmailValidator validator = EmailValidator.getInstance();

        if (!validator.isValid(email) || email.length() > MAXIMUM_CHARACTER_256) {
            errors.rejectValue("email", "error.invalid", new Object[] {"email"}, "Invalid email");
        }
    }

}
