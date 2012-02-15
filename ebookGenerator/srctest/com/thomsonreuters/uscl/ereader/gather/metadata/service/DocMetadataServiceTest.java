/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import static org.junit.Assert.assertTrue;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.io.File;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class to run the service as a JUnit test. Each operation in the service is a
 * separate test.
 * 
 * @author - Nirupam Chatterjee
 * @author - Ray Cracauer
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration 
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class DocMetadataServiceTest {
	private static Logger LOG = Logger.getLogger(DocMetadataServiceTest.class);
	public Timestamp UPDATE_DATE = getCurrentTimeStamp();
	/**
	 * The service being tested, injected by Spring.
	 * 
	 */
	@Autowired
	protected DocMetadataService dockMetaService;

	protected DocMetadata docmetadata;

	/**
	 * Mock up the DAO and the Entity.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// mock up the document metadata
		
		saveDocMetadata(new Integer("1"));
	}

	/**
	 * Operation Unit Test Save an existing DocMetadata entity
	 * 
	 */
	public void saveDocMetadata(Integer seqNum) {
		docmetadata = new com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata();
		docmetadata.setCollectionName("test_choto_Collection");
		docmetadata.setDocFamilyUuid("1234567890");
		docmetadata.setDocType("codes");
		docmetadata.setDocUuid("1234567890001");
		docmetadata.setFindOrig(null);
		docmetadata.setJobInstanceId(new Integer("123456"));
		docmetadata.setLastUpdated(UPDATE_DATE);
		docmetadata.setNormalizedFirstlineCite(null);
		docmetadata.setSerialNumber(null);
		docmetadata.setTitleId("TL-URB"+seqNum.toString());
		docmetadata.setTocSeqNumber(seqNum);
		dockMetaService.saveDocMetadata(docmetadata);
	}
	/**
	 * Operation Unit Test
	 * 
	 * @author Kirsten Gunn
	 */
	@Test
	public void findOrderedDocMetadataByJobIdTest() {

		Integer seqNum = new Integer("2");
		saveDocMetadata(seqNum);

		
		String titleId = "TL-URB";
		Integer jobInstanceId = new Integer("123456");
		Integer tocSeqNum = new Integer("1");
		String docUuid = "1234567890001";

		List<DocMetadata> response = null;
		DocMetadata expected = new DocMetadata();
		expected.setTitleId(titleId + tocSeqNum.toString());
		expected.setJobInstanceId(jobInstanceId);
		expected.setDocUuid(docUuid);
		expected.setDocFamilyUuid("1234567890");
		expected.setDocType("codes");
		expected.setNormalizedFirstlineCite(null);
		expected.setFindOrig(null);
		expected.setSerialNumber(null);
		expected.setCollectionName("test_choto_Collection");
		expected.setLastUpdated(UPDATE_DATE);
		expected.setTocSeqNumber(tocSeqNum);
		
		LOG.debug(" expected " +expected);
		
		DocMetadata expected2 = new DocMetadata();
		expected2.setTitleId(titleId+seqNum.toString());
		expected2.setJobInstanceId(jobInstanceId);
		expected2.setDocUuid(docUuid);
		expected2.setDocFamilyUuid("1234567890");
		expected2.setDocType("codes");
		expected2.setNormalizedFirstlineCite(null);
		expected2.setFindOrig(null);
		expected2.setSerialNumber(null);
		expected2.setCollectionName("test_choto_Collection");
		expected2.setLastUpdated(UPDATE_DATE);
		expected2.setTocSeqNumber(seqNum);
		
		LOG.debug(" expected2 " +expected2);
		
		response = dockMetaService.findOrderedDocMetadataByJobId(jobInstanceId);
		
		LOG.debug(" response0 " + response.get(0));
		
		LOG.debug(" response1 " + response.get(1));

		Assert.assertEquals(response.get(0).toString(),expected.toString());
		Assert.assertEquals(response.get(1).toString(),expected2.toString());
	}
	/**
	 * Operation Unit Test Delete an existing DocMetadata entity
	 * 
	 */
	@After
	public void deleteDocMetadata() {
		dockMetaService.deleteDocMetadata(docmetadata);
	}

	/**
	 * Operation Unit Test
	 */
	@Test
	public void findDocMetadataByPrimaryKey() {
		String titleId = docmetadata.getTitleId();
		Integer jobInstanceId = docmetadata.getJobInstanceId();
		String docUuid = docmetadata.getDocUuid();
		DocMetadata response = null;
		response = dockMetaService.findDocMetadataByPrimaryKey(titleId,
				jobInstanceId, docUuid);
		assertTrue(response != null);
	}

	/**
	 * Operation Unit Test
	 * 
	 * @author Ray Cracauer
	 */
	@Test
	public void findDocMetadataByPrimaryKeyNegativeTitleId() {
		String titleId = "TEST_TILE";
		Integer jobInstanceId = new Integer("-12345");
		String docUuid = "123456";

		DocMetadata response = null;
		response = dockMetaService.findDocMetadataByPrimaryKey(titleId,
				jobInstanceId, docUuid);
		assertTrue(response == null);
	}

	/**
	 * Operation Unit Test
	 * 
	 * @author Ray Cracauer
	 */
	@Test
	public void findDocMetadataByPrimaryKeyAllNulls() {
		String titleId = null;
		Integer jobInstanceId = null;
		String docUuid = null;

		DocMetadata response = null;
		response = dockMetaService.findDocMetadataByPrimaryKey(titleId,
				jobInstanceId, docUuid);
		assertTrue(response == null);
	}

	/**
	 * Operation Unit Test
	 * 
	 * @author Ray Cracauer
	 */
	@Test
	public void findDocMetadataByPrimaryNullTitleId() {
		String titleId = null;
		Integer jobInstanceId = new Integer("12345");
		String docUuid = "123456";

		DocMetadata response = null;
		response = dockMetaService.findDocMetadataByPrimaryKey(titleId,
				jobInstanceId, docUuid);
		assertTrue(response == null);
	}

	/**
	 * Operation Unit Test
	 * 
	 * @author Ray Cracauer
	 */
	@Test
	public void findDocMetadataByPrimaryNullJobInstanceId() {
		String titleId = "TEST_TILE";
		Integer jobInstanceId = null;
		String docUuid = "123456";

		DocMetadata response = null;
		response = dockMetaService.findDocMetadataByPrimaryKey(titleId,
				jobInstanceId, docUuid);
		assertTrue(response == null);
	}

	/**
	 * Operation Unit Test
	 * 
	 * @author Ray Cracauer
	 */
	@Test
	public void findDocMetadataByPrimaryNullDocUuid() {
		String titleId = "TEST_TILE";
		Integer jobInstanceId = new Integer("12345");
		String docUuid = null;

		DocMetadata response = null;
		response = dockMetaService.findDocMetadataByPrimaryKey(titleId,
				jobInstanceId, docUuid);
		assertTrue(response == null);
	}

	/**
	 * Operation Unit Test
	 * 
	 * @author Ray Cracauer
	 */
	@Test
	public void findDocMetadataByPrimaryKeyObjectValues() {
	
		String titleId = "TL-URB1";
		Integer jobInstanceId = new Integer("123456");
		Integer tocSeqNum = new Integer("1");
		String docUuid = "1234567890001";

		DocMetadata response = null;
		DocMetadata expected = new DocMetadata();
		expected.setTitleId(titleId);
		expected.setJobInstanceId(jobInstanceId);
		expected.setDocUuid(docUuid);
		expected.setDocFamilyUuid("1234567890");
		expected.setDocType("codes");
		expected.setNormalizedFirstlineCite(null);
		expected.setFindOrig(null);
		expected.setSerialNumber(null);
		expected.setCollectionName("test_choto_Collection");
		expected.setLastUpdated(UPDATE_DATE);
		expected.setTocSeqNumber(tocSeqNum);
		
		LOG.debug(" expected " +expected);
		

		response = dockMetaService.findDocMetadataByPrimaryKey(titleId,
				jobInstanceId, docUuid);
		LOG.debug(" response " +response);

	
		Assert.assertEquals(response.toString(),expected.toString());
	}


	
	/**
	 * Operation Unit Test Parse DocMetadata xml and persist it
	 * 
	 */
	@Ignore
	public void parseAndStoreDocMetadata() {
		String titleId = "TEST_TILE";
		Integer jobInstanceId = new Integer("12345");
		String tocSeqNum = "1";
		String docUuid = "123456";
		String collectionName = "collection_name";
		dockMetaService.parseAndStoreDocMetadata(titleId, jobInstanceId,
				collectionName, new File(docUuid), tocSeqNum);
	}

	/**
	 * Operation Unit Test Return all DocMetadata entity
	 * 
	 */
	@Test
	public void findDocFamilyUuidByDocId() {
		String docUuid = "123456";
		Map<String, String> docFamilyMap = dockMetaService
				.findDocMetadataByDocUuid(docUuid);
		assertTrue(docFamilyMap.size() > 0);
	}

	/**
	 * Get the current timestamp
	 * 
	 * @return Timestamp
	 */
	private Timestamp getCurrentTimeStamp() {
		// create a java calendar instance
		Calendar calendar = Calendar.getInstance();
		return new java.sql.Timestamp(calendar.getTime().getTime());

	}
}
