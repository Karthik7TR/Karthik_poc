/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class CodeServiceIntegrationTest  {
	private static final Logger log = Logger.getLogger(CodeServiceIntegrationTest.class);
	@Autowired
	private CodeService service;
	
	@Autowired
	private BookDefinitionService bookDefinitionService;
	
	
	@Test
	public void testGetAllStates() {
		List<StateCode> stateCodes = service.getAllStateCodes();
		log.debug(stateCodes);
		Assert.assertEquals(50, stateCodes.size());
	}
	
	@Test
	public void testStateCodeCRUD() {
		
		// Create StateCode
		StateCode createCode = new StateCode();
		createCode.setName("Test");
		service.saveStateCode(createCode);

		// Get
		StateCode readCode = service.getStateCodeById(createCode.getId());
		Assert.assertEquals(createCode, readCode);
		Assert.assertEquals("Test", readCode.getName());
		
		// Update
		readCode.setName("Test2");
		service.saveStateCode(readCode);
		
		// Get 2
		readCode = service.getStateCodeById(createCode.getId());
		Assert.assertEquals("Test2", readCode.getName());
		
		// Delete
		service.deleteStateCode(readCode);
		readCode = service.getStateCodeById(createCode.getId());
		Assert.assertEquals(null, readCode);
	}
	
	@Test
	public void testGetAllJuris() {
		List<JurisTypeCode> codes = service.getAllJurisTypeCodes();
		log.debug(codes);
		Assert.assertEquals(51, codes.size());
	}
	
	@Test
	public void testJurisTypeCodeCRUD() {
		
		// Create StateCode
		JurisTypeCode createCode = new JurisTypeCode();
		createCode.setName("Test");
		service.saveJurisTypeCode(createCode);

		// Get
		JurisTypeCode readCode = service.getJurisTypeCodeById(createCode.getId());
		Assert.assertEquals(createCode, readCode);
		Assert.assertEquals("Test", readCode.getName());
		
		// Update
		readCode.setName("Test2");
		service.saveJurisTypeCode(readCode);
		
		// Get 2
		readCode = service.getJurisTypeCodeById(createCode.getId());
		Assert.assertEquals("Test2", readCode.getName());
		
		// Delete
		service.deleteJurisTypeCode(readCode);
		readCode = service.getJurisTypeCodeById(createCode.getId());
		Assert.assertEquals(null, readCode);
	}
	
	@Test
	public void testGetAllPubType() {
		List<PubTypeCode> codes = service.getAllPubTypeCodes();
		log.debug(codes);
		Assert.assertEquals(6, codes.size());
	}
	
	@Test
	public void testPubTypeCodeCRUD() {
		
		// Create StateCode
		PubTypeCode createCode = new PubTypeCode();
		createCode.setName("Test");
		service.savePubTypeCode(createCode);

		// Get
		PubTypeCode readCode = service.getPubTypeCodeById(createCode.getId());
		Assert.assertEquals(createCode, readCode);
		Assert.assertEquals("Test", readCode.getName());
		
		// Update
		readCode.setName("Test2");
		service.savePubTypeCode(readCode);
		
		// Get 2
		readCode = service.getPubTypeCodeById(createCode.getId());
		Assert.assertEquals("Test2", readCode.getName());
		
		// Delete
		service.deletePubTypeCode(readCode);
		readCode = service.getPubTypeCodeById(createCode.getId());
		Assert.assertEquals(null, readCode);
	}
	
	@Test
	public void testGetAllDocumentType() {
		List<DocumentTypeCode> codes = service.getAllDocumentTypeCodes();
		log.debug(codes);
		Assert.assertEquals(3, codes.size());
	}
	
	@Test
	public void testDocumentTypeCodeCRUD() {
		
		// Create StateCode
		DocumentTypeCode createCode = new DocumentTypeCode();
		createCode.setName("Test");
		createCode.setAbbreviation("t");
		createCode.setUsePublishCutoffDateFlag("F");
		service.saveDocumentTypeCode(createCode);

		// Get
		DocumentTypeCode readCode = service.getDocumentTypeCodeById(createCode.getId());
		Assert.assertEquals(createCode, readCode);
		Assert.assertEquals("Test", readCode.getName());
		Assert.assertEquals("t", readCode.getAbbreviation());
		
		// Update
		readCode.setName("Test2");
		readCode.setAbbreviation("t2");
		service.saveDocumentTypeCode(readCode);
		
		// Get 2
		readCode = service.getDocumentTypeCodeById(createCode.getId());
		Assert.assertEquals("Test2", readCode.getName());
		Assert.assertEquals("t2", readCode.getAbbreviation());
		
		// Delete
		service.deleteDocumentTypeCode(readCode);
		readCode = service.getDocumentTypeCodeById(createCode.getId());
		Assert.assertEquals(null, readCode);
	}
	
	@Test
	public void testGetAllPublisher() {
		List<PublisherCode> codes = service.getAllPublisherCodes();
		log.debug(codes);
		Assert.assertEquals(1, codes.size());
	}
	
	@Test
	public void testPublisherCodeCRUD() {
		
		// Create StateCode
		PublisherCode createCode = new PublisherCode();
		createCode.setName("Test");
		service.savePublisherCode(createCode);

		// Get
		PublisherCode readCode = service.getPublisherCodeById(createCode.getId());
		Assert.assertEquals(createCode, readCode);
		Assert.assertEquals("Test", readCode.getName());
		
		// Update
		readCode.setName("Test2");
		service.savePublisherCode(readCode);
		
		// Get 2
		readCode = service.getPublisherCodeById(createCode.getId());
		Assert.assertEquals("Test2", readCode.getName());
		
		// Delete
		service.deletePublisherCode(readCode);
		readCode = service.getPublisherCodeById(createCode.getId());
		Assert.assertEquals(null, readCode);
	}
	
	@Test
	public void testGetAllKeywordCodes() {
		List<KeywordTypeCode> codes = service.getAllKeywordTypeCodes();
		log.debug(codes);
		Assert.assertEquals(4, codes.size());
	}
	
	@Test
	public void testGetKeywordCodeCRUD() {
		// Create StateCode
		KeywordTypeCode createCode = new KeywordTypeCode();
		createCode.setName("Test");
		service.saveKeywordTypeCode(createCode);
		
		Collection<KeywordTypeValue> values = new ArrayList<KeywordTypeValue>();
		for(int i = 0; i < 5 ; i++) {
			KeywordTypeValue value = new KeywordTypeValue();
			value.setName(String.valueOf(i));
			value.setKeywordTypeCode(createCode);
			values.add(value);
			service.saveKeywordTypeValue(value);
		}
		createCode.setValues(values);
		//service.saveKeywordTypeCode(createCode);
		
		// Get
		KeywordTypeCode readCode = service.getKeywordTypeCodeById(createCode.getId());
		Assert.assertEquals(values, readCode.getValues());
		Assert.assertEquals(createCode, readCode);
		Assert.assertEquals("Test", readCode.getName());
		
		
		// Update
		readCode.setName("Test2");
		service.saveKeywordTypeCode(readCode);
		
		// Get 2
		readCode = service.getKeywordTypeCodeById(createCode.getId());
		Assert.assertEquals("Test2", readCode.getName());
		
		// Delete
		service.deleteKeywordTypeCode(readCode);
		readCode = service.getKeywordTypeCodeById(createCode.getId());
		List<KeywordTypeValue> emptyValues = service.getAllKeywordTypeValues(createCode.getId());
		Assert.assertEquals(null, readCode);
		Assert.assertEquals(new ArrayList<KeywordTypeValue>(), emptyValues);
	}
	
	@Test
	public void testGetAllKeywordValues() {
		List<KeywordTypeValue> codes = service.getAllKeywordTypeValues();
		log.debug(codes);
		Assert.assertEquals(9, codes.size());
	}
	
	@Test
	public void testGetAllKeywordValuesByCodeId() {
		List<KeywordTypeValue> codes = service.getAllKeywordTypeValues(Long.parseLong("1"));
		log.debug(codes);
		Assert.assertEquals(2, codes.size());
	}
	
	@Test
	public void testGetKeywordValueCRUD() {
		// Create StateCode
		KeywordTypeCode createCode = new KeywordTypeCode();
		createCode.setName("Test");
		service.saveKeywordTypeCode(createCode);
		
		Collection<KeywordTypeValue> createValues = new ArrayList<KeywordTypeValue>();
		for(int i = 0; i < 5 ; i++) {
			KeywordTypeValue value = new KeywordTypeValue();
			value.setName(String.valueOf(i));
			value.setKeywordTypeCode(createCode);
			createValues.add(value);
			service.saveKeywordTypeValue(value);
		}
		
		List<KeywordTypeValue> values = service.getAllKeywordTypeValues(createCode.getId());
		Assert.assertEquals(createValues, values);

		int size = values.size();
		for(KeywordTypeValue value : values) {
			service.deleteKeywordTypeValue(value);
			size--;
			List<KeywordTypeValue> newValues = service.getAllKeywordTypeValues(createCode.getId());
			Assert.assertEquals(size , newValues.size());
			Assert.assertFalse(newValues.contains(value));
			
		}

		// Delete
		List<KeywordTypeValue> emptyValues = service.getAllKeywordTypeValues(createCode.getId());
		Assert.assertEquals(new ArrayList<KeywordTypeValue>(), emptyValues);
	}
	
	@Test
	public void testKeywordTypeValues() {
		List<KeywordTypeValue> allValues = service.getAllKeywordTypeValues();
		int keywordValueSize = allValues.size();
		String titleId = "uscl/an/abcd1234_test";
		setupBookDef(titleId);
		
		BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(titleId);
		log.debug(book);
		Set<KeywordTypeValue> values = book.getKeywordTypeValues(); 
		Assert.assertEquals(3, values.size());
		
		values.clear();
		book.setKeywordTypeValues(values);
		bookDefinitionService.saveBookDefinition(book);
		
		book = bookDefinitionService.findBookDefinitionByTitle(titleId);
		values = book.getKeywordTypeValues(); 
		Assert.assertEquals(0, values.size());
		
		Assert.assertEquals(keywordValueSize, service.getAllKeywordTypeValues().size());
		
	}
	
	private void setupBookDef(String titleId) {
		BookDefinition book = new BookDefinition();
    	book.setFullyQualifiedTitleId(titleId);
    	DocumentTypeCode dtc = new DocumentTypeCode();
    	dtc.setId(Long.parseLong("1"));
    	book.setDocumentTypeCodes(dtc);
    	
    	EbookName bookName = new EbookName();
    	bookName.setBookNameText("Test book name");
    	bookName.setSequenceNum(Integer.parseInt("1"));
    	bookName.setEbookDefinition(book);
    	book.getEbookNames().add(bookName);
    	
    	PublisherCode publisherCode = new PublisherCode();
    	publisherCode.setId(Long.parseLong("1"));
    	book.setPublisherCodes(publisherCode);
    	
    	book.setMaterialId("random");
    	book.setCopyright("something");
    	book.setIsTocFlag(false);
    	book.setIsDeletedFlag(false);
    	book.setIsProviewTableViewFlag(false);
    	book.setEbookDefinitionCompleteFlag(false);
    	book.setAutoUpdateSupportFlag(true);
    	book.setSearchIndexFlag(true);
    	book.setPublishedOnceFlag(false);
    	book.setOnePassSsoLinkFlag(true);
    	book.setKeyciteToplineFlag(true);
		
    	Set<KeywordTypeValue> values = new HashSet<KeywordTypeValue>();
    	for(int i = 1; i < 4; i++) {
    		KeywordTypeValue value = new KeywordTypeValue();
    		value.setId(Long.parseLong(Integer.toString(i)));
    		values.add(value);
    	}
		book.setKeywordTypeValues(values);
		
		bookDefinitionService.saveBookDefinition(book);
	}
	
	
	
	
	
	
	
}
