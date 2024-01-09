package com.thomsonreuters.uscl.ereader.core.outage.dao;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
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
        testGetPlannedOutages(
            () -> {
                expect(mockCriteria.add(Restrictions.ge("endTime", anyObject(Date.class)))).andReturn(mockCriteria);
                expect(mockCriteria.addOrder(anyObject(Order.class))).andReturn(mockCriteria);
            },
            () -> dao.getAllActiveAndScheduledPlannedOutages()
        );
    }

    @Test
    public void testGetActiveAndScheduledPlannedOutagesForType() {
        testGetPlannedOutages(
            () -> {
                expect(mockCriteria.add(Restrictions.eq("outageType", anyObject(OutageType.class)))).andReturn(mockCriteria);
                expect(mockCriteria.add(Restrictions.ge("endTime", anyObject(Date.class)))).andReturn(mockCriteria);
                expect(mockCriteria.addOrder(anyObject(Order.class))).andReturn(mockCriteria);
            },
            () -> dao.getActiveAndScheduledPlannedOutagesForType(1L)
        );
    }

    @Test
    public void testGetInactivePlannedOutagesForType() {
        testGetPlannedOutages(
            () -> {
                expect(mockCriteria.add(Restrictions.eq("outageType", anyObject(OutageType.class)))).andReturn(mockCriteria);
                expect(mockCriteria.add(Restrictions.le("endTime", anyObject(Date.class)))).andReturn(mockCriteria);
                expect(mockCriteria.addOrder(anyObject(Order.class))).andReturn(mockCriteria);
            },
            () -> dao.getInactivePlannedOutagesForType(1L)
        );
    }

    @Test
    public void testGetAllPlannedOutages() {
        testGetPlannedOutages(
            () -> expect(mockCriteria.addOrder(anyObject(Order.class))).andReturn(mockCriteria),
            () -> dao.getAllPlannedOutages()
        );
    }

    @Test
    public void testGetPlannedOutagesForType() {
        testGetPlannedOutages(
            () -> expect(mockCriteria.add(Restrictions.eq("outageType", anyObject(OutageType.class)))).andReturn(mockCriteria),
            () -> dao.getPlannedOutagesForType(new OutageType())
        );
    }

    private void testGetPlannedOutages(final Runnable expectRestrictions, final Supplier<List<PlannedOutage>> runCheckedMethod) {
        expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        expect(mockSession.createCriteria(PlannedOutage.class)).andReturn(mockCriteria);
        expectRestrictions.run();

        expect(mockCriteria.list()).andReturn(PLANNED_OUTAGE_LIST);
        replay(mockSessionFactory);
        replay(mockSession);
        replay(mockCriteria);
        final List<PlannedOutage> actual = runCheckedMethod.get();
        Assert.assertEquals(PLANNED_OUTAGE_LIST, actual);

        verify(mockSessionFactory);
        verify(mockSession);
        verify(mockCriteria);
    }

    @Test
    public void testFindPlannedOutageByPrimaryKey() {
        final Long id = 99L;
        final PlannedOutage outage = new PlannedOutage();
        outage.setId(id);

        expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        expect(mockSession.get(PlannedOutage.class, id)).andReturn(outage);
        replay(mockSessionFactory);
        replay(mockSession);

        final PlannedOutage actual = dao.findPlannedOutageByPrimaryKey(id);
        assertEquals(outage, actual);

        verify(mockSessionFactory);
        verify(mockSession);
    }

    @Test
    public void testSavePlannedOutages() {
        final List<PlannedOutage> outages = Arrays.asList(new PlannedOutage(), new PlannedOutage());

        expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        outages.forEach(outage -> mockSession.saveOrUpdate(outage));
        mockSession.flush();

        replay(mockSessionFactory);
        replay(mockSession);

        dao.savePlannedOutages(outages);

        verify(mockSessionFactory);
        verify(mockSession);
    }

    @Test
    public void testFindOutageTypeBySystemAndSubSystem() {
        final String system = "system";
        final String subSystem = "subSystem";
        final OutageType outageType = new OutageType();
        outageType.setSystem(system);
        outageType.setSubSystem(subSystem);

        expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        expect(mockSession.createCriteria(OutageType.class)).andReturn(mockCriteria);

        expect(mockCriteria.add(Restrictions.eq("system", anyObject(String.class)))).andReturn(mockCriteria);
        expect(mockCriteria.add(Restrictions.eq("subSystem", anyObject(String.class)))).andReturn(mockCriteria);
        expect(mockCriteria.list()).andReturn(Arrays.asList(outageType));

        replay(mockSessionFactory);
        replay(mockSession);
        replay(mockCriteria);

        final OutageType actual = dao.findOutageTypeBySystemAndSubSystem(system, subSystem);
        assertEquals(outageType, actual);

        verify(mockSessionFactory);
        verify(mockSession);
        verify(mockCriteria);
    }

    @Test
    public void testRemoveOutageType() {
        final OutageType outageType = new OutageType();
        expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        mockSession.saveOrUpdate(outageType);
        mockSession.flush();

        replay(mockSessionFactory);
        replay(mockSession);

        dao.removeOutageType(outageType);

        verify(mockSessionFactory);
        verify(mockSession);

        assertTrue(outageType.getRemoved());
    }
}
