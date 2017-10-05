package com.thomsonreuters.uscl.ereader.gather.step;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class GetTocTaskTest {
    private GetTocTask getTocTask;
    private List<String> splitGuidList;
    private List<String> dupGuidList;

    @Before
    public void setUp() {
        getTocTask = new GetTocTask();
    }

    @Test
    public void testNoDuplicateToc() {
        boolean thrown = false;
        try {
            splitGuidList = new ArrayList<>();
            splitGuidList.add("abcd");
            getTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
        } catch (final Exception ex) {
            thrown = true;
        }
        Assert.assertEquals(false, thrown);
    }

    @Test
    public void testDuplicateToc() {
        boolean thrown = false;
        try {
            splitGuidList = new ArrayList<>();
            splitGuidList.add("abcd");
            dupGuidList = new ArrayList<>();
            dupGuidList.add("abcd");
            getTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
        } catch (final Exception ex) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testManualSplitDuplcateToc() {
        boolean thrown = false;
        try {
            splitGuidList = new ArrayList<>();
            splitGuidList.add("abcd");
            dupGuidList = new ArrayList<>();
            dupGuidList.add("1234");
            getTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
        } catch (final Exception ex) {
            thrown = true;
        }
        Assert.assertEquals(false, thrown);
    }

    @Test
    public void testAutoSplitDuplcateToc() {
        boolean thrown = false;
        try {
            dupGuidList = new ArrayList<>();
            dupGuidList.add("1234");
            getTocTask.duplicateTocCheck(null, dupGuidList);
        } catch (final Exception ex) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testAutoSplitNoDuplcateToc() {
        boolean thrown = false;
        try {
            getTocTask.duplicateTocCheck(null, null);
        } catch (final Exception ex) {
            thrown = true;
        }
        Assert.assertEquals(false, thrown);
    }
}
