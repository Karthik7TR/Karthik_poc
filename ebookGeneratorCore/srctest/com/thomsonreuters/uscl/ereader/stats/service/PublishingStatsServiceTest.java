/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

public class PublishingStatsServiceTest  {
	private static final Long BOOK_DEFINITION_ID = 1L;
	private static final String TITLE_ID = "uscl/an/book";
	private List<PublishingStats> STATS = new ArrayList<PublishingStats>();

	private PublishingStatsServiceImpl service;
	
	private PublishingStatsDao mockDao;
	
	@Before
	public void setUp() throws Exception {
		this.mockDao = EasyMock.createMock(PublishingStatsDao.class);
		
		this.service = new PublishingStatsServiceImpl();
		service.setPublishingStatsDAO(mockDao);
		
		for(int i = 0; i < 10; i++) {
			PublishingStats stat = new PublishingStats();
			stat.setJobInstanceId((long) i);
			stat.setPublishStatus(publishStatusMessage(i));
			EbookAudit audit = new EbookAudit();
			audit.setAuditId((long) i);
			stat.setAudit(audit);
			STATS.add(stat);
		}
	}
	
	private String publishStatusMessage(int i) {
		switch(i) {
		case 3:
			return PublishingStats.SEND_EMAIL_COMPLETE;
		case 5:
			return PublishingStats.SUCCESFULL_PUBLISH_STATUS;
		default:
			return "not this one";
		}
	}
	
	@Test
	public void testFindLastSuccessfulJobStatsAuditByEbookDef() {
		EasyMock.expect(mockDao.findPublishingStatsByEbookDef(BOOK_DEFINITION_ID)).andReturn(STATS);
		EasyMock.replay(mockDao);
		
		EbookAudit audit = service.findLastSuccessfulJobStatsAuditByEbookDef(BOOK_DEFINITION_ID);
		
		Long auditId = 5L;
		Assert.assertEquals(auditId, audit.getAuditId());
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testHasIsbnBeenPublished() {
		List<String> isbns = new ArrayList<String>();
		isbns.add("1-2-3");
		isbns.add("1-1");
		isbns.add("1-2");
		
		EasyMock.expect(mockDao.findSuccessfullyPublishedIsbnByTitleId(TITLE_ID)).andReturn(isbns);
		EasyMock.replay(mockDao);
		
		String isbn = "123";
		Boolean hasBeenPublished = service.hasIsbnBeenPublished(isbn, TITLE_ID);
		Assert.assertEquals(true, hasBeenPublished);
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testHasIsbnBeenPublished2() {
		List<String> isbns = new ArrayList<String>();
		isbns.add("1-2-3");
		isbns.add("1-1");
		isbns.add("1-2");
		
		EasyMock.expect(mockDao.findSuccessfullyPublishedIsbnByTitleId(TITLE_ID)).andReturn(isbns);
		EasyMock.replay(mockDao);
		
		String isbn = "1";
		Boolean hasBeenPublished = service.hasIsbnBeenPublished(isbn, TITLE_ID);
		Assert.assertEquals(false, hasBeenPublished);
		EasyMock.verify(mockDao);
	}
	
//	@Test
//	public void testMaxRowsInExcel() {
//		PublishingStatsFilter filter = new PublishingStatsFilter();
//		
//		EasyMock.expect(mockDao.findPublishingStats(filter)).andReturn(STATS);
//		EasyMock.replay(mockDao);
//		
//		Workbook wb = service.createExcelDocument(filter);
//		Sheet sheet = wb.getSheetAt(0);
//		
//		int lastRow = sheet.getLastRowNum();
//		Assert.assertEquals(PublishingStatsServiceImpl.MAX_EXCEL_SHEET_ROW_NUM, lastRow);
//		
//		EasyMock.verify(mockDao);
//	}
}
