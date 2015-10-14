package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionServiceImpl;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.AutoSplitGuiIdsHandlerTest;

public class AutoSplitGuidsServiceTest {

	AutoSplitGuidsServiceImpl autoSplitGuidsService;
	BookDefinition bookDefinition;
	Long jobInstanceId;
	private BookDefinitionServiceImpl service;
	private Long bookId = new Long(1);

	@Before
	public void setUp() throws Exception {
		autoSplitGuidsService = new AutoSplitGuidsServiceImpl();
		bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(bookId);
		jobInstanceId = new Long(1);
		this.service = EasyMock.createMock(BookDefinitionServiceImpl.class);
		autoSplitGuidsService.setBookDefinitionService(service);
	}

	@After
	public void tearDown() throws IOException {

	}

	@Test
	public void testSplitPartSize() {
		int size = autoSplitGuidsService.getSizeforEachPart(5000, 5409);
		Assert.assertEquals(2704, size);
	}

	@Test
	public void testSplitPartPersistedDocs() {
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_5</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc></EBookToc>"
				+ "</EBook>";

		List<String> expectTedGuidList = new ArrayList<String>();
		expectTedGuidList.add("TABLEOFCONTENTS33CHARACTERSLONG_5");

		ByteArrayInputStream input = new ByteArrayInputStream(xmlTestStr.getBytes());
		Map<String, String> splitGuidTextMap = new HashMap<String, String>();
		
		List<SplitDocument> persistedSplitDocuments = new ArrayList<SplitDocument>();
		SplitDocument splitDocument = new SplitDocument();
		splitDocument.setBookDefinition(bookDefinition);
		splitDocument.setNote("note");
		splitDocument.setTocGuid("TABLEOFCONTENTS33CHARACTERSLONG_5");
		persistedSplitDocuments.add(splitDocument);

		
		EasyMock.expect(service.findSplitDocuments(bookDefinition
				.getEbookDefinitionId())).andReturn(persistedSplitDocuments);
		EasyMock.replay(service);

		List<String> splitGuidList = autoSplitGuidsService.getAutoSplitNodes(input, bookDefinition, new Integer(5),
				jobInstanceId, false, splitGuidTextMap);
		EasyMock.verify(service);
		Assert.assertEquals(expectTedGuidList.size(), splitGuidList.size());
	}
	
	@Test
	public void testSplitPartNoList() {
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_5</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc></EBookToc>"
				+ "</EBook>";

		
		ByteArrayInputStream input = new ByteArrayInputStream(xmlTestStr.getBytes());
		Map<String, String> splitGuidTextMap = new HashMap<String, String>();
				
		EasyMock.expect(service.findSplitDocuments(bookDefinition
				.getEbookDefinitionId())).andReturn(null);
		EasyMock.replay(service);
		

		DocumentTypeCode dc = new DocumentTypeCode();
		dc.setThresholdValue(100);
		dc.setThresholdPercent(10);
		bookDefinition.setDocumentTypeCodes(dc);

		List<String> splitGuidList = autoSplitGuidsService.getAutoSplitNodes(input, bookDefinition, new Integer(5),
				jobInstanceId, true, splitGuidTextMap);
		EasyMock.verify(service);
		Assert.assertEquals(0, splitGuidList.size());
	}
	
	@Test
	public void testSplitPartList() {
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_5</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc></EBookToc>"
				+ "</EBook>";

		
		ByteArrayInputStream input = new ByteArrayInputStream(xmlTestStr.getBytes());
		Map<String, String> splitGuidTextMap = new HashMap<String, String>();
				
		EasyMock.expect(service.findSplitDocuments(bookDefinition
				.getEbookDefinitionId())).andReturn(null);
		EasyMock.replay(service);
		

		DocumentTypeCode dc = new DocumentTypeCode();
		dc.setThresholdValue(4);
		dc.setThresholdPercent(10);
		bookDefinition.setDocumentTypeCodes(dc);

		List<String> splitGuidList = autoSplitGuidsService.getAutoSplitNodes(input, bookDefinition, new Integer(5),
				jobInstanceId, true, splitGuidTextMap);
		EasyMock.verify(service);
		
		List<String> expectTedGuidList = new ArrayList<String>();
		expectTedGuidList.add("TABLEOFCONTENTS33CHARACTERSLONG_5");
		
		Assert.assertEquals(expectTedGuidList.size(), splitGuidList.size());
		Assert.assertEquals(expectTedGuidList.get(0), splitGuidList.get(0));
	}
	
	@Test
	public void testSplitPartListwithFile() throws Exception{
		
		InputStream tocXml;
		File tocFile;
		URL url = AutoSplitGuiIdsHandlerTest.class.getResource("toc.xml");
		tocFile = new File(url.getPath());
		tocXml = new FileInputStream(tocFile);
		
		
		Map<String, String> splitGuidTextMap = new HashMap<String, String>();
				
		EasyMock.expect(service.findSplitDocuments(bookDefinition
				.getEbookDefinitionId())).andReturn(null);
		EasyMock.replay(service);
		

		DocumentTypeCode dc = new DocumentTypeCode();
		dc.setThresholdValue(100);
		dc.setThresholdPercent(10);
		bookDefinition.setDocumentTypeCodes(dc);

		List<String> splitGuidList = autoSplitGuidsService.getAutoSplitNodes(tocXml, bookDefinition, new Integer(125),
				jobInstanceId, true, splitGuidTextMap);
		EasyMock.verify(service);
		
		List<String> expectTedGuidList = new ArrayList<String>();
		expectTedGuidList.add("N83D0A8501B3811DBA0038642A4AEA132");
		
		Assert.assertEquals(expectTedGuidList.size(), splitGuidList.size());
		Assert.assertEquals(expectTedGuidList.get(0), splitGuidList.get(0));
	}
}
