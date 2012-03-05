/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;


import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class CodeServiceIntegrationTest  {
	private static final Logger log = Logger.getLogger(CodeServiceIntegrationTest.class);
	@Autowired
	private CodeService service;
	
	
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
}
