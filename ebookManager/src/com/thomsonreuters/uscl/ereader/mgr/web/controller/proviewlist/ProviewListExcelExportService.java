package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.ioutil.BaseExcelExportService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ProviewListExcelExportService extends BaseExcelExportService {
    public static final String TITLES_NAME = "ProviewGroups";
    public static final String[] TITLES_HEADER =
        {"ProView DisplayName", "Title ID", "Total Versions", "Split Parts", "Latest Version", "Status", "Publisher", "Last Update"};

    public ProviewListExcelExportService() {
        super();
        SHEET_NAME = TITLES_NAME;
        EXCEL_HEADER = TITLES_HEADER;
    }

    @Override
    protected void fillRows(final Sheet sheet, final CellStyle cellStyle, final HttpSession session) {
        final List<ProviewTitleInfo> titles = fetchSelectedProviewTitleInfo(session);
        if (titles == null) {
            throw new NullPointerException("No Title Information Found");
        }
        int rowIndex = 1;
        for (final ProviewTitleInfo title : titles) {
            // Create a row and put some cells in it.
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(title.getTitle());
            row.createCell(1).setCellValue(title.getTitleId());
            row.createCell(2).setCellValue(title.getTotalNumberOfVersions());
            row.createCell(3).setCellValue(title.getSplitParts());
            row.createCell(4).setCellValue(title.getVersion());
            row.createCell(5).setCellValue(title.getStatus());
            row.createCell(6).setCellValue(title.getPublisher());
            row.createCell(7).setCellValue(title.getLastupdate());
            if (rowIndex == (MAX_EXCEL_SHEET_ROW_NUM - 1)) {
                row = sheet.createRow(MAX_EXCEL_SHEET_ROW_NUM);
                row.createCell(0).setCellValue(
                    "You have reached the maximum amount of rows.  Please reduce the amount of rows by using the filter on the eBook Manager before generating the Excel file.");
                break;
            }
            rowIndex++;
        }
    }

    private List<ProviewTitleInfo> fetchSelectedProviewTitleInfo(final HttpSession session) {
        return (List<ProviewTitleInfo>) session.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES);
    }
}
