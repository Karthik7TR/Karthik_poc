package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleReportInfo;
import com.thomsonreuters.uscl.ereader.ioutil.BaseExcelExportService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ProviewListExcelExportTitleReportServiceTest {
    private ProviewListExcelTitleReportExportService exportService;
    private HttpSession httpSession;

    @Before
    public void setUp() {
        exportService = new ProviewListExcelTitleReportExportService();
        httpSession = (new MockHttpServletRequest()).getSession();
    }

    @Test
    public void testHappyPath() {
        final ProviewTitleReportInfo title = new ProviewTitleReportInfo();
        title.setId("TitleId");
        title.setTotalNumberOfVersions(2);
        title.setVersion("v1");
        title.setStatus("Ready");
        title.setMaterialId("Material01");
        title.setName("Art Artifact Architecture and Museum Law");
        final List<ProviewTitleReportInfo> selectedProvieTitleReport = new ArrayList<>();
        selectedProvieTitleReport.add(title);
        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES_REPORT, selectedProvieTitleReport);

        final Workbook wb = exportService.createExcelDocument(httpSession);
        Assert.assertTrue(wb.getSheet(ProviewListExcelTitleReportExportService.TITLES_NAME).getLastRowNum() == 1);
    }

    @Test
    public void testMaxExcelRows() {
        final ProviewTitleReportInfo title = new ProviewTitleReportInfo();
        title.setTotalNumberOfVersions(1);
        final List<ProviewTitleReportInfo> titles = new ArrayList<>();

        for (int i = 0; i < BaseExcelExportService.MAX_EXCEL_SHEET_ROW_NUM; i++) {
            titles.add(title);
        }

        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES_REPORT, titles);

        final Workbook wb = exportService.createExcelDocument(httpSession);
        Assert.assertTrue(
            wb.getSheet(ProviewListExcelTitleReportExportService.TITLES_NAME)
                .getLastRowNum() == BaseExcelExportService.MAX_EXCEL_SHEET_ROW_NUM);
    }
}
