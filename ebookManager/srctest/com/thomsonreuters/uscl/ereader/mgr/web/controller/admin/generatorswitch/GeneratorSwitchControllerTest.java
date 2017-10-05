package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.generatorswitch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.job.service.ServerAccessService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.util.EBookServerException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class GeneratorSwitchControllerTest {
    private static final String BINDING_RESULT_KEY = BindingResult.class.getName() + "." + StopGeneratorForm.FORM_NAME;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    private GeneratorSwitchController controller;
    private StopGeneratorFormValidator validator;
    private ServerAccessService mockServerAccessService;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        mockServerAccessService = EasyMock.createMock(ServerAccessService.class);
        validator = new StopGeneratorFormValidator();

        // Set up the controller
        controller =
            new GeneratorSwitchController(mockServerAccessService, validator, "test", "test", "test", "test", "test");
    }

    /**
     * Test the GET to the Stop Generator page
     */
    @Test
    public void testGetStopGenerator() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STOP_GENERATOR);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_STOP_GENERATOR, mav.getViewName());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST to the Stop Generator page
     */
    @Test
    public void testPostStopGenerator() {
        final String code = "Stop all";
        final String property = "test";
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STOP_GENERATOR);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("code", code);

        try {
            EasyMock.expect(mockServerAccessService.stopServer(property, property, property, property, property))
                .andReturn("done");
        } catch (final EBookServerException e1) {
            e1.printStackTrace();
        }
        EasyMock.replay(mockServerAccessService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_STOP_GENERATOR, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            Assert.assertFalse(bindingResult.hasErrors());

            final List<InfoMessage> infoMessages = (List<InfoMessage>) model.get(WebConstants.KEY_INFO_MESSAGES);
            Assert.assertEquals(1, infoMessages.size());

            final String message = infoMessages.get(0).getText();
            Assert.assertEquals("done", message);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockServerAccessService);
    }

    /**
     * Test the POST to the Stop Generator page validation failure
     */
    @Test
    public void testPostStopGeneratorFailure() {
        final String code = "random";
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_STOP_GENERATOR);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("code", code);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_STOP_GENERATOR, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            Assert.assertTrue(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the GET to the Start Generator page
     */
    @Test
    public void testGetStartGenerator() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_START_GENERATOR);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_START_GENERATOR, mav.getViewName());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST to the Start Generator page
     */
    @Test
    public void testPostStartGenerator() {
        final String property = "test";
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_START_GENERATOR);
        request.setMethod(HttpMethod.POST.name());

        try {
            EasyMock.expect(mockServerAccessService.startServer(property, property, property, property, property))
                .andReturn("done");
        } catch (final EBookServerException e1) {
            e1.printStackTrace();
        }
        EasyMock.replay(mockServerAccessService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_START_GENERATOR, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            final List<InfoMessage> infoMessages = (List<InfoMessage>) model.get(WebConstants.KEY_INFO_MESSAGES);
            Assert.assertEquals(1, infoMessages.size());

            final String message = infoMessages.get(0).getText();
            Assert.assertEquals("done", message);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockServerAccessService);
    }
}
