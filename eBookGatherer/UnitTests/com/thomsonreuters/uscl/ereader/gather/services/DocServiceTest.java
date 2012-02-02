package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;
import com.westgroup.novus.productapi.Document;
import com.westgroup.novus.productapi.Find;
import com.westgroup.novus.productapi.Novus;

public class DocServiceTest {
	private static final String COLLECTION_NAME = "w_an_rcc_cajur_toc";
	private static final String GUID = "I8A302FE4920F47B00079B5381C71638B";
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	private NovusFactory mockNovusFactory;
	private Novus mockNovus;
	private Find mockFinder;
	private Document mockDocument;
	private DocServiceImpl docService;
	
	@Before
	public void setUp() {
		this.mockNovusFactory = EasyMock.createMock(NovusFactory.class);
		this.mockNovus = EasyMock.createMock(Novus.class);
		this.mockFinder = EasyMock.createMock(Find.class);
		this.mockDocument = EasyMock.createMock(Document.class);
		
		// The object under test
		this.docService = new DocServiceImpl();
		docService.setNovusFactory(mockNovusFactory);
	}
	
	@Test
	public void testFetchDocumentsFromNovus() {
		File workDir = temporaryFolder.getRoot();
		File contentDir = new File(workDir, "junit_content");
		File metadataDir = new File(workDir, "junit_metadata");
		File contentFile = new File(contentDir, GUID+EBConstants.XML_FILE_EXTENSION); 
		File metadataFile = new File(metadataDir, COLLECTION_NAME+"-"+GUID+EBConstants.XML_FILE_EXTENSION);
		
		try {
			// Record expected calls
			EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
			EasyMock.expect(mockNovus.getFind()).andReturn(mockFinder);
			EasyMock.expect(mockFinder.getDocument(COLLECTION_NAME, null, GUID)).andReturn(mockDocument);
			EasyMock.expect(mockDocument.getGuid()).andReturn(GUID).times(2);
			EasyMock.expect(mockDocument.getText()).andReturn("This is a novus document");
			EasyMock.expect(mockDocument.getMetaData()).andReturn("This is document metadata");
			EasyMock.expect(mockDocument.getCollection()).andReturn(COLLECTION_NAME);
			mockNovus.shutdownMQ();
	
			// Set up for replay
			EasyMock.replay(mockNovusFactory);
			EasyMock.replay(mockNovus);
			EasyMock.replay(mockFinder);
			EasyMock.replay(mockDocument);
			
			// Invoke the object under test
			contentDir.mkdirs();
			metadataDir.mkdirs();
			
			List<String> guids = new ArrayList<String>();
			guids.add(GUID);
			docService.fetchDocuments(guids, COLLECTION_NAME, contentDir, metadataDir);
			
			// Verify created files and directories
			Assert.assertTrue(contentFile.exists());
			Assert.assertTrue(metadataFile.exists());
			
			// Verify all call made as expected
			EasyMock.verify(mockNovusFactory);
			EasyMock.verify(mockNovus);
			EasyMock.verify(mockFinder);
			EasyMock.verify(mockDocument);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {

		}
	}
	@Test
	public void testFetchDocumentsFromNovusNullCollection() {
		File workDir = temporaryFolder.getRoot();
		File contentDir = new File(workDir, "junit_content");
		File metadataDir = new File(workDir, "junit_metadata");
		File contentFile = new File(contentDir, GUID+EBConstants.XML_FILE_EXTENSION); 
		File metadataFile = new File(metadataDir, COLLECTION_NAME+"-"+GUID+EBConstants.XML_FILE_EXTENSION);
		
		try {
			// Record expected calls
			EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
			EasyMock.expect(mockNovus.getFind()).andReturn(mockFinder);
			EasyMock.expect(mockFinder.getDocument(null, GUID)).andReturn(mockDocument);
			EasyMock.expect(mockDocument.getGuid()).andReturn(GUID).times(2);
			EasyMock.expect(mockDocument.getText()).andReturn("This is a novus document");
			EasyMock.expect(mockDocument.getMetaData()).andReturn("This is document metadata");
			EasyMock.expect(mockDocument.getCollection()).andReturn(COLLECTION_NAME);
			mockNovus.shutdownMQ();
	
			// Set up for replay
			EasyMock.replay(mockNovusFactory);
			EasyMock.replay(mockNovus);
			EasyMock.replay(mockFinder);
			EasyMock.replay(mockDocument);
			
			// Invoke the object under test
			contentDir.mkdirs();
			metadataDir.mkdirs();
			
			List<String> guids = new ArrayList<String>();
			guids.add(GUID);
			docService.fetchDocuments(guids, null, contentDir, metadataDir);
			
			// Verify created files and directories
			Assert.assertTrue(contentFile.exists());
			Assert.assertTrue(metadataFile.exists());
			
			// Verify all call made as expected
			EasyMock.verify(mockNovusFactory);
			EasyMock.verify(mockNovus);
			EasyMock.verify(mockFinder);
			EasyMock.verify(mockDocument);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {

		}
	}
}
