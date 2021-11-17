package com.thomsonreuters.uscl.ereader.gather.services;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;
import com.westgroup.novus.productapi.TOC;
import com.westgroup.novus.productapi.TOCNode;

public final class TocServiceTest {
    private static final String COLLECTION_NAME = "w_an_rcc_cajur_toc";
    private static final String TOC_GUID = "I7b3ec600675a11da90ebf04471783734";
    private static final boolean IS_FINAL_STAGE = true;
    private NovusFactory mockNovusFactory;
    private Novus mockNovus;
    private TOC mockToc;
    private TOCNode mockTocRootNode;
    private TOCNode mockTocNode;
    private TocServiceImpl tocService;
    private File tocDir;
    private static Logger LOG = LogManager.getLogger(TocServiceTest.class);
    private NovusUtility mockNovusUtility;
    private ExcludeDocument mockExcludeDocument;
    private GatherResponse gatherResponse;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        mockNovusFactory = EasyMock.createMock(NovusFactory.class);
        mockNovus = EasyMock.createMock(Novus.class);
        mockToc = EasyMock.createMock(TOC.class);
        mockTocNode = EasyMock.createMock(TOCNode.class);
        mockTocRootNode = EasyMock.createMock(TOCNode.class);
        mockNovusUtility = EasyMock.createMock(NovusUtility.class);
        mockExcludeDocument = EasyMock.createMock(ExcludeDocument.class);

        // The object under test
        tocService = new TocServiceImpl();
        tocService.setNovusFactory(mockNovusFactory);
        tocService.setNovusUtility(mockNovusUtility);
        tocDir = temporaryFolder.newFolder("junit_toc");
    }

    @Test
    public void testGetTocDataFromNovus() throws Exception {
        final File tocFile = new File(tocDir, "TOC" + COLLECTION_NAME + TOC_GUID + EBConstants.XML_FILE_EXTENSION);

        // Record expected calls
        initiateMocks("3");

        mockTocRootNode = mockTocNode;

        final TOCNode[] children = getChildNodes(5, 'a', 999).toArray(new TOCNode[] {});

        EasyMock.expect(mockToc.getNode(TOC_GUID)).andReturn(mockTocRootNode);
        EasyMock.expect(mockTocNode.getName()).andReturn(" &lt; Root &amp;  &quot; Node&apos;s &gt; ").times(2);
        EasyMock.expect(mockTocNode.getDocGuid()).andReturn(null).anyTimes();
        EasyMock.expect(mockTocNode.getGuid()).andReturn("tocGuid").anyTimes();
        EasyMock.expect(mockTocNode.getChildrenCount()).andReturn(5).anyTimes();
        EasyMock.expect(mockTocNode.getChildren()).andReturn(children).anyTimes();

        mockNovus.shutdownMQ();
        tocDir.mkdirs();

        // Set up for replay
        replay();

        try {
            tocService.findTableOfContents(TOC_GUID, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            // Temporary file will clean up after itself.
        }

        final String tocFileContents = FileUtils.readFileToString(tocFile);
        LOG.debug("tocFileContents =" + tocFileContents);
        assertTrue(tocFileContents != null);

        final StringBuffer expectedTocContent = new StringBuffer(1000);

        expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        expectedTocContent.append("<EBook>\r\n");
        expectedTocContent
            .append("<EBookToc><Name> &lt; Root &amp;  &quot; Node&apos;s &gt; </Name><Guid>tocGuid</Guid>\r\n");
        expectedTocContent
            .append("<EBookToc><Name>Child 0a</Name><Guid>TOC_UUID_0a</Guid><DocumentGuid>UUID_0a</DocumentGuid>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 0b</Name><Guid>TOC_UUID_0b</Guid><DocumentGuid>UUID_0b</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 1a</Name><Guid>TOC_UUID_1a</Guid><DocumentGuid>UUID_1a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 2a</Name><Guid>TOC_UUID_2a</Guid><DocumentGuid>UUID_2a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 3a</Name><Guid>TOC_UUID_3a</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 4a</Name><Guid>TOC_UUID_4a</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append("</EBook>\r\n");
        LOG.debug("expectedTocContent =" + expectedTocContent.toString());

        Assert.assertEquals(expectedTocContent.toString(), tocFileContents);
        EasyMock.verify(mockNovusFactory);
        EasyMock.verify(mockNovus);
        EasyMock.verify(mockToc);
    }

    @Test
    public void testGetTocDataFromNovusWithNoName() throws Exception {
        final File tocFile = new File(tocDir, "TOC" + COLLECTION_NAME + TOC_GUID + EBConstants.XML_FILE_EXTENSION);

        // Record expected calls
        initiateMocks("3");

        mockTocRootNode = mockTocNode;

        EasyMock.expect(mockToc.getNode(TOC_GUID)).andReturn(mockTocRootNode);
        EasyMock.expect(mockTocNode.getName()).andReturn(null).times(2); // No Name node
        EasyMock.expect(mockTocNode.getDocGuid()).andReturn(null).anyTimes();
        EasyMock.expect(mockTocNode.getGuid()).andReturn("tocGuid").anyTimes();
        EasyMock.expect(mockTocNode.getChildrenCount()).andReturn(0).anyTimes();
        EasyMock.expect(mockTocNode.getChildren()).andReturn(null).anyTimes();

        mockNovus.shutdownMQ();
        tocDir.mkdirs();

        // Set up for replay
        replay();

        try {
            tocService.findTableOfContents(TOC_GUID, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
        } catch (final Exception e) {
            LOG.debug(e.getMessage());
            Assert.assertEquals("Failed with empty node Name for guid tocGuid", e.getMessage());
        } finally {
            // Temporary file will clean up after itself.
        }

        EasyMock.verify(mockNovusFactory);
        EasyMock.verify(mockNovus);
        EasyMock.verify(mockToc);
    }

    @Test
    public void testGetTocDataSkipNodesFromNovus() throws Exception {
        final File tocFile = new File(tocDir, "TOC" + COLLECTION_NAME + TOC_GUID + EBConstants.XML_FILE_EXTENSION);

        // Record expected calls
        initiateMocks("1");

        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId((long) 1);
        jobstats.setGatherTocDocCount(1);
        jobstats.setGatherTocNodeCount(1);
        jobstats.setPublishStatus("TEST");

        mockTocRootNode = mockTocNode;

        final TOCNode[] children = getChildNodes(5, 'a', 1).toArray(new TOCNode[] {});

        EasyMock.expect(mockToc.getNode(TOC_GUID)).andReturn(mockTocRootNode);
        EasyMock.expect(mockTocNode.getName()).andReturn(" &lt; Root &amp;  &quot; Node&apos;s &gt; ").times(2);
        EasyMock.expect(mockTocNode.getDocGuid()).andReturn(null).anyTimes();
        EasyMock.expect(mockTocNode.getGuid()).andReturn("tocGuid").anyTimes();
        EasyMock.expect(mockTocNode.getChildrenCount()).andReturn(5).anyTimes();
        EasyMock.expect(mockTocNode.getChildren()).andReturn(children).anyTimes();

        mockNovus.shutdownMQ();
        tocDir.mkdirs();

        // Set up for replay
        replay();

        try {
            gatherResponse =
                tocService.findTableOfContents(TOC_GUID, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            // Temporary file will clean up after itself.
        }
        LOG.debug(gatherResponse);
        assertTrue(gatherResponse.getDocCount() == 4);
        assertTrue(gatherResponse.getNodeCount() == 7);

        final String tocFileContents = FileUtils.readFileToString(tocFile);
        LOG.debug("tocFileContents =" + tocFileContents);
        assertTrue(tocFileContents != null);

        final StringBuffer expectedTocContent = new StringBuffer(1000);

        expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        expectedTocContent.append("<EBook>\r\n");
        expectedTocContent
            .append("<EBookToc><Name> &lt; Root &amp;  &quot; Node&apos;s &gt; </Name><Guid>tocGuid</Guid>\r\n");
        expectedTocContent
            .append("<EBookToc><Name>Child 0a</Name><Guid>TOC_UUID_0a</Guid><DocumentGuid>UUID_0a</DocumentGuid>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 0b</Name><Guid>TOC_UUID_0b</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 1a</Name><Guid>TOC_UUID_1a</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 2a</Name><Guid>TOC_UUID_2a</Guid><DocumentGuid>UUID_2a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 3a</Name><Guid>TOC_UUID_3a</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 4a</Name><Guid>TOC_UUID_4a</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append("</EBook>\r\n");
        LOG.debug("expectedTocContent =" + expectedTocContent.toString());

        Assert.assertEquals(expectedTocContent.toString(), tocFileContents);
        EasyMock.verify(mockNovusFactory);
        EasyMock.verify(mockNovus);
        EasyMock.verify(mockToc);
    }

    @Test(expected = GatherException.class)
    public void testGetTocDataWithNovusException() throws Exception {
        final File tocFile = new File(tocDir, "FAIL" + COLLECTION_NAME + TOC_GUID + EBConstants.XML_FILE_EXTENSION);

        final NovusException mockNovusException = new MockNovusException();

        // Record expected calls
        EasyMock.expect(mockNovusFactory.createNovus(IS_FINAL_STAGE)).andReturn(mockNovus);
        EasyMock.expect(mockNovusUtility.getDocRetryCount()).andReturn("3").times(2);
        EasyMock.expect(mockNovusUtility.getNortRetryCount()).andReturn("3").times(2);
        EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("3").times(2);
        EasyMock.expect(mockNovusUtility.handleException(mockNovusException, 0, 3)).andReturn(3);
        EasyMock.expect(mockToc.getCollection()).andReturn("collection");
        EasyMock.expect(mockNovus.getTOC()).andReturn(mockToc);
        mockToc.setCollection(COLLECTION_NAME);
        mockToc.setShowChildrenCount(true);
        mockNovus.shutdownMQ();
        EasyMock.expect(mockToc.getNode(TOC_GUID)).andThrow(mockNovusException);

        // Replay
        EasyMock.replay(mockNovusFactory);
        EasyMock.replay(mockNovus);
        EasyMock.replay(mockToc);
        EasyMock.replay(mockNovusUtility);

        try {
            tocService.findTableOfContents(TOC_GUID, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
        } finally {
            FileUtils.deleteQuietly(tocFile);
        }

        EasyMock.verify(mockNovusFactory);
        EasyMock.verify(mockNovus);
        EasyMock.verify(mockToc);
        EasyMock.replay(mockTocNode);
    }

    /**
     * Creates child nodes.this is a helper method.
     * If you change the number of children, be sure to modify the fillTOCNodes above.
     * @param maxChildren number of children to create
     * @return List of TOCNodes with child expects set.
     * @throws java.io.IOException
     */
    private List<TOCNode> getChildNodes(final int maxChildren, final char prefix, final int skipDoc) throws Exception {
        final List<TOCNode> childNodes = new ArrayList<>();

        for (int i = 0; i < maxChildren; i++) {
            final TOCNode child = EasyMock.createMock(TOCNode.class);
            EasyMock.expect(child.getName()).andReturn("Child " + i + prefix).anyTimes();
            if (i == skipDoc) {
                EasyMock.expect(child.getDocGuid()).andReturn(null).times(2);
            } else {
                EasyMock.expect(child.getDocGuid()).andReturn("UUID_" + i + prefix).times(2);
            }
            EasyMock.expect(child.getGuid()).andReturn("TOC_UUID_" + i + prefix).times(2);

            if (i == 0 && prefix == 'a') {
                final TOCNode[] tocChildren = getChildNodes(1, 'b', skipDoc - 1).toArray(new TOCNode[] {});
                EasyMock.expect(child.getChildren()).andReturn(tocChildren).anyTimes();
                EasyMock.expect(child.getChildrenCount()).andReturn(1).anyTimes();
            } else {
                EasyMock.expect(child.getChildrenCount()).andReturn(0).anyTimes();
                EasyMock.expect(child.getChildren()).andReturn(null).anyTimes();
            }

            EasyMock.replay(child);
            childNodes.add(child);
        }
        return childNodes;
    }

    @Ignore
    public void testGetTocDataExcludedDocsFromNovus() throws Exception {
        final File tocFile = new File(tocDir, "TOC" + COLLECTION_NAME + TOC_GUID + EBConstants.XML_FILE_EXTENSION);

        // Record expected calls
        initiateMocks("1");

        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId((long) 1);
        jobstats.setGatherTocDocCount(1);
        jobstats.setGatherTocNodeCount(1);
        jobstats.setPublishStatus("TEST");

        mockTocRootNode = mockTocNode;

        final TOCNode[] children = getChildNodes(5, 'a', 1).toArray(new TOCNode[] {});
        EasyMock.expect(mockToc.getNode(TOC_GUID)).andReturn(mockTocRootNode);
        EasyMock.expect(mockTocNode.getName()).andReturn(" &lt; Root &amp;  &quot; Node&apos;s &gt; ").times(2);
        EasyMock.expect(mockTocNode.getDocGuid()).andReturn(null).anyTimes();
        EasyMock.expect(mockTocNode.getGuid()).andReturn("tocGuid").anyTimes();
        EasyMock.expect(mockTocNode.getChildrenCount()).andReturn(5).anyTimes();
        EasyMock.expect(mockTocNode.getChildren()).andReturn(children).anyTimes();

        mockNovus.shutdownMQ();
        tocDir.mkdirs();

        // Set up for replay
        replay();

        try {
            final List<ExcludeDocument> excludeDocuments = new ArrayList<>();
            mockExcludeDocument.setDocumentGuid("UUID_1a");
            excludeDocuments.add(mockExcludeDocument);
            gatherResponse = tocService.findTableOfContents(
                TOC_GUID,
                COLLECTION_NAME,
                tocFile,
                excludeDocuments,
                null,
                IS_FINAL_STAGE,
                null,
                0);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            // Temporary file will clean up after itself.
        }
        LOG.debug(gatherResponse);
        assertTrue(gatherResponse.getDocCount() == 3);
        assertTrue(gatherResponse.getNodeCount() == 7);

        final String tocFileContents = FileUtils.readFileToString(tocFile);
        LOG.debug("tocFileContents =" + tocFileContents);
        assertTrue(tocFileContents != null);

        final StringBuffer expectedTocContent = new StringBuffer(1000);

        expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        expectedTocContent.append("<EBook>\r\n");
        expectedTocContent
            .append("<EBookToc><Name> &lt; Root &amp;  &quot; Node&apos;s &gt; </Name><Guid>tocGuid</Guid>\r\n");
        expectedTocContent
            .append("<EBookToc><Name>Child 0a</Name><Guid>TOC_UUID_0a</Guid><DocumentGuid>UUID_0a</DocumentGuid>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 0b</Name><Guid>TOC_UUID_0b</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 1a</Name><Guid>TOC_UUID_1a</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 3a</Name><Guid>TOC_UUID_3a</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append(
            "<EBookToc><Name>Child 4a</Name><Guid>TOC_UUID_4a</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
        expectedTocContent.append("</EBookToc>\r\n");
        expectedTocContent.append("</EBook>\r\n");
        LOG.debug("expectedTocContent =" + expectedTocContent.toString());

        Assert.assertEquals(expectedTocContent.toString(), tocFileContents);
        EasyMock.verify(mockNovusFactory);
        EasyMock.verify(mockNovus);
        EasyMock.verify(mockToc);
    }

    private List<TOCNode> getChildNodeswithDuplicateToc(final int maxChildren, final char prefix, final int skipDoc)
        throws Exception {
        final List<TOCNode> childNodes = new ArrayList<>();

        for (int i = 0; i < maxChildren; i++) {
            final TOCNode child = EasyMock.createMock(TOCNode.class);
            EasyMock.expect(child.getName()).andReturn("Child " + i + prefix).anyTimes();

            if (i == skipDoc) {
                EasyMock.expect(child.getDocGuid()).andReturn(null).times(2);
            } else {
                EasyMock.expect(child.getDocGuid()).andReturn("UUID_" + i + prefix).times(2);
            }
            EasyMock.expect(child.getGuid()).andReturn("TABLE_OF_CONTENTS_DUPLICATE_UUID_" + i + prefix).times(2);

            if (i == 0 && prefix == 'a') {
                final TOCNode[] tocChildren = getChildNodes(1, 'b', skipDoc - 1).toArray(new TOCNode[] {});
                EasyMock.expect(child.getChildren()).andReturn(tocChildren).anyTimes();
                EasyMock.expect(child.getChildrenCount()).andReturn(1).anyTimes();
            } else {
                EasyMock.expect(child.getChildrenCount()).andReturn(0).anyTimes();
                EasyMock.expect(child.getChildren()).andReturn(null).anyTimes();
            }

            EasyMock.replay(child);
            childNodes.add(child);
        }
        return childNodes;
    }

    @Test
    public void testSplitBook() throws Exception {
        final File tocFile = new File(tocDir, "TOC" + COLLECTION_NAME + TOC_GUID + EBConstants.XML_FILE_EXTENSION);

        System.out.println("tocFile " + tocFile.getAbsolutePath());

        final List<String> splitTocGuidList = new ArrayList<>();
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_2";
        splitTocGuidList.add(guid1);

        // Record expected calls
        initiateMocks("3");

        mockTocRootNode = mockTocNode;

        final TOCNode[] children = getChildNodeswithDuplicateToc(5, 'a', 999).toArray(new TOCNode[] {});

        //mock duplicate toc guid
        //children[0].
        System.out.println("children-----" + children[0].getGuid());
        EasyMock.expect(mockToc.getNode(TOC_GUID)).andReturn(mockTocRootNode);
        EasyMock.expect(mockTocNode.getName()).andReturn(" &lt; Root &amp;  &quot; Node&apos;s &gt; ").times(2);
        EasyMock.expect(mockTocNode.getDocGuid()).andReturn(null).anyTimes();
        EasyMock.expect(mockTocNode.getGuid()).andReturn("tocGuid").anyTimes();
        EasyMock.expect(mockTocNode.getChildrenCount()).andReturn(5).anyTimes();
        EasyMock.expect(mockTocNode.getChildren()).andReturn(children).anyTimes();

        mockNovus.shutdownMQ();
        tocDir.mkdirs();

        // Set up for replay
        replay();

        try {
            GatherResponse gatherResponse = tocService.findTableOfContents(
                TOC_GUID,
                COLLECTION_NAME,
                tocFile,
                null,
                null,
                IS_FINAL_STAGE,
                splitTocGuidList,
                0);
            System.out.println("tocService " + gatherResponse.getDuplicateTocGuids());
            Assert.assertEquals(1, gatherResponse.getDuplicateTocGuids().size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            // Temporary file will clean up after itself.
        }

        final String tocFileContents = FileUtils.readFileToString(tocFile);
        LOG.debug("tocFileContents =" + tocFileContents);
        assertTrue(tocFileContents != null);

        EasyMock.verify(mockNovusFactory);
        EasyMock.verify(mockNovus);
        EasyMock.verify(mockToc);
    }

    @SneakyThrows
    private void initiateMocks(final String tocRetryCount) {
        EasyMock.expect(mockNovusFactory.createNovus(IS_FINAL_STAGE)).andReturn(mockNovus);
        EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn(tocRetryCount).anyTimes();
        EasyMock.expect(mockNovus.getTOC()).andReturn(mockToc);
        mockToc.setCollection(COLLECTION_NAME);
        mockToc.setShowChildrenCount(true);
    }

    private void replay() {
        EasyMock.replay(mockNovusFactory);
        EasyMock.replay(mockNovus);
        EasyMock.replay(mockToc);
        EasyMock.replay(mockTocNode);
        EasyMock.replay(mockNovusUtility);
    }
}
