package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("outageFormValidator")
public class OutageFormValidator extends BaseFormValidator implements Validator {
    private static final int MAXIMUM_CHARACTER_2048 = 2048;

    @Override
    public boolean supports(final Class<?> clazz) {
        return (OutageForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final OutageForm form = (OutageForm) obj;

        ValidationUtils
            .rejectIfEmptyOrWhitespace(errors, "outageTypeId", "error.required.field", new Object[] {"Outage Type"});
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "startTimeString",
            "error.required.field",
            new Object[] {"Start Date/Time"});
        ValidationUtils
            .rejectIfEmptyOrWhitespace(errors, "endTimeString", "error.required.field", new Object[] {"End Date/Time"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reason", "error.required.field", new Object[] {"Reason"});

        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_2048,
            form.getReason(),
            "reason",
            new Object[] {"Reason", MAXIMUM_CHARACTER_2048});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_2048,
            form.getServersImpacted(),
            "serversImpacted",
            new Object[] {"Servers Impacted", MAXIMUM_CHARACTER_2048});
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_2048,
            form.getSystemImpactDescription(),
            "systemImpactDescription",
            new Object[] {"System Impact Description", MAXIMUM_CHARACTER_2048});

        final Date fromDate = form.getStartTime();
        final Date toDate = form.getEndTime();

        validateDate(form.getStartTimeString(), fromDate, "Start", errors);
        validateDate(form.getEndTimeString(), toDate, "End", errors);

        checkDateRange(fromDate, toDate, errors);
    }

    private void checkDateRange(final Date fromDate, final Date toDate, final Errors errors) {
        if (fromDate != null) {
            final Date currentDate = new Date();
            if (fromDate.before(currentDate)) {
                errors.reject("error.start.date.in.past");
            }

            if (toDate != null) {
                if (toDate.before(currentDate)) {
                    errors.reject("error.end.date.in.past");
                }
                if (fromDate.after(toDate)) {
                    errors.reject("error.start.date.after.end.date");
                }
                if (toDate.before(fromDate)) {
                    errors.reject("error.end.date.before.start.date");
                }
            }
        }
    }
}
