/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;

public class KeywordValueFormValidatorTest {
	private KeywordTypeCode KEYWORD_CODE = new KeywordTypeCode();
	private static final Long KEYWORD_CODE_ID = 1L;
	private static final String KEYWORD_CODE_NAME = "test";
	private static final String KEYWORD_VALUE_NAME = "Value name";
	private static final Long KEYWORD_VALUE_ID = 1L;
    private CodeService mockCodeService;
    private KeywordValueFormValidator validator;
    private KeywordValueForm form;
    private Errors errors;
    
	@Before
	public void setUp() throws Exception {
		// Mock up the service
    	this.mockCodeService = EasyMock.createMock(CodeService.class);
    	
    	// Setup Validator
    	validator = new KeywordValueFormValidator();
    	validator.setCodeService(mockCodeService);
    	
    	KEYWORD_CODE.setId(KEYWORD_CODE_ID);
    	KEYWORD_CODE.setName(KEYWORD_CODE_NAME);
    	
    	form = new KeywordValueForm();
    	form.setTypeId(KEYWORD_VALUE_ID);
		form.setName(KEYWORD_VALUE_NAME);
		form.setKeywordTypeCode(KEYWORD_CODE);
		
		errors = new BindException(form, "form");
	}

	@Test
	public void testNoName() {
		EasyMock.expect(mockCodeService.getKeywordTypeCodeById(KEYWORD_CODE_ID)).andReturn(KEYWORD_CODE).times(2);
		EasyMock.replay(mockCodeService);
		
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
		KeywordTypeValue value = new KeywordTypeValue();
		value.setId(KEYWORD_VALUE_ID);
		value.setName(KEYWORD_VALUE_NAME);
		KEYWORD_CODE.getValues().add(value);
		
		EasyMock.expect(mockCodeService.getKeywordTypeCodeById(KEYWORD_CODE_ID)).andReturn(KEYWORD_CODE);
		EasyMock.replay(mockCodeService);
		
		// Verify name requirement
		validator.validate(form, errors);
		Assert.assertEquals("error.exist.keyword", errors.getFieldError("name").getCode());
		
		EasyMock.verify(mockCodeService);
	}

	
}
