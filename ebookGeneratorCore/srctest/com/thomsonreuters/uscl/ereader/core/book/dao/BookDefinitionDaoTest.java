package com.thomsonreuters.uscl.ereader.core.book.dao;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.junit.Before;
import org.junit.Test;

public final class BookDefinitionDaoTest
{
    private static final long BOOK_KEY = 1L;
    private static final BookDefinition BOOK_DEFINITION = new BookDefinition();

    private SessionFactory mockSessionFactory;
    private org.hibernate.Session mockSession;
    private BookDefinitionDaoImpl dao;
    private Criteria mockCriteria;

    @Before
    public void setUp()
    {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(org.hibernate.Session.class);
        mockCriteria = EasyMock.createMock(org.hibernate.Criteria.class);
        dao = new BookDefinitionDaoImpl(mockSessionFactory);
    }

    @Test
    public void testFindBookDefinition()
    {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(BookDefinition.class, BOOK_KEY)).andReturn(BOOK_DEFINITION);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        final BookDefinition actualBookDefinition = dao.findBookDefinitionByEbookDefId(BOOK_KEY);
        assertEquals(BOOK_DEFINITION, actualBookDefinition);
        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }

    @Test
    public void testSaveSplitNodeInfo()
    {
        BookDefinition bookDefinition = new BookDefinition();

        final List<SplitNodeInfo> splitNodes = new ArrayList<>();
        final SplitNodeInfo splitNodeInfo1 = new SplitNodeInfo();
        splitNodeInfo1.setBookDefinition(bookDefinition);
        splitNodeInfo1.setBookVersionSubmitted("1");
        splitNodeInfo1.setSpitBookTitle("part1");
        splitNodeInfo1.setSplitNodeGuid("tocGuid1");
        splitNodes.add(splitNodeInfo1);
        final SplitNodeInfo splitNodeInfo2 = new SplitNodeInfo();
        splitNodeInfo2.setBookDefinition(bookDefinition);
        splitNodeInfo2.setBookVersionSubmitted("1");
        splitNodeInfo2.setSpitBookTitle("part2");
        splitNodeInfo2.setSplitNodeGuid("tocGuid2");
        splitNodes.add(splitNodeInfo2);

        bookDefinition.setSplitNodes(splitNodes);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(BookDefinition.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.add((Criterion) EasyMock.anyObject())).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.uniqueResult()).andReturn(bookDefinition);
        EasyMock.expect(mockSession.merge(EasyMock.anyObject())).andReturn(bookDefinition);
        mockSession.flush();
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<SplitNodeInfo> splitNodeInfoList = new ArrayList<>();
        final SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
        splitNodeInfo.setBookDefinition(bookDefinition);
        splitNodeInfo.setBookVersionSubmitted("2");
        splitNodeInfo.setSpitBookTitle("part1");
        splitNodeInfo.setSplitNodeGuid("tocGuid1");
        splitNodeInfoList.add(splitNodeInfo);
        bookDefinition = dao.saveBookDefinition(BOOK_KEY, splitNodeInfoList, "2");

        final List<SplitNodeInfo> expectedSplitNodes = new ArrayList<>();
        expectedSplitNodes.addAll(splitNodeInfoList);
        expectedSplitNodes.addAll(splitNodes);

        bookDefinition.getSplitNodes();

        assertThat(bookDefinition.getSplitNodesAsList(), hasSize(expectedSplitNodes.size()));
        assertThat(bookDefinition.getSplitNodesAsList(), hasItems(expectedSplitNodes.toArray(new SplitNodeInfo[0])));
    }

    @Test
    public void testSaveSplitNodeInfo2()
    {
        BookDefinition bookDefinition = new BookDefinition();

        final List<SplitNodeInfo> splitNodes = new ArrayList<>();
        final SplitNodeInfo splitNodeInfo1 = new SplitNodeInfo();
        splitNodeInfo1.setBookDefinition(bookDefinition);
        splitNodeInfo1.setBookVersionSubmitted("1");
        splitNodeInfo1.setSpitBookTitle("part1");
        splitNodeInfo1.setSplitNodeGuid("tocGuid1");
        splitNodes.add(splitNodeInfo1);
        final SplitNodeInfo splitNodeInfo2 = new SplitNodeInfo();
        splitNodeInfo2.setBookDefinition(bookDefinition);
        splitNodeInfo2.setBookVersionSubmitted("1");
        splitNodeInfo2.setSpitBookTitle("part2");
        splitNodeInfo2.setSplitNodeGuid("tocGuid2");
        splitNodes.add(splitNodeInfo2);

        bookDefinition.setSplitNodes(splitNodes);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(BookDefinition.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.add((Criterion) EasyMock.anyObject())).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.uniqueResult()).andReturn(bookDefinition);
        EasyMock.expect(mockSession.merge(EasyMock.anyObject())).andReturn(bookDefinition);
        mockSession.flush();
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<SplitNodeInfo> splitNodeInfoList = new ArrayList<>();
        final SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
        splitNodeInfo.setBookDefinition(bookDefinition);
        splitNodeInfo.setBookVersionSubmitted("1");
        splitNodeInfo.setSpitBookTitle("part1");
        splitNodeInfo.setSplitNodeGuid("tocGuid1");
        splitNodeInfoList.add(splitNodeInfo);
        bookDefinition = dao.saveBookDefinition(BOOK_KEY, splitNodeInfoList, "1");

        final List<SplitNodeInfo> expectedSplitNodes = new ArrayList<>();
        expectedSplitNodes.addAll(splitNodeInfoList);

        bookDefinition.getSplitNodes();
        assertEquals(bookDefinition.getSplitNodesAsList(), expectedSplitNodes);
    }

    @Test
    public void testSaveSplitNodeInfo3()
    {
        BookDefinition bookDefinition = new BookDefinition();

        final List<SplitNodeInfo> splitNodes = new ArrayList<>();
        final SplitNodeInfo splitNodeInfo1 = new SplitNodeInfo();
        splitNodeInfo1.setBookDefinition(bookDefinition);
        splitNodeInfo1.setBookVersionSubmitted("1");
        splitNodeInfo1.setSpitBookTitle("part1");
        splitNodeInfo1.setSplitNodeGuid("tocGuid1");
        splitNodes.add(splitNodeInfo1);

        bookDefinition.setSplitNodes(splitNodes);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(BookDefinition.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.add((Criterion) EasyMock.anyObject())).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.uniqueResult()).andReturn(bookDefinition);
        EasyMock.expect(mockSession.merge(EasyMock.anyObject())).andReturn(bookDefinition);
        mockSession.flush();
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<SplitNodeInfo> splitNodeInfoList = new ArrayList<>();
        final SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
        splitNodeInfo.setBookDefinition(bookDefinition);
        splitNodeInfo.setBookVersionSubmitted("1");
        splitNodeInfo.setSpitBookTitle("part1");
        splitNodeInfo.setSplitNodeGuid("tocGuid1");
        splitNodeInfoList.add(splitNodeInfo);
        bookDefinition = dao.saveBookDefinition(BOOK_KEY, splitNodeInfoList, "1");

        final List<SplitNodeInfo> expectedSplitNodes = new ArrayList<>();
        expectedSplitNodes.addAll(splitNodeInfoList);

        bookDefinition.getSplitNodes();
        assertEquals(bookDefinition.getSplitNodesAsList(), expectedSplitNodes);
    }
}
