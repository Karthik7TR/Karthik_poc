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
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;

public class CodeServiceTest  {
	private static final StateCode STATE_CODE = new StateCode();
	private static final List<StateCode> ALL_STATE_CODES = new ArrayList<StateCode>();
	private static final Long STATE_CODES_ID = new Long("1");
	
	private static final JurisTypeCode JURIS_TYPE_CODE = new JurisTypeCode();
	private static final Long JURIS_TYPE_CODES_ID = new Long("1");
	private static final List<JurisTypeCode> ALL_JURIS_TYPE_CODES = new ArrayList<JurisTypeCode>();
	
	private static final PubTypeCode PUB_TYPE_CODE = new PubTypeCode();
	private static final Long PUB_TYPE_CODES_ID = new Long("1");
	private static final List<PubTypeCode> ALL_PUB_TYPE_CODES = new ArrayList<PubTypeCode>();
	
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
	}
	
	@Test
	public void testGetStateCode() {
		EasyMock.expect(mockCodeDao.getStateCode(STATE_CODES_ID)).andReturn(STATE_CODE);
		EasyMock.replay(mockCodeDao);
		StateCode actual = service.getStateCode(STATE_CODES_ID);
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
		EasyMock.expect(mockCodeDao.getPubTypeCode(PUB_TYPE_CODES_ID)).andReturn(PUB_TYPE_CODE);
		EasyMock.replay(mockCodeDao);
		PubTypeCode actual = service.getPubTypeCode(PUB_TYPE_CODES_ID);
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
		EasyMock.expect(mockCodeDao.getJurisTypeCode(JURIS_TYPE_CODES_ID)).andReturn(JURIS_TYPE_CODE);
		EasyMock.replay(mockCodeDao);
		JurisTypeCode actual = service.getJurisTypeCode(JURIS_TYPE_CODES_ID);
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
	
	
}
