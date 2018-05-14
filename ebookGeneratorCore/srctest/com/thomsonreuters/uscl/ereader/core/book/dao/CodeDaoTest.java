package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class CodeDaoTest {
    private final PubTypeCode PUB_TYPE_CODE = new PubTypeCode();
    private final Long PUB_TYPE_CODES_ID = Long.valueOf("1");
    private final List<PubTypeCode> ALL_PUB_TYPE_CODES = new ArrayList<>();

    private SessionFactory mockSessionFactory;
    private Session mockSession;
    private Criteria mockCriteria;
    private CodeDaoImpl dao;

    @Before
    public void setUp() {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);
        dao = new CodeDaoImpl(mockSessionFactory);

        PUB_TYPE_CODE.setId(PUB_TYPE_CODES_ID);
    }

    @Test
    public void testGetPubTypeCode() {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(PubTypeCode.class, PUB_TYPE_CODES_ID)).andReturn(PUB_TYPE_CODE);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);

        final PubTypeCode actual = dao.getPubTypeCodeById(PUB_TYPE_CODES_ID);
        final PubTypeCode expected = new PubTypeCode();
        expected.setId(PUB_TYPE_CODES_ID);

        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }

    @Test
    public void testGetAllPubTypeCodes() {
        ALL_PUB_TYPE_CODES.add(PUB_TYPE_CODE);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(PubTypeCode.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(ALL_PUB_TYPE_CODES);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<PubTypeCode> actual = dao.getAllPubTypeCodes();
        final List<PubTypeCode> expected = new ArrayList<>();
        expected.add(PUB_TYPE_CODE);
        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }
}
