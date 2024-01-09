package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.publishtypecode;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
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
public class PublishTypeCodeController {
    private final CodeService codeService;
    private final Validator validator;

    @Autowired
    public PublishTypeCodeController(
        final CodeService codeService,
        @Qualifier("publishTypeCodeFormValidator") final Validator validator) {
        this.codeService = codeService;
        this.validator = validator;
    }

    @InitBinder(PublishTypeCodeForm.FORM_NAME)
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
    @RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW, method = RequestMethod.GET)
    public ModelAndView viewPublishTypeCode(final Model model) throws Exception {
        model.addAttribute(WebConstants.KEY_PUB_TYPE_CODE, codeService.getAllPubTypeCodes());

        return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_VIEW);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_CREATE, method = RequestMethod.GET)
    public ModelAndView createPublishTypeCode(
        @ModelAttribute(PublishTypeCodeForm.FORM_NAME) final PublishTypeCodeForm form,
        final BindingResult bindingResult,
        final Model model) {
        return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_CREATE, method = RequestMethod.POST)
    public ModelAndView createPublishTypeCodePost(
        @ModelAttribute(PublishTypeCodeForm.FORM_NAME) @Valid final PublishTypeCodeForm form,
        final BindingResult bindingResult,
        final Model model) {
        if (!bindingResult.hasErrors()) {
            codeService.savePubTypeCode(form.makeCode());

            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW));
        }

        return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_EDIT, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_EDIT)
    public ModelAndView editPublishTypeCode(
        @RequestParam("id") final Long id,
        @ModelAttribute(PublishTypeCodeForm.FORM_NAME) final PublishTypeCodeForm form,
        final Model model) {
        final PubTypeCode code = codeService.getPubTypeCodeById(id);

        if (code != null) {
            model.addAttribute(WebConstants.KEY_PUB_TYPE_CODE, code);
            form.initialize(code);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_EDIT, method = RequestMethod.POST)
    public ModelAndView editPublishTypeCodePost(
        @ModelAttribute(PublishTypeCodeForm.FORM_NAME) @Valid final PublishTypeCodeForm form,
        final BindingResult bindingResult,
        final Model model) {
        if (!bindingResult.hasErrors()) {
            codeService.savePubTypeCode(form.makeCode());

            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW));
        }

        final PubTypeCode code = codeService.getPubTypeCodeById(form.getPubTypeId());
        model.addAttribute(WebConstants.KEY_PUB_TYPE_CODE, code);
        return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_DELETE, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_DELETE)
    public ModelAndView deletePublishTypeCode(
        @RequestParam("id") final Long id,
        @ModelAttribute(PublishTypeCodeForm.FORM_NAME) final PublishTypeCodeForm form,
        final Model model) {
        final PubTypeCode code = codeService.getPubTypeCodeById(id);

        if (code != null) {
            model.addAttribute(WebConstants.KEY_PUB_TYPE_CODE, code);
            form.initialize(code);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_DELETE, method = RequestMethod.POST)
    public ModelAndView deletePublishTypeCodePost(
        @ModelAttribute(PublishTypeCodeForm.FORM_NAME) final PublishTypeCodeForm form,
        final BindingResult bindingResult,
        final Model model) {
        codeService.deletePubTypeCode(form.makeCode());

        // Redirect user
        return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW));
    }
}
