package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("proviewAuditFilterFormValidator")
public class ProviewAuditFilterFormValidator extends BaseFormValidator implements Validator {
    @Override
    public boolean supports(final Class<?> clazz) {
        return (ProviewAuditFilterForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final ProviewAuditFilterForm form = (ProviewAuditFilterForm) obj;
        // Do not validate form if we are simply resetting its values to the defaults
        if (form.areAllFiltersBlank()) {
            return;
        }

        final Date fromDate = form.getRequestFromDate();
        final Date toDate = form.getRequestToDate();

        if (StringUtils.isNotBlank(form.getRequestFromDateString())) {
            validateDate(form.getRequestFromDateString(), fromDate, "FROM", errors);
        }
        if (StringUtils.isNotBlank(form.getRequestToDateString())) {
            validateDate(form.getRequestToDateString(), toDate, "TO", errors);
        }

        validateDateRange(fromDate, toDate, errors);
    }
}
