package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.ioutil.BaseExcelExportService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public final class ProviewListExcelExportServiceTest {
    private ProviewListExcelExportService exportService;
    private HttpSession httpSession;

    @Before
    public void setUp() {
        exportService = new ProviewListExcelExportService();
        httpSession = (new MockHttpServletRequest()).getSession();
    }

    @Test
    public void testHappyPath() {
        final ProviewTitleInfo title = new ProviewTitleInfo();
        title.setTitle("");
        title.setTitleId("");
        title.setTotalNumberOfVersions(2);
        title.setVersion("");
        title.setStatus("");
        title.setPublisher("");
        title.setLastupdate("");
        final List<ProviewTitleInfo> selectedProviewGroups = new ArrayList<>();
        selectedProviewGroups.add(title);
        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, selectedProviewGroups);

        final Workbook wb = exportService.createExcelDocument(httpSession);
        Assert.assertTrue(wb.getSheet(ProviewListExcelExportService.TITLES_NAME).getLastRowNum() == 1);
    }

    @Test
    public void testMaxExcelRows() {
        final ProviewTitleInfo title = new ProviewTitleInfo();
        title.setTotalNumberOfVersions(1);
        final List<ProviewTitleInfo> titles = new ArrayList<>();

        for (int i = 0; i < BaseExcelExportService.MAX_EXCEL_SHEET_ROW_NUM; i++) {
            titles.add(title);
        }

        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, titles);

        final Workbook wb = exportService.createExcelDocument(httpSession);
        Assert.assertTrue(
            wb.getSheet(ProviewListExcelExportService.TITLES_NAME)
                .getLastRowNum() == BaseExcelExportService.MAX_EXCEL_SHEET_ROW_NUM);
    }
}
