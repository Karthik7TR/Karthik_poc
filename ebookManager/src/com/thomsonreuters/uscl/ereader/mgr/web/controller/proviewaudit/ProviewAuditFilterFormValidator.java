package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

public class ProviewAuditFilterFormValidator extends BaseFormValidator implements Validator {
	
	//private static final Logger log = Logger.getLogger(ProviewAuditFilterFormValidator.class);
	@Override
    public boolean supports(Class<?> clazz) {
		return (ProviewAuditFilterForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	ProviewAuditFilterForm form = (ProviewAuditFilterForm) obj;
    	// Do not validate form if we are simply resetting its values to the defaults
    	if (ProviewAuditFilterForm.FilterCommand.RESET.equals(form.getFilterCommand())) {
    		return;
    	}
    	
    	Date fromDate = form.getRequestFromDate();
    	Date toDate = form.getRequestToDate();

		if (StringUtils.isNotBlank(form.getRequestFromDateString())) {
			validateDate(form.getRequestFromDateString(), fromDate, "FROM", errors);
		}
		if (StringUtils.isNotBlank(form.getRequestToDateString())) {
			validateDate(form.getRequestToDateString(), toDate, "TO", errors);
		}

		validateDateRange(fromDate, toDate, errors);
	}
}
