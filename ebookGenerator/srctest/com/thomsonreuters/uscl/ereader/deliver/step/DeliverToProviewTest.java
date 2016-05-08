package com.thomsonreuters.uscl.ereader.deliver.step;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;

public class DeliverToProviewTest {
	private DeliverToProview deliverToProview;
	BookDefinition bookDefinition;
	ProviewException exp;
	ProviewClient proviewClient;
	
	@Before
	public void setUp() throws Exception
	{
		proviewClient = EasyMock.createMock(ProviewClient.class);
		deliverToProview = new DeliverToProview();
	}
	
	@After
	public void tearDown() throws Exception
	{
		
	}
	
	
	@Test
	public void testRetry() throws ProviewException{
		
		
		deliverToProview.setProviewClient(proviewClient);
		String title = "abcd";
		String version = "1.0";
		
		exp = new ProviewException(CoreConstants.TTILE_IN_QUEUE);
		proviewClient.removeTitle(title, version);
		EasyMock.expectLastCall().andThrow(exp).anyTimes();
		EasyMock.replay(proviewClient);
    	
		deliverToProview.setBaseSleepTimeInMinutes(0);
		deliverToProview.setSleepTimeInMinutes(0);

		
		boolean thrown = false;
		try {
			deliverToProview.removeGroupWithRetry(title,version);
		} catch (ProviewRuntimeException ex) {
			Assert.assertEquals(true, ex.getMessage().contains("Tried 3 t"
					+ "imes"));
			thrown = true;
		}

		Assert.assertTrue(thrown);
	}
	
	@Test
	public void testRetryRemove() throws ProviewException{		
		
		deliverToProview.setProviewClient(proviewClient);
		String title = "abcd";
		String version = "1.0";
		
		exp = new ProviewException("exception occurred");
		proviewClient.removeTitle(title, version);
		EasyMock.expectLastCall().andThrow(exp).anyTimes();
		EasyMock.replay(proviewClient);
    	
		deliverToProview.setBaseSleepTimeInMinutes(0);
		deliverToProview.setSleepTimeInMinutes(0);

		
		boolean thrown = false;
		try {
			deliverToProview.removeGroupWithRetry(title,version);
		} catch (ProviewRuntimeException ex) {
			Assert.assertEquals(false, ex.getMessage().contains("Tried 3 t"
					+ "imes"));
			thrown = true;
		}

		Assert.assertTrue(thrown);
	}
	
	@Test
	public void testRetryDelete() throws ProviewException{		
		
		deliverToProview.setProviewClient(proviewClient);
		String title = "abcd";
		String version = "1.0";
		
		EasyMock.expect(proviewClient.removeTitle(title, version)).andReturn("200");
		EasyMock.expect(proviewClient.deleteTitle(title, version)).andReturn("200");
    	EasyMock.replay(proviewClient);
    	
		deliverToProview.setBaseSleepTimeInMinutes(0);
		deliverToProview.setSleepTimeInMinutes(0);

		
		boolean thrown = false;
		try {
			deliverToProview.removeGroupWithRetry(title,version);
		} catch (ProviewRuntimeException ex) {
			thrown = true;
		}

		Assert.assertFalse(thrown);
	}
	
	
	
}
