/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;

public class CodeServiceTest  {
	private final StateCode STATE_CODE = new StateCode();
	private final List<StateCode> ALL_STATE_CODES = new ArrayList<StateCode>();
	private final Long STATE_CODES_ID = new Long("1");
	
	private final JurisTypeCode JURIS_TYPE_CODE = new JurisTypeCode();
	private final Long JURIS_TYPE_CODES_ID = new Long("2");
	private final List<JurisTypeCode> ALL_JURIS_TYPE_CODES = new ArrayList<JurisTypeCode>();
	
	private final PubTypeCode PUB_TYPE_CODE = new PubTypeCode();
	private final Long PUB_TYPE_CODES_ID = new Long("3");
	private final List<PubTypeCode> ALL_PUB_TYPE_CODES = new ArrayList<PubTypeCode>();
	
	private final DocumentTypeCode DOCUMENT_TYPE_CODE = new DocumentTypeCode();
	private final Long DOCUMENT_TYPE_CODES_ID = new Long("4");
	private final List<DocumentTypeCode> ALL_DOCUMENT_TYPE_CODES = new ArrayList<DocumentTypeCode>();
	
	private final PublisherCode PUBLISHER_CODE = new PublisherCode();
	private final Long PUBLISHER_CODES_ID = new Long("5");
	private final List<PublisherCode> ALL_PUBLISHER_CODES = new ArrayList<PublisherCode>();
	
	private final KeywordTypeCode KEYWORD_TYPE_CODE = new KeywordTypeCode();
	private final Long KEYWORD_TYPE_CODES_ID = new Long("6");
	private final List<KeywordTypeCode> ALL_KEYWORD_TYPE_CODES = new ArrayList<KeywordTypeCode>();
	
	private final KeywordTypeValue KEYWORD_TYPE_VALUE = new KeywordTypeValue();
	private final Long KEYWORD_TYPE_VALUES_ID = new Long("7");
	private final List<KeywordTypeValue> ALL_KEYWORD_TYPE_VALUES = new ArrayList<KeywordTypeValue>();
	
	private CodeServiceImpl service;
	private CodeDao mockCodeDao;
	
	
	@Before
	public void setUp() throws Exception {
		this.mockCodeDao = EasyMock.createMock(CodeDao.class);
		
		this.service = new CodeServiceImpl();
		service.setCodeDao(mockCodeDao);
		
		STATE_CODE.setId(STATE_CODES_ID);
		JURIS_TYPE_CODE.setId(JURIS_TYPE_CODES_ID);
		PUB_TYPE_CODE.setId(PUB_TYPE_CODES_ID);
		DOCUMENT_TYPE_CODE.setId(DOCUMENT_TYPE_CODES_ID);
		PUBLISHER_CODE.setId(PUBLISHER_CODES_ID);
		KEYWORD_TYPE_CODE.setId(KEYWORD_TYPE_CODES_ID);
		KEYWORD_TYPE_VALUE.setId(KEYWORD_TYPE_VALUES_ID);
	}
	
	@Test
	public void testGetStateCode() {
		EasyMock.expect(mockCodeDao.getStateCodeById(STATE_CODES_ID)).andReturn(STATE_CODE);
		EasyMock.replay(mockCodeDao);
		StateCode actual = service.getStateCodeById(STATE_CODES_ID);
		Assert.assertEquals(STATE_CODE, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	@Test
	public void testGetAllStateCodes() {
		ALL_STATE_CODES.add(STATE_CODE);
		EasyMock.expect(mockCodeDao.getAllStateCodes()).andReturn(ALL_STATE_CODES);
		EasyMock.replay(mockCodeDao);
		List<StateCode> actual = service.getAllStateCodes();
		List<StateCode> expected = new ArrayList<StateCode>();
		expected.add(STATE_CODE);
		
		Assert.assertEquals(expected, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	@Test
	public void testGetPubTypeCode() {
		EasyMock.expect(mockCodeDao.getPubTypeCodeById(PUB_TYPE_CODES_ID)).andReturn(PUB_TYPE_CODE);
		EasyMock.replay(mockCodeDao);
		PubTypeCode actual = service.getPubTypeCodeById(PUB_TYPE_CODES_ID);
		Assert.assertEquals(PUB_TYPE_CODE, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	@Test
	public void testGetAllPubTypeCodes() {
		ALL_PUB_TYPE_CODES.add(PUB_TYPE_CODE);
		EasyMock.expect(mockCodeDao.getAllPubTypeCodes()).andReturn(ALL_PUB_TYPE_CODES);
		EasyMock.replay(mockCodeDao);
		List<PubTypeCode> actual = service.getAllPubTypeCodes();
		List<PubTypeCode> expected = new ArrayList<PubTypeCode>();
		expected.add(PUB_TYPE_CODE);
		
		Assert.assertEquals(expected, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	@Test
	public void testGetJurisTypeCode() {
		EasyMock.expect(mockCodeDao.getJurisTypeCodeById(JURIS_TYPE_CODES_ID)).andReturn(JURIS_TYPE_CODE);
		EasyMock.replay(mockCodeDao);
		JurisTypeCode actual = service.getJurisTypeCodeById(JURIS_TYPE_CODES_ID);
		Assert.assertEquals(JURIS_TYPE_CODE, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	@Test
	public void testGetAllJurisTypeCodes() {
		ALL_JURIS_TYPE_CODES.add(JURIS_TYPE_CODE);
		EasyMock.expect(mockCodeDao.getAllJurisTypeCodes()).andReturn(ALL_JURIS_TYPE_CODES);
		EasyMock.replay(mockCodeDao);
		List<JurisTypeCode> actual = service.getAllJurisTypeCodes();
		List<JurisTypeCode> expected = new ArrayList<JurisTypeCode>();
		expected.add(JURIS_TYPE_CODE);
		
		Assert.assertEquals(expected, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	
	@Test
	public void testGetDocumentTypeCode() {
		EasyMock.expect(mockCodeDao.getDocumentTypeCodeById(DOCUMENT_TYPE_CODES_ID)).andReturn(DOCUMENT_TYPE_CODE);
		EasyMock.replay(mockCodeDao);
		DocumentTypeCode actual = service.getDocumentTypeCodeById(DOCUMENT_TYPE_CODES_ID);
		Assert.assertEquals(DOCUMENT_TYPE_CODE, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	@Test
	public void testGetAllDocumentTypeCodes() {
		ALL_DOCUMENT_TYPE_CODES.add(DOCUMENT_TYPE_CODE);
		EasyMock.expect(mockCodeDao.getAllDocumentTypeCodes()).andReturn(ALL_DOCUMENT_TYPE_CODES);
		EasyMock.replay(mockCodeDao);
		List<DocumentTypeCode> actual = service.getAllDocumentTypeCodes();
		List<DocumentTypeCode> expected = new ArrayList<DocumentTypeCode>();
		expected.add(DOCUMENT_TYPE_CODE);
		
		Assert.assertEquals(expected, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	
	@Test
	public void testGetPublisherCode() {
		EasyMock.expect(mockCodeDao.getPublisherCodeById(PUBLISHER_CODES_ID)).andReturn(PUBLISHER_CODE);
		EasyMock.replay(mockCodeDao);
		PublisherCode actual = service.getPublisherCodeById(PUBLISHER_CODES_ID);
		Assert.assertEquals(PUBLISHER_CODE, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	@Test
	public void testGetAllPublisherCodes() {
		ALL_PUBLISHER_CODES.add(PUBLISHER_CODE);
		EasyMock.expect(mockCodeDao.getAllPublisherCodes()).andReturn(ALL_PUBLISHER_CODES);
		EasyMock.replay(mockCodeDao);
		List<PublisherCode> actual = service.getAllPublisherCodes();
		List<PublisherCode> expected = new ArrayList<PublisherCode>();
		expected.add(PUBLISHER_CODE);
		
		Assert.assertEquals(expected, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	
	@Test
	public void testGetKeywordTypeCode() {
		EasyMock.expect(mockCodeDao.getKeywordTypeCodeById(KEYWORD_TYPE_CODES_ID)).andReturn(KEYWORD_TYPE_CODE);
		EasyMock.replay(mockCodeDao);
		KeywordTypeCode actual = service.getKeywordTypeCodeById(KEYWORD_TYPE_CODES_ID);
		Assert.assertEquals(KEYWORD_TYPE_CODE, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	@Test
	public void testGetAllKeywordTypeCodes() {
		ALL_KEYWORD_TYPE_CODES.add(KEYWORD_TYPE_CODE);
		EasyMock.expect(mockCodeDao.getAllKeywordTypeCodes()).andReturn(ALL_KEYWORD_TYPE_CODES);
		EasyMock.replay(mockCodeDao);
		List<KeywordTypeCode> actual = service.getAllKeywordTypeCodes();
		List<KeywordTypeCode> expected = new ArrayList<KeywordTypeCode>();
		expected.add(KEYWORD_TYPE_CODE);
		
		Assert.assertEquals(expected, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	@Test
	public void testGetKeywordTypeValue() {
		EasyMock.expect(mockCodeDao.getKeywordTypeValueById(KEYWORD_TYPE_VALUES_ID)).andReturn(KEYWORD_TYPE_VALUE);
		EasyMock.replay(mockCodeDao);
		KeywordTypeValue actual = service.getKeywordTypeValueById(KEYWORD_TYPE_VALUES_ID);
		Assert.assertEquals(KEYWORD_TYPE_VALUE, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	@Test
	public void testGetAllKeywordTypeValues() {
		ALL_KEYWORD_TYPE_VALUES.add(KEYWORD_TYPE_VALUE);
		EasyMock.expect(mockCodeDao.getAllKeywordTypeValues()).andReturn(ALL_KEYWORD_TYPE_VALUES);
		EasyMock.replay(mockCodeDao);
		List<KeywordTypeValue> actual = service.getAllKeywordTypeValues();
		List<KeywordTypeValue> expected = new ArrayList<KeywordTypeValue>();
		expected.add(KEYWORD_TYPE_VALUE);
		
		Assert.assertEquals(expected, actual);
		EasyMock.verify(mockCodeDao);
	}
	
	
}
