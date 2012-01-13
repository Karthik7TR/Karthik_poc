/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.restclient.service;


import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class GatherRestServiceIntegrationTest  {
	private static final String COLLECTION_NAME = "w_an_rcc_cajur_toc";	
	
	@Autowired
	private GatherService gatherService;
	
	@Test
	public void testGetToc()
	{
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		System.out.println("tempDir =" +tempDir);
		GatherTocRequest gatherTocRequest = new GatherTocRequest("testGuid","w_an_rcc_cajur_toc",tempDir );
		
		
		GatherResponse gatherResponse = gatherService.getToc(gatherTocRequest);
		System.out.println(gatherResponse.toString());
		Assert.assertNotNull(gatherResponse);
		Assert.assertEquals(0, gatherResponse.getErrorCode());
		
		
	}
	

}
