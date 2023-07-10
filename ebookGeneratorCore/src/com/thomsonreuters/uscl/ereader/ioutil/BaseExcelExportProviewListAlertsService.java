package com.thomsonreuters.uscl.ereader.ioutil;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class BaseExcelExportProviewListAlertsService {
    public static final int MAX_EXCEL_SHEET_ROW_NUM = 65535;

    protected String[] EXCEL_HEADER;
    protected String SHEET_NAME;

    protected abstract void fillRows(Sheet sheet, CellStyle cellStyle, List<ProviewTitleInfo> proviewTitleInfoList);

    @Transactional(readOnly = true)
    public Workbook createExcelDocument(final List<ProviewTitleInfo> proviewTitleInfoList) {
        final Workbook wb = new HSSFWorkbook();
        final CreationHelper createHelper = wb.getCreationHelper();
        final Sheet sheet = wb.createSheet(SHEET_NAME);

        final CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN));
        final Row headRow = sheet.createRow(0);

        int columnIndex = 0;
        for (final String header : EXCEL_HEADER) {
            headRow.createCell(columnIndex).setCellValue(header);
            sheet.autoSizeColumn(columnIndex);
            columnIndex++;
        }

        fillRows(sheet, cellStyle, proviewTitleInfoList);

        return wb;
    }
}