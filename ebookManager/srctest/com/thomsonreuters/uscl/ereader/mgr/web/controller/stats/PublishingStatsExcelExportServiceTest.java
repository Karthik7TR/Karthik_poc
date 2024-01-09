package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.ioutil.BaseExcelExportService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public final class PublishingStatsExcelExportServiceTest {
    private PublishingStatsExcelExportService exportService;
    private HttpSession httpSession;

    @Before
    public void setUp() {
        exportService = new PublishingStatsExcelExportService();
        httpSession = (new MockHttpServletRequest()).getSession();
    }

    @Test
    public void testHappyPath() {
        final EbookAudit audit = new EbookAudit();
        audit.setTitleId("titleId");
        audit.setProviewDisplayName("ProviewName");
        audit.setAuditId((long) 127);
        final PublishingStats stat = new PublishingStats();
        stat.setAudit(audit);
        stat.setJobInstanceId((long) 127);
        stat.setEbookDefId((long) 127);
        stat.setJobSubmitTimestamp(new Date(1));
        stat.setPublishStartTimestamp(new Date(1));
        stat.setGatherTocNodeCount(Integer.valueOf(1));
        stat.setGatherTocSkippedCount(Integer.valueOf(1));
        stat.setGatherTocDocCount(Integer.valueOf(1));
        stat.setGatherTocRetryCount(Integer.valueOf(1));
        stat.setGatherDocExpectedCount(Integer.valueOf(1));
        stat.setGatherDocRetryCount(Integer.valueOf(1));
        stat.setGatherDocRetrievedCount(Integer.valueOf(1));
        stat.setGatherMetaExpectedCount(Integer.valueOf(1));
        stat.setGatherMetaRetrievedCount(Integer.valueOf(1));
        stat.setGatherMetaRetryCount(Integer.valueOf(1));
        stat.setGatherImageExpectedCount(Integer.valueOf(1));
        stat.setGatherImageRetrievedCount(Integer.valueOf(1));
        stat.setGatherImageRetryCount(Integer.valueOf(1));
        stat.setFormatDocCount(Integer.valueOf(1));
        stat.setTitleDocCount(Integer.valueOf(1));
        stat.setTitleDupDocCount(Integer.valueOf(1));
        stat.setPublishStatus("good");
        stat.setPublishEndTimestamp(new Date(1));
        stat.setLastUpdated(new Date(1));
        stat.setBookSize((long) 1);
        stat.setLargestDocSize((long) 1);
        stat.setLargestImageSize((long) 1);
        stat.setLargestPdfSize((long) 1);
        final List<PublishingStats> stats = new ArrayList<>();
        stats.add(stat);

        httpSession.setAttribute(WebConstants.KEY_PUBLISHING_STATS_LIST, stats);

        final Workbook wb = exportService.createExcelDocument(httpSession);
        Assert.assertTrue(wb.getSheet(PublishingStatsExcelExportService.STATS_NAME).getLastRowNum() == 1);
    }

    @Test
    public void testMaxExcelRows() {
        final EbookAudit audit = new EbookAudit();
        final PublishingStats stat = new PublishingStats();
        stat.setAudit(audit);
        final List<PublishingStats> stats = new ArrayList<>();

        for (int i = 0; i < BaseExcelExportService.MAX_EXCEL_SHEET_ROW_NUM; i++) {
            stats.add(stat);
        }

        httpSession.setAttribute(WebConstants.KEY_PUBLISHING_STATS_LIST, stats);

        final Workbook wb = exportService.createExcelDocument(httpSession);
        Assert.assertTrue(
            wb.getSheet(PublishingStatsExcelExportService.STATS_NAME)
                .getLastRowNum() == BaseExcelExportService.MAX_EXCEL_SHEET_ROW_NUM);
    }
}
