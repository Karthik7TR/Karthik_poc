/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jurisdictioncode;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;

public class JurisdictionCodeFormValidatorTest {
	private static final String JURIS_NAME = "test";
    private CodeService mockCodeService;
    private JurisdictionCodeFormValidator validator;
    private JurisdictionCodeForm form;
    private Errors errors;
    
	@Before
	public void setUp() throws Exception {
		// Mock up the dashboard service
    	this.mockCodeService = EasyMock.createMock(CodeService.class);
    	
    	// Setup Validator
    	validator = new JurisdictionCodeFormValidator();
    	validator.setCodeService(mockCodeService);
    	
    	form = new JurisdictionCodeForm();
    	form.setJurisId(1L);
		form.setName(JURIS_NAME);
    	
    	errors = new BindException(form, "form");
	}

	@Test
	public void testNoName() {
		// Check Valid name
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify name requirement
		form.setName(null);
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("name").getCode());
	}

	@Test
	public void testNameExists() {
		EasyMock.expect(mockCodeService.getJurisTypeCodeByName(JURIS_NAME)).andReturn(new JurisTypeCode());
		EasyMock.replay(mockCodeService);
		
		// Verify name requirement
		validator.validate(form, errors);
		Assert.assertEquals("error.exist", errors.getFieldError("name").getCode());
		
		EasyMock.verify(mockCodeService);
	}

	
}
