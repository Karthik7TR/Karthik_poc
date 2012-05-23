/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.UserPreferencesForm.HomepageProperty;

public class UserPreferencesFormValidatorTest {
    private UserPreferencesFormValidator validator;
    private UserPreferencesForm form;
    private Errors errors;
    
	@Before
	public void setUp() throws Exception {
		validator = new UserPreferencesFormValidator();
		form = new UserPreferencesForm();
		form.setStartPage(HomepageProperty.LIBRARY);
		
    	errors = new BindException(form, "form");
	}

	@Test
	public void testSaveOnlyStartPage() {
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify Start page requirement
		form.setStartPage(null);
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("startPage").getCode());
	}
	
	@Test
	public void testLength() {
		String length = "dfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsaffffffffffffffffffdfdsafffsdf@as.com";
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify input length requirement
		form.setAuditFilterProviewName(length);
		form.setAuditFilterTitleId(length);
		form.setJobSummaryFilterProviewName(length);
		form.setJobSummaryFilterTitleId(length);
		form.setLibraryFilterProviewName(length);
		form.setLibraryFilterTitleId(length);
		
		validator.validate(form, errors);
		Assert.assertEquals("error.max.length", errors.getFieldError("auditFilterProviewName").getCode());
		Assert.assertEquals("error.max.length", errors.getFieldError("auditFilterTitleId").getCode());
		Assert.assertEquals("error.max.length", errors.getFieldError("libraryFilterProviewName").getCode());
		Assert.assertEquals("error.max.length", errors.getFieldError("libraryFilterTitleId").getCode());
		Assert.assertEquals("error.max.length", errors.getFieldError("jobSummaryFilterProviewName").getCode());
		Assert.assertEquals("error.max.length", errors.getFieldError("jobSummaryFilterTitleId").getCode());
	}
	
	@Test
	public void testEmailLength() {
		String email = "asdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsaddsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsaasdasdsadsadsa@as.com";
		
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		List<String> emails = new ArrayList<String>();
		emails.add(email);
		
		// Verify input length requirement
		form.setEmails(emails);
		
		validator.validate(form, errors);
		Assert.assertEquals("error.invalid", errors.getFieldError("emails[0]").getCode());
	}
	
	@Test
	public void testEmailMax() {
		String email = "@as.com";
		
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		List<String> emails = new ArrayList<String>();
		
		for(int i = 0; i < 500; i++) {
			emails.add(i + email);
		}
		
		
		// Verify input length requirement
		form.setEmails(emails);
		
		validator.validate(form, errors);
		Assert.assertEquals("error.email.too.many", errors.getFieldError("emails").getCode());
	}
	
	@Test
	public void testEmailDuplicate() {
		String email = "as@as.com";
		
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		List<String> emails = new ArrayList<String>();
		emails.add(email);
		emails.add(email);
		
		// Verify input length requirement
		form.setEmails(emails);
		
		validator.validate(form, errors);
		Assert.assertEquals("error.duplicate", errors.getFieldError("emails[1]").getCode());
	}
}