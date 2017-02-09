package com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Required;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserPreferencesController
{
    private UserPreferenceService service;
    private Validator validator;

    @InitBinder(UserPreferencesForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder)
    {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));

        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_USER_PREFERENCES, method = RequestMethod.GET)
    public ModelAndView getPreferences(
        @ModelAttribute(UserPreferencesForm.FORM_NAME) final UserPreferencesForm form,
        final BindingResult bindingResult,
        final Model model)
    {
        final UserPreference preference = service.findByUsername(UserUtils.getAuthenticatedUserName());
        form.load(preference);
        model.addAttribute("numberOfEmails", form.getEmails().size());

        return new ModelAndView(WebConstants.VIEW_USER_PREFERENCES);
    }

    @RequestMapping(value = WebConstants.MVC_USER_PREFERENCES, method = RequestMethod.POST)
    public ModelAndView postPreferences(
        @ModelAttribute(UserPreferencesForm.FORM_NAME) @Valid final UserPreferencesForm form,
        final BindingResult bindingResult,
        final Model model)
    {
        if (!bindingResult.hasErrors())
        {
            final UserPreference preference = form.makeUserPreference();
            preference.setUserName(UserUtils.getAuthenticatedUserName());
            service.save(preference);

            return new ModelAndView(new RedirectView(form.getURL()));
        }

        model.addAttribute("numberOfEmails", form.getEmails().size());
        return new ModelAndView(WebConstants.VIEW_USER_PREFERENCES);
    }

    @Required
    public void setUserPreferenceService(final UserPreferenceService service)
    {
        this.service = service;
    }

    @Required
    public void setValidator(final Validator validator)
    {
        this.validator = validator;
    }
}
