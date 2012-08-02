package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

public class PublishingStatsFormValidator extends BaseFormValidator implements Validator {
	
	//private static final Logger log = Logger.getLogger(PublishingStatsFormValidator.class);
	@Override
    public boolean supports(Class<?> clazz) {
		return (PublishingStatsFilterForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	PublishingStatsFilterForm form = (PublishingStatsFilterForm) obj;
    	// Do not validate form if we are simply resetting its values to the defaults
    	if (PublishingStatsFilterForm.FilterCommand.RESET.equals(form.getFilterCommand())) {
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

		validateDateRange(fromDate, toDate, errors);
	}
}
