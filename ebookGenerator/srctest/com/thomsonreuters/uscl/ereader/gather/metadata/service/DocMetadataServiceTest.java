/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import static org.junit.Assert.assertTrue;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

import java.util.List;
import java.util.Set;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class to run the service as a JUnit test. Each operation in the service is a separate test.
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class DocMetadataServiceTest {

	/**
	 * The service being tested, injected by Spring.
	 *
	 */
	@Autowired
	protected DocMetadataService dockMetaService;
	
	protected DocMetadata docmetadata;		
	
	
    /**
     * Mock up the DAO and the Entity.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
    	//mock up the document metadata
//    	saveDocMetadata();
    }	

	/**
	 * Operation Unit Test
	 * Return all DocMetadata entity
	 * 
	 */
	@Test
	public void findAllDocMetadatas() { 
		Integer startResult = 0;
		Integer maxRows = 0;
		List<DocMetadata> response = null;
		response = dockMetaService.findAllDocMetadatas(startResult, maxRows);
		assertTrue(response.size() > 0) ;
	}

	/**
	 * Operation Unit Test
	 * Save an existing DocMetadata entity
	 * 
	 */
	
	@Ignore
	public void saveDocMetadata() {
    	docmetadata = new com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata();
    	docmetadata.setCollectionName("test_Collection");
    	docmetadata.setDocFamilyUuid("123456789");
    	docmetadata.setDocType("codes");
    	docmetadata.setDocUuid("1234567890");
    	docmetadata.setFindOrig(null);
    	docmetadata.setJobInstanceId(new Integer("123456"));
    	docmetadata.setLastUpdated("Jan-05-2012");
    	docmetadata.setNormalizedFirstlineCite(null);
    	docmetadata.setSerialNumber(null);
    	docmetadata.setTitleId("TL-URB");
    	dockMetaService.saveDocMetadata(docmetadata);
	}

	/**
	 * Operation Unit Test
	 * Load an existing DocMetadata entity
	 * 
	 */
	@Test
	public void loadDocMetadatas() {
		Set<DocMetadata> response = null;
		response = dockMetaService.loadDocMetadatas();
		assertTrue(response.size() >= 0) ;
	}

	/**
	 * Operation Unit Test
	 * Return a count of all DocMetadata entity
	 * 
	 */
	@Test
	public void countDocMetadatas() {
		Integer response = null;
		response = dockMetaService.countDocMetadatas();
		assertTrue(response >= 0) ;
	}

	/**
	 * Operation Unit Test
	 * Delete an existing DocMetadata entity
	 * 
	 */
	@After
	public void deleteDocMetadata() {
//		dockMetaService.deleteDocMetadata(docmetadata);
	}

	/**
	 * Operation Unit Test
	 */
	@Test
	public void findDocMetadataByPrimaryKey() {
		// TODO: JUnit - Populate test inputs for operation: findDocMetadataByPrimaryKey 
/*		String titleId = docmetadata.getTitleId();
		Integer jobInstanceId = docmetadata.getJobInstanceId();
		String docUuid = docmetadata.getDocUuid();*/
		
		String titleId = "TEST_TILE";
		Integer jobInstanceId = new Integer("12345");
		String docUuid = "123456";		
		
		DocMetadata response = null;
		response = dockMetaService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docUuid);
		assertTrue(response != null) ;
	}
	
	/**
	 * Operation Unit Test
	 * Parse DocMetadata xml and 
	 * persist it
	 * 
	 */
	@Ignore
	public void parseAndStoreDocMetadata() {
		String titleId = "TEST_TILE";
		Integer jobInstanceId = new Integer("12345");
		String docUuid = "123456";	
		dockMetaService.parseAndStoreDocMetadata(titleId, jobInstanceId, new File(docUuid));
	}
}
