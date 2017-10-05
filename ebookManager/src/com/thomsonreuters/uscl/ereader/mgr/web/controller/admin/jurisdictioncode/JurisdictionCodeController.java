package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jurisdictioncode;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
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
public class JurisdictionCodeController
{
    private final CodeService codeService;
    private final Validator validator;

    @Autowired
    public JurisdictionCodeController(final CodeService codeService,
                                      @Qualifier("jurisdictionCodeFormValidator") final Validator validator)
    {
        this.codeService = codeService;
        this.validator = validator;
    }

    @InitBinder(JurisdictionCodeForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder)
    {
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
    @RequestMapping(value = WebConstants.MVC_ADMIN_JURIS_CODE_VIEW, method = RequestMethod.GET)
    public ModelAndView viewJurisCodeList(final Model model) throws Exception
    {
        model.addAttribute(WebConstants.KEY_JURIS_TYPE_CODE, codeService.getAllJurisTypeCodes());

        return new ModelAndView(WebConstants.VIEW_ADMIN_JURIS_CODE_VIEW);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_JURIS_CODE_CREATE, method = RequestMethod.GET)
    public ModelAndView createJurisCode(
        @ModelAttribute(JurisdictionCodeForm.FORM_NAME) final JurisdictionCodeForm form,
        final BindingResult bindingResult,
        final Model model)
    {
        return new ModelAndView(WebConstants.VIEW_ADMIN_JURIS_CODE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_JURIS_CODE_CREATE, method = RequestMethod.POST)
    public ModelAndView createJurisCodePost(
        @ModelAttribute(JurisdictionCodeForm.FORM_NAME) @Valid final JurisdictionCodeForm form,
        final BindingResult bindingResult,
        final Model model)
    {
        if (!bindingResult.hasErrors())
        {
            codeService.saveJurisTypeCode(form.makeCode());

            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_JURIS_CODE_VIEW));
        }

        return new ModelAndView(WebConstants.VIEW_ADMIN_JURIS_CODE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_JURIS_CODE_EDIT, method = RequestMethod.GET)
    public ModelAndView editJurisCode(
        @RequestParam("id") final Long id,
        @ModelAttribute(JurisdictionCodeForm.FORM_NAME) final JurisdictionCodeForm form,
        final BindingResult bindingResult,
        final Model model)
    {
        final JurisTypeCode code = codeService.getJurisTypeCodeById(id);

        if (code != null)
        {
            model.addAttribute(WebConstants.KEY_JURIS_TYPE_CODE, code);
            form.initialize(code);
        }

        return new ModelAndView(WebConstants.VIEW_ADMIN_JURIS_CODE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_JURIS_CODE_EDIT, method = RequestMethod.POST)
    public ModelAndView editJurisCodePost(
        @ModelAttribute(JurisdictionCodeForm.FORM_NAME) @Valid final JurisdictionCodeForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception
    {
        if (!bindingResult.hasErrors())
        {
            codeService.saveJurisTypeCode(form.makeCode());

            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_JURIS_CODE_VIEW));
        }

        final JurisTypeCode code = codeService.getJurisTypeCodeById(form.getJurisId());
        model.addAttribute(WebConstants.KEY_JURIS_TYPE_CODE, code);
        return new ModelAndView(WebConstants.VIEW_ADMIN_JURIS_CODE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_JURIS_CODE_DELETE, method = RequestMethod.GET)
    public ModelAndView deleteJurisCode(
        @RequestParam("id") final Long id,
        @ModelAttribute(JurisdictionCodeForm.FORM_NAME) final JurisdictionCodeForm form,
        final BindingResult bindingResult,
        final Model model)
    {
        final JurisTypeCode code = codeService.getJurisTypeCodeById(id);

        if (code != null)
        {
            model.addAttribute(WebConstants.KEY_JURIS_TYPE_CODE, code);
            form.initialize(code);
        }

        return new ModelAndView(WebConstants.VIEW_ADMIN_JURIS_CODE_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_JURIS_CODE_DELETE, method = RequestMethod.POST)
    public ModelAndView deleteJurisCodePost(
        @ModelAttribute(JurisdictionCodeForm.FORM_NAME) final JurisdictionCodeForm form,
        final BindingResult bindingResult,
        final Model model)
    {
        codeService.deleteJurisTypeCode(form.makeCode());

        // Redirect user
        return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_JURIS_CODE_VIEW));
    }
}
