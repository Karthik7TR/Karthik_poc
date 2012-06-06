/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;

public class KeywordCodeFormValidatorTest {
	private static final String KEYWORD_CODE_NAME = "test";
    private CodeService mockCodeService;
    private KeywordCodeFormValidator validator;
    private KeywordCodeForm form;
    private Errors errors;
    
	@Before
	public void setUp() throws Exception {
		// Mock up the service
    	this.mockCodeService = EasyMock.createMock(CodeService.class);
    	
    	// Setup Validator
    	validator = new KeywordCodeFormValidator();
    	validator.setCodeService(mockCodeService);
    	
    	form = new KeywordCodeForm();
    	form.setCodeId(1L);
		form.setName(KEYWORD_CODE_NAME);
    	
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
		EasyMock.expect(mockCodeService.getKeywordTypeCodeByName(KEYWORD_CODE_NAME)).andReturn(new KeywordTypeCode());
		EasyMock.replay(mockCodeService);
		
		// Verify name requirement
		validator.validate(form, errors);
		Assert.assertEquals("error.exist", errors.getFieldError("name").getCode());
		
		EasyMock.verify(mockCodeService);
	}

	
}
