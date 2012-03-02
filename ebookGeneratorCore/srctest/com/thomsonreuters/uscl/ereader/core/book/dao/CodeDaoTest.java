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

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;


public class CodeDaoTest  {
	private static final StateCode STATE_CODE = new StateCode();
	private static final Long STATE_CODES_ID = new Long("1");
	private static final List<StateCode> ALL_STATE_CODES = new ArrayList<StateCode>();

	private static final JurisTypeCode JURIS_TYPE_CODE = new JurisTypeCode();
	private static final Long JURIS_TYPE_CODES_ID = new Long("1");
	private static final List<JurisTypeCode> ALL_JURIS_TYPE_CODES = new ArrayList<JurisTypeCode>();
	
	private static final PubTypeCode PUB_TYPE_CODE = new PubTypeCode();
	private static final Long PUB_TYPE_CODES_ID = new Long("1");
	private static final List<PubTypeCode> ALL_PUB_TYPE_CODES = new ArrayList<PubTypeCode>();
	
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
	}
	
	@Test
	public void testGetStateCode() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(StateCode.class, STATE_CODES_ID)).andReturn(STATE_CODE);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		StateCode actualStateCode = dao.getStateCode(STATE_CODES_ID);
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
		
		PubTypeCode actual = dao.getPubTypeCode(PUB_TYPE_CODES_ID);
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
		
		JurisTypeCode actual = dao.getJurisTypeCode(JURIS_TYPE_CODES_ID);
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
}
