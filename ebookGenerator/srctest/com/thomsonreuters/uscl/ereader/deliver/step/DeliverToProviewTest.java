package com.thomsonreuters.uscl.ereader.deliver.step;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;

public class DeliverToProviewTest {
	private DeliverToProview deliverToProview;
	BookDefinition bookDefinition;
	
	@Before
	public void setUp() throws Exception
	{
		deliverToProview = new DeliverToProview();
		bookDefinition = new BookDefinition();
		PublisherCode publisherCode = new PublisherCode();
		publisherCode.setId(new Long(1));
		publisherCode.setName("ucl");
		bookDefinition.setPublisherCodes(publisherCode);
		bookDefinition.setFullyQualifiedTitleId("/fullyQualifiedTitleId");
	}
	
	@After
	public void tearDown() throws Exception
	{
		
	}
	
	@Ignore
	@Test
	public void testGroupId(){
		//String actualGroupId =deliverToProview.getGroupId(bookDefinition, "1");
		//Assert.assertEquals("ucl/fullyQualifiedTitleId_1",actualGroupId);
	}
	
	@Ignore
	@Test
	public void testGroupId2(){
		bookDefinition.setFullyQualifiedTitleId("a/b/c");
		//String actualGroupId =deliverToProview.getGroupId(bookDefinition, "11");
		//Assert.assertEquals("ucl/c_11",actualGroupId);
	}
	
}
