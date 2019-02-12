package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode;

import static com.thomsonreuters.uscl.ereader.mgr.web.controller.ControllerUtils.handleRequest;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCodeService;
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
public class StateCodeController {
    private final StateCodeService stateCodeService;
    private final Validator validator;

    @Autowired
    public StateCodeController(
        final StateCodeService stateCodeService,
        @Qualifier("stateCodeFormValidator") final Validator validator) {
        this.stateCodeService = stateCodeService;
        this.validator = validator;
    }

    @InitBinder(StateCodeForm.FORM_NAME)
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
    @RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_VIEW, method = RequestMethod.GET)
    public ModelAndView viewStateCode(final Model model) throws Exception {
        model.addAttribute(WebConstants.KEY_STATE_CODE, stateCodeService.getAllStateCodes());

        return new ModelAndView(WebConstants.VIEW_ADMIN_STATE_CODE_VIEW);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_CREATE, method = RequestMethod.GET)
    public ModelAndView createStateCode(
        @ModelAttribute(StateCodeForm.FORM_NAME) final StateCodeForm form,
        final BindingResult bindingResult,
        final Model model) {
        return new ModelAndView(WebConstants.VIEW_ADMIN_STATE_CODE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_CREATE, method = RequestMethod.POST)
    public ModelAndView createStateCodePost(
        @ModelAttribute(StateCodeForm.FORM_NAME) @Valid final StateCodeForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception {
        if (!bindingResult.hasErrors()) {
            stateCodeService.saveStateCode(form.makeCode());
            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_STATE_CODE_VIEW));
        }

        return new ModelAndView(WebConstants.VIEW_ADMIN_STATE_CODE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_EDIT, method = RequestMethod.GET)
    public ModelAndView editStateCode(
        @RequestParam("id") final Long id,
        @ModelAttribute(StateCodeForm.FORM_NAME) final StateCodeForm form,
        final Model model) {
        return handleRequest(() -> {
            final StateCode code = stateCodeService.getStateCodeById(id);

            if (code != null) {
                model.addAttribute(WebConstants.KEY_STATE_CODE, code);
                form.initialize(code);
            }

        }, WebConstants.VIEW_ADMIN_STATE_CODE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_EDIT, method = RequestMethod.POST)
    public ModelAndView editStateCodePost(
        @ModelAttribute(StateCodeForm.FORM_NAME) @Valid final StateCodeForm form,
        final BindingResult bindingResult,
        final Model model) {
        if (!bindingResult.hasErrors()) {
            stateCodeService.saveStateCode(form.makeCode());

            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_STATE_CODE_VIEW));
        }

        final StateCode code = stateCodeService.getStateCodeById(form.getStateId());
        model.addAttribute(WebConstants.KEY_STATE_CODE, code);
        return new ModelAndView(WebConstants.VIEW_ADMIN_STATE_CODE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_DELETE, method = RequestMethod.GET)
    public ModelAndView deleteStateCode(
        @RequestParam("id") final Long id,
        @ModelAttribute(StateCodeForm.FORM_NAME) final StateCodeForm form,
        final Model model) {
        return handleRequest(() -> {
            final StateCode code = stateCodeService.getStateCodeById(id);

            if (code != null) {
                model.addAttribute(WebConstants.KEY_STATE_CODE, code);
                form.initialize(code);
            }

        }, WebConstants.VIEW_ADMIN_STATE_CODE_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_DELETE, method = RequestMethod.POST)
    public ModelAndView deleteStateCodePost(
        @ModelAttribute(StateCodeForm.FORM_NAME) final StateCodeForm form,
        final BindingResult bindingResult,
        final Model model) {
        stateCodeService.deleteStateCode(form.makeCode());

        // Redirect user
        return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_STATE_CODE_VIEW));
    }
}
