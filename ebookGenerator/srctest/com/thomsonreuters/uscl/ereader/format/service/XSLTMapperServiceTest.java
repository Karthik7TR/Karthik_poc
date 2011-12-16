/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.thomsonreuters.uscl.ereader.format.dao.XSLTMapperDao;
import com.thomsonreuters.uscl.ereader.format.domain.XSLTMapperEntity;

/**
 * Class to run the XSLTMapperService as a JUnit test. 
 * @author Ripu Jain U0115290
 */
public class XSLTMapperServiceTest {
	
	private static final String COLLECTION = "w_codesstaflnvdp"; 
	private static final String DOC_TYPE = "6A";
	private static final String XSLT = "CodesStatutes.xsl";
    private XSLTMapperDao mockXsltMapperDao;
    private XSLTMapperEntity mockXsltMapperEntity;
    
    /**
     * The service being tested, injected by Spring.
     */
    @Autowired
    protected XSLTMapperServiceImpl service;
     
    /**
     * Mock up the DAO and the Entity.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
    	//mock up the xslt mapper DAO
    	mockXsltMapperDao = EasyMock.createMock(XSLTMapperDao.class);
    	mockXsltMapperEntity = EasyMock.createMock(XSLTMapperEntity.class);
    	
    	service = new XSLTMapperServiceImpl();
    	service.setXsltMapperDao(mockXsltMapperDao);
    }
    
    /**
     * Test the happy path.
     * @throws Exception
     */
    @Test
    public void testGetXsltWorksAsExpected() throws Exception {
    	
    	EasyMock.expect(mockXsltMapperDao.getXSLT(COLLECTION, DOC_TYPE)).andReturn(mockXsltMapperEntity);
    	EasyMock.expect(mockXsltMapperEntity.getXSLT()).andReturn(XSLT);
    	
    	EasyMock.replay(mockXsltMapperDao);
    	EasyMock.replay(mockXsltMapperEntity);
    	
    	assertEquals(XSLT, service.getXSLT(COLLECTION, DOC_TYPE));
    	
    	EasyMock.verify(mockXsltMapperDao);
    	EasyMock.verify(mockXsltMapperEntity);
    }
    
    /**
     * Test whether the service breaks if DAO is not injected.
     * @throws Exception
     */
    @Test
    public void testNoNPEThrownWhenDaoIsNotSet() throws Exception {
    	
    	try {
    		XSLTMapperService service = new XSLTMapperServiceImpl();
    		service.getXSLT(COLLECTION, DOC_TYPE);
    	}
    	catch (NullPointerException e) {
    		fail("A NullPointerException should not have been thrown!");
    	}
    	catch (IllegalArgumentException e)
    	{
    		//expected scenario
    	}    	
    }
}
