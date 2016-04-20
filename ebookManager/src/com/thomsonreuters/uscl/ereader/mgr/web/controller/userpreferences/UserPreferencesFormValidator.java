/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.UserPreferencesForm.HomepageProperty;

@Component("editBookDefinitionFormValidator")
public class UserPreferencesFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = Logger.getLogger(UserPreferencesFormValidator.class);
	
	private static final int MAXIMUM_CHARACTER_64 = 64;
	private static final int MAXIMUM_CHARACTER_256 = 256;
	private static final int MAXIMUM_CHARACTER_1024 = 1024;
	private static final int MAXIMUM_CHARACTER_2048 = 2048;

	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(Class clazz) {
		return (UserPreferencesForm.class.isAssignableFrom(clazz));
	}

	@Override
	public void validate(Object obj, Errors errors) {
	
		UserPreferencesForm form = (UserPreferencesForm) obj;
		
		// MaxLength Validations
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getAuditFilterProviewName(), "auditFilterProviewName", new Object[] {"ProView Display Name", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getAuditFilterTitleId(), "auditFilterTitleId", new Object[] {"Title ID", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getJobSummaryFilterProviewName(), "jobSummaryFilterProviewName", new Object[] {"ProView Display Name", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getJobSummaryFilterTitleId(), "jobSummaryFilterTitleId", new Object[] {"Title ID", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getLibraryFilterProviewName(), "libraryFilterProviewName", new Object[] {"ProView Display Name", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getLibraryFilterTitleId(), "libraryFilterTitleId", new Object[] {"Title ID", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getGroupFilterName(), "groupFilterName", new Object[] {"ProView Display Name", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getGroupFilterId(), "groupFilterId", new Object[] {"Group ID", MAXIMUM_CHARACTER_1024});
		
		HomepageProperty startPage = form.getStartPage();
		if(startPage != null) {
			checkMaxLength(errors, MAXIMUM_CHARACTER_64, startPage.toString(), "startPage", new Object[] {"Start Page", MAXIMUM_CHARACTER_64});
		} else {
			errors.rejectValue("startPage", "error.required");
		}
		
		List<String> emails = form.getEmails();
		EmailValidator validator = EmailValidator.getInstance();
		List<String> checkDuplicateEmails = new ArrayList<String>();
		
		int i = 0;
		for(String email: emails) {
			if(!validator.isValid(email) || email.length() > MAXIMUM_CHARACTER_256){
				errors.rejectValue("emails["+ i +"]", "error.invalid", new Object[] {"email"}, "Invalid email");
			} else {
				if(checkDuplicateEmails.contains(email)){
					errors.rejectValue("emails["+ i +"]", "error.duplicate", new Object[] {"Email"}, "Duplicate Email");
				} else {
					checkDuplicateEmails.add(email);
				}
			}
			i++;
		}
		
		String emailStr = StringUtils.join(emails, ",");
		if(emailStr.length() > MAXIMUM_CHARACTER_2048) {
			errors.rejectValue("emails", "error.email.too.many");
		}
	}
}
