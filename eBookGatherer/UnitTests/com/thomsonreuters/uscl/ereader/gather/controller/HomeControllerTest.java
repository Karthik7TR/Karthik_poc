package com.thomsonreuters.uscl.ereader.gather.controller;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * Test for HomeController
 */
public final class HomeControllerTest
{
    private HomeController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp()
    {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        controller = new HomeController();
        controller.setEnvironmentName("workstation");
    }

    @Test
    public void testInboundGet() throws Exception
    {
        request.setRequestURI("/" + EBConstants.URI_HOME);
        request.setMethod(HttpMethod.GET.name());
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        Assert.assertEquals(EBConstants.VIEW_HOME, mav.getViewName());
    }
}
