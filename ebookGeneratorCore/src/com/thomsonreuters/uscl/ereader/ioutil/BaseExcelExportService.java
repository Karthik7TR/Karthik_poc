package com.thomsonreuters.uscl.ereader.ioutil;

import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;

public abstract class BaseExcelExportService {

	public static final int MAX_EXCEL_SHEET_ROW_NUM = 65535;

	protected String[] EXCEL_HEADER;
	protected String SHEET_NAME;

	@Transactional(readOnly = true)
	public Workbook createExcelDocument(HttpSession httpSession) {

		Workbook wb = new HSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheet = wb.createSheet(SHEET_NAME);

		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN));
		Row headRow = sheet.createRow(0);

		int columnIndex = 0;
		for (String header : EXCEL_HEADER) {
			headRow.createCell(columnIndex).setCellValue(header);
			sheet.autoSizeColumn(columnIndex);
			columnIndex++;
		}

		fillRows(sheet, cellStyle, httpSession);

		return wb;
	}

	protected void fillRows(Sheet sheet, CellStyle cellStyle, HttpSession session) {
		// override in child classes
	}
}
