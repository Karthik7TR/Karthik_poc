package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

public class BookLibraryFilterFormValidator extends BaseFormValidator implements Validator  {
	
	//private static final Logger log = Logger.getLogger(BookLibraryFilterFormValidator.class);
	@Override
    public boolean supports(Class<?> clazz) {
		return (BookLibraryFilterForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
		BookLibraryFilterForm form = (BookLibraryFilterForm) obj;
    	// Do not validate form if we are simply resetting its values to the defaults
    	if (BookLibraryFilterForm.FilterCommand.RESET.equals(form.getFilterCommand())) {
    		return;
    	}
    	
    	Date fromDate = form.getFrom();
    	Date toDate = form.getTo();

		if (StringUtils.isNotBlank(form.getFromString())) {
			validateDate(form.getFromString(), fromDate, "FROM", errors);
		}
		if (StringUtils.isNotBlank(form.getToString())) {
			validateDate(form.getToString(), toDate, "TO", errors);
		}

		validateDateRange(fromDate, toDate, errors);
	}

	
}
