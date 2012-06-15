/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.support;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public class SupportFormValidatorTest {
    private SupportFormValidator validator;
    private SupportForm form;
    private Errors errors;
    
	@Before
	public void setUp() throws Exception {
    	// Setup Validator
    	validator = new SupportFormValidator();
    	form = new SupportForm();
    	form.setLinkAddress("http://www.google.com");
    	form.setLinkDescription("testing");
    	
    	errors = new BindException(form, "form");
	}

	@Test
	public void testNoLinkAddress() {
		// Check Valid name entry
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify name requirement
		form.setLinkAddress(null);
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("linkAddress").getCode());
	}
	
	@Test
	public void testInvalidAddress() {
		// Check Valid name entry
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify name requirement
		form.setLinkAddress("www.google.com");
		validator.validate(form, errors);
		Assert.assertEquals("error.invalid.url", errors.getFieldError("linkAddress").getCode());
	}

	@Test
	public void testNoLinkDescription() {
		// Verify name requirement
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify name requirement
		form.setLinkDescription(null);
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("linkDescription").getCode());
	}

	
}
