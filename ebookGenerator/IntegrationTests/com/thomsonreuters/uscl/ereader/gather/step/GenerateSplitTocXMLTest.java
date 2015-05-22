package com.thomsonreuters.uscl.ereader.gather.step;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thomsonreuters.uscl.ereader.format.parsinghandler.SplitBookTocFilter;
import com.thomsonreuters.uscl.ereader.format.service.SplitBookTocParseServiceImpl;
import com.thomsonreuters.uscl.ereader.format.service.SplitBookTocParseServiceTest;
import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

public class GenerateSplitTocXMLTest {

	private static Logger LOG = Logger.getLogger(GenerateSplitTocXMLTest.class);
	private static final String FINE_NAME = "split_toc_InputFile.xml";

	GenerateSplitTocTask generateSplitTocTask;
	List<String> splitTocGuidList;
	InputStream tocXml;
	OutputStream splitTocXml;
	File tranformedDirectory;
	File splitTocFile;
	SplitBookTocParseServiceImpl splitBookTocParseService;
	private final String testExtension = ".transformed";
	Long jobInstanceId;
	private DocMetadataService mockDocMetadataService;;

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		generateSplitTocTask = new GenerateSplitTocTask();

		splitTocGuidList = new ArrayList<String>();
		String guid1 = "Iff5a5a9d7c8f11da9de6e47d6d5aa7a5";
		String guid2 = "Iff5a5aac7c8f11da9de6e47d6d5aa7a5";
		splitTocGuidList.add(guid1);
		splitTocGuidList.add(guid2);

		URL url = GenerateSplitTocTask.class.getResource(FINE_NAME);
		tocXml = new FileInputStream(url.getPath());

		File workDir = temporaryFolder.getRoot();
		File splitEbookDirectory = new File(workDir, "splitEbook");

		tranformedDirectory = new File(workDir, "transforned");
		splitEbookDirectory.mkdirs();
		tranformedDirectory.mkdirs();

		splitTocFile = new File(splitEbookDirectory, "splitToc.xml");
		splitTocXml = new FileOutputStream(splitTocFile);

		splitBookTocParseService = new SplitBookTocParseServiceImpl();
		FileHandlingHelper fileHandlingHelper = new FileHandlingHelper();
		FileExtensionFilter fileExtFilter = new FileExtensionFilter();
		fileExtFilter.setAcceptedFileExtensions(new String[] { testExtension });
		fileHandlingHelper.setFilter(fileExtFilter);
		generateSplitTocTask.setfileHandlingHelper(fileHandlingHelper);

		jobInstanceId = new Long(1);


		generateSplitTocTask.setSplitBookTocParseService(splitBookTocParseService);
		
		this.mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
		generateSplitTocTask.setDocMetadataService(mockDocMetadataService);
		
		

	}
	

	@Test
	public void testParseAndUpdateSplitToc() throws Exception {
		Map<String, DocumentInfo> documentInfoMap = new HashMap<String, DocumentInfo>();
		
		mockDocMetadataService.updateSplitBookFields(jobInstanceId, documentInfoMap);

		File documentFile1 = new File(tranformedDirectory, "Iff5a5a987c8f11da9de6e47d6d5aa7a5.transformed");
		writeDocumentLinkFile(documentFile1, false);
		File documentFile2 = new File(tranformedDirectory, "Iff5a5a9e7c8f11da9de6e47d6d5aa7a5.transformed");
		writeDocumentLinkFile(documentFile2, false);
		File documentFile3 = new File(tranformedDirectory, "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5.transformed");
		writeDocumentLinkFile(documentFile3, true);
		File documentFile5 = new File(tranformedDirectory, "Iff5a5aa17c8f11da9de6e47d6d5aa7a5.transformed");
		writeDocumentLinkFile(documentFile5, false);
		File documentFile4 = new File(tranformedDirectory, "Iff5a5aa47c8f11da9de6e47d6d5aa7a5.transformed");
		writeDocumentLinkFile(documentFile4, true);
		File documentFile6 = new File(tranformedDirectory, "Iff5a5aa77c8f11da9de6e47d6d5aa7a5.transformed");
		writeDocumentLinkFile(documentFile6, false);
		File documentFile7 = new File(tranformedDirectory, "Iff5a5aaa7c8f11da9de6e47d6d5aa7a5.transformed");
		writeDocumentLinkFile(documentFile7, false);
		File documentFile8 = new File(tranformedDirectory, "Iff5a81a27c8f11da9de6e47d6d5aa7a5.transformed");
		writeDocumentLinkFile(documentFile8, false);
		File documentFile9 = new File(tranformedDirectory, "Iff5a5aad7c8f11da9de6e47d6d5aa7a5.transformed");
		writeDocumentLinkFile(documentFile9, false);
		File documentFile10 = new File(tranformedDirectory, "Iff5a81a57c8f11da9de6e47d6d5aa7a5.transformed");
		writeDocumentLinkFile(documentFile10, true);
		
		Assert.assertTrue(tranformedDirectory.exists());
		Assert.assertTrue(splitTocFile.exists());

		String titleBreakLabel = "Title part ";
		generateSplitTocTask.generateAndUpdateSplitToc(tocXml, splitTocXml, splitTocGuidList, titleBreakLabel,
				tranformedDirectory, jobInstanceId);

		Assert.assertTrue(splitTocFile.length() > 0);

		assertTrue(FileUtils.readFileToString(splitTocFile).contains("<titlebreak>"));
		
		documentInfoMap = generateSplitTocTask.getDocumentInfoMap();
		
		DocumentInfo expectedDocInfo1  = new DocumentInfo();
		expectedDocInfo1.setDocSize(new Long(146));
		expectedDocInfo1.setSplitTitleId("Title part 3");
		
		DocumentInfo expectedDocInfo2  = new DocumentInfo();
		expectedDocInfo2.setDocSize(new Long(219));
		expectedDocInfo2.setSplitTitleId("Title part 1");
		
		DocumentInfo docInfo1 = documentInfoMap.get("Iff5a81a27c8f11da9de6e47d6d5aa7a5");
		DocumentInfo docInfo2 = documentInfoMap.get("Iff5a5a9b7c8f11da9de6e47d6d5aa7a5");
		Assert.assertEquals(expectedDocInfo1.toString(),docInfo1.toString());
		Assert.assertEquals(expectedDocInfo2.toString(),docInfo2.toString());

	}

	protected void writeDocumentLinkFile(File tFile, boolean addNewLine) {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(tFile));

			writer.write("NF8C65500AFF711D8803AE0632FEDDFBF,N129FCFD29AA24CD5ABBAA83B0A8A2D7B275|");
			writer.newLine();
			writer.write("NDF4CB9C0AFF711D8803AE0632FEDDFBF,N8E37708B96244CD1B394155616B3C66F190|");

			writer.newLine();
			if (addNewLine) {
				writer.write("NF8C65500AFF711D8803AE0632FEDDFBF,N129FCFD29AA24CD5ABBAA83B0A8A2D7B275|");
				writer.newLine();
			}

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

}
