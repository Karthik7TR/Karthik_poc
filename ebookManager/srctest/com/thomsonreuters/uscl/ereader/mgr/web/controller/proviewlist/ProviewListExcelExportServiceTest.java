package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsExcelExportService;

public class ProviewListExcelExportServiceTest {

	private ProviewListExcelExportService exportService;
	private HttpSession httpSession;

	@Before
	public void setUp() {
		this.exportService = new ProviewListExcelExportService();
		this.httpSession = (new MockHttpServletRequest()).getSession();

	}

	@Test
	public void testHappyPath() {
		ProviewTitleInfo title = new ProviewTitleInfo();
		title.setTitle("");
		title.setTitleId("");
		title.setTotalNumberOfVersions(2);
		title.setVersion("");
		title.setStatus("");
		title.setPublisher("");
		title.setLastupdate("");
		List<ProviewTitleInfo> selectedProviewGroups = new ArrayList<ProviewTitleInfo>();
		selectedProviewGroups.add(title);
		httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, selectedProviewGroups);

		Workbook wb = exportService.createExcelDocument(httpSession);
		Assert.assertTrue(wb.getSheet(ProviewListExcelExportService.TITLES_NAME).getLastRowNum()==1);
	}

	@Test
	public void testMaxExcelRows() {
		ProviewTitleInfo title = new ProviewTitleInfo();
		title.setTotalNumberOfVersions(1);
		List<ProviewTitleInfo> titles = new ArrayList<ProviewTitleInfo>();

		for (int i = 0; i < PublishingStatsExcelExportService.MAX_EXCEL_SHEET_ROW_NUM; i++) {
			titles.add(title);
		}

		httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, titles);

		Workbook wb = exportService.createExcelDocument(httpSession);
		Assert.assertTrue(wb.getSheet(ProviewListExcelExportService.TITLES_NAME).getLastRowNum()==PublishingStatsExcelExportService.MAX_EXCEL_SHEET_ROW_NUM);
	}
}
