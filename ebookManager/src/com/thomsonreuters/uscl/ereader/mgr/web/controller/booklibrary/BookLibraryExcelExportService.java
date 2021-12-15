package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.ioutil.BaseExcelExportService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.COPYRIGHT;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.ENTITLEMENT;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.GROUP_NAME;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.INDEX_INCLUDED;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.INLINE_TOC_INCLUDED;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.ISBN;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.IS_SPLIT_BOOK;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.LAST_UPDATED;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.MATERIAL_ID;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.PRINT_PAGE_NUMBERS;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.PRINT_SET_NUMBER;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.PRINT_SUB_NUMBER;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.PROVIEW_DISPLAY_NAME;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.PUBLISHED_DATE;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.SOURCE_TYPE;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.SUBGROUP_HEADING;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryExcelExportService.Header.TITLE_ID;
import static java.util.Optional.ofNullable;

public class BookLibraryExcelExportService extends BaseExcelExportService {
    public static final String BOOK_DEFINITIONS_SHEET_NAME = "BookDefinitions";
    private static final String MAX_EXCEL_SHEET_ROW_NUM_REACHED_MSG = "You have reached the maximum amount of rows. " +
            "Please reduce the amount of rows by using the filter " +
            "on the eBook Manager before generating the Excel file.";
    private static final String NO_BOOK_DEFINITIONS_FOUND_MSG = "No Book Definitions Found";

    public enum Header {
        PROVIEW_DISPLAY_NAME,
        SOURCE_TYPE,
        TITLE_ID,
        COPYRIGHT,
        MATERIAL_ID,
        ENTITLEMENT,
        ISBN,
        IS_SPLIT_BOOK,
        GROUP_NAME,
        SUBGROUP_HEADING,
        PRINT_SET_NUMBER,
        PRINT_SUB_NUMBER,
        PRINT_PAGE_NUMBERS,
        INLINE_TOC_INCLUDED,
        INDEX_INCLUDED,
        LAST_UPDATED,
        PUBLISHED_DATE,
    }

    public BookLibraryExcelExportService() {
        super();
        SHEET_NAME = BOOK_DEFINITIONS_SHEET_NAME;
        EXCEL_HEADER = Arrays.stream(Header.values())
                .map(Header::name)
                .toArray(String[]::new);
    }

    @Override
    protected void fillRows(final Sheet sheet, final CellStyle cellStyle, final HttpSession session) {
        final List<BookDefinition> items = fetchRows(session);
        if (items == null) {
            throw new NullPointerException(NO_BOOK_DEFINITIONS_FOUND_MSG);
        }
        items.stream().limit(MAX_EXCEL_SHEET_ROW_NUM - 1).forEach(title -> fillRow(sheet, title));
        if (items.size() == MAX_EXCEL_SHEET_ROW_NUM - 1) {
            createMaxRowsAmountExceededRow(sheet);
        }
    }

    private void fillRow(final Sheet sheet, final BookDefinition item) {
        final Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        createCell(row, PROVIEW_DISPLAY_NAME, item.getProviewDisplayName());
        createCell(row, SOURCE_TYPE, item.getSourceType().name());
        createCell(row, TITLE_ID, item.getFullyQualifiedTitleId());
        createCell(row, COPYRIGHT, item.getCopyright());
        createCell(row, MATERIAL_ID, item.getMaterialId());
        createCell(row, ENTITLEMENT, item.getEntitlement());
        createCell(row, ISBN, item.getIsbn());
        createCell(row, IS_SPLIT_BOOK, item.isSplitBook());
        createCell(row, GROUP_NAME, item.getGroupName());
        createCell(row, SUBGROUP_HEADING, item.getSubGroupHeading());
        createCell(row, PRINT_SET_NUMBER, item.getPrintSetNumber());
        createCell(row, PRINT_SUB_NUMBER, item.getPrintSubNumber());
        createCell(row, PRINT_PAGE_NUMBERS, item.isPrintPageNumbers());
        createCell(row, INLINE_TOC_INCLUDED, item.isInlineTocIncluded());
        createCell(row, INDEX_INCLUDED, item.isIndexIncluded());
        createCell(row, LAST_UPDATED, item.getLastUpdated());
        createCell(row, PUBLISHED_DATE, item.getPublishedDate());
    }

    private void createCell(final Row row, final Header header, final String cell) {
        row.createCell(header.ordinal()).setCellValue(cell);
    }

    private void createCell(final Row row, final Header header, final boolean cell) {
        createCell(row, header, String.valueOf(cell));
    }

    private void createCell(final Row row, final Header header, final Date cell) {
        createCell(row, header,
                ofNullable(cell).map(this::getFormattedDate)
                        .orElse(StringUtils.EMPTY)
                );
    }

    private String getFormattedDate(final Date date) {
        return new SimpleDateFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN).format(date);
    }

    private void createMaxRowsAmountExceededRow(final Sheet sheet) {
        sheet.createRow(MAX_EXCEL_SHEET_ROW_NUM).createCell(0).setCellValue(MAX_EXCEL_SHEET_ROW_NUM_REACHED_MSG);
    }

    @SuppressWarnings("unchecked")
    private List<BookDefinition> fetchRows(final HttpSession session) {
        return ofNullable((List<BookDefinition>)session.getAttribute(WebConstants.KEY_BOOK_DEFINITIONS_LIST))
                .orElse(Collections.emptyList());
    }
}
