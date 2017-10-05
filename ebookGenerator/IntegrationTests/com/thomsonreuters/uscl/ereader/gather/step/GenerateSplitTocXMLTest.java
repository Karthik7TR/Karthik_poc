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

import com.thomsonreuters.uscl.ereader.format.service.SplitBookTocParseServiceImpl;
import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public final class GenerateSplitTocXMLTest {
    private static Logger LOG = LogManager.getLogger(GenerateSplitTocXMLTest.class);
    private static final String FINE_NAME = "split_toc_InputFile.xml";

    private GenerateSplitTocTask generateSplitTocTask;
    private List<String> splitTocGuidList;
    private InputStream tocXml;
    private OutputStream splitTocXml;
    private File tranformedDirectory;
    private File splitTocFile;
    private SplitBookTocParseServiceImpl splitBookTocParseService;
    private final String testExtension = ".transformed";
    private Long jobInstanceId;
    private DocMetadataService mockDocMetadataService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        generateSplitTocTask = new GenerateSplitTocTask();

        splitTocGuidList = new ArrayList<>();
        final String guid1 = "Iff5a5a9d7c8f11da9de6e47d6d5aa7a5";
        final String guid2 = "Iff5a5aac7c8f11da9de6e47d6d5aa7a5";
        splitTocGuidList.add(guid1);
        splitTocGuidList.add(guid2);

        final URL url = GenerateSplitTocTask.class.getResource(FINE_NAME);
        tocXml = new FileInputStream(url.getPath());

        final File workDir = temporaryFolder.getRoot();
        final File splitEbookDirectory = new File(workDir, "splitEbook");

        tranformedDirectory = new File(workDir, "transforned");
        splitEbookDirectory.mkdirs();
        tranformedDirectory.mkdirs();

        splitTocFile = new File(splitEbookDirectory, "splitToc.xml");
        splitTocXml = new FileOutputStream(splitTocFile);

        splitBookTocParseService = new SplitBookTocParseServiceImpl();
        final FileHandlingHelper fileHandlingHelper = new FileHandlingHelper();
        final FileExtensionFilter fileExtFilter = new FileExtensionFilter();
        fileExtFilter.setAcceptedFileExtensions(new String[] {testExtension});
        fileHandlingHelper.setFilter(fileExtFilter);
        generateSplitTocTask.setfileHandlingHelper(fileHandlingHelper);

        jobInstanceId = Long.valueOf(1);

        generateSplitTocTask.setSplitBookTocParseService(splitBookTocParseService);

        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        generateSplitTocTask.setDocMetadataService(mockDocMetadataService);
    }

    @Test
    public void testParseAndUpdateSplitToc() throws Exception {
        Map<String, DocumentInfo> documentInfoMap = new HashMap<>();

        mockDocMetadataService.updateSplitBookFields(jobInstanceId, documentInfoMap);

        final File documentFile1 = new File(tranformedDirectory, "Iff5a5a987c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile1, false);
        final File documentFile2 = new File(tranformedDirectory, "Iff5a5a9e7c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile2, false);
        final File documentFile3 = new File(tranformedDirectory, "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile3, true);
        final File documentFile5 = new File(tranformedDirectory, "Iff5a5aa17c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile5, false);
        final File documentFile4 = new File(tranformedDirectory, "Iff5a5aa47c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile4, true);
        final File documentFile6 = new File(tranformedDirectory, "Iff5a5aa77c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile6, false);
        final File documentFile7 = new File(tranformedDirectory, "Iff5a5aaa7c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile7, false);
        final File documentFile8 = new File(tranformedDirectory, "Iff5a81a27c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile8, false);
        final File documentFile9 = new File(tranformedDirectory, "Iff5a5aad7c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile9, false);
        final File documentFile10 = new File(tranformedDirectory, "Iff5a81a57c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile10, true);

        Assert.assertTrue(tranformedDirectory.exists());
        Assert.assertTrue(splitTocFile.exists());

        generateSplitTocTask.generateAndUpdateSplitToc(
            tocXml,
            splitTocXml,
            splitTocGuidList,
            tranformedDirectory,
            jobInstanceId,
            "splitTitle");

        Assert.assertTrue(splitTocFile.length() > 0);

        assertTrue(FileUtils.readFileToString(splitTocFile).contains("<titlebreak>"));

        documentInfoMap = generateSplitTocTask.getDocumentInfoMap();

        final DocumentInfo expectedDocInfo1 = new DocumentInfo();
        expectedDocInfo1.setDocSize(Long.valueOf(146));
        expectedDocInfo1.setSplitTitleId("splitTitle_pt3");

        final DocumentInfo expectedDocInfo2 = new DocumentInfo();
        expectedDocInfo2.setDocSize(Long.valueOf(219));
        expectedDocInfo2.setSplitTitleId("splitTitle");

        final DocumentInfo docInfo1 = documentInfoMap.get("Iff5a81a27c8f11da9de6e47d6d5aa7a5");
        final DocumentInfo docInfo2 = documentInfoMap.get("Iff5a5a9b7c8f11da9de6e47d6d5aa7a5");
        Assert.assertEquals(expectedDocInfo1.toString(), docInfo1.toString());
        Assert.assertEquals(expectedDocInfo2.toString(), docInfo2.toString());
    }

    @Test
    public void testWithMissingDoc() throws Exception {
        Map<String, DocumentInfo> documentInfoMap = new HashMap<>();

        mockDocMetadataService.updateSplitBookFields(jobInstanceId, documentInfoMap);

        splitTocGuidList = new ArrayList<>();
        final String guid1 = "NF81E0E40C40911DA87C3A6A101BC03A2";
        final String guid2 = "NC1A2A41006F411DB956CABAE160C185B";
        final String guid3 = "N66C5A630FAFD11DA989FE57CEE210EFD";
        final String guid4 = "N712BD9A0FAFD11DA989FE57CEE210EFD34";
        splitTocGuidList.add(guid1);
        splitTocGuidList.add(guid2);
        splitTocGuidList.add(guid3);
        splitTocGuidList.add(guid4);

        final File documentFile1 = new File(tranformedDirectory, "Iff5a5a987c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile1, false);
        final File documentFile2 = new File(tranformedDirectory, "Iff5a5a9e7c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile2, false);
        final File documentFile3 = new File(tranformedDirectory, "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile3, true);
        final File documentFile5 = new File(tranformedDirectory, "Iff5a5aa17c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile5, false);
        final File documentFile4 = new File(tranformedDirectory, "Iff5a5aa47c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile4, true);
        final File documentFile6 = new File(tranformedDirectory, "Iff5a5aa77c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile6, false);
        final File documentFile7 = new File(tranformedDirectory, "Iff5a5aaa7c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile7, false);
        final File documentFile8 = new File(tranformedDirectory, "Iff5a81a27c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile8, false);
        final File documentFile9 = new File(tranformedDirectory, "Iff5a5aad7c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile9, false);
        final File documentFile10 = new File(tranformedDirectory, "Iff5a81a57c8f11da9de6e47d6d5aa7a5.transformed");
        writeDocumentLinkFile(documentFile10, true);

        Assert.assertTrue(tranformedDirectory.exists());
        Assert.assertTrue(splitTocFile.exists());

        generateSplitTocTask.generateAndUpdateSplitToc(
            tocXml,
            splitTocXml,
            splitTocGuidList,
            tranformedDirectory,
            jobInstanceId,
            "splitTitle");

        Assert.assertTrue(splitTocFile.length() > 0);

        assertTrue(FileUtils.readFileToString(splitTocFile).contains("<titlebreak>"));

        documentInfoMap = generateSplitTocTask.getDocumentInfoMap();

        final DocumentInfo expectedDocInfo1 = new DocumentInfo();
        expectedDocInfo1.setDocSize(Long.valueOf(146));
        expectedDocInfo1.setSplitTitleId("splitTitle");

        final DocumentInfo expectedDocInfo2 = new DocumentInfo();
        expectedDocInfo2.setDocSize(Long.valueOf(219));
        expectedDocInfo2.setSplitTitleId("splitTitle");

        final DocumentInfo docInfo1 = documentInfoMap.get("Iff5a81a27c8f11da9de6e47d6d5aa7a5");
        final DocumentInfo docInfo2 = documentInfoMap.get("Iff5a5a9b7c8f11da9de6e47d6d5aa7a5");
        Assert.assertEquals(expectedDocInfo1.toString(), docInfo1.toString());
        Assert.assertEquals(expectedDocInfo2.toString(), docInfo2.toString());
    }

    @Test
    public void testIncorrectSplitToc() throws Exception {
        boolean thrown = false;
        splitTocGuidList = new ArrayList<>();
        final String guid1 = "NF81E0E40C40911DA87C3A6A101BC03A2";
        final String guid2 = "NC1A2A41006F411DB956CABAE160C185B";
        final String guid3 = "N66C5A630FAFD11DA989FE57CEE210EFD";
        final String guid4 = "NF66DD160C24F11DD8003BB48904FDE9B";
        splitTocGuidList.add(guid1);
        splitTocGuidList.add(guid2);
        splitTocGuidList.add(guid3);
        splitTocGuidList.add(guid4);
        final URL url = GenerateSplitTocTask.class.getResource("toc.xml");
        tocXml = new FileInputStream(url.getPath());

        try {
            generateSplitTocTask.generateAndUpdateSplitToc(
                tocXml,
                splitTocXml,
                splitTocGuidList,
                tranformedDirectory,
                jobInstanceId,
                "splitTitle");
        } catch (final RuntimeException e) {
            thrown = true;
            Assert.assertEquals(
                true,
                e.getMessage().contains(
                    "Split occured at an incorrect level. NF81E0E40C40911DA87C3A6A101BC03A2, N66C5A630FAFD11DA989FE57CEE210EFD, NC1A2A41006F411DB956CABAE160C185B"));
        }
        assertTrue(thrown);
    }

    protected void writeDocumentLinkFile(final File tFile, final boolean addNewLine) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tFile))) {
            writer.write("NF8C65500AFF711D8803AE0632FEDDFBF,N129FCFD29AA24CD5ABBAA83B0A8A2D7B275|");
            writer.newLine();
            writer.write("NDF4CB9C0AFF711D8803AE0632FEDDFBF,N8E37708B96244CD1B394155616B3C66F190|");

            writer.newLine();
            if (addNewLine) {
                writer.write("NF8C65500AFF711D8803AE0632FEDDFBF,N129FCFD29AA24CD5ABBAA83B0A8A2D7B275|");
                writer.newLine();
            }

            writer.flush();
        } catch (final IOException e) {
            final String errMessage = "Encountered an IO Exception while processing: " + tFile.getAbsolutePath();
            LOG.error(errMessage, e);
        }

        LOG.debug("size of file : " + tFile.length());
    }
}
