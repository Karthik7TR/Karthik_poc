/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import javax.xml.transform.Transformer;

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
	XSLTMapperService mockXsltMapperService;
	DocMetadata mockDocMetadata;
	DocMetadataService mockDocMetadataService;
	String titleId;
	String novusXmlFilename;
	String novusMetadataFilename;
	
	long jobId;
	private static final String MOCK_DOCTYPE = "analytical";
	private static final String MOCK_COLLECTION = "w_foo_collection";
	private static final String CODES_STATUTES_XSLT = "CodesStatutes.xsl";
	
	@Rule
	public TemporaryFolder tempDirectory = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		mockXsltMapperService = EasyMock.createMock(XSLTMapperService.class);
		mockDocMetadata = EasyMock.createMock(DocMetadata.class);
		mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
		
		EasyMock.expect(mockDocMetadataService.findDocMetadataByPrimaryKey("uscl/an/IMPH", 
						new Integer(12345), "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5")).andReturn(mockDocMetadata);

		EasyMock.expect(mockDocMetadata.getCollectionName()).andReturn(MOCK_COLLECTION).times(2);
		EasyMock.expect(mockDocMetadata.getDocType()).andReturn(MOCK_DOCTYPE);
		EasyMock.expect(
				mockXsltMapperService.getXSLT(MOCK_COLLECTION, MOCK_DOCTYPE)).andReturn(CODES_STATUTES_XSLT);
		EasyMock.replay(mockDocMetadataService);
		EasyMock.replay(mockDocMetadata);
		EasyMock.replay(mockXsltMapperService);
		
		transformerService = new TransformerServiceImpl();
		transformerService.setxsltMapperService(mockXsltMapperService);
		transformerService.setdocMetadataService(mockDocMetadataService);
		
		titleId = "uscl/an/IMPH";
		novusXmlFilename = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5.xml";
		novusMetadataFilename = "w_an_rcc_cajur-Iff5a5a9b7c8f11da9de6e47d6d5aa7a5.xml";
		jobId = 12345L;
	}

	@Test
	public void testGenerateMudLinksUsingCodesStatutesStylesheet() throws Exception {
		File novusXml = new File(
				PersistentUrlTransformIntegrationTests.class.getResource(novusXmlFilename).getFile());
		File transformedDirectory = tempDirectory.newFolder("transformed");
		Map<String, Transformer> xsltCache = new HashMap<String, Transformer>();
		
		transformerService.transformFile(novusXml, novusXml.getParentFile(), 
				transformedDirectory, titleId, jobId, xsltCache);
		
		verifyAll();
		
		
		String fileContent = IOUtils.toString(new FileInputStream(
				new File(transformedDirectory, "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5.TRANSFORMED")));
		String expectedWlnHyperlinkText = "<a href=\"\">Some text</a>";
		//https://a.next.westlaw.com/Link/Document/FullText?findType=l&pubNum=1077005&cite=UUID(IDD474D90A9-3611DFB1E7A-A5E642A0D53)&originationContext=document&transitionType=DocumentItem&contextData=(sc.Category)
		//TODO: Update the assertion to detect one hyperlink within the transformed document.
		Assert.isTrue(fileContent.contains(expectedWlnHyperlinkText), 
				"file content should have contained a hyperlink to WLN, but did not!");
	}

	private void verifyAll() {
		EasyMock.verify(mockDocMetadataService);
		EasyMock.verify(mockDocMetadata);
		EasyMock.verify(mockXsltMapperService);
	}
}
