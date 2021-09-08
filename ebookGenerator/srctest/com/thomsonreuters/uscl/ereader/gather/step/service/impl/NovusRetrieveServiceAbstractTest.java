package com.thomsonreuters.uscl.ereader.gather.step.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NovusRetrieveServiceAbstractTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private NovusRetrieveServiceAbstract getTocTask;
    private List<String> splitGuidList;
    private List<String> dupGuidList;

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
        assertFalse(thrown);
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
        assertFalse(thrown);
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
        assertFalse(thrown);
    }
}
