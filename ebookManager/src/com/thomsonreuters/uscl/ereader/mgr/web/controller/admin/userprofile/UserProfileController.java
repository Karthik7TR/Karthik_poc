package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.userprofile;
import javax.validation.Valid;
import com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfileService;
import com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfiles;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final Validator validator;

    @Autowired
    public UserProfileController(
            final UserProfileService userProfileService,
            @Qualifier("userProfileFormValidator") final Validator validator) {
        this.userProfileService = userProfileService;
        this.validator = validator;
    }

    @InitBinder(UserProfileForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    /**
     * Handle initial in-bound HTTP get request to the page.
     * No query string parameters are expected.
     * Only Super users allowed
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_ADMIN_USER_PROFILE_VIEW, method = RequestMethod.GET)
    public ModelAndView viewUserProfile(final Model model) throws Exception {
        model.addAttribute(WebConstants.KEY_USER_PROFILE, userProfileService.getAllUserProfiles());

        return new ModelAndView(WebConstants.VIEW_ADMIN_USER_PROFILE_VIEW);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_USER_PROFILE_CREATE, method = RequestMethod.GET)
    public ModelAndView createUserProfile(
            @ModelAttribute(UserProfileForm.FORM_NAME) final UserProfileForm form,
            final BindingResult bindingResult,
            final Model model) {
        return new ModelAndView(WebConstants.VIEW_ADMIN_USER_PROFILE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_USER_PROFILE_CREATE, method = RequestMethod.POST)
    public ModelAndView createUserProfilePost(
            @ModelAttribute(UserProfileForm.FORM_NAME) @Valid final UserProfileForm form,
            final BindingResult bindingResult,
            final Model model) throws Exception {
        if (!bindingResult.hasErrors()) {
            userProfileService.saveUserProfile(form.makeUser());
            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_USER_PROFILE_VIEW));
        }

        return new ModelAndView(WebConstants.VIEW_ADMIN_USER_PROFILE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_USER_PROFILE_EDIT, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ADMIN_USER_PROFILE_EDIT)
    public ModelAndView editUserProfile(
            @RequestParam("id") final String id,
            @ModelAttribute(UserProfileForm.FORM_NAME) final UserProfileForm form,
            final Model model) {
        final UserProfiles code = userProfileService.getUserProfileById(id);

        if (code != null) {
            model.addAttribute(WebConstants.KEY_USER_PROFILE, code);
            form.initialize(code);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_USER_PROFILE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_USER_PROFILE_EDIT, method = RequestMethod.POST)
    public ModelAndView editUserProfilePost(
            @ModelAttribute(UserProfileForm.FORM_NAME) @Valid final UserProfileForm form,
            final BindingResult bindingResult,
            final Model model) {
        if (!bindingResult.hasErrors()) {
            userProfileService.saveUserProfile(form.makeUser());

            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_USER_PROFILE_VIEW));
        }

        final UserProfiles code = userProfileService.getUserProfileById(form.getUserId());
        model.addAttribute(WebConstants.KEY_USER_PROFILE, code);
        return new ModelAndView(WebConstants.VIEW_ADMIN_USER_PROFILE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_USER_PROFILE_DELETE, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ADMIN_USER_PROFILE_DELETE)
    public ModelAndView deleteUserProfile(
            @RequestParam("id") final String id,
            @ModelAttribute(UserProfileForm.FORM_NAME) final UserProfileForm form,
            final Model model) {
        final UserProfiles code = userProfileService.getUserProfileById(id);

        if (code != null) {
            model.addAttribute(WebConstants.KEY_USER_PROFILE, code);
            form.initialize(code);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_USER_PROFILE_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_USER_PROFILE_DELETE, method = RequestMethod.POST)
    public ModelAndView deleteUserProfilePost(
            @ModelAttribute(UserProfileForm.FORM_NAME) final UserProfileForm form,
            final BindingResult bindingResult,
            final Model model) {
        userProfileService.deleteUserProfile(form.makeUser());

        // Redirect user
        return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_USER_PROFILE_VIEW));
    }

}
