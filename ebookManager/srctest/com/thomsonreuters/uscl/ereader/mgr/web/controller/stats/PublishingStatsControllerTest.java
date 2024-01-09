package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_DIR;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_OBJECTS_PER_PAGE;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_PAGE;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_PAGE_SIZE;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_SORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsFilterForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

@RunWith(MockitoJUnitRunner.class)
public final class PublishingStatsControllerTest {
    private static final String ASC = "asc";
    private static final Integer PAGE_NUMBER = 5;
    private static final Integer OBJECTS_PER_PAGE = 28;
    public static final int MAX_EXCEL_SHEET_ROW_NUM = 65535;

    @InjectMocks
    private PublishingStatsController controller;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private HandlerAdapter handlerAdapter;
    @Mock
    private PublishingStatsService publishingStatsService;
    @Mock
    private OutageService outageService;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
    }

    @Test
    public void testStats() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_STATS);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_STATS, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        assertNotNull(model.get(WebConstants.KEY_DISPLAY_OUTAGE));
        assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        assertEquals(WebConstants.DEFAULT_PAGE_SIZE, model.get(KEY_PAGE_SIZE).toString());
        verify(publishingStatsService).findPublishingStats(any(PublishingStatsFilter.class),
                any(PublishingStatsSort.class));
        verify(publishingStatsService).findPublishingStatsForExcelReport(any(PublishingStatsFilter.class),
                any(PublishingStatsSort.class), eq(MAX_EXCEL_SHEET_ROW_NUM));
        verify(publishingStatsService).numberOfPublishingStats(any(PublishingStatsFilter.class));
        verify(outageService).getAllPlannedOutagesToDisplay();
    }

    @SneakyThrows
    @Test
    public void stats_bookDefinitionIdIsGiven_idIsAddedToModel() {
        request.setRequestURI("/" + WebConstants.MVC_STATS);
        request.setMethod(HttpMethod.GET.name());
        final Long bookDefinitionId = 42L;
        request.setParameter(WebConstants.KEY_BOOK_ID, bookDefinitionId.toString());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_STATS, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        assertNotNull(model.get(PublishingStatsFilterForm.FORM_NAME));
        final Long actualBookDefinitionId =
            ((PublishingStatsFilterForm) model.get(PublishingStatsFilterForm.FORM_NAME)).getBookDefinitionId();
        assertEquals(bookDefinitionId, actualBookDefinitionId);
    }

    @Test
    public void stats_sortingParamsAreGiven_paramsAreAddedToModel() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_STATS);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(KEY_SORT, DisplayTagSortProperty.EBOOK_DEFINITION_ID.toString());
        request.setParameter(KEY_DIR, ASC);
        request.setParameter(KEY_OBJECTS_PER_PAGE, OBJECTS_PER_PAGE.toString());
        request.setParameter(KEY_PAGE, PAGE_NUMBER.toString());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_STATS, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        final PublishingStatsFilterForm form = (PublishingStatsFilterForm) model.get(PublishingStatsFilterForm.FORM_NAME);
        assertEquals(DisplayTagSortProperty.EBOOK_DEFINITION_ID, form.getSort());
        assertEquals(ASC, form.getDir());
        assertEquals(OBJECTS_PER_PAGE, form.getObjectsPerPage());
        assertEquals(PAGE_NUMBER, form.getPage());
        verify(publishingStatsService).findPublishingStats(any(PublishingStatsFilter.class),
                any(PublishingStatsSort.class));
        verify(publishingStatsService).findPublishingStatsForExcelReport(any(PublishingStatsFilter.class),
                any(PublishingStatsSort.class), eq(MAX_EXCEL_SHEET_ROW_NUM));
        verify(publishingStatsService).numberOfPublishingStats(any(PublishingStatsFilter.class));
    }

    @Test
    public void testDownloadPublishingStatsExcel() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_STATS_DOWNLOAD);
        request.setMethod(HttpMethod.GET.name());
        final List<PublishingStats> stats = new ArrayList<>();
        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_PUBLISHING_STATS_LIST, stats);
        request.setSession(session);

        handlerAdapter.handle(request, response, controller);

        final ServletOutputStream outStream = response.getOutputStream();
        assertFalse(outStream.toString().isEmpty());
    }
}
