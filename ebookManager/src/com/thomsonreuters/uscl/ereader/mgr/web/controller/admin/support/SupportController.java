package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.support;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.service.support.SupportPageLinkService;
import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;
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
public class SupportController {
    private final SupportPageLinkService service;
    private final Validator validator;

    @Autowired
    public SupportController(
        final SupportPageLinkService service,
        @Qualifier("supportFormValidator") final Validator validator) {
        this.service = service;
        this.validator = validator;
    }

    @InitBinder(SupportForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    /**
     * Handle initial in-bound HTTP get request to the page.
     * No query string parameters are expected.
     * Only Super users allowed and support roles allowed on this page
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_ADMIN_SUPPORT_VIEW, method = RequestMethod.GET)
    public ModelAndView adminSupportPageLink(final Model model) throws Exception {
        model.addAttribute(WebConstants.KEY_SUPPORT, service.findAllSupportPageLink());

        return new ModelAndView(WebConstants.VIEW_ADMIN_SUPPORT_VIEW);
    }

    /**
     * This is shown to all users from the Support Links tab in the header
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_SUPPORT_PAGE_VIEW, method = RequestMethod.GET)
    public ModelAndView supportPageLink(final Model model) throws Exception {
        model.addAttribute(WebConstants.KEY_SUPPORT, service.findAllSupportPageLink());

        return new ModelAndView(WebConstants.VIEW_SUPPORT_PAGE_VIEW);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_SUPPORT_CREATE, method = RequestMethod.GET)
    public ModelAndView createSupportPageLink(
        @ModelAttribute(SupportForm.FORM_NAME) final SupportForm form,
        final Model model) {
        return new ModelAndView(WebConstants.VIEW_ADMIN_SUPPORT_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_SUPPORT_CREATE, method = RequestMethod.POST)
    public ModelAndView createSupportPageLinkPost(
        @ModelAttribute(SupportForm.FORM_NAME) @Valid final SupportForm form,
        final BindingResult bindingResult,
        final Model model) {
        if (!bindingResult.hasErrors()) {
            service.save(form.makeCode());

            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_SUPPORT_VIEW));
        }

        return new ModelAndView(WebConstants.VIEW_ADMIN_SUPPORT_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_SUPPORT_EDIT, method = RequestMethod.GET)
    public ModelAndView editSupportPageLink(
        @RequestParam("id") final Long id,
        @ModelAttribute(SupportForm.FORM_NAME) final SupportForm form,
        final Model model) {
        final SupportPageLink spl = service.findByPrimaryKey(id);

        if (spl != null) {
            model.addAttribute(WebConstants.KEY_SUPPORT, spl);
            form.initialize(spl);
        }

        return new ModelAndView(WebConstants.VIEW_ADMIN_SUPPORT_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_SUPPORT_EDIT, method = RequestMethod.POST)
    public ModelAndView editSupportPageLinkPost(
        @ModelAttribute(SupportForm.FORM_NAME) @Valid final SupportForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception {
        if (!bindingResult.hasErrors()) {
            service.save(form.makeCode());

            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_SUPPORT_VIEW));
        }

        final SupportPageLink code = service.findByPrimaryKey(form.getSupportPageLinkId());
        model.addAttribute(WebConstants.KEY_SUPPORT, code);
        return new ModelAndView(WebConstants.VIEW_ADMIN_SUPPORT_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_SUPPORT_DELETE, method = RequestMethod.GET)
    public ModelAndView deleteSupportPageLink(
        @RequestParam("id") final Long id,
        @ModelAttribute(SupportForm.FORM_NAME) final SupportForm form,
        final BindingResult bindingResult,
        final Model model) {
        final SupportPageLink code = service.findByPrimaryKey(id);

        if (code != null) {
            model.addAttribute(WebConstants.KEY_SUPPORT, code);
            form.initialize(code);
        }

        return new ModelAndView(WebConstants.VIEW_ADMIN_SUPPORT_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_SUPPORT_DELETE, method = RequestMethod.POST)
    public ModelAndView deleteSupportPageLinkPost(
        @ModelAttribute(SupportForm.FORM_NAME) final SupportForm form,
        final BindingResult bindingResult,
        final Model model) {
        service.delete(form.makeCode());

        // Redirect user
        return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_SUPPORT_VIEW));
    }
}
