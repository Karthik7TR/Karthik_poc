package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.SneakyThrows;
import org.junit.Assert;
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
import org.springframework.web.servlet.view.RedirectView;

@RunWith(MockitoJUnitRunner.class)
public final class PublishingStatsControllerTest {
    @InjectMocks
    private PublishingStatsController controller;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private HandlerAdapter handlerAdapter;
    @Mock
    private PublishingStatsService publishingStatsService;
    @SuppressWarnings("unused")
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

        final PublishingStatsFilterForm filterForm = new PublishingStatsFilterForm();

        final HttpSession session = request.getSession();
        session.setAttribute(PublishingStatsFilterForm.FORM_NAME, filterForm);
        request.setSession(session);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertEquals(WebConstants.VIEW_STATS, mav.getViewName());
        verify(publishingStatsService).findPublishingStats(any(PublishingStatsFilter.class), any(PublishingStatsSort.class));
        verify(publishingStatsService).numberOfPublishingStats(any(PublishingStatsFilter.class));
    }

    @SneakyThrows
    @Test
    public void specificBookStat_bookDefinitionIdIsGiven_idIsAddedToModel() {
        request.setRequestURI("/" + WebConstants.MVC_STATS_SPECIFIC_BOOK);
        request.setMethod(HttpMethod.GET.name());
        final Long bookDefinitionId = 42L;
        request.setParameter(WebConstants.KEY_ID, bookDefinitionId.toString());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_STATS, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        assertNotNull(model.get(PublishingStatsForm.FORM_NAME));
        final Long actualBookDefinitionId =
            ((PublishingStatsFilterForm) model.get(PublishingStatsFilterForm.FORM_NAME)).getBookDefinitionId();
        assertEquals(bookDefinitionId, actualBookDefinitionId);
    }

    @Test
    public void testPublishingStatsSorting() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_STATS_PAGE_AND_SORT);
        request.setMethod(HttpMethod.GET.name());

        final PublishingStatsForm filterForm = new PublishingStatsForm();
        request.setParameter("sort", DisplayTagSortProperty.EBOOK_DEFINITION_ID.toString());

        final HttpSession session = request.getSession();
        session.setAttribute(PublishingStatsForm.FORM_NAME, filterForm);
        request.setSession(session);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertEquals(WebConstants.VIEW_STATS, mav.getViewName());
        verify(publishingStatsService).findPublishingStats(any(PublishingStatsFilter.class), any(PublishingStatsSort.class));
        verify(publishingStatsService).numberOfPublishingStats(any(PublishingStatsFilter.class));
    }

    @Test
    public void testPublishingStatsPaging() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_STATS_PAGE_AND_SORT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("page", "5");

        final PublishingStatsForm filterForm = new PublishingStatsForm();
        request.setParameter("sort", DisplayTagSortProperty.EBOOK_DEFINITION_ID.toString());

        final HttpSession session = request.getSession();
        session.setAttribute(PublishingStatsForm.FORM_NAME, filterForm);
        request.setSession(session);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertEquals(WebConstants.VIEW_STATS, mav.getViewName());
        verify(publishingStatsService).findPublishingStats(any(PublishingStatsFilter.class), any(PublishingStatsSort.class));
        verify(publishingStatsService).numberOfPublishingStats(any(PublishingStatsFilter.class));
    }

    @Test
    public void testHandleChangeInItemsToDisplayGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_STATS_CHANGE_ROW_COUNT);
        request.setMethod(HttpMethod.GET.name());
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        Assert.assertEquals(WebConstants.MVC_STATS, ((RedirectView) mav.getView()).getUrl());
    }

    @Test
    public void testHandleChangeInItemsToDisplay() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_STATS_CHANGE_ROW_COUNT);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("objectsPerPage", "20");

        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            new PageAndSort<>(1, DisplayTagSortProperty.JOB_SUBMIT_TIMESTAMP, false);

        final HttpSession session = request.getSession();
        session.setAttribute(BasePublishingStatsController.PAGE_AND_SORT_NAME, pageAndSort);
        request.setSession(session);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertEquals(WebConstants.VIEW_STATS, mav.getViewName());
        verify(publishingStatsService).findPublishingStats(any(PublishingStatsFilter.class), any(PublishingStatsSort.class));
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
        Assert.assertFalse(outStream.toString().isEmpty());
        verify(publishingStatsService).findPublishingStats(any(PublishingStatsFilter.class));
    }
}
