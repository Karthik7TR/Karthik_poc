package com.thomsonreuters.uscl.ereader.deliver.step;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public final class ArchiveBookTest
{
    private ArchiveBook archiveBook;
    private String currentVersion;
    private SplitNodeInfo splitNodeInfo;

    @Before
    public void setUp()
    {
        archiveBook = new ArchiveBook();
        currentVersion = "V1";
        splitNodeInfo = new SplitNodeInfo();
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setEbookDefinitionId(Long.valueOf(1));
        splitNodeInfo.setBookDefinition(bookDefinition);
        splitNodeInfo.setBookVersionSubmitted("V1");
        splitNodeInfo.setSpitBookTitle("splitBookTitle");
        splitNodeInfo.setSplitNodeGuid("splitNodeGuid");
    }

    @After
    public void tearDown()
    {
        //Intentionally left blank
    }

    @Test
    public void testEmptyPersistedList()
    {
        final List<SplitNodeInfo> currentsplitNodeList = new ArrayList<>();
        final List<SplitNodeInfo> persistedSplitNodes = new ArrayList<>();
        final boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);
        assertTrue(!actual);
    }

    @Test
    public void testEqualLists()
    {
        final List<SplitNodeInfo> currentsplitNodeList = new ArrayList<>();
        currentsplitNodeList.add(splitNodeInfo);
        final List<SplitNodeInfo> persistedSplitNodes = new ArrayList<>();
        persistedSplitNodes.add(splitNodeInfo);
        final boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);
        assertTrue(actual);
    }

    @Test
    public void testSyncList1()
    {
        final List<SplitNodeInfo> currentsplitNodeList = new ArrayList<>();
        currentsplitNodeList.add(splitNodeInfo);
        final List<SplitNodeInfo> persistedSplitNodes = new ArrayList<>();
        final boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);
        assertTrue(!actual);
    }

    @Test
    public void testSyncList2()
    {
        final List<SplitNodeInfo> currentsplitNodeList = new ArrayList<>();
        final List<SplitNodeInfo> persistedSplitNodes = null;
        final boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);
        assertTrue(!actual);
    }

    @Test
    public void testSyncList3()
    {
        final List<SplitNodeInfo> currentsplitNodeList = new ArrayList<>();
        final List<SplitNodeInfo> persistedSplitNodes = new ArrayList<>();
        splitNodeInfo.setBookVersionSubmitted("V0");
        persistedSplitNodes.add(splitNodeInfo);
        final boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);
        assertTrue(!actual);
    }

    @Test
    public void testSyncList6()
    {
        final List<SplitNodeInfo> currentsplitNodeList = new ArrayList<>();
        final List<SplitNodeInfo> persistedSplitNodes = new ArrayList<>();
        final SplitNodeInfo splitNodeInfo2 = new SplitNodeInfo();
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setEbookDefinitionId(Long.valueOf(1));
        splitNodeInfo2.setBookDefinition(bookDefinition);
        splitNodeInfo2.setBookVersionSubmitted("V1");
        splitNodeInfo2.setSpitBookTitle("splitBookTitle");
        splitNodeInfo2.setSplitNodeGuid("splitNodeGuid");
        final boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);
        assertTrue(!actual);
    }

    /**
     * If currentNodes has less Rows than previousNodes and versions are same
     * then the new list should get only currentNodes
     */
    @Test
    public void testSyncList4()
    {
        final List<SplitNodeInfo> currentsplitNodeList = new ArrayList<>();
        final List<SplitNodeInfo> persistedSplitNodes = new ArrayList<>();
        persistedSplitNodes.add(splitNodeInfo);
        final SplitNodeInfo splitNodeInfo2 = new SplitNodeInfo();
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setEbookDefinitionId(Long.valueOf(1));
        splitNodeInfo2.setBookDefinition(bookDefinition);
        splitNodeInfo2.setBookVersionSubmitted("V1");
        splitNodeInfo2.setSpitBookTitle("splitBookTitle2");
        splitNodeInfo2.setSplitNodeGuid("splitNodeGuid");
        persistedSplitNodes.add(splitNodeInfo2);
        final boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);
        assertTrue(!actual);
    }

    /**
     * If versions are different
     * then the new list should get currentNodes and previousNodes
     */
    @Test
    public void testSyncList5()
    {
        final List<SplitNodeInfo> currentsplitNodeList = new ArrayList<>();
        currentsplitNodeList.add(splitNodeInfo);
        final List<SplitNodeInfo> persistedSplitNodes = new ArrayList<>();
        final SplitNodeInfo splitNodeInfo2 = new SplitNodeInfo();
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setEbookDefinitionId(Long.valueOf(1));
        splitNodeInfo2.setBookDefinition(bookDefinition);
        splitNodeInfo2.setBookVersionSubmitted("V0");
        splitNodeInfo2.setSpitBookTitle("splitBookTitle");
        splitNodeInfo2.setSplitNodeGuid("splitNodeGuid");
        persistedSplitNodes.add(splitNodeInfo2);
        final boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);
        assertTrue(!actual);
    }

    @Test
    public void testSyncList7()
    {
        final List<SplitNodeInfo> currentsplitNodeList = new ArrayList<>();
        currentsplitNodeList.add(splitNodeInfo);
        final List<SplitNodeInfo> persistedSplitNodes = new ArrayList<>();
        final SplitNodeInfo splitNodeInfo2 = new SplitNodeInfo();
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setEbookDefinitionId(Long.valueOf(1));
        splitNodeInfo2.setBookDefinition(bookDefinition);
        splitNodeInfo2.setBookVersionSubmitted("V1");
        splitNodeInfo2.setSpitBookTitle("splitBookTitle");
        splitNodeInfo2.setSplitNodeGuid("splitNodeGuid");
        persistedSplitNodes.add(splitNodeInfo2);
        final boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);
        assertTrue(actual);
    }
}
