package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.ioutil.BaseExcelExportService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public final class ProviewGroupExcelExportServiceTest
{
    private ProviewGroupExcelExportService exportService;
    private HttpSession httpSession;

    @Before
    public void setUp()
    {
        exportService = new ProviewGroupExcelExportService();
        httpSession = (new MockHttpServletRequest()).getSession();
    }

    @Test
    public void testHappyPath()
    {
        final ProviewGroup group = new ProviewGroup();
        group.setGroupName("");
        group.setGroupId("");
        group.setGroupStatus("");
        group.setTotalNumberOfVersions(2);
        group.setGroupVersion("");
        final List<ProviewGroup> selectedProviewGroups = new ArrayList<>();
        selectedProviewGroups.add(group);
        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, selectedProviewGroups);

        final Workbook wb = exportService.createExcelDocument(httpSession);
        Assert.assertTrue(wb.getSheet(ProviewGroupExcelExportService.GROUPS_NAME).getLastRowNum() == 1);
    }

    @Test
    public void testMaxExcelRows()
    {
        final ProviewGroup group = new ProviewGroup();
        group.setTotalNumberOfVersions(1);
        final List<ProviewGroup> groups = new ArrayList<>();

        for (int i = 0; i < BaseExcelExportService.MAX_EXCEL_SHEET_ROW_NUM; i++)
        {
            groups.add(group);
        }

        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, groups);

        final Workbook wb = exportService.createExcelDocument(httpSession);
        Assert.assertTrue(
            wb.getSheet(ProviewGroupExcelExportService.GROUPS_NAME)
                .getLastRowNum() == BaseExcelExportService.MAX_EXCEL_SHEET_ROW_NUM);
    }
}
