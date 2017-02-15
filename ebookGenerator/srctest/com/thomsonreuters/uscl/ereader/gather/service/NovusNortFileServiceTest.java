package com.thomsonreuters.uscl.ereader.gather.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.runner.commonsio.FileUtils;

public final class NovusNortFileServiceTest
{
    private static Logger LOG = LogManager.getLogger(NovusNortFileServiceTest.class);

    private static final String LT_ROOT_AMP_QUOT_NODE_APOS_S_GT = " &lt; Root &amp;  �  &quot; Node&apos;s &gt; ";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private File nortDir;

    private NovusNortFileServiceImpl novusNortFileService;

    @Before
    public void setUp() throws IOException
    {
        nortDir = temporaryFolder.newFolder("junit_nortFile");

        // The object under test
        novusNortFileService = new NovusNortFileServiceImpl();
    }

    @Test
    public void testCreateNortTreeFile() throws Exception
    {
        final File nortFile = new File(nortDir, "NORT" + EBConstants.XML_FILE_EXTENSION);

        final Date date = new Date();
        final String YYYYM1DDHHmmss = createOneDayAheadString(date);
        final DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");
        final String startDateFinal = formatterFinal.format(date);

        // Create nodes
        final List<RelationshipNode> rootNodes = new ArrayList<>();
        final RelationshipNode root = createRootNode(LT_ROOT_AMP_QUOT_NODE_APOS_S_GT, YYYYM1DDHHmmss, "nortGuid", 1);
        createNode("Child 0", YYYYM1DDHHmmss, root, "NORT_UUID_0", "UUID_0a", 2);
        createNode("Child 1", YYYYM1DDHHmmss, root, "NORT_UUID_1", "UUID_1a", 3);
        createNode("Child 2", YYYYM1DDHHmmss, root, "NORT_UUID_2", null, 4);
        createNode("Child 3", YYYYM1DDHHmmss, root, "NORT_UUID_3", "UUID_3a", 5);
        createNode("Child 4", YYYYM1DDHHmmss, root, "NORT_UUID_4", "UUID_4a", 6);
        rootNodes.add(root);

        // Invoke the object under test
        nortDir.mkdirs();

        final GatherResponse gatherResponse =
            novusNortFileService.findTableOfContents(rootNodes, nortFile, date, null, null, null, 0);
        LOG.debug(gatherResponse);

        // Verify created files and directories
        assertTrue(nortFile.exists());

        // compare file contents.
        final String tocFromNORT = FileUtils.readFileToString(nortFile, "UTF-8");
        LOG.debug("tocFromNORT =" + tocFromNORT);
        assertTrue(tocFromNORT != null);

        final StringBuffer expectedTocContent = new StringBuffer(1000);

        expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        expectedTocContent.append("<EBook>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name> &amp;lt; Root &amp;amp;  &#65533;  &amp;quot; Node&amp;apos;s &amp;gt;  (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent.append(") </Name><Guid>nortGuid1</Guid>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 0 (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_02</Guid><DocumentGuid>UUID_0a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 1 (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_13</Guid><DocumentGuid>UUID_1a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 2 (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_24</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 3 (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_35</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 4 (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_46</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append("</EBook>\r\n");
        LOG.debug("expectedTocContent =" + expectedTocContent.toString());

        assertEquals(expectedTocContent.toString(), tocFromNORT);
    }

    @Test
    public void testCreateNort2NodeTreeFile() throws Exception
    {
        final File nortFile = new File(nortDir, "DblRootNode" + EBConstants.XML_FILE_EXTENSION);
        final Date date = new Date();
        final String YYYYM1DDHHmmss = createOneDayAheadString(date);
        final DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");
        final String startDateFinal = formatterFinal.format(date);

        // Create nodes
        final List<RelationshipNode> rootNodes = new ArrayList<>();
        final RelationshipNode root = createRootNode("Root 1", YYYYM1DDHHmmss, "nortGuid a", 1);
        createNode("Child 0a", YYYYM1DDHHmmss, root, "NORT_UUID_0a", "UUID_0a", 2);
        createNode("Child 1a", YYYYM1DDHHmmss, root, "NORT_UUID_1a", "UUID_1a", 3);
        createNode("Child 2a", YYYYM1DDHHmmss, root, "NORT_UUID_2a", null, 4);
        createNode("Child 3a", YYYYM1DDHHmmss, root, "NORT_UUID_3a", "UUID_3a", 5);
        createNode("Child 4a", YYYYM1DDHHmmss, root, "NORT_UUID_4a", "UUID_4a", 6);
        rootNodes.add(root);
        final RelationshipNode root2 = createRootNode("Root 2", YYYYM1DDHHmmss, "nortGuid b", 1);
        createNode("Child 0b", YYYYM1DDHHmmss, root2, "NORT_UUID_0b", "UUID_0b", 2);
        createNode("Child 1b", YYYYM1DDHHmmss, root2, "NORT_UUID_1b", "UUID_1b", 3);
        rootNodes.add(root2);

        // Invoke the object under test
        nortDir.mkdirs();

        novusNortFileService.findTableOfContents(rootNodes, nortFile, date, null, null, null, 0);

        // Verify created files and directories
        assertTrue(nortFile.exists());

        // compare file contents.
        final String tocFromNORT = FileUtils.readFileToString(nortFile, "UTF-8");
        LOG.debug("tocFromNORT2roots =" + tocFromNORT);
        assertTrue(tocFromNORT != null);

        final StringBuffer expectedTocContent = new StringBuffer(1000);

        expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        expectedTocContent.append("<EBook>\r\n");
        expectedTocContent.append("<EBookToc><Name>Root 1 (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent.append(") </Name><Guid>nortGuid a1</Guid>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 0a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_0a2</Guid><DocumentGuid>UUID_0a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 1a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_1a3</Guid><DocumentGuid>UUID_1a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 2a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_2a4</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");

        expectedTocContent.append("<EBookToc><Name>Child 3a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_3a5</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 4a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_4a6</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Root 2 (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent.append(") </Name><Guid>nortGuid b7</Guid>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 0b (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_0b8</Guid><DocumentGuid>UUID_0b</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 1b (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_1b9</Guid><DocumentGuid>UUID_1b</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append("</EBook>\r\n");
        LOG.debug("expectedTocContent2roots =" + expectedTocContent.toString());

        assertEquals(expectedTocContent.toString(), tocFromNORT);
    }

    @Test
    public void testMissingDocument() throws Exception
    {
        final File nortFile = new File(nortDir, "missingDocument" + EBConstants.XML_FILE_EXTENSION);

        final Date date = new Date();
        final String YYYYM1DDHHmmss = createOneDayAheadString(date);
        final DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");
        final String startDateFinal = formatterFinal.format(date);

        // Create nodes
        final List<RelationshipNode> rootNodes = new ArrayList<>();
        final RelationshipNode root = createRootNode("Root 1", YYYYM1DDHHmmss, "nortGuid a", 1);
        createNode("Child 0a", YYYYM1DDHHmmss, root, "NORT_UUID_0a", "UUID_0a", 2);
        createNode("Child 1a", YYYYM1DDHHmmss, root, "NORT_UUID_1a", "UUID_1a", 3);
        createNode("Child 2a", YYYYM1DDHHmmss, root, "NORT_UUID_2a", null, 4);
        createNode("Child 3a", YYYYM1DDHHmmss, root, "NORT_UUID_3a", "UUID_3a", 5);
        createNode("Child 4a", YYYYM1DDHHmmss, root, "NORT_UUID_4a", "UUID_4a", 6);
        rootNodes.add(root);
        final RelationshipNode root2 = createRootNode("Root 2", YYYYM1DDHHmmss, "nortGuid b", 1);
        rootNodes.add(root2);

        // Invoke the object under test
        nortDir.mkdirs();

        novusNortFileService.findTableOfContents(rootNodes, nortFile, date, null, null, null, 0);

        // Verify created files and directories
        Assert.assertTrue(nortFile.exists());

        // compare file contents
        final String tocFromNORT = FileUtils.readFileToString(nortFile, "UTF-8");
        LOG.debug("tocFromNORT2roots =" + tocFromNORT);
        assertTrue(tocFromNORT != null);

        final StringBuffer expectedTocContent = new StringBuffer(1000);

        expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        expectedTocContent.append("<EBook>\r\n");
        expectedTocContent.append("<EBookToc><Name>Root 1 (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent.append(") </Name><Guid>nortGuid a1</Guid>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 0a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_0a2</Guid><DocumentGuid>UUID_0a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 1a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_1a3</Guid><DocumentGuid>UUID_1a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 2a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_2a4</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");

        expectedTocContent.append("<EBookToc><Name>Child 3a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_3a5</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 4a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_4a6</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Root 2 (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent.append(") </Name><Guid>nortGuid b7</Guid>");
        expectedTocContent.append("<MissingDocument></MissingDocument></EBookToc>\r\n");
        expectedTocContent.append("</EBook>\r\n");
        LOG.debug("expectedTocContent2roots =" + expectedTocContent.toString());

        Assert.assertEquals(expectedTocContent.toString(), tocFromNORT);
    }

    @Test
    public void testMissingDoc2Level() throws Exception
    {
        final File nortFile = new File(nortDir, "DblRootNode" + EBConstants.XML_FILE_EXTENSION);

        final Date date = new Date();
        final String YYYYM1DDHHmmss = createOneDayAheadString(date);
        final DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");
        final String startDateFinal = formatterFinal.format(date);

        // Create nodes
        final List<RelationshipNode> rootNodes = new ArrayList<>();
        final RelationshipNode root = createRootNode("Root 1", YYYYM1DDHHmmss, "nortGuid a", 1);
        createNode("Child 0a", YYYYM1DDHHmmss, root, "NORT_UUID_0a", "UUID_0a", 2);
        createNode("Child 1a", YYYYM1DDHHmmss, root, "NORT_UUID_1a", "UUID_1a", 3);
        createNode("Child 2a", YYYYM1DDHHmmss, root, "NORT_UUID_2a", null, 4);
        createNode("Child 3a", YYYYM1DDHHmmss, root, "NORT_UUID_3a", "UUID_3a", 5);
        createNode("Child 4a", YYYYM1DDHHmmss, root, "NORT_UUID_4a", "UUID_4a", 6);
        rootNodes.add(root);
        final RelationshipNode root2 = createRootNode("Root 2", YYYYM1DDHHmmss, "nortGuid b", 1);
        createNode("Child 0b", YYYYM1DDHHmmss, root2, "NORT_UUID_0b", null, 2);
        rootNodes.add(root2);

        // Invoke the object under test
        nortDir.mkdirs();

        novusNortFileService.findTableOfContents(rootNodes, nortFile, date, null, null, null, 0);

        // Verify created files and directories
        Assert.assertTrue(nortFile.exists());

        // compare file contents.
        final String tocFromNORT = FileUtils.readFileToString(nortFile, "UTF-8");
        LOG.debug("tocFromNORT2roots =" + tocFromNORT);
        assertTrue(tocFromNORT != null);

        final StringBuffer expectedTocContent = new StringBuffer(1000);

        expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        expectedTocContent.append("<EBook>\r\n");
        expectedTocContent.append("<EBookToc><Name>Root 1 (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent.append(") </Name><Guid>nortGuid a1</Guid>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 0a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_0a2</Guid><DocumentGuid>UUID_0a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 1a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_1a3</Guid><DocumentGuid>UUID_1a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 2a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_2a4</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");

        expectedTocContent.append("<EBookToc><Name>Child 3a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_3a5</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 4a (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_4a6</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append("<EBookToc><Name>Root 2 (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent.append(") </Name><Guid>nortGuid b7</Guid>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 0b (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_0b8</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append("</EBook>\r\n");
        LOG.debug("expectedTocContent2roots =" + expectedTocContent.toString());

        Assert.assertEquals(expectedTocContent.toString(), tocFromNORT);
    }

    @Test
    public void testSplitNodeExists() throws Exception
    {
        final File nortFile = new File(nortDir, "DblRootNode" + EBConstants.XML_FILE_EXTENSION);

        final Date date = new Date();
        final String YYYYM1DDHHmmss = createOneDayAheadString(date);

        // Create nodes
        final List<RelationshipNode> rootNodes = new ArrayList<>();
        final RelationshipNode root = createRootNode("Root 1", YYYYM1DDHHmmss, "nortGuid a", 1);
        createNode("Child 0a", YYYYM1DDHHmmss, root, "NORT_UUID_0a", "UUID_0a", 2);
        createNode("Child 1a", YYYYM1DDHHmmss, root, "Iff5a5aac7c8f11da9de6e47d6d5aa7a5", "UUID_1a", 3);
        createNode("Child 2a", YYYYM1DDHHmmss, root, "NORT_UUID_2a", null, 4);
        createNode("Child 3a", YYYYM1DDHHmmss, root, "NORT_UUID_3a", "UUID_3a", 5);
        createNode("Child 4a", YYYYM1DDHHmmss, root, "NORT_UUID_4a", "UUID_4a", 6);
        rootNodes.add(root);
        final RelationshipNode root2 = createRootNode("Root 2", YYYYM1DDHHmmss, "nortGuid b", 1);
        createNode("Child 0b", YYYYM1DDHHmmss, root2, "NORT_UUID_0b", null, 2);
        rootNodes.add(root2);

        // Invoke the object under test
        nortDir.mkdirs();

        final List<String> splitTocGuidList = new ArrayList<>();
        final String guid1 = "Iff5a5a9d7c8f11da9de6e47d6d5aa7a5";
        final String guid2 = "Iff5a5aac7c8f11da9de6e47d6d5aa7a5";
        splitTocGuidList.add(guid1);
        splitTocGuidList.add(guid2);

        novusNortFileService.findTableOfContents(rootNodes, nortFile, date, null, null, splitTocGuidList, 0);

        LOG.debug("expectedTocContent2roots =" + novusNortFileService.getSplitTocGuidList().size());

        Assert.assertEquals(1, novusNortFileService.getSplitTocGuidList().size());
    }

    @Test
    public void testNoSplitNode() throws Exception
    {
        final File nortFile = new File(nortDir, "DblRootNode" + EBConstants.XML_FILE_EXTENSION);

        final Date date = new Date();
        final String YYYYM1DDHHmmss = createOneDayAheadString(date);

        // Create nodes
        final List<RelationshipNode> rootNodes = new ArrayList<>();
        final RelationshipNode root = createRootNode("Root 1", YYYYM1DDHHmmss, "nortGuid a", 1);
        createNode("Child 0a", YYYYM1DDHHmmss, root, "Iff5a5a9d7c8f11da9de6e47d6d5aa7a5", "UUID_0a", 2);
        createNode("Child 1a", YYYYM1DDHHmmss, root, "Iff5a5aac7c8f11da9de6e47d6d5aa7a5", "UUID_1a", 3);
        createNode("Child 2a", YYYYM1DDHHmmss, root, "NORT_UUID_2a", null, 4);
        createNode("Child 3a", YYYYM1DDHHmmss, root, "NORT_UUID_3a", "UUID_3a", 5);
        createNode("Child 4a", YYYYM1DDHHmmss, root, "NORT_UUID_4a", "UUID_4a", 6);
        rootNodes.add(root);
        final RelationshipNode root2 = createRootNode("Root 2", YYYYM1DDHHmmss, "nortGuid b", 1);
        createNode("Child 0b", YYYYM1DDHHmmss, root2, "NORT_UUID_0b", null, 2);
        rootNodes.add(root2);

        // Invoke the object under test
        nortDir.mkdirs();

        final List<String> splitTocGuidList = new ArrayList<>();
        final String guid1 = "Iff5a5a9d7c8f11da9de6e47d6d5aa7a5";
        final String guid2 = "Iff5a5aac7c8f11da9de6e47d6d5aa7a5";
        splitTocGuidList.add(guid1);
        splitTocGuidList.add(guid2);

        novusNortFileService.findTableOfContents(rootNodes, nortFile, date, null, null, splitTocGuidList, 0);

        LOG.debug("expectedTocContent2roots =" + novusNortFileService.getSplitTocGuidList().size());

        Assert.assertEquals(0, novusNortFileService.getSplitTocGuidList().size());
    }

    @Test
    public void testMissingDocSections() throws Exception
    {
        final File nortFile = new File(nortDir, "MissingSection" + EBConstants.XML_FILE_EXTENSION);

        final Date date = new Date();
        final String YYYYM1DDHHmmss = createOneDayAheadString(date);
        final DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");
        final String startDateFinal = formatterFinal.format(date);

        // Create nodes
        final List<RelationshipNode> rootNodes = new ArrayList<>();
        final RelationshipNode root = createRootNode(LT_ROOT_AMP_QUOT_NODE_APOS_S_GT, YYYYM1DDHHmmss, "nortGuid", 1);
        final RelationshipNode child1 = createNode("Child c", YYYYM1DDHHmmss, root, "NORT_UUID", null, 2);
        createNode("Child 0b", YYYYM1DDHHmmss, child1, "NORT_UUID_0b", "UUID_0b", 3);
        rootNodes.add(root);

        // Invoke the object under test
        nortDir.mkdirs();

        final GatherResponse gatherResponse =
            novusNortFileService.findTableOfContents(rootNodes, nortFile, date, null, null, null, 0);
        LOG.debug(gatherResponse);

        // Verify created files and directories
        Assert.assertTrue(nortFile.exists());

        // compare file contents.
        final String tocFromNORT = FileUtils.readFileToString(nortFile, "UTF-8");
        LOG.debug("tocFromNORT =" + tocFromNORT);
        assertTrue(tocFromNORT != null);

        final StringBuffer expectedTocContent = new StringBuffer(1000);

        expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        expectedTocContent.append("<EBook>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name> &amp;lt; Root &amp;amp;  &#65533;  &amp;quot; Node&amp;apos;s &amp;gt;  (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent.append(") </Name><Guid>nortGuid1</Guid>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child c (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent.append(") </Name><Guid>NORT_UUID2</Guid>\r\n");
        expectedTocContent.append("<EBookToc><Name>Child 0b (effective ");
        expectedTocContent.append(startDateFinal);
        expectedTocContent
            .append(") </Name><Guid>NORT_UUID_0b3</Guid><DocumentGuid>UUID_0b</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append("</EBook>\r\n");
        LOG.debug("expectedTocContent =" + expectedTocContent.toString());

        Assert.assertEquals(expectedTocContent.toString(), tocFromNORT);
    }

    @Test
    public void testGetNortDataWithMissingLabel() throws Exception
    {
        final File nortFile = new File(nortDir, "NORT" + EBConstants.XML_FILE_EXTENSION);
        final Date date = new Date();
        final String YYYYM1DDHHmmss = createOneDayAheadString(date);

        // Create nodes
        final List<RelationshipNode> rootNodes = new ArrayList<>();
        final RelationshipNode root = createRootNode(null, YYYYM1DDHHmmss, "nortGuid", 1);
        rootNodes.add(root);

        // Invoke the object under test
        nortDir.mkdirs();

        try
        {
            novusNortFileService.findTableOfContents(rootNodes, nortFile, date, null, null, null, 0);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            LOG.debug(e.getMessage());
            Assert.assertEquals("Failed with empty node Label for guid nortGuid", e.getMessage());
        }
    }

    @Test(expected = GatherException.class)
    public void testGatherException() throws Exception
    {
        final File nortFile = new File(nortDir, "FAIL" + EBConstants.XML_FILE_EXTENSION);

        final Date date = new Date();
        final List<RelationshipNode> nodes = new ArrayList<>();
        novusNortFileService.findTableOfContents(nodes, nortFile, date, null, null, null, 0);
    }

    private RelationshipNode createRootNode(
        final String label,
        final String dateStr,
        final String nortGuid,
        final int rank)
    {
        final RelationshipNode node = createNode(label, dateStr, null, nortGuid, null, rank);
        node.setRootNode(true);
        node.setParentNode(null);
        node.setParentNortGuid(null);
        return node;
    }

    private RelationshipNode createNode(
        final String label,
        final String dateStr,
        final RelationshipNode parentNode,
        final String nortGuid,
        final String docGuid,
        final int rank)
    {
        final RelationshipNode node = new RelationshipNode();
        node.setDocumentGuid(docGuid);
        node.setEndDateStr(dateStr);
        node.setLabel(label);
        node.setNodeType("node");
        node.setNortGuid(nortGuid);
        node.setNortRank(0);
        node.setRank(rank);
        node.setStartDateStr(dateStr);

        if (parentNode != null)
        {
            node.setParentNode(parentNode);
            node.setParentNortGuid(parentNode.getNortGuid());
            parentNode.getChildNodes().add(node);
        }

        return node;
    }

    private String createOneDayAheadString(final Date date)
    {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String YYYYMMDDHHmmss;
        YYYYMMDDHHmmss = formatter.format(date);
        return "" + Long.valueOf(YYYYMMDDHHmmss) + 1;
    }
}
