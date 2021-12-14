package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BookLibraryExcelExportServiceTest {
    private BookLibraryExcelExportService exportService;
    private HttpSession httpSession;

    @Before
    public void setUp() {
        exportService = new BookLibraryExcelExportService();
        httpSession = (new MockHttpServletRequest()).getSession();
    }

    @Test
    public void testCreateExcelDocument() {
        List<BookDefinition> bookDefinitions = new ArrayList<>();
        bookDefinitions.add(new BookDefinition());
        httpSession.setAttribute(WebConstants.KEY_BOOK_DEFINITIONS_LIST, bookDefinitions);

        final Workbook wb = exportService.createExcelDocument(httpSession);
        assertEquals(1, wb.getSheet(BookLibraryExcelExportService.BOOK_DEFINITIONS_SHEET_NAME).getLastRowNum());
    }
}
