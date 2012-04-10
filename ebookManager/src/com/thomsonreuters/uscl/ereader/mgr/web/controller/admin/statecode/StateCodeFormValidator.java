/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

@Component("stateCodeFormValidator")
public class StateCodeFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = Logger.getLogger(PubdictionCodeFormValidator.class);
	private static final int MAXIMUM_CHARACTER_1024 = 1024;
	private CodeService codeService;

	
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (StateCodeForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	
    	StateCodeForm form = (StateCodeForm) obj;
    	
    	String name = form.getName();
    	
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.required");
    	checkMaxLength(errors, MAXIMUM_CHARACTER_1024, name, "name", new Object[] {"Name", MAXIMUM_CHARACTER_1024});
    	checkForSpaces(errors, name, "name", "Name");
    	checkSpecialCharacters(errors, name, "name", true);
    	
    	if(!StringUtils.isBlank(name)) {
	    	StateCode code = codeService.getStateCodeByName(name);
	    	if(code != null && code.getId() != form.getId()) {
	    		errors.rejectValue("name", "error.exist", new Object[] {"Name"}, "Already exists");
	    	}
    	}
	}
	
	
	@Required
	public void setCodeService(CodeService service) {
		this.codeService = service;
	}
	
}
