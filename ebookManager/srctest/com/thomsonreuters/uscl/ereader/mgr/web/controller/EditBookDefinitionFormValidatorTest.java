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

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionFormValidator;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

public class EditBookDefinitionFormValidatorTest {
    private CoreService mockCoreService;
    private CodeService mockCodeService;
    private EditBookDefinitionForm form;
    private EditBookDefinitionFormValidator validator;
    private Errors errors;
    
    private DocumentTypeCode analyticalCode;
    
	@Before
	public void setUp() throws Exception {
    	// Mock up the dashboard service
    	this.mockCoreService = EasyMock.createMock(CoreService.class);
    	this.mockCodeService = EasyMock.createMock(CodeService.class);
    	
    	// Setup Validator
    	validator = new EditBookDefinitionFormValidator();
    	validator.setCoreService(mockCoreService);
    	validator.setCodeService(mockCodeService);
    	
    	form = new EditBookDefinitionForm();
    	
    	errors = new BindException(form, "form");
    	
    	analyticalCode = new DocumentTypeCode();
    	analyticalCode.setId(Long.parseLong("1"));
    	analyticalCode.setAbbreviation(WebConstants.KEY_ANALYTICAL_ABBR);
    	analyticalCode.setName(WebConstants.KEY_ANALYTICAL);
	}

	/**
     * Test No data set on form
     */
	@Test
	public void testNoContentType() {
		// verify errors
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("contentTypeId").getCode());
		Assert.assertEquals("error.required", errors.getFieldError("titleId").getCode());
		Assert.assertEquals(2, errors.getAllErrors().size());
	}

	/**
     * Test Analytical Title Validation
     */
	@Test
	public void testAnalyticalTitleId() {
		setupPublisherAndTitleId("uscl/an/abcd", analyticalCode, 2);
		
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
		EasyMock.verify(mockCodeService);
	}
	
	/**
     * Test Court Rule Title Validation
     */
	@Test
	public void testCourtRuleTitleId() {
		
		DocumentTypeCode courtRulesCode = new DocumentTypeCode();
    	courtRulesCode.setId(Long.parseLong("1"));
    	courtRulesCode.setAbbreviation(WebConstants.KEY_COURT_RULES_ABBR);
    	courtRulesCode.setName(WebConstants.KEY_COURT_RULES);
		
		setupPublisherAndTitleId("uscl/cr/tx_state", courtRulesCode, 2);

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
		
		DocumentTypeCode sliceCodesCode = new DocumentTypeCode();
    	sliceCodesCode.setId(Long.parseLong("1"));
    	sliceCodesCode.setAbbreviation(WebConstants.KEY_SLICE_CODES_ABBR);
    	sliceCodesCode.setName(WebConstants.KEY_SLICE_CODES);
    	
		setupPublisherAndTitleId("uscl/sc/us_abcd", sliceCodesCode, 2);

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
		setupPublisherAndTitleId("uscl/an/abcd", analyticalCode, 2);

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
		setupPublisherAndTitleId("uscl/an/abcd", analyticalCode, 2);

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

		EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
		
		EasyMock.expect(mockCoreService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null);
    	EasyMock.replay(mockCoreService);
    	
    	populateFormDataAnalyticalNort();
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		EasyMock.verify(mockCodeService);
		EasyMock.verify(mockCoreService);
	}
	
	/**
	 * Test unique Title Id when Editing Book Definition
	 * and title changed
	 */
	@Test
	public void testUniquTitleIdWhenEditing() {
		EasyMock.expect(mockCoreService.findBookDefinitionByEbookDefId(EasyMock.anyObject(Long.class))).andReturn(initializeBookDef("asd/asd", analyticalCode));
		EasyMock.expect(mockCoreService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null);
    	EasyMock.replay(mockCoreService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
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
	public void testTitleDoesNotChange() {
		BookDefinition book = initializeBookDef("uscl/an/abcd", analyticalCode);
		book.setPublishedOnceFlag(true);
		EasyMock.expect(mockCoreService.findBookDefinitionByEbookDefId(EasyMock.anyObject(Long.class))).andReturn(book);
    	EasyMock.replay(mockCoreService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
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
		EasyMock.expect(mockCoreService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null);
    	EasyMock.replay(mockCoreService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
		
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
		EasyMock.expect(mockCoreService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(2);
    	EasyMock.replay(mockCoreService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(2);
		EasyMock.replay(mockCodeService);
    	
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
		EasyMock.expect(mockCoreService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(2);
    	EasyMock.replay(mockCoreService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(2);
		EasyMock.replay(mockCodeService);
    	
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
		EasyMock.expect(mockCoreService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(1);
    	EasyMock.replay(mockCoreService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
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
		EasyMock.expect(mockCoreService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(1);
    	EasyMock.replay(mockCoreService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
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
		EasyMock.expect(mockCoreService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(1);
    	EasyMock.replay(mockCoreService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
    	populateFormDataAnalyticalToc();
    	form.setIsComplete(true);
		form.setPublicationCutoffDate("1234");
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.date.format", errors.getFieldError("publicationCutoffDate").getCode());
		
		EasyMock.verify(mockCoreService);
	}

	private void setupPublisherAndTitleId(String titleId, DocumentTypeCode contentType, int mockReplayTimes) {
		BookDefinition book = initializeBookDef(titleId, contentType);

		form.initialize(book);
		form.setIsComplete(book.IsEbookDefinitionCompleteFlag());
		form.setBookdefinitionId(Long.parseLong("1"));
		
		EasyMock.expect(mockCoreService.findBookDefinitionByEbookDefId(EasyMock.anyObject(Long.class))).andReturn(book).times(mockReplayTimes);
    	EasyMock.replay(mockCoreService);

		EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(contentType).times(mockReplayTimes);
		EasyMock.replay(mockCodeService);
	}
	
	
	private BookDefinition populateFormDataAnalyticalNort() {
		String titleId = "uscl/an/abcd";
		BookDefinition book = initializeBookDef(titleId, analyticalCode);
		book.setIsTocFlag(false);
		populateFormData(book);
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
		BookDefinition book = initializeBookDef(titleId, analyticalCode);
		book.setIsTocFlag(true);
		populateFormData(book);
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
	
	private void populateFormData(BookDefinition book){
		form.initialize(book);
		form.setProviewDisplayName("Proview Display Name");
		form.setCopyright("copyright");
		form.setMaterialId("a12345678123456781234567812345678");
		form.setIsbn("978-193-5-18235-1");
		form.setKeyCiteToplineFlag(book.IsKeyciteToplineFlag());
		form.setIsComplete(book.IsEbookDefinitionCompleteFlag());
		form.setValidateForm(false);
		form.setIsTOC(book.getIsTocFlag());
	}
	
	private BookDefinition initializeBookDef(String titleId, DocumentTypeCode contentType) {
		BookDefinition book = new BookDefinition();
		book.setDocumentTypeCodes(contentType);
		book.setFullyQualifiedTitleId(titleId);
		book.setIsTocFlag(false);
    	book.setIsDeletedFlag(false);
    	book.setIsProviewTableViewFlag(false);
    	book.setEbookDefinitionCompleteFlag(false);
    	book.setAutoUpdateSupportFlag(true);
    	book.setSearchIndexFlag(true);
    	book.setPublishedOnceFlag(false);
    	book.setOnePassSsoLinkFlag(true);
    	book.setKeyciteToplineFlag(true);
		
		return book;
	}
	
}
