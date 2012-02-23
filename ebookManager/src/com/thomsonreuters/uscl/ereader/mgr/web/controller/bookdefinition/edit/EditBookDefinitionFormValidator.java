package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Component("editBookDefinitionFormValidator")
public class EditBookDefinitionFormValidator implements Validator {
	private static final int MAXIMUM_TITLE_ID_LENGTH = 40;
	
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
    		// Validate publisher information
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publisher", "error.required");
    		
    		if(contentType.equals(WebConstants.KEY_ANALYTICAL)) {
    			// Validate Analytical fields are filled out
    			String pubAbbr = form.getPubAbbr();
        		checkForSpaces(errors, pubAbbr, "pubAbbr", "Pub Abbreviation");
        		checkSpecialCharacters(errors, pubAbbr, "pubAbbr", false);
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubAbbr", "error.required");
        	} else if (contentType.equals(WebConstants.KEY_COURT_RULES)) {
        		// Validate Court Rules fields are filled out
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "error.required");
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubType", "error.required");
        	} else if (contentType.equals(WebConstants.KEY_SLICE_CODES)) {
        		// Validate Slice Codes fields are filled out
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jurisdiction", "error.required");
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", "error.required");
        	}
    		
    		// Validate Title ID
    		String titleId = form.getTitleId();
    		checkMaxLength(errors, MAXIMUM_TITLE_ID_LENGTH, titleId, "titleId", new Object[] {"Title ID", MAXIMUM_TITLE_ID_LENGTH});
    		
    		// Validate Publication Information
    		String pubInfo = form.getPubInfo();
    		checkForSpaces(errors, pubInfo, "pubInfo", "Pub Info");
    		checkSpecialCharacters(errors, pubInfo, "pubInfo", true);
    		
    		if(form.getIsComplete()) {
    			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nameLine1", "error.required");
    			
    			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "copyright", "error.required");
    			
    			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "materialId", "error.required");
    			
    			if (form.getIsTOC()) {
    				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rootTocGuid", "error.required");
    				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tocCollectionName", "error.required");
    			} else {
    				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nortDomain", "error.required");
    				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nortFilterView", "error.required");
    			}
    			
    			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "isbn", "error.required");
    		}
    	}
	}
	
	private void checkMaxLength(Errors errors, int maxValue ,String text, String fieldName,  Object[]  args) {
		if(text.length() > maxValue) {
			errors.rejectValue(fieldName, "error.max.length", args, "Must be maximum of " + maxValue + " characters or under");
		}
	}
	
	private void checkForSpaces(Errors errors, String text, String fieldName, String arg) {
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(text);
		
		if(matcher.find()) {
			errors.rejectValue(fieldName, "error.no.spaces", new Object[]{arg}, "No spaces allowed");
		}
	}
	
	private void checkSpecialCharacters(Errors errors, String text, String fieldName, boolean includeUnderscore) {
		Pattern pattern = includeUnderscore ? Pattern.compile("[^a-z0-9_ ]", Pattern.CASE_INSENSITIVE):
			Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		
		Matcher matcher = pattern.matcher(text);
		
		if(matcher.find()) {
			errors.rejectValue(fieldName, "error.special.characters");
		}
	}
}
