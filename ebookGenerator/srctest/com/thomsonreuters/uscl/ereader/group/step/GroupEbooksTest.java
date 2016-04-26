package com.thomsonreuters.uscl.ereader.group.step;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.group.service.GroupServiceImpl;

public class GroupEbooksTest {
	private GroupEbooks groupEbooks;
	GroupServiceImpl groupService;
	String groupInfoXML;
	List<String> splitTitles;
	ProviewClient proviewClient;
	ProviewException exp;

	@Before
	public void setUp() throws Exception {
		groupEbooks = new GroupEbooks();
		groupService = EasyMock.createMock(GroupServiceImpl.class);
		groupEbooks.setGroupService(groupService);

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testCreateGroup() throws Exception {

		GroupDefinition groupDef = new GroupDefinition();
		groupDef.setGroupVersion(new Long(1));
		groupEbooks.setBaseSleepTimeInMinutes(0);
		groupEbooks.setSleepTimeInMinutes(0);

		exp = new ProviewException(CoreConstants.NO_TITLE_IN_PROVIEW);

		groupService.createGroup(groupDef);
		EasyMock.expectLastCall().andThrow(exp).anyTimes();
		EasyMock.replay(groupService);
		boolean thrown = false;
		try {
			groupEbooks.createGroupWithRetry(groupDef);
		} catch (ProviewRuntimeException ex) {
			Assert.assertEquals(new Long(1), groupDef.getGroupVersion());
			Assert.assertEquals(true, ex.getMessage().contains("Tried 3 times"));
			thrown = true;
		}

		Assert.assertTrue(thrown);

	}

	@Test
	public void testCreateGroup2() throws Exception {
		GroupDefinition groupDef = new GroupDefinition();
		groupDef.setGroupVersion(new Long(1));
		groupEbooks.setBaseSleepTimeInMinutes(0);
		groupEbooks.setSleepTimeInMinutes(0);

		exp = new ProviewException(CoreConstants.GROUP_AND_VERSION_EXISTS);

		groupService.createGroup(groupDef);
		EasyMock.expectLastCall().andThrow(exp).anyTimes();
		EasyMock.replay(groupService);
		boolean thrown = false;
		try {
			groupEbooks.createGroupWithRetry(groupDef);
		} catch (ProviewRuntimeException ex) {
			Assert.assertEquals(new Long(4), groupDef.getGroupVersion());
			Assert.assertEquals(true, ex.getMessage().contains("Tried 3 times"));
			thrown = true;
		}

		Assert.assertTrue(thrown);

	}
	
	
	@Test
	public void testCreateGroup3() throws Exception{
		GroupDefinition groupDef = new GroupDefinition();
		groupDef.setGroupVersion(new Long(1));
		groupEbooks.setBaseSleepTimeInMinutes(0);
		groupEbooks.setSleepTimeInMinutes(0);
		
		exp = new ProviewException(CoreConstants.GROUP_AND_VERSION_EXISTS);
		
		
		groupService.createGroup(groupDef) ;
		EasyMock.expectLastCall();
		EasyMock.replay(groupService);
		boolean thrown = false;
		try{
		groupEbooks.createGroupWithRetry(groupDef);
		}
		catch(ProviewRuntimeException ex){
			thrown = true;
		}
		
		Assert.assertEquals(new Long(1),groupDef.getGroupVersion());
		Assert.assertFalse(thrown);
		
		
		
	}

}
