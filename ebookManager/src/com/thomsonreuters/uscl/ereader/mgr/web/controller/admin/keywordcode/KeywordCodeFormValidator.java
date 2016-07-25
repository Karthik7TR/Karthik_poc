/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

@Component("keywordCodeFormValidator")
public class KeywordCodeFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = LogManager.getLogger(PubdictionCodeFormValidator.class);
	private static final int MAXIMUM_CHARACTER_1024 = 1024;
	private CodeService codeService;

	
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (KeywordCodeForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	
    	KeywordCodeForm form = (KeywordCodeForm) obj;
    	
    	String name = form.getName();
    	
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.required");
    	checkMaxLength(errors, MAXIMUM_CHARACTER_1024, name, "name", new Object[] {"Name", MAXIMUM_CHARACTER_1024});
    	
    	if(!StringUtils.isBlank(name)) {
	    	KeywordTypeCode code = codeService.getKeywordTypeCodeByName(name);
	    	if(code != null && code.getId() != form.getCodeId()) {
	    		errors.rejectValue("name", "error.exist", new Object[] {"Name"}, "Already exists");
	    	}
    	}
	}
	
	
	@Required
	public void setCodeService(CodeService service) {
		this.codeService = service;
	}
	
}
