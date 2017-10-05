package com.thomsonreuters.uscl.ereader.smoketest.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class SmokeTestTest {
    @Before
    public void setUp() {
        //Intentionally left blank
    }

    @Test
    public void smokeTestTest() {
        final String name = "name";
        final String address = "home";
        final boolean isRunning = false;

        final SmokeTest test = new SmokeTest();
        test.setAddress(address);
        test.setIsRunning(isRunning);
        test.setName(name);

        Assert.assertEquals(name, test.getName());
        Assert.assertEquals(isRunning, test.isRunning());
        Assert.assertEquals(address, test.getAddress());
    }
}
