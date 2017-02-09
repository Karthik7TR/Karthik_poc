package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookaudit;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AdminAuditFilterFormValidator extends BaseFormValidator implements Validator
{
    //private static final Logger log = LogManager.getLogger(FilterFormValidator.class);
    @Override
    public boolean supports(final Class<?> clazz)
    {
        return (AdminAuditFilterForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors)
    {
        final AdminAuditFilterForm form = (AdminAuditFilterForm) obj;
    }
}
