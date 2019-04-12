package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.service.job.JobService;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

@RunWith(MockitoJUnitRunner.class)
public final class FilterControllerTest {
    @InjectMocks
    private FilterController controller;
    @Mock
    private JobService mockJobService;
    @Mock
    private OutageService mockOutageService;
    private FilterFormValidator validator;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AnnotationMethodHandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
        validator = new FilterFormValidator();
        controller = new FilterController(mockJobService, mockOutageService, validator);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
    }

    @Test
    public void testDoFilterGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_JOB_SUMMARY_FILTER_POST);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        Assert.assertEquals(((RedirectView) mav.getView()).getUrl(), WebConstants.MVC_JOB_SUMMARY);
    }
}
