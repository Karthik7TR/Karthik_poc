package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

public class PublishingStatsFilterControllerTest {
    @InjectMocks
    private PublishingStatsFilterController controller;
    @Mock
    private PublishingStatsService mockJobService;
    @Mock
    private OutageService mockOutageService;
    private PublishingStatsFormValidator validator;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AnnotationMethodHandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
        validator = new PublishingStatsFormValidator();
        controller = new PublishingStatsFilterController(mockJobService, mockOutageService, validator);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
    }

    @Test
    public void testDoFilterGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_STATS_FILTER);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        Assert.assertEquals(((RedirectView) mav.getView()).getUrl(), WebConstants.MVC_STATS);
    }
}
