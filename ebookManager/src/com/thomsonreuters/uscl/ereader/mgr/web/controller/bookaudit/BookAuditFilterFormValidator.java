package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BookAuditFilterFormValidator implements Validator {
	
	//private static final Logger log = Logger.getLogger(FilterFormValidator.class);
	@Override
    public boolean supports(Class<?> clazz) {
		return (BookAuditFilterForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	BookAuditFilterForm form = (BookAuditFilterForm) obj;
    	// Do not validate form if we are simply resetting its values to the defaults
    	if (BookAuditFilterForm.FilterCommand.RESET.equals(form.getFilterCommand())) {
    		return;
    	}
    	
    	Date fromDate = form.getFromDate();
    	Date toDate = form.getToDate();

		if (StringUtils.isNotBlank(form.getFromDateString())) {
			validateDate(form.getFromDateString(), fromDate, "FROM", errors);
		}
		if (StringUtils.isNotBlank(form.getToDateString())) {
			validateDate(form.getToDateString(), toDate, "TO", errors);
		}

		Date timeNow = new Date();
		String codeDateAfterToday = "error.date.after.today";
		if (fromDate != null) {
			if (fromDate.after(timeNow)) {
				String[] args = { "FROM" };
				errors.reject(codeDateAfterToday, args, "ERR: FROM date cannot be after today");
			}
			if (toDate != null) {
				if (fromDate.after(toDate)) {
					errors.reject("error.from.date.after.to.date");	
				}
				if (toDate.before(fromDate)) {
					errors.reject("error.to.date.before.from.date");	
				}
			}
		}
		if (toDate != null) {
			if (toDate.after(timeNow)) {
				String[] args = { "TO" };
				errors.reject(codeDateAfterToday, args, "ERR: TO date cannot be after today");
			}
		}
	}

	private static void validateDate(String dateString, Date parsedDate, String label, Errors errors) {
		if (StringUtils.isNotBlank(dateString)) {
			if (parsedDate == null) {
				Object[] args = { label };
				errors.reject("error.invalid.date", args, "Invalid Date: " + label);
			}
		}
	}
}
