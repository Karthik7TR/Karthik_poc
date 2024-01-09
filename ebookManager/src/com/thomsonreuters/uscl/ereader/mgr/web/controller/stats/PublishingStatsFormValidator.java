package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("publishingStatsFormValidator")
public class PublishingStatsFormValidator extends BaseFormValidator implements Validator {
    @Override
    public boolean supports(final Class<?> clazz) {
        return (PublishingStatsFilterForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        // Do not validate inputs if there were binding errors since you cannot validate garbage (like "abc" entered instead of a valid integer).
        if (errors.hasErrors()) {
            return;
        }
        final PublishingStatsFilterForm form = (PublishingStatsFilterForm) obj;
        // Do not validate form if we are simply resetting its values to the defaults
        if (form.areAllFiltersBlank()) {
            return;
        }

        final Date fromDate = form.getFromDate();
        final Date toDate = form.getToDate();
        if (StringUtils.isNotBlank(form.getFromDateString())) {
            validateDate(form.getFromDateString(), fromDate, "FROM", errors);
        }
        if (StringUtils.isNotBlank(form.getToDateString())) {
            validateDate(form.getToDateString(), toDate, "TO", errors);
        }
        validateDateRange(fromDate, toDate, errors);
    }
}
