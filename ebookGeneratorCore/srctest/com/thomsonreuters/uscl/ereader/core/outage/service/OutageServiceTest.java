package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDao;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class OutageServiceTest
{
    private List<PlannedOutage> PLANNED_OUTAGE_LIST;
    private OutageDao mockDao;
    private OutageServiceImpl service;

    @Before
    public void setUp()
    {
        mockDao = EasyMock.createMock(OutageDao.class);

        service = new OutageServiceImpl();
        service.setOutageDao(mockDao);

        final PlannedOutage outage = new PlannedOutage();
        PLANNED_OUTAGE_LIST = new ArrayList<>();
        PLANNED_OUTAGE_LIST.add(outage);
    }

    @Test
    public void testGetAllActiveAndScheduledPlannedOutages()
    {
        EasyMock.expect(mockDao.getAllActiveAndScheduledPlannedOutages()).andReturn(PLANNED_OUTAGE_LIST);
        EasyMock.replay(mockDao);

        final List<PlannedOutage> actual = service.getAllActiveAndScheduledPlannedOutages();
        Assert.assertEquals(PLANNED_OUTAGE_LIST, actual);

        EasyMock.verify(mockDao);
    }

    @Test
    public void testGetAllPlannedOutages()
    {
        EasyMock.expect(mockDao.getAllPlannedOutages()).andReturn(PLANNED_OUTAGE_LIST);
        EasyMock.replay(mockDao);

        final List<PlannedOutage> actual = service.getAllPlannedOutages();
        Assert.assertEquals(PLANNED_OUTAGE_LIST, actual);

        EasyMock.verify(mockDao);
    }

    @Test
    public void testFindPlannedOutageByPrimaryKey()
    {
        final Long id = 99L;
        final PlannedOutage outage = new PlannedOutage();
        outage.setId(id);

        EasyMock.expect(mockDao.findPlannedOutageByPrimaryKey(id)).andReturn(outage);
        EasyMock.replay(mockDao);

        final PlannedOutage actual = service.findPlannedOutageByPrimaryKey(id);
        Assert.assertEquals(outage, actual);

        EasyMock.verify(mockDao);
    }

    @Test
    public void testSaveOutageType()
    {
        final Long id = 99L;
        final OutageType outage = new OutageType();
        outage.setId(id);

        mockDao.saveOutageType(outage);
        EasyMock.expect(mockDao.findOutageTypeByPrimaryKey(id)).andReturn(outage);
        EasyMock.replay(mockDao);
        service.saveOutageType(outage);

        EasyMock.verify(mockDao);
    }
}
