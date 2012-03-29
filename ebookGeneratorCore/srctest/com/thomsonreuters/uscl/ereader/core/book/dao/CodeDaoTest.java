/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;


public class CodeDaoTest  {
	private final StateCode STATE_CODE = new StateCode();
	private final Long STATE_CODES_ID = new Long("1");
	private final List<StateCode> ALL_STATE_CODES = new ArrayList<StateCode>();

	private final JurisTypeCode JURIS_TYPE_CODE = new JurisTypeCode();
	private final Long JURIS_TYPE_CODES_ID = new Long("1");
	private final List<JurisTypeCode> ALL_JURIS_TYPE_CODES = new ArrayList<JurisTypeCode>();
	
	private final PubTypeCode PUB_TYPE_CODE = new PubTypeCode();
	private final Long PUB_TYPE_CODES_ID = new Long("1");
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
	
	private SessionFactory mockSessionFactory;
	private Session mockSession;
	private Criteria mockCriteria;
	private CodeDaoImpl dao;
	
	@Before
	public void setUp() throws Exception {
		
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(Session.class);
		this.mockCriteria = EasyMock.createMock(Criteria.class);
		this.dao = new CodeDaoImpl(mockSessionFactory);
		
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
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(StateCode.class, STATE_CODES_ID)).andReturn(STATE_CODE);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		StateCode actualStateCode = dao.getStateCodeById(STATE_CODES_ID);
		StateCode expected = new StateCode();
		expected.setId(STATE_CODES_ID);
		
		Assert.assertEquals(expected, actualStateCode);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
	
	@Test
	public void testGetAllStateCodes() {
		ALL_STATE_CODES.add(STATE_CODE);
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(StateCode.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(ALL_STATE_CODES);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<StateCode> actualStateCode = dao.getAllStateCodes();
		List<StateCode> expectedStateCodes = new ArrayList<StateCode>();
		expectedStateCodes.add(STATE_CODE);
		Assert.assertEquals(expectedStateCodes, actualStateCode);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
	
	@Test
	public void testGetPubTypeCode() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(PubTypeCode.class, PUB_TYPE_CODES_ID)).andReturn(PUB_TYPE_CODE);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		PubTypeCode actual = dao.getPubTypeCodeById(PUB_TYPE_CODES_ID);
		PubTypeCode expected = new PubTypeCode();
		expected.setId(PUB_TYPE_CODES_ID);
		
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
	
	@Test
	public void testGetAllPubTypeCodes() {
		ALL_PUB_TYPE_CODES.add(PUB_TYPE_CODE);
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(PubTypeCode.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(ALL_PUB_TYPE_CODES);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<PubTypeCode> actual = dao.getAllPubTypeCodes();
		List<PubTypeCode> expected = new ArrayList<PubTypeCode>();
		expected.add(PUB_TYPE_CODE);
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
	
	@Test
	public void testGetJurisTypeCode() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(JurisTypeCode.class, PUB_TYPE_CODES_ID)).andReturn(JURIS_TYPE_CODE);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		JurisTypeCode actual = dao.getJurisTypeCodeById(JURIS_TYPE_CODES_ID);
		JurisTypeCode expected = new JurisTypeCode();
		expected.setId(PUB_TYPE_CODES_ID);
		
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
	
	@Test
	public void testGetAllJurisTypeCodes() {
		ALL_JURIS_TYPE_CODES.add(JURIS_TYPE_CODE);
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(JurisTypeCode.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(ALL_JURIS_TYPE_CODES);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<JurisTypeCode> actual = dao.getAllJurisTypeCodes();
		List<JurisTypeCode> expected = new ArrayList<JurisTypeCode>();
		expected.add(JURIS_TYPE_CODE);
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
	
	@Test
	public void testGetDocumentTypeCode() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(DocumentTypeCode.class, DOCUMENT_TYPE_CODES_ID)).andReturn(DOCUMENT_TYPE_CODE);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		DocumentTypeCode actual = dao.getDocumentTypeCodeById(DOCUMENT_TYPE_CODES_ID);
		DocumentTypeCode expected = new DocumentTypeCode();
		expected.setId(DOCUMENT_TYPE_CODES_ID);
		
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
	
	@Test
	public void testGetAllDocumentTypeCodes() {
		ALL_DOCUMENT_TYPE_CODES.add(DOCUMENT_TYPE_CODE);
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(DocumentTypeCode.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(ALL_DOCUMENT_TYPE_CODES);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<DocumentTypeCode> actual = dao.getAllDocumentTypeCodes();
		List<DocumentTypeCode> expected = new ArrayList<DocumentTypeCode>();
		expected.add(DOCUMENT_TYPE_CODE);
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
	
	
	@Test
	public void testGetPublisherCode() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(PublisherCode.class, PUBLISHER_CODES_ID)).andReturn(PUBLISHER_CODE);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		PublisherCode actual = dao.getPublisherCodeById(PUBLISHER_CODES_ID);
		PublisherCode expected = new PublisherCode();
		expected.setId(PUBLISHER_CODES_ID);
		
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
	
	@Test
	public void testGetAllPublisherCodes() {
		ALL_PUBLISHER_CODES.add(PUBLISHER_CODE);
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(PublisherCode.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(ALL_PUBLISHER_CODES);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<PublisherCode> actual = dao.getAllPublisherCodes();
		List<PublisherCode> expected = new ArrayList<PublisherCode>();
		expected.add(PUBLISHER_CODE);
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
	
	
	@Test
	public void testGetKeywordTypeCode() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(KeywordTypeCode.class, KEYWORD_TYPE_CODES_ID)).andReturn(KEYWORD_TYPE_CODE);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		KeywordTypeCode actual = dao.getKeywordTypeCodeById(KEYWORD_TYPE_CODES_ID);
		KeywordTypeCode expected = new KeywordTypeCode();
		expected.setId(KEYWORD_TYPE_CODES_ID);
		
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
	
	@Test
	public void testGetAllKeywordTypeCodes() {
		ALL_KEYWORD_TYPE_CODES.add(KEYWORD_TYPE_CODE);
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(KeywordTypeCode.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(ALL_KEYWORD_TYPE_CODES);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<KeywordTypeCode> actual = dao.getAllKeywordTypeCodes();
		List<KeywordTypeCode> expected = new ArrayList<KeywordTypeCode>();
		expected.add(KEYWORD_TYPE_CODE);
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
	
	
	@Test
	public void testGetKeywordTypeValue() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(KeywordTypeValue.class, KEYWORD_TYPE_VALUES_ID)).andReturn(KEYWORD_TYPE_VALUE);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		KeywordTypeValue actual = dao.getKeywordTypeValueById(KEYWORD_TYPE_VALUES_ID);
		KeywordTypeValue expected = new KeywordTypeValue();
		expected.setId(KEYWORD_TYPE_VALUES_ID);
		
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
	
	@Test
	public void testGetAllKeywordTypeValues() {
		ALL_KEYWORD_TYPE_VALUES.add(KEYWORD_TYPE_VALUE);
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(KeywordTypeValue.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(ALL_KEYWORD_TYPE_VALUES);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<KeywordTypeValue> actual = dao.getAllKeywordTypeValues();
		List<KeywordTypeValue> expected = new ArrayList<KeywordTypeValue>();
		expected.add(KEYWORD_TYPE_VALUE);
		Assert.assertEquals(expected, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}

}
