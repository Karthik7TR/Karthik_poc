package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class AdminControllerTest
{
    private AdminController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp()
    {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Set up the controller
        controller = new AdminController();
    }

    /**
     * Test the Get of Admin page
     */
    @Test
    public void testAdmin()
    {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_MAIN);
        request.setMethod(HttpMethod.GET.name());

        try
        {
            final ModelAndView mav = handlerAdapter.handle(request, response, controller);
            assertNotNull(mav);
        }
        catch (final Exception e)
        {
            assertEquals(e.getClass(), MissingServletRequestParameterException.class);
        }
    }
}
