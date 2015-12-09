package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

public class GroupFilterFormValidator extends BaseFormValidator implements Validator  {
	
	//private static final Logger log = Logger.getLogger(BookLibraryFilterFormValidator.class);
	@Override
    public boolean supports(Class<?> clazz) {
		return (GroupListFilterForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
		GroupListFilterForm form = (GroupListFilterForm) obj;
    	// Do not validate form if we are simply resetting its values to the defaults
    	if (GroupListFilterForm.FilterCommand.RESET.equals(form.getFilterCommand())) {
    		return;
    	}    	
	}

	
}

