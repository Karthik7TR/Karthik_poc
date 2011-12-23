/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test class that connects to the database and test the service
 * class (XSLTMapperService) functionality.
 * @author Ripu Jain U0115290
 * @author Ray Cracauer U0113997
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class XSLTMapperServiceIntegrationTest {
	
	@Autowired
	protected XSLTMapperService xsltMapperService;
	
	private static final String COLLECTION = "w_codesstaflnvdp"; 
	private static final String DOC_TYPE = "6A";
	private static final String XSLT = "CodesStatutes.xsl";
	
	@Test
	public void testGetXsltFromDatabase() {
		assertEquals(XSLT, xsltMapperService.getXSLT(COLLECTION, DOC_TYPE));
	}
	
	@Test
	public void testGetXsltFromDatabaseNullDocType() {
		String COLLECTION = "w_3rd_plirpub";
		String DOC_TYPE = null;
		String XSLT = "AnalyticalEaganProducts.xsl";
		assertEquals(XSLT, xsltMapperService.getXSLT(COLLECTION, DOC_TYPE));
	}
	
	@Test
	public void testGetXsltFromDatabaseEmptryDocType() {
		String COLLECTION = "w_3rd_plirpub";
		String DOC_TYPE = "";
		String XSLT = "AnalyticalEaganProducts.xsl";
		assertEquals(XSLT, xsltMapperService.getXSLT(COLLECTION, DOC_TYPE));
	}
}
