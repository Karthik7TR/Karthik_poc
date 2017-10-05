package com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;
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
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

public final class UserPreferencesControllerTest {
    private static final String BINDING_RESULT_KEY =
        BindingResult.class.getName() + "." + UserPreferencesForm.FORM_NAME;

    private UserPreferencesController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private UserPreferenceService mockService;
    private UserPreferencesFormValidator validator;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up the Code service
        mockService = EasyMock.createMock(UserPreferenceService.class);
        validator = new UserPreferencesFormValidator();
        // Set up the controller
        controller = new UserPreferencesController(mockService, validator);
    }

    /**
     * Test the GET to the User Preferences Page
     */
    @Test
    public void testGetPreferences() {
        request.setRequestURI("/" + WebConstants.MVC_USER_PREFERENCES);
        request.setMethod(HttpMethod.GET.name());

        final UserPreference preference = new UserPreference();
        preference.setEmails("a@a.com,b@b.com");

        EasyMock.expect(mockService.findByUsername(EasyMock.anyObject(String.class))).andReturn(preference);
        EasyMock.replay(mockService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_USER_PREFERENCES, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final Integer emailSize = (Integer) model.get("numberOfEmails");

            Assert.assertTrue(2 == emailSize);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockService);
    }

    /**
     * Test the POST to the User Preferences Success
     */
    @Test
    public void testPostPreferences() {
        request.setRequestURI("/" + WebConstants.MVC_USER_PREFERENCES);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("emails", new String[] {"a@a.com"});

        final UserPreference preference = new UserPreference();
        mockService.save(preference);
        EasyMock.replay(mockService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            Assert.assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockService);
    }

    /**
     * Test the POST to the User Preferences with validation error
     */
    @Test
    public void testPostPreferencesFail() {
        request.setRequestURI("/" + WebConstants.MVC_USER_PREFERENCES);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("emails", new String[] {"acom"});

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            assertEquals(WebConstants.VIEW_USER_PREFERENCES, mav.getViewName());

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
}
