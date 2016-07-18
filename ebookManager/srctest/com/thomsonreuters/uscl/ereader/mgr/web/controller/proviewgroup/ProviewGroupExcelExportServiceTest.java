package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsExcelExportService;

public class ProviewGroupExcelExportServiceTest {

	private ProviewGroupExcelExportService exportService;
	private HttpSession httpSession;

	@Before
	public void setUp() {
		this.exportService = new ProviewGroupExcelExportService();
		this.httpSession = (new MockHttpServletRequest()).getSession();

	}

	@Test
	public void testHappyPath() {
		ProviewGroup group = new ProviewGroup();
		group.setGroupName("");
		group.setGroupId("");
		group.setGroupStatus("");
		group.setTotalNumberOfVersions(2);
		group.setGroupVersion("");
		List<ProviewGroup> selectedProviewGroups = new ArrayList<ProviewGroup>();
		selectedProviewGroups.add(group);
		httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, selectedProviewGroups);

		Workbook wb = exportService.createExcelDocument(httpSession);
		Assert.assertTrue(wb.getSheet(ProviewGroupExcelExportService.GROUPS_NAME).getLastRowNum()==1);
	}

	@Test
	public void testMaxExcelRows() {
		ProviewGroup group = new ProviewGroup();
		group.setTotalNumberOfVersions(1);
		List<ProviewGroup> groups = new ArrayList<ProviewGroup>();

		for (int i = 0; i < PublishingStatsExcelExportService.MAX_EXCEL_SHEET_ROW_NUM; i++) {
			groups.add(group);
		}

		httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, groups);

		Workbook wb = exportService.createExcelDocument(httpSession);
		Assert.assertTrue(wb.getSheet(ProviewGroupExcelExportService.GROUPS_NAME).getLastRowNum()==PublishingStatsExcelExportService.MAX_EXCEL_SHEET_ROW_NUM);
	}
}
