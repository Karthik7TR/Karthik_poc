package com.thomsonreuters.uscl.ereader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionServiceImpl;
import com.thomsonreuters.uscl.ereader.format.service.AutoSplitGuidsServiceImpl;

public class SendingEmailNotificationTest {
	
	private SendingEmailNotification sendingEmailNotification;
	BookDefinition bookDefinition;
	private Long bookId = new Long(1);
	Long jobInstanceId;
	AutoSplitGuidsServiceImpl autoSplitGuidsService;
	private BookDefinitionServiceImpl service;

	@Before
	public void setUp(){
		sendingEmailNotification = new SendingEmailNotification();
		bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(bookId);
		jobInstanceId = new Long(1);
		this.service = EasyMock.createMock(BookDefinitionServiceImpl.class);
		autoSplitGuidsService = new AutoSplitGuidsServiceImpl();
		autoSplitGuidsService.setBookDefinitionService(service);
		sendingEmailNotification.setAutoSplitGuidsService(autoSplitGuidsService);
		DocumentTypeCode dc = new DocumentTypeCode();
		dc.setThresholdValue(100);
		dc.setThresholdPercent(10);
		bookDefinition.setDocumentTypeCodes(dc);
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	
	@Test
	public void testMetrics(){
		URL url = SendingEmailNotificationTest.class.getResource("toc.xml");
		List<SplitDocument> persistedSplitDocuments = new ArrayList<SplitDocument>();
		SplitDocument splitDocument = new SplitDocument();
		splitDocument.setBookDefinition(bookDefinition);
		splitDocument.setNote("note");
		splitDocument.setTocGuid("TABLEOFCONTENTS33CHARACTERSLONG_5");
		persistedSplitDocuments.add(splitDocument);
		EasyMock.expect(service.findSplitDocuments(bookDefinition
				.getEbookDefinitionId())).andReturn(persistedSplitDocuments);
		EasyMock.replay(service);
		String msg = sendingEmailNotification.getMetricsInfo(bookDefinition, 100, jobInstanceId, url.getPath());		
		Assert.assertTrue(msg.contains("**WARNING**: The book exceeds threshold value."));
		EasyMock.verify(service);
	}
}
