package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.ioutil.BaseExcelExportProviewListAlertsService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ProviewListExcelReviewBooksAlertExportService extends BaseExcelExportProviewListAlertsService {
    public static final String TITLES_NAME = "ProviewReviewBooks";
    public static final String[] TITLES_HEADER =
            {"Title ID", "Latest Version", "User Name"};

    public ProviewListExcelReviewBooksAlertExportService() {
        super();
        SHEET_NAME = TITLES_NAME;
        EXCEL_HEADER = TITLES_HEADER;
    }

    @Override
    protected void fillRows(final Sheet sheet, final CellStyle cellStyle, final List<ProviewTitleInfo> proviewTitleInfoList) {
        if (proviewTitleInfoList == null) {
            throw new NullPointerException("No Title Information Found");
        }
        int rowIndex = 1;
        for (final ProviewTitleInfo title : proviewTitleInfoList) {
            // Create a row and put some cells in it.
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(title.getTitleId());
            row.createCell(1).setCellValue(title.getVersion());
            row.createCell(2).setCellValue(title.getJobSubmitterName());
            if (rowIndex == (MAX_EXCEL_SHEET_ROW_NUM - 1)) {
                row = sheet.createRow(MAX_EXCEL_SHEET_ROW_NUM);
                row.createCell(0).setCellValue(
                        "You have reached the maximum amount of rows.  Please reduce the amount of rows by using the filter on the eBook Manager before generating the Excel file.");
                break;
            }
            rowIndex++;
        }
    }

}

