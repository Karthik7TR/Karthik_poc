/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.task;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thomsonreuters.uscl.ereader.format.service.SplitBookTocParseServiceImpl;
import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.gather.step.GenerateSplitTocTask;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

public class GenerateSplitTaskTest {

	InputStream tocXml;
	OutputStream splitTocXml;
	File tranformedDirectory;
	File splitTocFile;
	SplitBookTocParseServiceImpl splitBookTocParseService;
	private final String testExtension = ".transformed";
	Long jobInstanceId;
	private DocMetadataService mockDocMetadataService;
	List<String> splitTocGuidList;
	GenerateSplitTocTask generateSplitTocTask;
	
	
	private static Logger LOG = LogManager.getLogger(GenerateSplitTaskTest.class);
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception{
		generateSplitTocTask = new GenerateSplitTocTask();
		splitTocGuidList = new ArrayList<String>();
		String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_2";
		splitTocGuidList.add(guid1);
		
		this.mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
		generateSplitTocTask.setDocMetadataService(mockDocMetadataService);
		
		splitTocXml = new ByteArrayOutputStream(1024);

		tocXml = new ByteArrayInputStream(
				"<EBook><EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc><EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc></EBook>"
						.getBytes());
		
		jobInstanceId = new Long(1);
		
		File workDir = temporaryFolder.getRoot();
		tranformedDirectory = new File(workDir, "transforned");
		tranformedDirectory.mkdirs();
		
		splitBookTocParseService = new SplitBookTocParseServiceImpl();
		FileHandlingHelper fileHandlingHelper = new FileHandlingHelper();
		FileExtensionFilter fileExtFilter = new FileExtensionFilter();
		fileExtFilter.setAcceptedFileExtensions(new String[] { testExtension });
		fileHandlingHelper.setFilter(fileExtFilter);
		generateSplitTocTask.setfileHandlingHelper(fileHandlingHelper);
		
		generateSplitTocTask.setSplitBookTocParseService(splitBookTocParseService);
	}
	
	@Test
	public void stringTest()
	{
		String s = "er:uscl/an/book_lohisplittoctest28_2/0#I90AD73D079DE11DDB771B1D8BA9725E3/NDFF943C0B65511D8983DF34406B5929B1";
		
		System.out.println(" # "+s.indexOf("#"));
		int i = StringUtils.indexOf(s, "/", s.indexOf("#"));
		System.out.println(" / "+StringUtils.indexOf(s, "/", s.indexOf("#")));
		System.out.println(" guid "+s.substring(s.indexOf("#")+1,i));
		
		String attsHrefValue = "er:#I90AD73D079DE11DDB771B1D8BA9725E3/NDFF943C0B65511D8983DF34406B5929B1";
		
		int indexOfSlash = StringUtils.indexOf(attsHrefValue, "/", attsHrefValue.indexOf("#"));				
		
		System.out.println(" guid2 "+StringUtils.substring(attsHrefValue, attsHrefValue.indexOf("#") + 1, indexOfSlash) ) ;
	}
	
	@Test
	public void testSplitTocHappyPath() throws Exception {
		Map<String, DocumentInfo> documentInfoMap = new HashMap<String, DocumentInfo>();
		File documentFile1 = new File(tranformedDirectory, "DOC_GUID1.transformed");
		writeDocumentLinkFile(documentFile1, false);
		File documentFile2 = new File(tranformedDirectory, "DOC_GUID2.transformed");
		writeDocumentLinkFile(documentFile2, false);
		
		mockDocMetadataService.updateSplitBookFields(jobInstanceId, documentInfoMap);
		
		generateSplitTocTask.generateAndUpdateSplitToc(tocXml, splitTocXml, splitTocGuidList, 
				tranformedDirectory, jobInstanceId,"splitTitle");
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<EBook><titlebreak>eBook 1 of 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>eBook 2 of 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc></EBook>";
		
		//System.out.println(splitTocXml.toString());
		
		
		Assert.assertEquals(expected,splitTocXml.toString());
		
		Assert.assertTrue(splitTocXml.toString().length() > 0);

		assertTrue(splitTocXml.toString().contains("<titlebreak>"));
		
		documentInfoMap = generateSplitTocTask.getDocumentInfoMap();
		
		DocumentInfo expectedDocInfo1  = new DocumentInfo();
		expectedDocInfo1.setDocSize(new Long(14));
		expectedDocInfo1.setSplitTitleId("splitTitle");
		
		DocumentInfo expectedDocInfo2  = new DocumentInfo();
		expectedDocInfo2.setDocSize(new Long(14));
		expectedDocInfo2.setSplitTitleId("splitTitle_pt2");
		
		DocumentInfo docInfo1 = documentInfoMap.get("DOC_GUID1");
		DocumentInfo docInfo2 = documentInfoMap.get("DOC_GUID2");
		Assert.assertEquals(expectedDocInfo1.toString(),docInfo1.toString());
		Assert.assertEquals(expectedDocInfo2.toString(),docInfo2.toString());

	}
	
	@Test
	public void testGenerateSplitToc() throws Exception {
		
		FileExtensionFilter fileExtFilter;
		FileHandlingHelper mockfileHandlingHelper;
		fileExtFilter = EasyMock.createMock(FileExtensionFilter.class);
	    mockfileHandlingHelper = EasyMock.createMock(FileHandlingHelper.class);
	    fileExtFilter.setAcceptedFileExtensions(new String[] {testExtension});
	    mockfileHandlingHelper.setFilter(fileExtFilter);
	    
	    generateSplitTocTask.setfileHandlingHelper(mockfileHandlingHelper);	 
    	
    	mockfileHandlingHelper.getFileList(tranformedDirectory, new ArrayList<File>());	    
	        
	        EasyMock.replay(fileExtFilter);
	        EasyMock.replay(mockfileHandlingHelper);
		generateSplitTocTask.generateAndUpdateSplitToc(tocXml, splitTocXml, splitTocGuidList, 
				tranformedDirectory, jobInstanceId,"splitTitle");
		
		Map<String, DocumentInfo> documentInfoMap = generateSplitTocTask.getDocumentInfoMap();
		
		DocumentInfo expectedDocInfo1  = new DocumentInfo();
		expectedDocInfo1.setSplitTitleId("splitTitle");
		
		DocumentInfo expectedDocInfo2  = new DocumentInfo();
		expectedDocInfo2.setSplitTitleId("splitTitle_pt2");
		
		DocumentInfo docInfo1 = documentInfoMap.get("DOC_GUID1");
		DocumentInfo docInfo2 = documentInfoMap.get("DOC_GUID2");
		
		Assert.assertEquals(expectedDocInfo1.toString(),docInfo1.toString());
		Assert.assertEquals(expectedDocInfo2.toString(),docInfo2.toString());
	
	}
	
	
	
	protected void writeDocumentLinkFile(File tFile, boolean addNewLine) {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(tFile));

			writer.write("Write anything");

			writer.flush();
		} catch (IOException e) {
			String errMessage = "Encountered an IO Exception while processing: " + tFile.getAbsolutePath();
			LOG.error(errMessage);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				LOG.error("Unable to close anchor target list file.", e);
			}
		}

		LOG.debug("size of file : " + tFile.length());
	}

	@Test
	public void testHandler() throws Exception {

	
	}

}
