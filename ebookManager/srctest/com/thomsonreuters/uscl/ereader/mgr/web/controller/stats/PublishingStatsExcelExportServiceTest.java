package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

public class PublishingStatsExcelExportServiceTest {

	private PublishingStatsExcelExportService exportService;
	private HttpSession httpSession;

	@Before
	public void setUp() {
		this.exportService = new PublishingStatsExcelExportService();
		this.httpSession = (new MockHttpServletRequest()).getSession();

	}

	@Test
	public void testHappyPath() {
		EbookAudit audit = new EbookAudit();
		audit.setTitleId("titleId");
		audit.setProviewDisplayName("ProviewName");
		audit.setAuditId((long) 127);
		PublishingStats stat = new PublishingStats();
		stat.setAudit(audit);
		stat.setJobInstanceId((long) 127);
		stat.setEbookDefId((long) 127);
		stat.setJobSubmitTimestamp(new Date(1));
		stat.setPublishStartTimestamp(new Date(1));
		stat.setGatherTocNodeCount(new Integer(1));
		stat.setGatherTocSkippedCount(new Integer(1));
		stat.setGatherTocDocCount(new Integer(1));
		stat.setGatherTocRetryCount(new Integer(1));
		stat.setGatherDocExpectedCount(new Integer(1));
		stat.setGatherDocRetryCount(new Integer(1));
		stat.setGatherDocRetrievedCount(new Integer(1));
		stat.setGatherMetaExpectedCount(new Integer(1));
		stat.setGatherMetaRetrievedCount(new Integer(1));
		stat.setGatherMetaRetryCount(new Integer(1));
		stat.setGatherImageExpectedCount(new Integer(1));
		stat.setGatherImageRetrievedCount(new Integer(1));
		stat.setGatherImageRetryCount(new Integer(1));
		stat.setFormatDocCount(new Integer(1));
		stat.setTitleDocCount(new Integer(1));
		stat.setTitleDupDocCount(new Integer(1));
		stat.setPublishStatus("good");
		stat.setPublishEndTimestamp(new Date(1));
		stat.setLastUpdated(new Date(1));
		stat.setBookSize((long) 1);
		stat.setLargestDocSize((long) 1);
		stat.setLargestImageSize((long) 1);
		stat.setLargestPdfSize((long) 1);
		List<PublishingStats> stats = new ArrayList<PublishingStats>();
		stats.add(stat);
		PublishingStatsPaginatedList paginated = new PublishingStatsPaginatedList(stats, 0, 0, 0, null, false);

		httpSession.setAttribute(WebConstants.KEY_PAGINATED_LIST, paginated);

		Workbook wb = exportService.createExcelDocument(httpSession);
		Assert.assertTrue(wb.getSheet(PublishingStatsExcelExportService.STATS_NAME).getLastRowNum()==1);
	}

	@Test
	public void testMaxExcelRows() {
		EbookAudit audit = new EbookAudit();
		PublishingStats stat = new PublishingStats();
		stat.setAudit(audit);
		List<PublishingStats> stats = new ArrayList<PublishingStats>();

		for (int i = 0; i < PublishingStatsExcelExportService.MAX_EXCEL_SHEET_ROW_NUM; i++) {
			stats.add(stat);
		}

		PublishingStatsPaginatedList paginated = new PublishingStatsPaginatedList(stats, 0, 0, 0, null, false);
		httpSession.setAttribute(WebConstants.KEY_PAGINATED_LIST, paginated);

		Workbook wb = exportService.createExcelDocument(httpSession);
		Assert.assertTrue(wb.getSheet(PublishingStatsExcelExportService.STATS_NAME).getLastRowNum()==PublishingStatsExcelExportService.MAX_EXCEL_SHEET_ROW_NUM);
	}
}
