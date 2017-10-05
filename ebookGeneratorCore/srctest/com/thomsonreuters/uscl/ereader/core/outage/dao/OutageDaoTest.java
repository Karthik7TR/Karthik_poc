package com.thomsonreuters.uscl.ereader.core.outage.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class OutageDaoTest {
    private List<PlannedOutage> PLANNED_OUTAGE_LIST;

    private SessionFactory mockSessionFactory;
    private org.hibernate.Session mockSession;
    private Criteria mockCriteria;
    private OutageDaoImpl dao;
    private Query mockQuery;

    @Before
    public void setUp() {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(org.hibernate.Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);
        dao = new OutageDaoImpl(mockSessionFactory);
        mockQuery = EasyMock.createMock(org.hibernate.Query.class);

        final PlannedOutage outage = new PlannedOutage();
        PLANNED_OUTAGE_LIST = new ArrayList<>();
        PLANNED_OUTAGE_LIST.add(outage);
    }

    @Test
    public void testGetAllActiveAndScheduledPlannedOutages() {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(PlannedOutage.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.add(Restrictions.ge("endTime", EasyMock.anyObject(Date.class))))
            .andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(PLANNED_OUTAGE_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<PlannedOutage> actual = dao.getAllActiveAndScheduledPlannedOutages();
        Assert.assertEquals(PLANNED_OUTAGE_LIST, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testgetAllPlannedOutagesForType() {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createQuery("select p from PlannedOutage p where p.outageType=1"))
            .andReturn(mockQuery);
        EasyMock.expect(mockQuery.list()).andReturn(PLANNED_OUTAGE_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);
        EasyMock.replay(mockQuery);
        final List<PlannedOutage> actual = dao.getAllPlannedOutagesForType(Long.valueOf(1));
        Assert.assertEquals(PLANNED_OUTAGE_LIST, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testGetAllPlannedOutages() {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(PlannedOutage.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(PLANNED_OUTAGE_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<PlannedOutage> actual = dao.getAllPlannedOutages();
        Assert.assertEquals(PLANNED_OUTAGE_LIST, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void TestFindPlannedOutageByPrimaryKey() {
        final Long id = 99L;
        final PlannedOutage outage = new PlannedOutage();
        outage.setId(id);

        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(PlannedOutage.class, id)).andReturn(outage);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);

        final PlannedOutage actual = dao.findPlannedOutageByPrimaryKey(id);
        Assert.assertEquals(outage, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }
}
