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
		saveDocMetadata();
	}

	/**
	 * Operation Unit Test Save an existing DocMetadata entity
	 * 
	 */
	public void saveDocMetadata() {
		docmetadata = new com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata();
		docmetadata.setCollectionName("test_choto_Collection");
		docmetadata.setDocFamilyUuid("1234567890");
		docmetadata.setDocType("codes");
		docmetadata.setDocUuid("1234567890001");
		docmetadata.setFindOrig(null);
		docmetadata.setJobInstanceId(new Integer("123456"));
		docmetadata.setLastUpdated(getCurrentTimeStamp());
		docmetadata.setNormalizedFirstlineCite(null);
		docmetadata.setSerialNumber(null);
		docmetadata.setTitleId("TL-URB");
		dockMetaService.saveDocMetadata(docmetadata);
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
	@Ignore
	public void findDocMetadataByPrimaryKeyObjectValues() {
		String titleId = "TEST_TILE";
		Integer jobInstanceId = new Integer("12345");
		String docUuid = "123456";

		DocMetadata response = null;
		DocMetadata expected = new DocMetadata();
		expected.setTitleId(titleId);
		expected.setJobInstanceId(jobInstanceId);
		expected.setDocUuid(docUuid);
		expected.setDocFamilyUuid("123456");
		expected.setDocType("codes");
		expected.setNormalizedFirstlineCite("12345");
		expected.setFindOrig("12345");
		expected.setSerialNumber(123);
		expected.setCollectionName("test_collection");
		expected.setLastUpdated(getCurrentTimeStamp());

		response = dockMetaService.findDocMetadataByPrimaryKey(titleId,
				jobInstanceId, docUuid);
		assertTrue(response.toString().equals(expected.toString()));
	}

	/**
	 * Operation Unit Test Parse DocMetadata xml and persist it
	 * 
	 */
	@Ignore
	public void parseAndStoreDocMetadata() {
		String titleId = "TEST_TILE";
		Integer jobInstanceId = new Integer("12345");
		String docUuid = "123456";
		String collectionName = "collection_name";
		dockMetaService.parseAndStoreDocMetadata(titleId, jobInstanceId,
				collectionName, new File(docUuid));
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
