package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.util.ArrayList;
import java.util.List;

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
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

public final class PublishingStatsControllerTest {
    private PublishingStatsController controller;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private AnnotationMethodHandlerAdapter handlerAdapter;
    private PublishingStatsService mockService;
    private OutageService outageService;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
        mockService = EasyMock.createMock(PublishingStatsService.class);
        outageService = EasyMock.createMock(OutageService.class);
        controller = new PublishingStatsController(mockService, outageService);
    }

    @Test
    public void testStats() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_STATS);
        request.setMethod(HttpMethod.GET.name());

        final PublishingStatsFilterForm filterForm = new PublishingStatsFilterForm();

        final HttpSession session = request.getSession();
        session.setAttribute(PublishingStatsFilterForm.FORM_NAME, filterForm);
        request.setSession(session);

        EasyMock
            .expect(
                mockService.findPublishingStats(
                    EasyMock.anyObject(PublishingStatsFilter.class),
                    EasyMock.anyObject(PublishingStatsSort.class)))
            .andReturn(null);
        EasyMock.expect(mockService.numberOfPublishingStats(EasyMock.anyObject(PublishingStatsFilter.class)))
            .andReturn(1);
        EasyMock.replay(mockService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_STATS);
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

        EasyMock
            .expect(
                mockService.findPublishingStats(
                    EasyMock.anyObject(PublishingStatsFilter.class),
                    EasyMock.anyObject(PublishingStatsSort.class)))
            .andReturn(null);
        EasyMock.expect(mockService.numberOfPublishingStats(EasyMock.anyObject(PublishingStatsFilter.class)))
            .andReturn(1);
        EasyMock.replay(mockService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_STATS);
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

        EasyMock
            .expect(
                mockService.findPublishingStats(
                    EasyMock.anyObject(PublishingStatsFilter.class),
                    EasyMock.anyObject(PublishingStatsSort.class)))
            .andReturn(null);
        EasyMock.expect(mockService.numberOfPublishingStats(EasyMock.anyObject(PublishingStatsFilter.class)))
            .andReturn(1);
        EasyMock.replay(mockService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_STATS);
    }

    @Test
    public void testHandleChangeInItemsToDisplayGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_STATS_CHANGE_ROW_COUNT);
        request.setMethod(HttpMethod.GET.name());
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        Assert.assertEquals(((RedirectView) mav.getView()).getUrl(), WebConstants.MVC_STATS);
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

        EasyMock
            .expect(
                mockService.findPublishingStats(
                    EasyMock.anyObject(PublishingStatsFilter.class),
                    EasyMock.anyObject(PublishingStatsSort.class)))
            .andReturn(null);
        EasyMock.expect(mockService.numberOfPublishingStats(EasyMock.anyObject(PublishingStatsFilter.class)))
            .andReturn(1);
        EasyMock.replay(mockService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_STATS);
    }

    @Test
    public void testDownloadPublishingStatsExcel() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_STATS_DOWNLOAD);
        request.setMethod(HttpMethod.GET.name());

        final List<PublishingStats> stats = new ArrayList<>();

        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_PUBLISHING_STATS_LIST, stats);
        request.setSession(session);

        EasyMock
            .expect(
                mockService.findPublishingStats(
                    EasyMock.anyObject(PublishingStatsFilter.class)
                   ))
            .andReturn(null);
        EasyMock.expect(mockService.numberOfPublishingStats(EasyMock.anyObject(PublishingStatsFilter.class)))
            .andReturn(1);
        EasyMock.replay(mockService);

        handlerAdapter.handle(request, response, controller);

        final ServletOutputStream outStream = response.getOutputStream();
        Assert.assertTrue(!outStream.toString().isEmpty());
    }
}
