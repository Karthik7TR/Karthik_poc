package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("editBookDefinitionFormValidator")
public class EditBookDefinitionFormValidator implements Validator {
	
	//private static final Logger log = Logger.getLogger(EditBookDefinitionFormValidator.class);
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (EditBookDefinitionForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	// Do not validate inputs if there were binding errors since you cannot validate garbage 
		// (like "abc" entered instead of a valid integer).
    	if (errors.hasErrors()) {
    		return;
    	}
    	//EditBookDefinitionForm form = (EditBookDefinitionForm) obj;
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "titleId", "error.required.field");
    	
	}
}
