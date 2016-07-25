/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

@Component("keywordValueFormValidator")
public class KeywordValueFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = LogManager.getLogger(KeywordValueFormValidator.class);
	private static final int MAXIMUM_CHARACTER_1024 = 1024;
	private CodeService codeService;

	
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (KeywordValueForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	
    	KeywordValueForm form = (KeywordValueForm) obj;
    	
    	String name = form.getName();
    	
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.required");
    	checkMaxLength(errors, MAXIMUM_CHARACTER_1024, name, "name", new Object[] {"Name", MAXIMUM_CHARACTER_1024});
    	
    	KeywordTypeCode code = codeService.getKeywordTypeCodeById(form.getKeywordTypeCode().getId());
    	if(code != null) {
	    	for(KeywordTypeValue value : code.getValues()) {
	    		if(value.getName().equalsIgnoreCase(name)) {
	    			errors.rejectValue("name", "error.exist.keyword", new Object[] {"Name"}, "Already exists in this Keyword Code");
	    		}
	    	}
    	} else {
    		errors.rejectValue("name", "error.invalid", new Object[] {"Keyword Code"}, "Invalid Keyword Code");
    	}
	}
	
	
	@Required
	public void setCodeService(CodeService service) {
		this.codeService = service;
	}
	
}
