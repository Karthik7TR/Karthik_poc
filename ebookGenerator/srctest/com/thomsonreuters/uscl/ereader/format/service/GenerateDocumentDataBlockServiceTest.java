/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.InputStream;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;


/**
 *  Tests Document Data block.
 *   
 *  @author Mahendra Survase (u0105927)
 */
public class GenerateDocumentDataBlockServiceTest  {
	
	
	private GenerateDocumentDataBlockServiceImpl service;
	private DocMetadataService mockDocMetadataService;
	

	
	/**
	 * Generic setup for all the tests.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		this.mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
		this.service = new GenerateDocumentDataBlockServiceImpl();
		service.setDocMetadataService(mockDocMetadataService);
		
		
	}
	
	/**
	 * positive test
	 * 
	 */
	@Test
	public void testGetDocumentDataBlockAsStream(){
		// test specific setup.
		String titleId = "TEST_TITLE_ID";
		Long jobId = 10l;
		String docGuid = "TEST_DOC_GUID";
		
		DocMetadata docMetaData = new DocMetadata();
		docMetaData.setCollectionName("TEST_COLLECTION");
		
		EasyMock.expect(mockDocMetadataService.findDocMetadataByPrimaryKey(titleId, jobId, docGuid)).andReturn(docMetaData);
		EasyMock.replay(mockDocMetadataService);
		
		InputStream docBlockStream =null;
		try {
			docBlockStream = service.getDocumentDataBlockAsStream(titleId, jobId, docGuid);
		} catch (EBookFormatException e) {
			e.printStackTrace();
		}
		EasyMock.verify(mockDocMetadataService);
		Assert.assertNotNull(docBlockStream);
		
		
	}
	
	
	
}

