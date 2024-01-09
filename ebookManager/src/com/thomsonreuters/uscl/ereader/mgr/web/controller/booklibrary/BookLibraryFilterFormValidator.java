package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("bookLibraryFilterFormValidator")
public class BookLibraryFilterFormValidator extends BaseFormValidator implements Validator {
    @Override
    public boolean supports(final Class<?> clazz) {
        return (BookLibraryFilterForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        // Do not validate inputs if there were binding errors since you cannot validate garbage (like "abc" entered instead of a valid integer).
        if (errors.hasErrors()) {
            return;
        }
        final BookLibraryFilterForm form = (BookLibraryFilterForm) obj;
        if (form.getCommand() == BookLibraryFilterForm.Command.GENERATE) {
            final String[] keys = form.getSelectedEbookKeys();
            if (keys == null || keys.length == 0) {
                errors.reject("error.required.bookselection");
            }
        }
        // Do not validate form if we are simply resetting its values to the defaults
        if (form.areAllFiltersBlank()) {
            return;
        }
        final Date fromDate = form.getFrom();
        final Date toDate = form.getTo();

        if (StringUtils.isNotBlank(form.getFromString())) {
            validateDate(form.getFromString(), fromDate, "FROM", errors);
        }
        if (StringUtils.isNotBlank(form.getToString())) {
            validateDate(form.getToString(), toDate, "TO", errors);
        }

        validateDateRange(fromDate, toDate, errors);
    }
}
