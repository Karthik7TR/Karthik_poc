/*
 * Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;

public class NovusDocFileServiceTest {
	private static final String COLLECTION_NAME = "w_an_rcc_cajur_toc";
	private static final String GUID_1 = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
	private static final String GUID_2 = "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC";
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	private NovusDocFileServiceImpl novusDocFileService;
	
	@Before
	public void setUp() {
		this.novusDocFileService = new NovusDocFileServiceImpl();
	}
	
	@Test
	public void testNullDocumentError() {
		String bookTitle = "book_title";
		File workDir = temporaryFolder.getRoot();
		File contentDir = new File(workDir, "junit_content");
		File metadataDir = new File(workDir, "junit_metadata");
		
		List<NortFileLocation> fileLocations = new ArrayList<NortFileLocation>();
		NortFileLocation location = new NortFileLocation();
		
		location.setLocationName("contentDir");
		location.setSequenceNum(1);
		fileLocations.add(location);
		
		File cwbDir = new File(workDir, "cwb_dir");
		File bookTitleDir = new File(cwbDir, bookTitle);
		File contentTypeDir = new File(bookTitleDir, location.getLocationName());
		File novusDoc = new File(contentTypeDir, "collectionName-12345_doc.xml");
		
		try {
			// Invoke the object under test
			contentDir.mkdirs();
			metadataDir.mkdirs();
			cwbDir.mkdir();
			bookTitleDir.mkdir();
			contentTypeDir.mkdir();
			novusDoc.createNewFile();
			
			addContentToFile(novusDoc, "<n-document guid=\"N29E94310C13311DF9DBEA900468B10B3\" control=\"ADD\"><n-metadata>"
					+ "</n-metadata><n-docbody></n-docbody></n-document>");
			
			HashMap<String, Integer> guids = new HashMap<String, Integer>();
			guids.put(GUID_1, 1);
			novusDocFileService.fetchDocuments(guids, cwbDir, "book_title", fileLocations, contentDir, metadataDir);
			
			fail("Should throw null documents error");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals("Null documents are found for the current ebook ", e.getMessage());
		} finally {
			temporaryFolder.delete();
		}
	}
	
	@Test
	public void testOneDocument() {
		String bookTitle = "book_title";
		File workDir = temporaryFolder.getRoot();
		File contentDir = new File(workDir, "junit_content");
		File metadataDir = new File(workDir, "junit_metadata");
		
		List<NortFileLocation> fileLocations = new ArrayList<NortFileLocation>();
		NortFileLocation location = new NortFileLocation();
		
		location.setLocationName("contentDir");
		location.setSequenceNum(1);
		fileLocations.add(location);
		
		File cwbDir = new File(workDir, "cwb_dir");
		File bookTitleDir = new File(cwbDir, bookTitle);
		File contentTypeDir = new File(bookTitleDir, location.getLocationName());
		File novusDoc = new File(contentTypeDir, COLLECTION_NAME + "-12345_doc.xml");
		
		File contentFile = new File(contentDir, GUID_1+EBConstants.XML_FILE_EXTENSION); 
		File metadataFile = new File(metadataDir, "1-"+COLLECTION_NAME+"-"+GUID_1+EBConstants.XML_FILE_EXTENSION);
		
		try {
			// Invoke the object under test
			contentDir.mkdirs();
			metadataDir.mkdirs();
			cwbDir.mkdir();
			bookTitleDir.mkdir();
			contentTypeDir.mkdir();
			novusDoc.createNewFile();
			
			addContentToFile(novusDoc, "<n-document guid=\""+ GUID_1 +"\" control=\"ADD\"><n-metadata>"
					+ "</n-metadata><n-docbody></n-docbody></n-document>");
			
			HashMap<String, Integer> guids = new HashMap<String, Integer>();
			guids.put(GUID_1, 1);
			novusDocFileService.fetchDocuments(guids, cwbDir, "book_title", fileLocations, contentDir, metadataDir);

			// Verify created files and directories
			assertTrue(contentFile.exists());
			assertTrue(metadataFile.exists());
			assertTrue(cwbDir.exists());
			assertTrue(bookTitleDir.exists());
			assertTrue(contentTypeDir.exists());
			assertTrue(novusDoc.exists());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			temporaryFolder.delete();
		}
	}
	
	@Test
	public void testTwoDocuments() {
		String bookTitle = "book_title";
		File workDir = temporaryFolder.getRoot();
		File contentDir = new File(workDir, "junit_content");
		File metadataDir = new File(workDir, "junit_metadata");
		
		List<NortFileLocation> fileLocations = new ArrayList<NortFileLocation>();
		NortFileLocation location = new NortFileLocation();
		
		location.setLocationName("contentDir");
		location.setSequenceNum(1);
		fileLocations.add(location);
		
		File cwbDir = new File(workDir, "cwb_dir");
		File bookTitleDir = new File(cwbDir, bookTitle);
		File contentTypeDir = new File(bookTitleDir, location.getLocationName());
		File novusDoc = new File(contentTypeDir, COLLECTION_NAME + "-12345_doc.xml");
		
		File contentFile = new File(contentDir, GUID_1+EBConstants.XML_FILE_EXTENSION); 
		File metadataFile = new File(metadataDir, "1-"+COLLECTION_NAME+"-"+GUID_1+EBConstants.XML_FILE_EXTENSION);
		File contentFile2 = new File(contentDir, GUID_2+EBConstants.XML_FILE_EXTENSION); 
		File metadataFile2 = new File(metadataDir, "2-"+COLLECTION_NAME+"-"+GUID_2+EBConstants.XML_FILE_EXTENSION);
		
		try {
			// Invoke the object under test
			contentDir.mkdirs();
			metadataDir.mkdirs();
			cwbDir.mkdir();
			bookTitleDir.mkdir();
			contentTypeDir.mkdir();
			novusDoc.createNewFile();
			
			addContentToFile(novusDoc, "<n-load loadcontenttype=\"SECTIONAL\"><n-document guid=\""+ GUID_1 +"\" "
					+ "control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document><n-document "
					+ "guid=\""+ GUID_2 +"\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody>"
					+ "</n-document></n-load>");
			
			HashMap<String, Integer> guids = new HashMap<String, Integer>();
			guids.put(GUID_1, 1);
			guids.put(GUID_2, 1);
			novusDocFileService.fetchDocuments(guids, cwbDir, "book_title", fileLocations, contentDir, metadataDir);

			// Verify created files and directories
			assertTrue(contentFile.exists());
			assertTrue(metadataFile.exists());
			assertTrue(contentFile2.exists());
			assertTrue(metadataFile2.exists());
			assertTrue(cwbDir.exists());
			assertTrue(bookTitleDir.exists());
			assertTrue(contentTypeDir.exists());
			assertTrue(novusDoc.exists());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			temporaryFolder.delete();
		}
	}
	
	@Test
	public void testTwoContentTypes() {
		String bookTitle = "book_title";
		File workDir = temporaryFolder.getRoot();
		File contentDir = new File(workDir, "junit_content");
		File metadataDir = new File(workDir, "junit_metadata");
		
		List<NortFileLocation> fileLocations = new ArrayList<NortFileLocation>();
		NortFileLocation location = new NortFileLocation();
		location.setLocationName("contentDir");
		location.setSequenceNum(1);
		fileLocations.add(location);
		
		NortFileLocation location2 = new NortFileLocation();
		location2.setLocationName("contentDir2");
		location2.setSequenceNum(2);
		fileLocations.add(location2);
		
		File cwbDir = new File(workDir, "cwb_dir");
		File bookTitleDir = new File(cwbDir, bookTitle);
		File contentTypeDir = new File(bookTitleDir, location.getLocationName());
		File contentTypeDir2 = new File(bookTitleDir, location2.getLocationName());
		File novusDoc = new File(contentTypeDir, COLLECTION_NAME + "-12345_doc.xml");
		File novusDoc2 = new File(contentTypeDir2, COLLECTION_NAME + "-ABCDE_doc.xml");
		
		File contentFile = new File(contentDir, GUID_1+EBConstants.XML_FILE_EXTENSION); 
		File metadataFile = new File(metadataDir, "1-"+COLLECTION_NAME+"-"+GUID_1+EBConstants.XML_FILE_EXTENSION);
		File contentFile2 = new File(contentDir, GUID_2+EBConstants.XML_FILE_EXTENSION); 
		File metadataFile2 = new File(metadataDir, "2-"+COLLECTION_NAME+"-"+GUID_2+EBConstants.XML_FILE_EXTENSION);
		
		try {
			// Invoke the object under test
			contentDir.mkdirs();
			metadataDir.mkdirs();
			cwbDir.mkdir();
			bookTitleDir.mkdir();
			contentTypeDir.mkdir();
			contentTypeDir2.mkdir();
			novusDoc.createNewFile();
			novusDoc2.createNewFile();
			
			addContentToFile(novusDoc, "<n-load loadcontenttype=\"SECTIONAL\"><n-document guid=\""+ GUID_1 +"\" "
					+ "control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document></n-load>");
			addContentToFile(novusDoc2, "<n-load loadcontenttype=\"SECTIONAL\"><n-document guid=\""+ GUID_2 +"\" "
					+ "control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document></n-load>");
			
			HashMap<String, Integer> guids = new HashMap<String, Integer>();
			guids.put(GUID_1, 1);
			guids.put(GUID_2, 1);
			novusDocFileService.fetchDocuments(guids, cwbDir, "book_title", fileLocations, contentDir, metadataDir);
			
			// Verify created files and directories
			assertTrue(cwbDir.exists());
			assertTrue(bookTitleDir.exists());
			assertTrue(contentTypeDir.exists());
			assertTrue(contentTypeDir2.exists());
			assertTrue(novusDoc.exists());
			assertTrue(novusDoc2.exists());
			assertTrue(contentFile.exists());
			assertTrue(metadataFile.exists());
			assertTrue(contentFile2.exists());
			assertTrue(metadataFile2.exists());	
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			temporaryFolder.delete();
		}
	}
	
	private void addContentToFile(File file, String text) {
		try {
			FileWriter fileOut = new FileWriter(file);
			fileOut.write(text);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
