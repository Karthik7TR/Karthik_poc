package com.thomsonreuters.uscl.ereader.core.outage.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDao;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public final class OutageServiceTest {
    private List<PlannedOutage> PLANNED_OUTAGE_LIST;
    private OutageDao mockDao;
    private OutageServiceImpl service;

    @Before
    public void setUp() {
        mockDao = EasyMock.createMock(OutageDao.class);

        service = new OutageServiceImpl(mockDao);

        final PlannedOutage outage = new PlannedOutage();
        PLANNED_OUTAGE_LIST = new ArrayList<>();
        PLANNED_OUTAGE_LIST.add(outage);
    }

    @Test
    public void testGetAllActiveAndScheduledPlannedOutages() {
        expect(mockDao.getAllActiveAndScheduledPlannedOutages()).andReturn(PLANNED_OUTAGE_LIST);
        replay(mockDao);

        final List<PlannedOutage> actual = service.getAllActiveAndScheduledPlannedOutages();
        assertEquals(PLANNED_OUTAGE_LIST, actual);

        verify(mockDao);
    }

    @Test
    public void testGetAllPlannedOutages() {
        expect(mockDao.getAllPlannedOutages()).andReturn(PLANNED_OUTAGE_LIST);
        replay(mockDao);

        final List<PlannedOutage> actual = service.getAllPlannedOutages();
        assertEquals(PLANNED_OUTAGE_LIST, actual);

        verify(mockDao);
    }

    @Test
    public void testGetActiveAndScheduledPlannedOutagesForType() {
        final Long outageTypeId = 1L;
        expect(mockDao.getActiveAndScheduledPlannedOutagesForType(outageTypeId)).andReturn(PLANNED_OUTAGE_LIST);
        replay(mockDao);

        final List<PlannedOutage> actual = service.getActiveAndScheduledPlannedOutagesForType(outageTypeId);
        assertEquals(PLANNED_OUTAGE_LIST, actual);

        verify(mockDao);
    }

    @Test
    public void testGetInactivePlannedOutagesForType() {
        final Long outageTypeId = 1L;
        expect(mockDao.getInactivePlannedOutagesForType(outageTypeId)).andReturn(PLANNED_OUTAGE_LIST);
        replay(mockDao);

        final List<PlannedOutage> actual = service.getInactivePlannedOutagesForType(outageTypeId);
        assertEquals(PLANNED_OUTAGE_LIST, actual);

        verify(mockDao);
    }

    @Test
    public void testFindPlannedOutageByPrimaryKey() {
        final Long id = 99L;
        final PlannedOutage outage = new PlannedOutage();
        outage.setId(id);

        expect(mockDao.findPlannedOutageByPrimaryKey(id)).andReturn(outage);
        replay(mockDao);

        final PlannedOutage actual = service.findPlannedOutageByPrimaryKey(id);
        assertEquals(outage, actual);

        verify(mockDao);
    }

    @Test
    public void testFindOutageTypeBySystemAndSubSystem() {
        final String system = "systemName";
        final String subSystem = "subSystemName";
        final OutageType outageType = new OutageType();

        expect(mockDao.findOutageTypeBySystemAndSubSystem(system, subSystem)).andReturn(outageType);
        replay(mockDao);

        final OutageType actual = service.findOutageTypeBySystemAndSubSystem(system, subSystem);
        assertEquals(outageType, actual);

        verify(mockDao);
    }

    @Test
    public void testSaveActiveOutage() {
        final Long id = 99L;
        final String system = "systemName";
        final String subSystem = "subSystemName";
        final OutageType outageType = getOutageType(id, system, subSystem);

        expect(mockDao.findOutageTypeBySystemAndSubSystem(system, subSystem)).andReturn(outageType);
        expect(mockDao.findOutageTypeByPrimaryKey(id)).andReturn(outageType);
        mockDao.saveOutageType(outageType);

        replay(mockDao);
        service.saveOutageType(outageType);

        verify(mockDao);
    }

    @Test
    public void testSaveActiveOutageTypeWithAnotherOutageTypeFound() {
        final Long id1 = 1L;
        final String system1 = "systemName1";
        final String subSystem1 = "subSystemName1";

        final Long id2 = 2L;
        final String system2 = "systemName2";
        final String subSystem2 = "subSystemName2";

        final OutageType outageTypeToSave = getOutageType(id1, system2, subSystem2);
        final OutageType outageTypeById = getOutageType(id1, system1, subSystem1);
        final OutageType outageTypeByName = getOutageType(id2, system2, subSystem2);

        expect(mockDao.findOutageTypeBySystemAndSubSystem(outageTypeToSave.getSystem(), outageTypeToSave.getSubSystem()))
                                                                        .andReturn(outageTypeByName);
        expect(mockDao.findOutageTypeByPrimaryKey(outageTypeToSave.getId()))
                                                                        .andReturn(outageTypeById);
        expect(mockDao.getPlannedOutagesForType(outageTypeByName)).andReturn(PLANNED_OUTAGE_LIST);
        mockDao.deleteOutageType(outageTypeByName);
        mockDao.savePlannedOutages(PLANNED_OUTAGE_LIST);
        mockDao.saveOutageType(outageTypeById);

        replay(mockDao);
        service.saveOutageType(outageTypeToSave);

        verify(mockDao);

        assertEquals(outageTypeById.getSystem(), system2);
        assertEquals(outageTypeById.getSubSystem(), subSystem2);
    }

    @Test
    public void testAttemptToCreateOutageWithNameWhichWasPreviouslyRemoved() {
        final Long id = 1L;
        final String system = "systemName";
        final String subSystem = "subSystemName";
        final OutageType outageTypeToCreate = getOutageType(null, system, subSystem);
        final OutageType outageTypeRemoved = getOutageType(id, system, subSystem);
        outageTypeRemoved.setRemoved(true);

        expect(mockDao.findOutageTypeBySystemAndSubSystem(system, subSystem)).andReturn(outageTypeRemoved);
        mockDao.saveOutageType(outageTypeRemoved);

        replay(mockDao);
        service.saveOutageType(outageTypeToCreate);

        verify(mockDao);
        assertFalse(outageTypeRemoved.getRemoved());
    }

    @Test
    public void testCreateOutage() {
        final String system = "systemName";
        final String subSystem = "subSystemName";
        final OutageType outageTypeToCreate = getOutageType(null, system, subSystem);

        expect(mockDao.findOutageTypeBySystemAndSubSystem(system, subSystem)).andReturn(null);
        mockDao.saveOutageType(outageTypeToCreate);

        replay(mockDao);
        service.saveOutageType(outageTypeToCreate);

        verify(mockDao);
    }

    @Test
    public void testRemoveOutageType() {
        final Long id = 99L;
        final OutageType outage = new OutageType();
        outage.setId(id);

        mockDao.removeOutageType(outage);
        expect(mockDao.findOutageTypeByPrimaryKey(id)).andReturn(outage);
        replay(mockDao);
        service.removeOutageType(id);

        verify(mockDao);
    }

    @Test
    public void testDeleteOutageType() {
        final Long id = 99L;
        final OutageType outage = new OutageType();
        outage.setId(id);

        mockDao.deleteOutageType(outage);
        expect(mockDao.findOutageTypeByPrimaryKey(id)).andReturn(outage);
        replay(mockDao);
        service.deleteOutageType(id);

        verify(mockDao);
    }

    private OutageType getOutageType(final Long id, final String system, final String subSystem) {
        final OutageType outageType = new OutageType();
        outageType.setId(id);
        outageType.setSystem(system);
        outageType.setSubSystem(subSystem);
        return outageType;
    }
}
