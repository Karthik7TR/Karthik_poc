package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.userprofile;
import com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfileService;
import com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfiles;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserProfileControllerTest {

    private static final String BINDING_RESULT_KEY = BindingResult.class.getName() + "." + UserProfileForm.FORM_NAME;
    private static final UserProfiles USER_PROFILE = new UserProfiles();
    private static final String USER_ID = "C286035";
    private static final String FIRST_NAME = "Ajay";
    private static final String LAST_NAME = "Arshad";
    private UserProfileController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private UserProfileService mockUserProfileService;
    private UserProfileFormValidator validator;
    private final List<UserProfiles> userProfiles = new ArrayList<>();

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();


        // Mock up the Code service
        mockUserProfileService = EasyMock.createMock(UserProfileService.class);
        validator = new UserProfileFormValidator(mockUserProfileService);

        // Set up the controller
        controller = new UserProfileController(mockUserProfileService, validator);


        USER_PROFILE.setId(USER_ID);
        USER_PROFILE.setFirstName(FIRST_NAME);
        USER_PROFILE.setLastName(LAST_NAME);

        userProfiles.add(USER_PROFILE);
    }

/**Test the GET to the List page*/
    @Test
    public void testViewUserProfileList() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_USER_PROFILE_VIEW);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(mockUserProfileService.getAllUserProfiles()).andReturn(new ArrayList<UserProfiles>());
        EasyMock.replay(mockUserProfileService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_USER_PROFILE_VIEW, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
           // System.err.println(mav);
            final List<UserProfiles> codes = (List<UserProfiles>) model.get(WebConstants.KEY_USER_PROFILE);
            assertEquals(0, codes.size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockUserProfileService);
    }
/**Test the GET to the Create Page*/
    @Test
    public void testCreateUserProfileGet() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_USER_PROFILE_CREATE);
        request.setMethod(HttpMethod.GET.name());
        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_USER_PROFILE_CREATE, mav.getViewName());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
/**Test the POST to the Create Page Success*/
    @Test
    public void testCreateUserProfilePost() {
        final String userId = "C286034";
        final String firstName = "Amit";
        final String lastName = "Singh";


        request.setRequestURI("/" + WebConstants.MVC_ADMIN_USER_PROFILE_CREATE);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("userId", userId);
        request.setParameter("firstName", firstName);
        request.setParameter("lastName", lastName);
        final UserProfiles code = new UserProfiles();
        code.setFirstName(firstName);
        code.setLastName(lastName);
        code.setId(userId);
        try {
        EasyMock.expect(mockUserProfileService.getUserProfileByFirstName(firstName)).andReturn(userProfiles);
        EasyMock.expect(mockUserProfileService.getUserProfileById(userId)).andReturn(null);
        mockUserProfileService.saveUserProfile(code);
        EasyMock.replay(mockUserProfileService);




        final ModelAndView mav;

            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);

            // Verify mav is a RedirectView
            //System.err.println(mav);
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
           /* final List<UserProfiles> codes = (List<UserProfiles>) model.get(WebConstants.KEY_USER_PROFILE);
            assertEquals(0, codes.size());*/
            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            Assert.assertFalse(bindingResult.hasErrors());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockUserProfileService);
    }
/**Test the POST to the Create Page Fail*/
    @Test
    public void testCreateUserProfilePostFail() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_USER_PROFILE_CREATE);
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_USER_PROFILE_CREATE, mav.getViewName());

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
/**Test the GET to the Edit Page*/
    @Test
    public void testEditUserProfileGet() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_USER_PROFILE_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", USER_ID.toString());

        EasyMock.expect(mockUserProfileService.getUserProfileById(USER_ID)).andReturn(USER_PROFILE);
        EasyMock.replay(mockUserProfileService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_USER_PROFILE_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final UserProfiles actual = (UserProfiles) model.get(WebConstants.KEY_USER_PROFILE);

            Assert.assertEquals(USER_PROFILE, actual);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
/**Test the POST to the Edit Page Succes*/
   @Test
    public void testEditStateCodePost() {

        final String userId = "C286035";
        final String firstName = "Ajay";
        final String lastName = "Arshadd";

        request.setRequestURI("/" + WebConstants.MVC_ADMIN_USER_PROFILE_EDIT);
        request.setMethod(HttpMethod.POST.name());

        request.setParameter("userId",userId);
        request.setParameter("firstName",firstName);
        request.setParameter("lastName",lastName );

       final UserProfiles code = new UserProfiles();
       code.setFirstName(firstName);
       code.setLastName(lastName);
       code.setId(userId);

       EasyMock.expect(mockUserProfileService.getUserProfileByFirstName(firstName)).andReturn(userProfiles);
       EasyMock.expect(mockUserProfileService.getUserProfileById(userId)).andReturn(null);
       mockUserProfileService.saveUserProfile(code);
       EasyMock.replay(mockUserProfileService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            System.err.println("mav"+mav);
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
        EasyMock.verify(mockUserProfileService);
    }
/**Test the POST to the Edit Page Fail*/
    @Test
    public void testEditStateCodePostFail() {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_USER_PROFILE_EDIT);
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_USER_PROFILE_EDIT, mav.getViewName());

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

    @Test(expected = IllegalArgumentException.class)
    public void testEditStateCodeGetException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_USER_PROFILE_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", "1");
        EasyMock.expect(mockUserProfileService.getUserProfileById(EasyMock.anyObject())).andThrow(new IllegalArgumentException());
        EasyMock.replay(mockUserProfileService);

        handlerAdapter.handle(request, response, controller);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteStateCodeGetException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_USER_PROFILE_DELETE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", "1");
        EasyMock.expect(mockUserProfileService.getUserProfileById(EasyMock.anyObject())).andThrow(new IllegalArgumentException());
        EasyMock.replay(mockUserProfileService);

        handlerAdapter.handle(request, response, controller);
    }
}


