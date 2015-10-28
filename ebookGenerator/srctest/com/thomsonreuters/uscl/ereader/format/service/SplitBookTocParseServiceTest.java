package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;

public class SplitBookTocParseServiceTest {

	SplitBookTocParseServiceImpl splitBookTocParseService;

	private InputStream tocXml;
	private OutputStream splitTocXml;
	private List<String> splitTocGuidList;
	private String title = "title";
	private String splitTitleId = "splitTitle";

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		splitBookTocParseService = new SplitBookTocParseServiceImpl();

		splitTocGuidList = new ArrayList<String>();
		String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_2";
		splitTocGuidList.add(guid1);

		tocXml = new ByteArrayInputStream(
				"<EBook><EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc><EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc></EBook>"
						.getBytes());
		splitTocXml = new ByteArrayOutputStream();
	}

	@Test
	public void testSplitBookToc() throws Exception {

		Map<String, DocumentInfo> documentInfoMap = new HashMap<String, DocumentInfo>();

		documentInfoMap = splitBookTocParseService.generateSplitBookToc(tocXml, splitTocXml, splitTocGuidList, splitTitleId);

		/*System.out.println("-----taskMap---------");
		for (Map.Entry<String, DocumentInfo> entry : documentInfoMap.entrySet()) {
			System.out.println(entry.getKey() + "/" + entry.getValue().toString());
		}*/

		DocumentInfo expectedDocInfo1 = new DocumentInfo();
		expectedDocInfo1.setSplitTitleId("splitTitle");

		DocumentInfo expectedDocInfo2 = new DocumentInfo();
		expectedDocInfo2.setSplitTitleId("splitTitle_pt2");

		DocumentInfo docInfo1 = documentInfoMap.get("DOC_GUID1");
		DocumentInfo docInfo2 = documentInfoMap.get("DOC_GUID2");
		Assert.assertEquals(expectedDocInfo1.toString(), docInfo1.toString());
		Assert.assertEquals(expectedDocInfo2.toString(), docInfo2.toString());
	}
	
	@Test
	public void testSplitTocDuplicateDoc() 
	{			
		Map<String, DocumentInfo> documentInfoMap = new HashMap<String, DocumentInfo>();
		
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";

		tocXml = new ByteArrayInputStream(xmlTestStr.getBytes());

		documentInfoMap = splitBookTocParseService.generateSplitBookToc(tocXml, splitTocXml, splitTocGuidList, splitTitleId);

		/*System.out.println("-----taskMap---------");
		for (Map.Entry<String, DocumentInfo> entry : documentInfoMap.entrySet()) {
			System.out.println(entry.getKey() + "/" + entry.getValue().toString());
		}*/
		
		DocumentInfo expectedDocInfo1 = new DocumentInfo();
		expectedDocInfo1.setSplitTitleId("splitTitle");

		DocumentInfo expectedDocInfo2 = new DocumentInfo();
		expectedDocInfo2.setSplitTitleId("splitTitle_pt2");

		DocumentInfo docInfo1 = documentInfoMap.get("DOC_GUID1");
		DocumentInfo docInfo2 = documentInfoMap.get("DOC_GUID2");
		Assert.assertEquals(expectedDocInfo1.toString(), docInfo1.toString());
		Assert.assertEquals(expectedDocInfo2.toString(), docInfo2.toString());
		Assert.assertEquals(documentInfoMap.size(), 2);
	}
	
	
	@Test
	public void testSplitBookTocNoUUID() throws Exception {
		boolean thrown = false;

		Map<String, DocumentInfo> documentInfoMap = new HashMap<String, DocumentInfo>();

		tocXml = new ByteArrayInputStream(
				"<EBook><EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid></EBookToc><EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid></EBookToc></EBook>"
						.getBytes());

		try{
			documentInfoMap = splitBookTocParseService.generateSplitBookToc(tocXml, splitTocXml, splitTocGuidList, splitTitleId);
		}
		catch (RuntimeException e){
			thrown = true;
			Assert.assertEquals(true,e.getMessage().contains("Split occured at an incorrect level. TABLEOFCONTENTS33CHARACTERSLONG_2"));
		}
		assertTrue(thrown);

		Assert.assertEquals(0, documentInfoMap.size());
	}

}
