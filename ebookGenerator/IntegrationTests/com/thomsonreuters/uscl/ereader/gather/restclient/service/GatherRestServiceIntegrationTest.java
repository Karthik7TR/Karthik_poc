/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.restclient.service;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sun.java2d.pipe.hw.ExtendedBufferCapabilities;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class GatherRestServiceIntegrationTest  {
	private static Logger log = Logger.getLogger(GatherRestServiceIntegrationTest.class);
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	
	@Autowired
	private GatherService gatherService;
	
	//@Test
	public void testGetToc() {
		String TOC_COLLECTION_NAME = "w_an_rcc_cajur_toc";	// Client
		String ROOT_TOC_GUID_IMPH = "I7b3ec600675a11da90ebf04471783734";
		String ROOT_TOC_GUID_FSLP = "I4fa9eea0b36011dab270c8080cf3148a";
		File tempDir = temporaryFolder.getRoot();
		GatherTocRequest gatherTocRequest = new GatherTocRequest(ROOT_TOC_GUID_FSLP, TOC_COLLECTION_NAME, tempDir );
		
		GatherResponse gatherResponse = gatherService.getToc(gatherTocRequest);
		log.debug(gatherResponse);
		Assert.assertNotNull(gatherResponse);
		Assert.assertEquals(0, gatherResponse.getErrorCode());
		File tocFile = new File(tempDir, "toc.xml");
		Assert.assertTrue(tocFile.exists());
		Assert.assertTrue(tocFile.length() > 0);
	}
	
	@Test
	public void testGetDoc() {
		String DOC_COLLECTION_NAME_PROD = "w_an_rcc_cajur";	
		String testDocGuid_PROD = "I2e91cd8ba11611d9ad0a81db1eb1d418";
		File tempDir = temporaryFolder.getRoot();
		File contentDir = new File(tempDir, "junit_content");
		File metadataDir = new File(tempDir, "junit_metadata");
		contentDir.mkdirs();
		metadataDir.mkdirs();
		File contentFile = new File(contentDir, testDocGuid_PROD+".xml");
		File metadataFile = new File(metadataDir, testDocGuid_PROD+".xml");
		Collection<String> guids = new ArrayList<String>();
		guids.add(testDocGuid_PROD);
		GatherDocRequest docRequest = new GatherDocRequest(guids, DOC_COLLECTION_NAME_PROD, contentDir, metadataDir);
		
		GatherResponse gatherResponse = gatherService.getDoc(docRequest);
		log.debug(gatherResponse);
		Assert.assertNotNull(gatherResponse);
		Assert.assertEquals(0, gatherResponse.getErrorCode());
		Assert.assertTrue(contentFile.exists());
		Assert.assertTrue(contentFile.length() > 0);
		Assert.assertTrue(metadataFile.exists());
		Assert.assertTrue(metadataFile.length() > 0);
	}
}
