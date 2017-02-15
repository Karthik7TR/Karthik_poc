package com.thomsonreuters.uscl.ereader.gather.step;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class GenerateTocTaskTest
{
    private GenerateTocTask generateTocTask;
    private List<String> splitGuidList;
    private List<String> dupGuidList;

    @Before
    public void setUp()
    {
        generateTocTask = new GenerateTocTask();
    }

    @Test
    public void testNoDuplicateToc()
    {
        boolean thrown = false;
        try
        {
            splitGuidList = new ArrayList<>();
            splitGuidList.add("abcd");
            generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
        }
        catch (final Exception ex)
        {
            thrown = true;
        }
        Assert.assertEquals(false, thrown);
    }

    @Test
    public void testDuplicateToc()
    {
        boolean thrown = false;
        try
        {
            splitGuidList = new ArrayList<>();
            splitGuidList.add("abcd");
            dupGuidList = new ArrayList<>();
            dupGuidList.add("abcd");
            generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
        }
        catch (final Exception ex)
        {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testManualSplitDuplcateToc()
    {
        boolean thrown = false;
        try
        {
            splitGuidList = new ArrayList<>();
            splitGuidList.add("abcd");
            dupGuidList = new ArrayList<>();
            dupGuidList.add("1234");
            generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
        }
        catch (final Exception ex)
        {
            thrown = true;
        }
        Assert.assertEquals(false, thrown);
    }

    @Test
    public void testAutoSplitDuplcateToc()
    {
        boolean thrown = false;
        try
        {
            dupGuidList = new ArrayList<>();
            dupGuidList.add("1234");
            generateTocTask.duplicateTocCheck(null, dupGuidList);
        }
        catch (final Exception ex)
        {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testAutoSplitNoDuplcateToc()
    {
        boolean thrown = false;
        try
        {
            generateTocTask.duplicateTocCheck(null, null);
        }
        catch (final Exception ex)
        {
            thrown = true;
        }
        Assert.assertEquals(false, thrown);
    }
}
