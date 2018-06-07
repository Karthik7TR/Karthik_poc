package com.thomsonreuters.uscl.ereader.gather.step;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public final class GenerateTocTaskTest {
    private GenerateTocTask generateTocTask;
    private List<String> splitGuidList;
    private List<String> dupGuidList;

    @Before
    public void setUp() {
        generateTocTask = new GenerateTocTask();
    }

    @Test
    public void testNoDuplicateToc() throws Exception {
        splitGuidList = new ArrayList<>();
        splitGuidList.add("abcd");
        generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
    }

    @Test(expected = RuntimeException.class)
    public void testDuplicateToc() throws Exception {
        splitGuidList = new ArrayList<>();
        splitGuidList.add("abcd");
        dupGuidList = new ArrayList<>();
        dupGuidList.add("abcd");
        generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
    }

    @Test
    public void testManualSplitDuplcateToc() throws Exception {
        splitGuidList = new ArrayList<>();
        splitGuidList.add("abcd");
        dupGuidList = new ArrayList<>();
        dupGuidList.add("1234");
        generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
    }

    @Test
    public void testAutoSplitDuplcateToc() throws Exception {
        dupGuidList = new ArrayList<>();
        dupGuidList.add("1234");
        generateTocTask.duplicateTocCheck(null, dupGuidList);
    }

    @Test
    public void testAutoSplitNoDuplcateToc() throws Exception {
        generateTocTask.duplicateTocCheck(null, null);
    }
}
