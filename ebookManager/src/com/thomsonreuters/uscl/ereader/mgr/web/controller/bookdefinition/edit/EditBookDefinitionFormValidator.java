package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

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
    	EditBookDefinitionForm form = (EditBookDefinitionForm) obj;
    	String contentType = form.getContentType();
    	
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contentType", "error.required");
    	
    	if (!contentType.isEmpty()) {
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publisher", "error.required");
    		
    		// Check Analytical fields are filled out
    		if(contentType.equals(WebConstants.KEY_ANALYTICAL)) {
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubAbbr", "error.required");
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "titleId", "error.incomplete");
        	} else if (contentType.equals(WebConstants.KEY_COURT_RULES)) {
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "error.required");
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubType", "error.required");
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "titleId", "error.incomplete");
        	} else if (contentType.equals(WebConstants.KEY_SLICE_CODES)) {
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jurisdiction", "error.required");
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", "error.required");
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "titleId", "error.incomplete");
        	}
    		
    		//TODO continue
    		/**String pubInfo = form.getPubInfo();
    		if(!pubInfo.isEmpty()) {
    			pubInfo.
    		}**/
    		
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bookName", "error.required");
    	}
    	
    	
	}
}
