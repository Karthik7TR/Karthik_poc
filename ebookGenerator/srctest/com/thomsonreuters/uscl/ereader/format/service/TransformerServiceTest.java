/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataServiceImpl;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

/**
 * JUnit test for the Transformer service.
 * 
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 */
public class TransformerServiceTest {
	private static final String STATIC_CONTENT_DIR = "com/thomsonreuters/uscl/ereader/format/service/staticContent";
	private TransformerServiceImpl transformerService;
	private File tempRootDir; // root directory for all test files
	private String guid;

	/* arguments */
	private File srcDir; // input, contains .preprocess files
	private File metaDir;
	private File imgMetaDir;
	private File targetDir; // output, contains .transformed files
	private String titleID;
	private long jobID;
	private BookDefinition bookDefinition;
	private File staticContentDir;

	/* service mocks and return values */
	private DocMetadataServiceImpl metadataMoc;
	private DocMetadata docMeta;
	private GenerateDocumentDataBlockServiceImpl dataBlockService;
	private InputStream dataStream;

	/**
	 * makeFile( File directory, String name, String content ) helper method to streamline file creation
	 * 
	 * @param directory Location the new file will be created in
	 * @param name Name of the new file
	 * @param content Content to be written into the new file
	 * @return returns a File object directing to the new file returns null if any errors occur
	 */
	private File makeFile(File directory, String name, String content) {
		try {
			File file = new File(directory, name);
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(content.getBytes());
			out.flush();
			out.close();
			return file;
		} catch (Exception e) {
			return null;
		}
	}

	@Before
	public void setUp() throws IOException {
		this.transformerService = new TransformerServiceImpl();

		this.tempRootDir = new File(System.getProperty("java.io.tmpdir") + "/EvenMoreTemp");
		this.tempRootDir.mkdir();
		this.guid = "ebook_source_test";

		/* TransformXMLDocuments arguments */
		this.staticContentDir = new PathMatchingResourcePatternResolver().getResource(STATIC_CONTENT_DIR).getFile();
		this.srcDir = staticContentDir;
		
		this.metaDir = new File(tempRootDir.getAbsolutePath(), "MetaDirectory");
		this.metaDir.mkdir();
		makeFile(metaDir, guid + ".xml", "");
		this.imgMetaDir = new File(tempRootDir.getAbsolutePath(), "imageMetaDirectory");
		this.imgMetaDir.mkdir();
		makeFile(imgMetaDir, guid + ".imgMeta", "");
		this.targetDir = new File(tempRootDir.getAbsolutePath(), "TransformedDirectory");
		this.targetDir.mkdir();
		this.jobID = 987654321;
		
		this.bookDefinition = new BookDefinition();
		this.bookDefinition.setFullyQualifiedTitleId("yarr/pirates");
		this.bookDefinition.setIncludeAnnotations(true);
		this.bookDefinition.setIncludeNotesOfDecisions(true);
		
		this.titleID = this.bookDefinition.getTitleId();

		/* service mocks and return values */
		FileExtensionFilter filter = new FileExtensionFilter();
		filter.setAcceptedFileExtensions(new String[] { "preprocess" });
		FileHandlingHelper helper = new FileHandlingHelper();
		helper.setFilter(filter);
		this.transformerService.setfileHandlingHelper(helper);

		this.metadataMoc = EasyMock.createMock(DocMetadataServiceImpl.class);
		this.transformerService.setdocMetadataService(metadataMoc);

		this.dataBlockService = EasyMock.createMock(GenerateDocumentDataBlockServiceImpl.class);
		this.transformerService.setGenerateDocumentDataBlockService(dataBlockService);

		this.docMeta = new DocMetadata();
		this.docMeta.setTitleId(titleID);
		this.docMeta.setJobInstanceId(jobID);
		this.docMeta.setDocUuid(guid);
		this.docMeta.setCollectionName("test");

		String currentDate = "20160703113830";
		StringBuffer documentDataBlocks = new StringBuffer();
		documentDataBlocks.append("<document-data>");
		documentDataBlocks.append("<collection>");
		documentDataBlocks.append(docMeta.getCollectionName());
		documentDataBlocks.append("</collection>");
		documentDataBlocks.append("<datetime>" + currentDate + "</datetime>");
		documentDataBlocks.append("<versioned>");
		documentDataBlocks.append("False");
		documentDataBlocks.append("</versioned>");
		documentDataBlocks.append("<doc-type></doc-type>");
		documentDataBlocks.append("<cite></cite>");
		documentDataBlocks.append("</document-data>");
		this.dataStream = new ByteArrayInputStream(documentDataBlocks.toString().getBytes());
	}

	@After
	public void tearDown() throws Exception {
		/* recursively deletes the root directory, and all its subdirectories and files */
		FileUtils.deleteDirectory(tempRootDir);
	}

	/**
	 * TransformerService should take a source directory with ".preprocess" files generated by XMLPreprocessService and
	 * perform the second step necessary to transform them into html files. The resulting ".transformed" files are
	 * created in the target directory
	 * @throws IOException 
	 */
	@Test
	public void TestTransformerServiceHappyPathWithNotesOfDecisions() throws IOException {
		this.bookDefinition.setIncludeNotesOfDecisions(true);
		File preprocess = testTransformerServiceHappyPath();
		assertTrue(notesOfDecisionsExist(preprocess));
	}
	
	@Test
	public void TestTransformerServiceHappyPathNoNotesOfDecisions() throws IOException {
		this.bookDefinition.setIncludeNotesOfDecisions(false);
		File preprocess = testTransformerServiceHappyPath();
		assertFalse(notesOfDecisionsExist(preprocess));
	}	

	private File testTransformerServiceHappyPath() {
		int numDocs = -1;

		try {
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
			EasyMock.replay(dataBlockService);
			
			numDocs = transformerService.transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID,
					bookDefinition, staticContentDir);
		} catch (Exception e) {
			fail();
		}
		assertEquals(1, numDocs);

		File preprocess = new File(targetDir.getAbsolutePath(), "ebook_source_test.transformed");
		assertTrue(preprocess.exists());
		return preprocess;
	}

	private boolean notesOfDecisionsExist(File file) throws IOException {
		String content = FileUtils.readFileToString(file);
		return content.contains("NotesOfDecisionsOutput");
	}

	@Test
	public void TestBadStaticDir() {
		boolean thrown = false;
		this.staticContentDir = targetDir; // does not contain ContentTypeMapData.xml
		try {
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
			EasyMock.replay(dataBlockService);

			transformerService.transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID,
					bookDefinition, staticContentDir);
		} catch (EBookFormatException e) {
			// e.printStackTrace();
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void TestNullsrcDir() throws EBookFormatException {
		boolean thrown = false;
		this.srcDir = null;
		try {
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
			EasyMock.replay(dataBlockService);

			transformerService.transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID,
					bookDefinition, staticContentDir);
		} catch (IllegalArgumentException e) {
			//e.printStackTrace();
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void TestNonexistentTargetDir() {
		int numDocs = -1;
		boolean thrown = false;
		FileUtils.deleteQuietly(targetDir);

		try {
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
			EasyMock.replay(dataBlockService);

			numDocs = transformerService.transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID,
					bookDefinition, staticContentDir);
		} catch (Exception e) {
			// e.printStackTrace();
			thrown = true;
		}
		assertTrue(!thrown); // should be able to create target directory on the fly and continue running
		assertTrue(numDocs == 1);

		File preprocess1 = new File(targetDir.getAbsolutePath(), "ebook_source_test.transformed");
		assertTrue(preprocess1.exists());
	}

	@Test
	public void TestBadsrcDir() {
		boolean thrown = false;
		this.srcDir = targetDir; // contains no preprocessed files
		try {
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
			EasyMock.replay(dataBlockService);

			transformerService.transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID,
					bookDefinition, staticContentDir);
		} catch (EBookFormatException e) {
			// e.printStackTrace();
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void TestNullDocMetadata() {
		boolean thrown = false;

		try {
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(null);
			EasyMock.replay(metadataMoc);

			EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
			EasyMock.replay(dataBlockService);

			transformerService.transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID,
					bookDefinition, staticContentDir);
		} catch (EBookFormatException e) {
			// e.printStackTrace();
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void TestBadDocMetadata() {
		boolean thrown = false;

		try {
			docMeta.setDocType("not_Real");
			docMeta.setCollectionName("not_Real");
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
			EasyMock.replay(dataBlockService);

			transformerService.transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID,
					bookDefinition, staticContentDir);
		} catch (EBookFormatException e) {
			// e.printStackTrace();
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void TestNullMetadataFile() {
		boolean thrown = false;

		try {
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
			EasyMock.replay(dataBlockService);

			transformerService.transformXMLDocuments(srcDir, targetDir, imgMetaDir, targetDir, jobID,
					bookDefinition, staticContentDir);
		} catch (EBookFormatException e) {
			// e.printStackTrace();
			thrown = true;
		}
		assertTrue(thrown);
	}
}
