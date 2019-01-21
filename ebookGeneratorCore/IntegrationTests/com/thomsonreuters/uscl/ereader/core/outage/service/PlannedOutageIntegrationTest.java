package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.Arrays;
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
        OUTAGE_TYPE = new OutageType();
        OUTAGE_TYPE.setLastUpdated(DATE);
        OUTAGE_TYPE.setSubSystem("sub system");
        OUTAGE_TYPE.setSystem("system");
        outageService.saveOutageType(OUTAGE_TYPE);
    }

    @Test
    public void createPlannedOutage() {
        final OutageType type = outageService.findOutageTypeByPrimaryKey(OUTAGE_TYPE.getId());

        final PlannedOutage outage = new PlannedOutage();
        outage.setStartTime(DATE);
        outage.setEndTime(DATE);
        outage.setLastUpdated(DATE);
        outage.setOperation(PlannedOutage.Operation.SAVE);
        outage.setOutageType(type);
        outage.setReason("Test");
        outage.setUpdatedBy("Me");

        outageService.savePlannedOutage(outage);

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
