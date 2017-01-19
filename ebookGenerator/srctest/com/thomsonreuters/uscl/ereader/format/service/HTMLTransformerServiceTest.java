/*
* Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataServiceImpl;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.PaceMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

/**
 * JUnit test for HTMLTransformerserviceImpl.java
 * 
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 */
@Ignore
public class HTMLTransformerServiceTest {

	private HTMLTransformerServiceImpl transformerService;
	private File tempRootDir; // root directory for all test files

	/* arguments */
	private File srcDir;
	private File targetDir;
	private File staticImgList;
	private List<TableViewer> tableViewers = null;
	private String title = "ebook_source_test";
	private Long jobId;
	private HashMap<String, HashSet<String>> targetAnchors;
	private File docsGuidFile;
	private File deDuppingFile;
	private boolean isHighlight = false;
	private boolean isStrikethrough = false;
	private boolean delEditorNodeHeading = false;
	private String version = "test";

	/* service mocks and return values */
	private DocMetadataServiceImpl metadataMoc;
	private DocumentMetadataAuthority docMetaAuthority;
	private DocMetadata docMeta;

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
	public void setUp() {
		this.transformerService = new HTMLTransformerServiceImpl();

		/* initialize arguments */
		this.tempRootDir = new File(System.getProperty("java.io.tmpdir") + "/EvenMoreTemp");
		this.tempRootDir.mkdir();
		this.srcDir = new File("srctest/com/thomsonreuters/uscl/ereader/format/service/staticContent");
		this.targetDir = new File(tempRootDir.getAbsolutePath(), "PostTransformDirectory");
		this.targetDir.mkdir();
		this.staticImgList = makeFile(tempRootDir, "StaticImageList", "");
		this.jobId = new Long(127);
		this.targetAnchors = new HashMap<String, HashSet<String>>();
		this.docsGuidFile = makeFile(tempRootDir, "docsGuidFile", "");
		this.deDuppingFile = makeFile(tempRootDir, "deDuppingFile", "");

		/* service mocks and return values */
		FileExtensionFilter filter = new FileExtensionFilter();
		filter.setAcceptedFileExtensions(new String[] { "transformed" });
		FileHandlingHelper helper = new FileHandlingHelper();
		helper.setFilter(filter);
		// FileHandlingHelper fileHelper = EasyMock.createMock(FileHandlingHelper.class); // cannot mock
		// pass-by-reference behavior
		this.transformerService.setfileHandlingHelper(helper);

		this.metadataMoc = EasyMock.createMock(DocMetadataServiceImpl.class);
		this.transformerService.setdocMetadataService(metadataMoc);

		Set<DocMetadata> docMetadataSet = new LinkedHashSet<DocMetadata>();
		this.docMetaAuthority = new DocumentMetadataAuthority(docMetadataSet);

		this.docMeta = new DocMetadata();
		this.docMeta.setTitleId(title);
		this.docMeta.setJobInstanceId(jobId);
		this.docMeta.setDocUuid(title);
		this.docMeta.setCollectionName("test");
	}

	@After
	public void tearDown() throws Exception {
		/* recursively deletes the root directory, and all its subdirectories and files */
		FileUtils.deleteDirectory(tempRootDir);
	}

	/**
	 * TransformHTML should take a source directory with ".transformed" files generated by TransformerService and
	 * perform the third step necessary to transform them into html files. The resulting ".postTransform" files are
	 * created in the target directory
	 */
	@Test
	public void testTransformerServiceHappyPath() {
		int numDocs = -1;
		boolean thrown = false;

		try {
			EasyMock.expect(metadataMoc.findAllDocMetadataForTitleByJobId(jobId)).andReturn(docMetaAuthority);
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(title, jobId, title)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			numDocs = transformerService.transformHTML(srcDir, targetDir, staticImgList, tableViewers, title, jobId,
					targetAnchors, docsGuidFile, deDuppingFile, isHighlight, isStrikethrough, delEditorNodeHeading,
					version);
		} catch (Exception e) {
			// e.printStackTrace();
			thrown = true;
		}
		assertTrue(!thrown);
		assertTrue(numDocs == 1);

		File postTransform = new File(targetDir.getAbsolutePath(), title + ".posttransform");
		assertTrue(postTransform.exists());
	}

	/**
	 * Test TransformHTML and various logical branches not taken by the happy path test above
	 */
	@Test
	public void testAltConditions() {
		int numDocs = -1;
		boolean thrown = false;

		/* test miscellaneous IF branches not taken by happy path */
		tableViewers = new ArrayList<TableViewer>();
		TableViewer table = new TableViewer();
		table.setDocumentGuid(title);
		tableViewers.add(table);
		version = "test.test";
		docMeta.setProviewFamilyUUIDDedup(new Integer(1));
		docMeta.setDocFamilyUuid("hello test!");
		FileUtils.deleteQuietly(targetDir);
		/* ----------------------------------------------------- */

		/* set up additional serviceMOCs */
		PaceMetadataService paceMetaMoc = EasyMock.createMock(PaceMetadataService.class);
		this.transformerService.setpaceMetadataService(paceMetaMoc);

		ImageService imgServiceMoc = EasyMock.createMock(ImageServiceImpl.class);
		this.transformerService.setimgService(imgServiceMoc);
		/* ---------------------------- */

		try {
			EasyMock.expect(metadataMoc.findAllDocMetadataForTitleByJobId(jobId)).andReturn(docMetaAuthority);
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(title, jobId, title)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			numDocs = transformerService.transformHTML(srcDir, targetDir, staticImgList, tableViewers, title, jobId,
					targetAnchors, docsGuidFile, deDuppingFile, isHighlight, isStrikethrough, delEditorNodeHeading,
					version);
		} catch (Exception e) {
			// e.printStackTrace();
			thrown = true;
		}
		assertTrue(!thrown);
		assertTrue(numDocs == 1);

		File postTransform = new File(targetDir.getAbsolutePath(), title + ".posttransform");
		assertTrue(postTransform.exists());
	}

	/**
	 * Tests the case were there are more table viewers than .transformed files to process. Should throw an
	 * EBookFormatException.
	 */
	@Test
	public void testWithExtraTableViewers() {
		boolean thrown = false;
		boolean expect = false;

		tableViewers = new ArrayList<TableViewer>();
		TableViewer table = new TableViewer();
		table.setDocumentGuid(title);
		tableViewers.add(table);
		tableViewers.add(table);

		try {
			EasyMock.expect(metadataMoc.findAllDocMetadataForTitleByJobId(jobId)).andReturn(docMetaAuthority);
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(title, jobId, title)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			transformerService.transformHTML(srcDir, targetDir, staticImgList, tableViewers, title, jobId,
					targetAnchors, docsGuidFile, deDuppingFile, isHighlight, isStrikethrough, delEditorNodeHeading,
					version);
		} catch (EBookFormatException e) {
			expect = true;
		} catch (Exception e) {
			// e.printStackTrace();
			thrown = true;
		}
		assertTrue(!thrown);
		assertTrue(expect);
	}

	/**
	 * test miscellaneous exceptions that may be thrown due to bad arguments
	 */
	@Test
	public void testExceptions() {
		boolean thrown = false;
		boolean expect = false;
		try {
			transformerService.transformHTML(null, targetDir, staticImgList, tableViewers, title, jobId, targetAnchors,
					docsGuidFile, deDuppingFile, isHighlight, isStrikethrough, delEditorNodeHeading, version);
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
			expect = true;
		} catch (Exception e) {
			// e.printStackTrace();
			thrown = true;
		}
		assertTrue(expect);
		assertTrue(!thrown);

		thrown = false;
		expect = false;

		try {
			transformerService.transformHTML(targetDir, targetDir, staticImgList, tableViewers, title, jobId,
					targetAnchors, docsGuidFile, deDuppingFile, isHighlight, isStrikethrough, delEditorNodeHeading,
					version);
		} catch (EBookFormatException e) {
			// e.printStackTrace();
			expect = true;
		} catch (Exception e) {
			// e.printStackTrace();
			thrown = true;
		}
		assertTrue(expect);
		assertTrue(!thrown);

	}

	/**
	 * test handling of exceptions thrown by the xml parser
	 */
	@Test
	public void testSaxParserException() {
		boolean thrown = false;
		boolean expect = false;
		makeFile(targetDir, title + ".transformed", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

		try {
			EasyMock.expect(metadataMoc.findAllDocMetadataForTitleByJobId(jobId)).andReturn(docMetaAuthority);
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(title, jobId, title)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			transformerService.transformHTML(targetDir, targetDir, staticImgList, tableViewers, title, jobId,
					targetAnchors, docsGuidFile, deDuppingFile, isHighlight, isStrikethrough, delEditorNodeHeading,
					version);
		} catch (EBookFormatException e) {
			// e.printStackTrace();
			expect = true;
		} catch (Exception e) {
			// e.printStackTrace();
			thrown = true;
		}

		assertTrue(expect);
		assertTrue(!thrown);

	}

	/**
	 * extend coverage to the function includeDeduppingAnchorRecords( .. )
	 */
	@Test
	public void testDeduppingAnchorRecords() {
		int numDocs = -1;
		boolean thrown = false;

		File srcFile = makeFile(targetDir, title + ".transformed",
				"<title><src id=\"1234\"/><src id=\"1234\"/></title>");

		try {
			EasyMock.expect(metadataMoc.findAllDocMetadataForTitleByJobId(jobId)).andReturn(docMetaAuthority);
			EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(title, jobId, title)).andReturn(docMeta);
			EasyMock.replay(metadataMoc);

			numDocs = transformerService.transformHTML(targetDir, targetDir, staticImgList, tableViewers, title, jobId,
					targetAnchors, docsGuidFile, deDuppingFile, isHighlight, isStrikethrough, delEditorNodeHeading,
					version);
		} catch (Exception e) {
			// e.printStackTrace();
			thrown = true;
		} finally {
			FileUtils.deleteQuietly(srcFile);
		}
		assertTrue(!thrown);
		assertTrue(numDocs == 1);

		File postTransform = new File(targetDir.getAbsolutePath(), title + ".posttransform");
		assertTrue(postTransform.exists());
	}
}
