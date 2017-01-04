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
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;

public class DeliverToProviewTest {
	private DeliverToProview deliverToProview;
	BookDefinition bookDefinition;
	ProviewException exp;
	ProviewHandler proviewHandler;
	
	@Before
	public void setUp() throws Exception
	{
		proviewHandler = EasyMock.createMock(ProviewHandler.class);
		deliverToProview = new DeliverToProview();
	}
	
	@After
	public void tearDown() throws Exception
	{
		
	}
	
	
	@Test
	public void testRetry() throws ProviewException{
		
		
		deliverToProview.setProviewHandler(proviewHandler);
		String title = "abcd";
		String version = "1.0";
		
		exp = new ProviewException(CoreConstants.TTILE_IN_QUEUE);
		proviewHandler.removeTitle(title, version);
		EasyMock.expectLastCall().andThrow(exp).anyTimes();
		EasyMock.replay(proviewHandler);
    	
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
		
		deliverToProview.setProviewHandler(proviewHandler);
		String title = "abcd";
		String version = "1.0";
		
		exp = new ProviewException("exception occurred");
		proviewHandler.removeTitle(title, version);
		EasyMock.expectLastCall().andThrow(exp).anyTimes();
		EasyMock.replay(proviewHandler);
    	
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
		
		deliverToProview.setProviewHandler(proviewHandler);
		String title = "abcd";
		String version = "1.0";
		
		EasyMock.expect(proviewHandler.removeTitle(title, version)).andReturn("200");
		EasyMock.expect(proviewHandler.deleteTitle(title, version)).andReturn(true);
    	EasyMock.replay(proviewHandler);
    	
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
