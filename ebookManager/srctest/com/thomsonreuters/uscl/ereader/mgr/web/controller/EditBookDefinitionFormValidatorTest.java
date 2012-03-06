/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionFormValidator;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

public class EditBookDefinitionFormValidatorTest {
    private CoreService mockCoreService;
    private EditBookDefinitionForm form;
    private EditBookDefinitionFormValidator validator;
    private Errors errors;
    
	@Before
	public void setUp() throws Exception {
    	// Mock up the dashboard service
    	this.mockCoreService = EasyMock.createMock(CoreService.class);
    	
    	// Setup Validator
    	validator = new EditBookDefinitionFormValidator();
    	validator.setCoreService(mockCoreService);
    	form = new EditBookDefinitionForm();
    	
    	errors = new BindException(form, "form");
	}

	/**
     * Test No data set on form
     */
	@Test
	public void testNoContentType() {
		// verify errors
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("contentType").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("titleId").getCode());
		Assert.assertEquals(2, errors.getAllErrors().size());
	}

	/**
     * Test Analytical Title Validation
     */
	@Test
	public void testAnalyticalTitleId() {
		setupPublisherAndTitleId("uscl/an/abcd", WebConstants.KEY_ANALYTICAL, 2);

		// Check Valid Analytical Title
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify Analytical content type requirements
		form.setPublisher(null);
		form.setPubAbbr(null);
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("publisher").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("pubAbbr").getCode());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
     * Test Court Rule Title Validation
     */
	@Test
	public void testCourtRuleTitleId() {
		setupPublisherAndTitleId("uscl/cr/tx_state", WebConstants.KEY_COURT_RULES, 2);

		// Check Valid Analytical Title
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify Analytical content type requirements
		form.setPublisher(null);
		form.setState(null);
		form.setPubType(null);
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("publisher").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("state").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("pubType").getCode());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
     * Test Slice Code Title Validation
     */
	@Test
	public void testSliceCodeTitleId() {
		setupPublisherAndTitleId("uscl/sc/us_abcd", WebConstants.KEY_SLICE_CODES, 2);

		// Check Valid Analytical Title
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify Analytical content type requirements
		form.setPublisher(null);
		form.setJurisdiction(null);
		form.setPubInfo(null);
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("publisher").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("jurisdiction").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("pubInfo").getCode());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
     * Test publisher Abbreviation and publisher information
     * No special characters
     */
	@Test
	public void testSpecialCharacters() {
		setupPublisherAndTitleId("uscl/an/abcd", WebConstants.KEY_ANALYTICAL, 2);

		// Check Valid Analytical Title
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify No special characters
		form.setPubAbbr("_");
		form.setPubInfo("!@");
		validator.validate(form, errors);
		Assert.assertEquals("error.special.characters", errors.getFieldError("pubAbbr").getCode());
		Assert.assertEquals("error.special.characters", errors.getFieldError("pubInfo").getCode());

		EasyMock.verify(mockCoreService);
	}
	
	/**
     * Test publisher Abbreviation and publisher information
     * No spaces
     */
	@Test
	public void testNoSpaces() {
		setupPublisherAndTitleId("uscl/an/abcd", WebConstants.KEY_ANALYTICAL, 2);

		// Check Valid Analytical Title
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		// Verify No spaces
		form.setPubAbbr("abc abc");
		form.setPubInfo("abc abc");
		validator.validate(form, errors);
		Assert.assertEquals("error.no.spaces", errors.getFieldError("pubAbbr").getCode());
		Assert.assertEquals("error.no.spaces", errors.getFieldError("pubInfo").getCode());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
	 * Test unique Title Id when creating new Book Definition
	 */
	@Test
	public void testUniquTitleIdWhenCreating() {
		EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null);
    	EasyMock.replay(mockCoreService);
    	
    	populateFormDataAnalyticalNort();
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
	 * Test unique Title Id when Editing Book Definition
	 * and title changed
	 */
	@Test
	@Ignore
	public void testUniquTitleIdWhenEditing() {
		//TODO: Need to implement when surrogate keys are made in Book Definition
		EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null);
    	EasyMock.replay(mockCoreService);
    	
    	populateFormDataAnalyticalNort();
    	form.setBookdefinitionId(Long.parseLong("1"));
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
	 * Test title does not change after book is published in Proview
	 */
	@Test
	@Ignore
	public void testTitleDoesNotChange() {
		//TODO: Need to implement when surrogate keys are made in Book Definition
		EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null);
    	EasyMock.replay(mockCoreService);
    	
    	populateFormDataAnalyticalNort();
    	form.setBookdefinitionId(Long.parseLong("1"));
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
	 * Test max length of Title Id
	 */
	@Test
	public void testMaxLengthTitleId() {
		EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null);
    	EasyMock.replay(mockCoreService);
    	
    	populateFormDataAnalyticalNort();
    	form.setTitleId("12345678901234567890123456789012345678901");
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.max.length", errors.getFieldError("titleId").getCode());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
	 * Test all required fields when put on Complete with NORT
	 */
	@Test
	public void testAllRequiredFieldsNort() {
		EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null).times(2);
    	EasyMock.replay(mockCoreService);
    	
    	populateFormDataAnalyticalNort();
    	form.setIsComplete(true);
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		form.setProviewDisplayName(null);
		form.setNameLines(new ArrayList<EbookName>());
		form.setCopyright(null);
		form.setMaterialId(null);
		form.setIsbn(null);
		form.setNortDomain(null);
		form.setNortFilterView(null);
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("proviewDisplayName").getCode());
		Assert.assertEquals("error.at.least.one", errors.getFieldError("nameLines").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("copyright").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("materialId").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("isbn").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("nortDomain").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("nortFilterView").getCode());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
	 * Test all required fields when put on Complete with TOC
	 */
	@Test
	public void testAllRequiredFieldsToc() {
		EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null).times(2);
    	EasyMock.replay(mockCoreService);
    	
    	populateFormDataAnalyticalToc();
    	form.setIsComplete(true);
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		form.setProviewDisplayName(null);
		form.setNameLines(new ArrayList<EbookName>());
		form.setCopyright(null);
		form.setMaterialId(null);
		form.setIsbn(null);
		form.setTocCollectionName(null);
		form.setRootTocGuid(null);
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("proviewDisplayName").getCode());
		Assert.assertEquals("error.at.least.one", errors.getFieldError("nameLines").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("copyright").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("materialId").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("isbn").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("tocCollectionName").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("rootTocGuid").getCode());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
	 * Test Root TOC GUID format
	 */
	@Test
	public void testGuidFormat() {
		EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null).times(1);
    	EasyMock.replay(mockCoreService);
    	
    	populateFormDataAnalyticalToc();
    	form.setIsComplete(true);
		form.setRootTocGuid("asdwwqwe");
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.guid.format", errors.getFieldError("rootTocGuid").getCode());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
	 * Test ISBN-13 format
	 */
	@Test
	public void testIsbnFormat() {
		EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null).times(1);
    	EasyMock.replay(mockCoreService);
    	
    	populateFormDataAnalyticalToc();
    	form.setIsComplete(true);
		form.setIsbn("1234");
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.isbn.format", errors.getFieldError("isbn").getCode());
		
		EasyMock.verify(mockCoreService);
	}
	
	/**
	 * Test PublicationCutoffDate format
	 */
	@Test
	public void testPublicationCutoffDateFormat() {
		EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(null).times(1);
    	EasyMock.replay(mockCoreService);
    	
    	populateFormDataAnalyticalToc();
    	form.setIsComplete(true);
		form.setPublicationCutoffDate("1234");
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.date.format", errors.getFieldError("publicationCutoffDate").getCode());
		
		EasyMock.verify(mockCoreService);
	}
	
	private void setupPublisherAndTitleId(String titleId, String contentType, int mockReplayTimes) {
		BookDefinition book = new BookDefinition();
		book.setContentType(contentType);
		BookDefinitionKey key = new BookDefinitionKey(titleId);
		book.setBookDefinitionKey(key);
		form.initialize(book);
		form.setIsComplete(false);
		form.setBookdefinitionId(Long.parseLong("1"));
		
		EasyMock.expect(mockCoreService.findBookDefinition(EasyMock.anyObject(BookDefinitionKey.class))).andReturn(book).times(mockReplayTimes);
    	EasyMock.replay(mockCoreService);
	}
	
	
	private BookDefinition populateFormDataAnalyticalNort() {
		String titleId = "uscl/an/abcd";
		BookDefinition book = new BookDefinition();
		book.setContentType(WebConstants.KEY_ANALYTICAL);
		BookDefinitionKey key = new BookDefinitionKey(titleId);
		book.setBookDefinitionKey(key);
		form.initialize(book);
		form.setProviewDisplayName("Proview Display Name");
		form.setCopyright("copyright");
		form.setMaterialId("a12345678123456781234567812345678");
		form.setIsbn("978-193-5-18235-1");
		form.setKeyCiteToplineFlag(true);
		form.setIsComplete(false);
		form.setValidateForm(false);
		form.setIsTOC(false);
		form.setNortDomain("1234");
		form.setNortFilterView("1234");
		Collection<EbookName> nameLines = new ArrayList<EbookName>();
		EbookName nameLine = new EbookName();
		nameLine.setEbookNameId(1);
		nameLine.setBookNameText("Book Title");
		nameLine.setSequenceNum(1);
		nameLines.add(nameLine);
		form.setNameLines(nameLines);
		
		return book;
	}
	
	private BookDefinition populateFormDataAnalyticalToc() {
		String titleId = "uscl/an/abcd";
		BookDefinition book = new BookDefinition();
		book.setContentType(WebConstants.KEY_ANALYTICAL);
		BookDefinitionKey key = new BookDefinitionKey(titleId);
		book.setBookDefinitionKey(key);
		form.initialize(book);
		form.setProviewDisplayName("Proview Display Name");
		form.setCopyright("copyright");
		form.setMaterialId("a12345678123456781234567812345678");
		form.setIsbn("978-193-5-18235-1");
		form.setKeyCiteToplineFlag(true);
		form.setIsComplete(false);
		form.setValidateForm(false);
		form.setIsTOC(true);
		form.setTocCollectionName("1234");
		form.setRootTocGuid("i12345678123456781234567812345678");
		Collection<EbookName> nameLines = new ArrayList<EbookName>();
		EbookName nameLine = new EbookName();
		nameLine.setEbookNameId(1);
		nameLine.setBookNameText("Book Title");
		nameLine.setSequenceNum(1);
		nameLines.add(nameLine);
		form.setNameLines(nameLines);
		
		return book;
	}
	
}
