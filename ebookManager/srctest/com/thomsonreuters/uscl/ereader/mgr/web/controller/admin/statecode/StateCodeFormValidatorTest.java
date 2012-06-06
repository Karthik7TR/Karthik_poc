/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;

public class StateCodeFormValidatorTest {
	private static final String STATE_NAME = "test";
    private CodeService mockCodeService;
    private StateCodeFormValidator validator;
    private StateCodeForm form;
    private Errors errors;
    
	@Before
	public void setUp() throws Exception {
		// Mock up the service
    	this.mockCodeService = EasyMock.createMock(CodeService.class);
    	
    	// Setup Validator
    	validator = new StateCodeFormValidator();
    	validator.setCodeService(mockCodeService);
    	
    	form = new StateCodeForm();
    	form.setStateId(1L);
		form.setName(STATE_NAME);
    	
    	errors = new BindException(form, "form");
	}

	@Test
	public void testNoName() {
		// Check Valid name entry
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify name requirement
		form.setName(null);
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("name").getCode());
	}

	@Test
	public void testNameExists() {
		EasyMock.expect(mockCodeService.getStateCodeByName(STATE_NAME)).andReturn(new StateCode());
		EasyMock.replay(mockCodeService);
		
		// Verify name requirement
		validator.validate(form, errors);
		Assert.assertEquals("error.exist", errors.getFieldError("name").getCode());
		
		EasyMock.verify(mockCodeService);
	}

	
}
