package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.ioutil.BaseExcelExportProviewListAlertsService;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ProviewListExcelReviewBooksAlertExportServiceTest {
    private ProviewListExcelReviewBooksAlertExportService exportService;

    @Before
    public void setUp() {
        exportService = new ProviewListExcelReviewBooksAlertExportService();
    }

    @Test
    public void testHappyPath() {
        final ProviewTitleInfo title = new ProviewTitleInfo();
        title.setTitle("Mac Donald");
        title.setTitleId("cw/eg/macdonaldhaflmotblviewer_en");
        title.setTotalNumberOfVersions(2);
        title.setSplitParts(Collections.singletonList(""));
        title.setVersion("v2.0");
        title.setStatus("Final");
        title.setPublisher("cw");
        title.setLastupdate("20220829");
        final List<ProviewTitleInfo> selectedProviewGroups = new ArrayList<>();
        selectedProviewGroups.add(title);

        final Workbook wb = exportService.createExcelDocument(selectedProviewGroups);
        Assert.assertTrue(wb.getSheet(ProviewListExcelReviewBooksAlertExportService.TITLES_NAME).getLastRowNum() == 1);
    }

    @Test
    public void testMaxExcelRows() {
        final ProviewTitleInfo title = new ProviewTitleInfo();
        title.setTotalNumberOfVersions(1);
        title.setSplitParts(Collections.singletonList(""));
        final List<ProviewTitleInfo> titles = new ArrayList<>();

        for (int i = 0; i < BaseExcelExportProviewListAlertsService.MAX_EXCEL_SHEET_ROW_NUM; i++) {
            titles.add(title);
        }

        final Workbook wb = exportService.createExcelDocument(titles);
        Assert.assertTrue(
                wb.getSheet(ProviewListExcelReviewBooksAlertExportService.TITLES_NAME)
                        .getLastRowNum() == BaseExcelExportProviewListAlertsService.MAX_EXCEL_SHEET_ROW_NUM);
    }
}
