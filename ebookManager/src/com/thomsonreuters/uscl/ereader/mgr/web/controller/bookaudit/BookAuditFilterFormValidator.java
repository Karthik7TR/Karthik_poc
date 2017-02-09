package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BookAuditFilterFormValidator extends BaseFormValidator implements Validator
{
    //private static final Logger log = LogManager.getLogger(FilterFormValidator.class);
    @Override
    public boolean supports(final Class<?> clazz)
    {
        return (BookAuditFilterForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors)
    {
        final BookAuditFilterForm form = (BookAuditFilterForm) obj;
        // Do not validate form if we are simply resetting its values to the defaults
        if (BookAuditFilterForm.FilterCommand.RESET.equals(form.getFilterCommand()))
        {
            return;
        }

        final Date fromDate = form.getFromDate();
        final Date toDate = form.getToDate();

        if (StringUtils.isNotBlank(form.getFromDateString()))
        {
            validateDate(form.getFromDateString(), fromDate, "FROM", errors);
        }
        if (StringUtils.isNotBlank(form.getToDateString()))
        {
            validateDate(form.getToDateString(), toDate, "TO", errors);
        }

        validateDateRange(fromDate, toDate, errors);
    }
}
