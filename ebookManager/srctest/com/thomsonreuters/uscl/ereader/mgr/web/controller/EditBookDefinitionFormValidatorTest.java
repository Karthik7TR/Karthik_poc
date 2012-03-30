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
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionFormValidator;

public class EditBookDefinitionFormValidatorTest {
    private BookDefinitionService mockBookDefinitionService;
    private CodeService mockCodeService;
    private EditBookDefinitionForm form;
    private EditBookDefinitionFormValidator validator;
    private Errors errors;
    
    private DocumentTypeCode analyticalCode;
    
	@Before
	public void setUp() throws Exception {
    	// Mock up the dashboard service
    	this.mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
    	this.mockCodeService = EasyMock.createMock(CodeService.class);
    	
    	// Setup Validator
    	validator = new EditBookDefinitionFormValidator();
    	validator.setBookDefinitionService(mockBookDefinitionService);
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
		Assert.assertEquals("mesg.errors.form", errors.getFieldError("validateForm").getCode());
		Assert.assertEquals(3, errors.getAllErrors().size());
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
		
		EasyMock.verify(mockBookDefinitionService);
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
		
		EasyMock.verify(mockBookDefinitionService);
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
		
		EasyMock.verify(mockBookDefinitionService);
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

		EasyMock.verify(mockBookDefinitionService);
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
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test unique Title Id when creating new Book Definition
	 */
	@Test
	public void testUniquTitleIdWhenCreating() {

		EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
		
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	populateFormDataAnalyticalNort();
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		EasyMock.verify(mockCodeService);
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test unique Title Id when Editing Book Definition
	 * and title changed
	 */
	@Test
	public void testUniquTitleIdWhenEditing() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(EasyMock.anyObject(Long.class))).andReturn(initializeBookDef("asd/asd", analyticalCode));
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
    	populateFormDataAnalyticalNort();
    	form.setBookdefinitionId(Long.parseLong("1"));
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test title does not change after book is published in Proview
	 */
	@Test
	public void testTitleDoesNotChange() {
		BookDefinition book = initializeBookDef("uscl/an/abcd", analyticalCode);
		book.setPublishedOnceFlag(true);
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(EasyMock.anyObject(Long.class))).andReturn(book);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
    	populateFormDataAnalyticalNort();
    	form.setBookdefinitionId(Long.parseLong("1"));
		validator.validate(form, errors);
		Assert.assertFalse(errors.hasErrors());
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test max length of Title Id
	 */
	@Test
	public void testMaxLengthTitleId() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
		
    	populateFormDataAnalyticalNort();
    	form.setTitleId("12345678901234567890123456789012345678901");
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.max.length", errors.getFieldError("titleId").getCode());
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test all required fields when put on Complete with NORT
	 */
	@Test
	public void testAllRequiredFieldsNort() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(2);
    	EasyMock.replay(mockBookDefinitionService);
    	
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
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test all required fields when put on Complete with TOC
	 */
	@Test
	public void testAllRequiredFieldsToc() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(2);
    	EasyMock.replay(mockBookDefinitionService);
    	
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
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test Root TOC GUID format
	 */
	@Test
	public void testGuidFormat() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(1);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
    	populateFormDataAnalyticalToc();
    	form.setIsComplete(true);
		form.setRootTocGuid("asdwwqwe");
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.guid.format", errors.getFieldError("rootTocGuid").getCode());
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test ISBN-13 format
	 */
	@Test
	public void testIsbnFormat() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(1);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
    	populateFormDataAnalyticalToc();
    	form.setIsComplete(true);
		form.setIsbn("1234");
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.isbn.format", errors.getFieldError("isbn").getCode());
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test PublicationCutoffDate format
	 */
	@Test
	public void testPublicationCutoffDateFormat() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(1);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
    	populateFormDataAnalyticalToc();
    	form.setIsComplete(true);
		form.setPublicationCutoffDate("1234");
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.date.format", errors.getFieldError("publicationCutoffDate").getCode());
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test Author required fields
	 */
	@Test
	public void testAuthorRequiredFields() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(1);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
    	populateFormDataAnalyticalToc();
    	Author author = new Author();
    	author.setAuthorFirstName("Test");
    	form.getAuthorInfo().add(author);
    	
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.required.field", errors.getFieldError("authorInfo[0].sequenceNum").getCode());
		Assert.assertEquals("error.author.last.name", errors.getFieldError("authorInfo[0].authorLastName").getCode());
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test Author required fields
	 */
	@Test
	public void testNameLineRequiredFields() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(1);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
    	populateFormDataAnalyticalToc();
    	EbookName name = new EbookName();
    	name.setBookNameText("Test");
    	form.getNameLines().add(name);
    	
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.required.field", errors.getFieldError("nameLines[1].sequenceNum").getCode());
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test FrontMatterPage required fields
	 */
	@Test
	public void testFrontMatterPageRequiredFields() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(1);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
    	populateFormDataAnalyticalToc();
    	FrontMatterPage page = new FrontMatterPage();
		form.getFrontMatters().add(page);
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.required.field", errors.getFieldError("frontMatters[0].pageTocLabel").getCode());
		Assert.assertEquals("error.required.field", errors.getFieldError("frontMatters[0].sequenceNum").getCode());
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test FrontMatterSection required fields
	 */
	@Test
	public void testFrontMatterSectionRequiredFields() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(1);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
    	populateFormDataAnalyticalToc();
    	FrontMatterPage page = new FrontMatterPage();
    	page.setPageTocLabel("Toc Label");
    	page.setSequenceNum(1);
    	
    	FrontMatterSection section = new FrontMatterSection();
    	page.getFrontMatterSections().add(section);
		form.getFrontMatters().add(page);
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.required.field", errors.getFieldError("frontMatters[0].frontMatterSections[0].sectionText").getCode());
		Assert.assertEquals("error.required.field", errors.getFieldError("frontMatters[0].frontMatterSections[0].sequenceNum").getCode());
		
		EasyMock.verify(mockBookDefinitionService);
	}
	
	/**
	 * Test FrontMatterPdf all fields required if one field is set
	 */
	@Test
	public void testFrontMatterPdfRequiredFields() {
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(EasyMock.anyObject(String.class))).andReturn(null).times(1);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(EasyMock.anyObject(Long.class))).andReturn(analyticalCode).times(1);
		EasyMock.replay(mockCodeService);
    	
    	populateFormDataAnalyticalToc();
    	FrontMatterPage page = new FrontMatterPage();
    	page.setPageTocLabel("Toc Label");
    	page.setSequenceNum(1);
    	
    	FrontMatterSection section = new FrontMatterSection();
    	section.setSectionText("text section");
    	section.setSequenceNum(1);
    	
    	FrontMatterPdf pdf = new FrontMatterPdf();
    	pdf.setPdfFilename("filename");
    	section.getPdf().add(pdf);
    	page.getFrontMatterSections().add(section);
		form.getFrontMatters().add(page);
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.required.pdf", errors.getFieldError("frontMatters[0].frontMatterSections[0].pdf[0].pdfFilename").getCode());
		
		EasyMock.verify(mockBookDefinitionService);
	}

	private void setupPublisherAndTitleId(String titleId, DocumentTypeCode contentType, int mockReplayTimes) {
		BookDefinition book = initializeBookDef(titleId, contentType);

		form.initialize(book);
		form.setIsComplete(book.getEbookDefinitionCompleteFlag());
		form.setBookdefinitionId(Long.parseLong("1"));
		
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(EasyMock.anyObject(Long.class))).andReturn(book).times(mockReplayTimes);
    	EasyMock.replay(mockBookDefinitionService);

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
		form.setKeyCiteToplineFlag(book.getKeyciteToplineFlag());
		form.setIsComplete(book.getEbookDefinitionCompleteFlag());
		form.setValidateForm(false);
		form.setIsTOC(book.isTocFlag());
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
    	book.setIsAuthorDisplayVertical(true);
		
		return book;
	}
	
}
