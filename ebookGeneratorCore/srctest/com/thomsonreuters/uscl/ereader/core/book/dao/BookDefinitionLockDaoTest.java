package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class BookDefinitionLockDaoTest {
    private static final List<BookDefinitionLock> BOOK_LOCK_LIST = new ArrayList<>();
    private static final BookDefinitionLock BOOK_LOCK = new BookDefinitionLock();

    private SessionFactory mockSessionFactory;
    private org.hibernate.Session mockSession;
    private Criteria mockCriteria;
    private BookDefinitionLockDaoImpl dao;

    @Before
    public void setUp() {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(org.hibernate.Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);
        dao = new BookDefinitionLockDaoImpl(mockSessionFactory);
    }

    @Test
    public void testFindAllActiveLocks() {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(BookDefinitionLock.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.createAlias("ebookDefinition", "book")).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.add(Restrictions.ge("checkoutTimestamp", EasyMock.anyObject(Date.class))))
            .andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY))
            .andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(BOOK_LOCK_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<BookDefinitionLock> locks = dao.findAllActiveLocks();
        Assert.assertEquals(BOOK_LOCK_LIST, locks);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testFindLocksByBookDefinition() {
        final BookDefinition book = new BookDefinition();

        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(BookDefinitionLock.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.add(Restrictions.eq("ebookDefinition", EasyMock.anyObject(BookDefinition.class))))
            .andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(BOOK_LOCK_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<BookDefinitionLock> locks = dao.findLocksByBookDefinition(book);
        Assert.assertEquals(BOOK_LOCK_LIST, locks);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testFindBookDefinitionLockByPrimaryKey() {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(BookDefinitionLock.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.add(Restrictions.eq("ebookDefinitionLockId", EasyMock.anyObject(Long.class))))
            .andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.setFetchMode("ebookDefinition", FetchMode.JOIN)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.uniqueResult()).andReturn(BOOK_LOCK);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final BookDefinitionLock lock = dao.findBookDefinitionLockByPrimaryKey(1L);
        Assert.assertEquals(BOOK_LOCK, lock);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }
}
