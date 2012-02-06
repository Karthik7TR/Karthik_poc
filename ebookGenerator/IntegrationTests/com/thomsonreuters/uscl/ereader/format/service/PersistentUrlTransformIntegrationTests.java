/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;


/**
 * This class is a spike implementation of XSLT pathway which produces persistent URLs to WLN via the MUD.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="PersistentUrlTransformIntegrationTest-context.xml")
public class PersistentUrlTransformIntegrationTests {

	TransformerServiceImpl transformerService;
	XSLTMapperService mockXsltMapperService; //doesn't really need to be mocked at the moment, but doing the right thing makes us good citizens.
	DocMetadata mockDocMetadata;
	DocMetadataService mockDocMetadataService;
	String titleId;
	String novusXmlFilename;
	long jobId;
	
	@Rule
	public TemporaryFolder tempDirectory = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		mockXsltMapperService = EasyMock.createMock(XSLTMapperService.class);
		mockDocMetadata = EasyMock.createMock(DocMetadata.class);
		mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
		
		EasyMock.expect(mockDocMetadataService.findDocMetadataByPrimaryKey("uscl/an/IMPH", new Integer(12345), "I01234567890123456789012345678901")).andReturn(mockDocMetadata);
		EasyMock.replay(mockDocMetadataService);
		EasyMock.replay(mockDocMetadata);
		EasyMock.replay(mockXsltMapperService);
		
		transformerService = new TransformerServiceImpl();
		transformerService.setxsltMapperService(mockXsltMapperService);
		transformerService.setdocMetadataService(mockDocMetadataService);
		
		titleId = "uscl/an/IMPH";
		novusXmlFilename = "I01234567890123456789012345678901.xml";
		jobId = 12345L;
	}

	@Ignore
	@Test
	public void testGenerateMudLinksUsingCodesStatutesStylesheet() throws Exception {
		File novusXml = new File(PersistentUrlTransformIntegrationTests.class.getResource(novusXmlFilename).getFile());
		File metadataDir = tempDirectory.newFolder("Metadata");
		File transformedDirectory = tempDirectory.newFolder("transformed");
		
		transformerService.transformFile(novusXml, metadataDir, transformedDirectory, titleId, jobId);
		verifyAll();
	}

	private void verifyAll() {
		EasyMock.verify(mockDocMetadataService);
		EasyMock.verify(mockDocMetadata);
		EasyMock.verify(mockXsltMapperService);
	}
}
