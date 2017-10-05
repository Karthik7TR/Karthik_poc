package com.thomsonreuters.uscl.ereader.core.outage.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class PlannedOutageTest {
    @Before
    public void setUp() {
        //Intentionally left blank
    }

    @Test
    public void booleanStateTest() {
        final PlannedOutage po = new PlannedOutage();
        // Null is initial field values, verify that they return as false
        Assert.assertFalse(po.isAllClearEmailSent());
        Assert.assertFalse(po.isNotificationEmailSent());

        // Check true state
        po.setAllClearEmailSent(true);
        Assert.assertTrue(po.isAllClearEmailSent());
        po.setNotificationEmailSent(true);
        Assert.assertTrue(po.isNotificationEmailSent());

        // Check false state
        po.setAllClearEmailSent(false);
        Assert.assertFalse(po.isAllClearEmailSent());
        po.setNotificationEmailSent(false);
        Assert.assertFalse(po.isNotificationEmailSent());
    }
}
