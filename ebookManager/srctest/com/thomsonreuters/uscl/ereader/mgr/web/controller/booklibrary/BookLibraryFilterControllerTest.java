package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class BookLibraryFilterControllerTest {
    private List<LibraryList> LIBRARY_LIST = new ArrayList<>();

    private BookLibraryFilterController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private LibraryListService mockLibraryListService;
    private KeywordTypeCodeSevice keywordTypeCodeSevice;
    private OutageService mockOutageService;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockLibraryListService = EasyMock.createMock(LibraryListService.class);
        keywordTypeCodeSevice = EasyMock.createMock(KeywordTypeCodeSevice.class);
        mockOutageService = EasyMock.createMock(OutageService.class);

        handlerAdapter = new AnnotationMethodHandlerAdapter();

        controller = new BookLibraryFilterController(mockLibraryListService, keywordTypeCodeSevice, mockOutageService, null);
    }

    @Test
    public void testBookLibraryFilterGetWithoutParams() throws Exception {
        testBookLibraryFilterGet();
    }

    @Test
    public void testBookLibraryFilterGetWithParams() throws Exception {
        // Set up the request URL
        // Filter form values
        final String titleId = "uscl/junit/test/abc";
        final String fromDate = "01/01/2012";
        final String toDate = "03/01/2012";
        // The filter values
        request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
        request.setParameter("fromString", fromDate);
        request.setParameter("toString", toDate);

        Map<String, Object> model = testBookLibraryFilterGet();

        // Verify the saved filter form
        final BookLibraryFilterForm filterForm = (BookLibraryFilterForm) model.get(BookLibraryFilterForm.FORM_NAME);
        Assert.assertEquals(titleId, filterForm.getTitleId());
        Assert.assertEquals(fromDate, filterForm.getFromString());
        Assert.assertEquals(toDate, filterForm.getToString());
    }

    public Map<String, Object> testBookLibraryFilterGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_FILTERED);
        request.setMethod(HttpMethod.GET.name());
        final HttpSession session = request.getSession();

        EasyMock
                .expect(
                        mockLibraryListService.findBookDefinitions(
                                EasyMock.anyObject(LibraryListFilter.class),
                                EasyMock.anyObject(LibraryListSort.class)))
                .andReturn(LIBRARY_LIST);
        EasyMock.expect(mockLibraryListService.numberOfBookDefinitions(EasyMock.anyObject(LibraryListFilter.class)))
                .andReturn(1);
        EasyMock.replay(mockLibraryListService);

        EasyMock.expect(keywordTypeCodeSevice.getAllKeywordTypeCodes()).andReturn(new ArrayList<>());
        EasyMock.replay(keywordTypeCodeSevice);

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<>());
        EasyMock.replay(mockOutageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        Assert.assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();
        BookLibraryControllerTest.validateModel(session, model);

        EasyMock.verify(mockLibraryListService);
        EasyMock.verify(keywordTypeCodeSevice);
        EasyMock.verify(mockOutageService);

        return model;
    }
}
