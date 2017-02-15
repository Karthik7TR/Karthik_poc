package com.thomsonreuters.uscl.ereader.group.step;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.group.service.GroupServiceImpl;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class GroupEbooksTest
{
    private GroupEbooks groupEbooks;
    private GroupServiceImpl groupService;
    private String groupInfoXML;
    private List<String> splitTitles;
    private ProviewClient proviewClient;
    private ProviewException exp;

    @Before
    public void setUp()
    {
        groupEbooks = new GroupEbooks();
        groupService = EasyMock.createMock(GroupServiceImpl.class);
        groupEbooks.setGroupService(groupService);
    }

    @After
    public void tearDown()
    {
        //Intentionally left blank
    }

    @Test
    public void testCreateGroup() throws Exception
    {
        final GroupDefinition groupDef = new GroupDefinition();
        groupDef.setGroupVersion(Long.valueOf(1));
        groupEbooks.setBaseSleepTimeInMinutes(0);
        groupEbooks.setSleepTimeInMinutes(0);

        exp = new ProviewException(CoreConstants.NO_TITLE_IN_PROVIEW);

        groupService.createGroup(groupDef);
        EasyMock.expectLastCall().andThrow(exp).anyTimes();
        EasyMock.replay(groupService);
        boolean thrown = false;
        try
        {
            groupEbooks.createGroupWithRetry(groupDef);
        }
        catch (final ProviewRuntimeException ex)
        {
            Assert.assertEquals(Long.valueOf(1), groupDef.getGroupVersion());
            Assert.assertEquals(true, ex.getMessage().contains("Tried 3 times"));
            thrown = true;
        }

        Assert.assertTrue(thrown);
    }

    @Test
    public void testCreateGroup2() throws Exception
    {
        final GroupDefinition groupDef = new GroupDefinition();
        groupDef.setGroupVersion(Long.valueOf(1));
        groupEbooks.setBaseSleepTimeInMinutes(0);
        groupEbooks.setSleepTimeInMinutes(0);

        exp = new ProviewException(CoreConstants.GROUP_AND_VERSION_EXISTS);

        groupService.createGroup(groupDef);
        EasyMock.expectLastCall().andThrow(exp).anyTimes();
        EasyMock.replay(groupService);
        boolean thrown = false;
        try
        {
            groupEbooks.createGroupWithRetry(groupDef);
        }
        catch (final ProviewRuntimeException ex)
        {
            Assert.assertEquals(Long.valueOf(4), groupDef.getGroupVersion());
            Assert.assertEquals(true, ex.getMessage().contains("Tried 3 times"));
            thrown = true;
        }

        Assert.assertTrue(thrown);
    }

    @Test
    public void testCreateGroup3() throws Exception
    {
        final GroupDefinition groupDef = new GroupDefinition();
        groupDef.setGroupVersion(Long.valueOf(1));
        groupEbooks.setBaseSleepTimeInMinutes(0);
        groupEbooks.setSleepTimeInMinutes(0);

        exp = new ProviewException(CoreConstants.GROUP_AND_VERSION_EXISTS);

        groupService.createGroup(groupDef);
        EasyMock.expectLastCall();
        EasyMock.replay(groupService);
        boolean thrown = false;
        try
        {
            groupEbooks.createGroupWithRetry(groupDef);
        }
        catch (final ProviewRuntimeException ex)
        {
            thrown = true;
        }

        Assert.assertEquals(Long.valueOf(1), groupDef.getGroupVersion());
        Assert.assertFalse(thrown);
    }

    @Test
    public void testCreateGroup4() throws Exception
    {
        final GroupDefinition groupDef = new GroupDefinition();
        groupDef.setGroupVersion(Long.valueOf(1));
        groupEbooks.setBaseSleepTimeInMinutes(0);
        groupEbooks.setSleepTimeInMinutes(0);

        exp = new ProviewException("Error thrown explicitly");

        groupService.createGroup(groupDef);
        EasyMock.expectLastCall().andThrow(exp).anyTimes();
        EasyMock.replay(groupService);
        boolean thrown = false;
        try
        {
            groupEbooks.createGroupWithRetry(groupDef);
        }
        catch (final ProviewRuntimeException ex)
        {
            Assert.assertEquals(Long.valueOf(1), groupDef.getGroupVersion());
            Assert.assertEquals(false, ex.getMessage().contains("Tried 3 times"));
            thrown = true;
        }

        Assert.assertTrue(thrown);
    }
}
