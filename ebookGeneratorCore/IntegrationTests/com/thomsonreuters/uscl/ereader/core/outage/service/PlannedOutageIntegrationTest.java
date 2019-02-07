package com.thomsonreuters.uscl.ereader.core.outage.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PlannedOutageIntegrationTestConfig.class)
@ActiveProfiles("IntegrationTests")
@Transactional
public class PlannedOutageIntegrationTest {
    //private static Logger log = LogManager.getLogger(PlannedOutageIntegrationTest.class);
    private static final Date DATE = new Date();
    private OutageType OUTAGE_TYPE;

    @Autowired
    protected OutageService outageService;

    @Before
    public void setUp() {
        OUTAGE_TYPE = createOutageType("system", "sub system");
    }

    @Test
    public void createPlannedOutage() {
        final OutageType type = outageService.findOutageTypeByPrimaryKey(OUTAGE_TYPE.getId());

        final PlannedOutage outage = getSavedPlannedOutage(type);

        final PlannedOutage actual = outageService.findPlannedOutageByPrimaryKey(outage.getId());

        Assert.assertEquals(outage, actual);
    }

    @Test
    public void testList() {
        final OutageType type = outageService.findOutageTypeByPrimaryKey(OUTAGE_TYPE.getId());

        for (int i = -2; i < 4; i++) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(DATE);
            cal.add(Calendar.HOUR, i);

            final PlannedOutage outage = new PlannedOutage();
            outage.setStartTime(cal.getTime());
            outage.setEndTime(cal.getTime());
            outage.setLastUpdated(DATE);
            outage.setOperation(PlannedOutage.Operation.SAVE);
            outage.setOutageType(type);
            outage.setReason("Test");
            outage.setUpdatedBy("Me");
            outageService.savePlannedOutage(outage);
        }

        final List<PlannedOutage> activeOutages = outageService.getAllActiveAndScheduledPlannedOutages();
        Assert.assertEquals(3, activeOutages.size());

        final List<PlannedOutage> allOutages = outageService.getAllPlannedOutages();
        Assert.assertEquals(6, allOutages.size());
    }

    @Test
    public void testGetAllPlannedOutagesToDisplay() {
        //given
        final OutageType type = outageService.findOutageTypeByPrimaryKey(OUTAGE_TYPE.getId());
        final Calendar before = Calendar.getInstance();
        before.add(Calendar.DAY_OF_MONTH, -2);
        final Calendar between = Calendar.getInstance();
        between.add(Calendar.DAY_OF_MONTH, 3);
        final Calendar after = Calendar.getInstance();
        after.add(Calendar.DAY_OF_MONTH, 8);
        final PlannedOutage outage1 = getSavedPlannedOutage(before, before, type);
        final PlannedOutage outage2 = getSavedPlannedOutage(before, between, type);
        final PlannedOutage outage3 = getSavedPlannedOutage(between, after, type);
        final PlannedOutage outage4 = getSavedPlannedOutage(before, after, type);
        final PlannedOutage outage5 = getSavedPlannedOutage(after, after, type);
        final PlannedOutage outage6 = getSavedPlannedOutage(between, between, type);
        final Set<PlannedOutage>
            expectedDisplayedOutages = Stream.of(outage2, outage3, outage4, outage6).collect(Collectors.toSet());

        //when
        final Set<PlannedOutage> result = new HashSet<>(outageService.getAllPlannedOutagesToDisplay());

        //then
        Assert.assertEquals(result, expectedDisplayedOutages);
    }

    @Test
    public void testSaveActiveOutageTypeWithAnotherOutageTypeFound() {
        //given
        final String system = "system2";
        final String subSystem = "subSystem2";

        final PlannedOutage outage = getSavedPlannedOutage(OUTAGE_TYPE);
        final OutageType outageType2 = createOutageType(system, subSystem);
        final PlannedOutage outage2 = getSavedPlannedOutage(outageType2);

        final OutageType outageTypeToSave = new OutageType();
        outageTypeToSave.setId(OUTAGE_TYPE.getId());
        outageTypeToSave.setSystem(system);
        outageTypeToSave.setSubSystem(subSystem);

        assertNotNull(outageService.findOutageTypeByPrimaryKey(outageType2.getId()));

        //when
        outageService.saveOutageType(outageTypeToSave);

        //then
        final OutageType actualOutageType1 = outageService.findOutageTypeByPrimaryKey(OUTAGE_TYPE.getId());
        final OutageType actualOutageType2 = outageService.findOutageTypeByPrimaryKey(outageType2.getId());
        final PlannedOutage actualOutage1 = outageService.findPlannedOutageByPrimaryKey(outage.getId());
        final PlannedOutage actualOutage2 = outageService.findPlannedOutageByPrimaryKey(outage2.getId());

        assertEquals(actualOutageType1.getSystem(), system);
        assertEquals(actualOutageType1.getSubSystem(), subSystem);
        assertNull(actualOutageType2);
        assertEquals(actualOutage1.getOutageType().getId(), actualOutageType1.getId());
        assertEquals(actualOutage2.getOutageType().getId(), actualOutageType1.getId());
    }

    @Test
    public void testAttemptToCreateOutageWithNameWhichWasPreviouslyRemoved() {
        //given
        final String system = "system2";
        final String subSystem = "subSystem2";

        final OutageType outageType2 = createOutageType(system, subSystem);
        outageService.removeOutageType(outageType2.getId());

        final OutageType outageTypeToCreate = new OutageType();
        outageTypeToCreate.setSystem(system);
        outageTypeToCreate.setSubSystem(subSystem);

        assertTrue(outageService.findOutageTypeByPrimaryKey(outageType2.getId()).getRemoved());

        //when
        outageService.saveOutageType(outageTypeToCreate);

        //then
        assertFalse(outageService.findOutageTypeByPrimaryKey(outageType2.getId()).getRemoved());
    }

    private OutageType createOutageType(final String systemName, final String subSystemName) {
        final OutageType outageType = new OutageType();
        outageType.setLastUpdated(DATE);
        outageType.setSystem(systemName);
        outageType.setSubSystem(subSystemName);
        outageService.saveOutageType(outageType);
        return outageType;
    }

    private PlannedOutage getSavedPlannedOutage(final OutageType type) {
        return getSavedPlannedOutage(Calendar.getInstance(), Calendar.getInstance(), type);
    }

    private PlannedOutage getSavedPlannedOutage(final Calendar startTime, final Calendar endTime, final OutageType type) {
        final PlannedOutage outage = new PlannedOutage();
        outage.setStartTime(startTime.getTime());
        outage.setEndTime(endTime.getTime());
        outage.setLastUpdated(DATE);
        outage.setOperation(PlannedOutage.Operation.SAVE);
        outage.setOutageType(type);
        outage.setReason("Test");
        outage.setUpdatedBy("Me");
        outageService.savePlannedOutage(outage);
        return outage;
    }
}
