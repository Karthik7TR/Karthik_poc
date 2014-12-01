/*
* Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.util.IOUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;


public class KeyCiteBlockGenerationServiceImplTest {
	
	private KeyCiteBlockGenerationServiceImpl service;
	private DocMetadataService mockDocMetadataService;
	DocMetadata mockDocMetadata;
	String titleId;
    long jobId;
    String docGuid;
	
	/**
	 * Generic setup for all the tests.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		mockDocMetadata = EasyMock.createMock(DocMetadata.class);
	    mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
		this.mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
		this.service = new KeyCiteBlockGenerationServiceImpl();
		service.setDocMetadataService(mockDocMetadataService);	
		service.setHostname("http://www.westlaw.com");
		service.setMudparamrs("ebbb3.0");
		service.setMudparamvr("3.0");
		titleId="uscl/an/IMPH";
		jobId=101;
		docGuid="I770806320bbb11e1948492503fc0d37f";
		
	}
	
	
	@Test
	public void testGetKeyCite(){
		DocMetadata docMetaData = new DocMetadata();
		
		
		EasyMock.expect(mockDocMetadataService.findDocMetadataByPrimaryKey(titleId, new Long(101), docGuid)).andReturn(docMetaData);
		EasyMock.replay(mockDocMetadataService);
		
		InputStream keyCiteStream =null;
		try {
			keyCiteStream = service.getKeyCiteInfo(titleId, jobId, docGuid);
			System.out.println(keyCiteStream);
		} catch (EBookFormatException e) {
			e.printStackTrace();
		}
		EasyMock.verify(mockDocMetadataService);
		Assert.assertNotNull(keyCiteStream);
		
	}
	
	@Test
	public void testSymbolConversion() throws IOException{
		DocMetadata docMetaData = new DocMetadata();
		docMetaData.setNormalizedFirstlineCite("Title 1 \u00A7100");
		docMetaData.setFirstlineCite("Title 2 \u00A7200");
		docMetaData.setSecondlineCite("Title 3 \u00A7300");
			
		EasyMock.expect(mockDocMetadataService.findDocMetadataByPrimaryKey(titleId, new Long(101), docGuid)).andReturn(docMetaData);
		EasyMock.replay(mockDocMetadataService);
		
		InputStream keyCiteStream =null;
		try {
			keyCiteStream = service.getKeyCiteInfo(titleId, jobId, docGuid);
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			IOUtils.copy(keyCiteStream, output);
			String keyCite = new String(output.toByteArray());
			System.out.println(keyCite);
			
			String expected = "<div id=\"ebookGeneratorKeyciteInfo\" class=\"co_flush x_introPara\">"
					+ "<a href=\"http://www.westlaw.com/Search/Results.html?query=kc%3ATITLE+2+S200%3BTITLE+3+S300%3BTITLE+1+"
					+ "S100%3B&amp;jurisdiction=ALLCASES&amp;contentType=ALL&amp;startIndex=1&amp;transitionType=Search&amp;"
					+ "contextData=(sc.Default)&amp;rs=ebbb3.0&amp;vr=3.0\"><img src=\"er:#keycite\" "
					+ "alt=\"KeyCite This Document\"/></a></div>";
			
			Assert.assertEquals(expected, keyCite);
		} catch (EBookFormatException e) {
			e.printStackTrace();
		}
		EasyMock.verify(mockDocMetadataService);
		Assert.assertNotNull(keyCiteStream);
	}

}
