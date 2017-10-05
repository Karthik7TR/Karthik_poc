package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import java.util.List;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class OutageTypeController {
    private static final Logger log = LogManager.getLogger(OutageTypeController.class);

    private final OutageService outageService;
    private final Validator validator;

    @Autowired
    public OutageTypeController(
        final OutageService outageService,
        @Qualifier("outageTypeFormValidator") final Validator validator) {
        this.outageService = outageService;
        this.validator = validator;
    }

    @InitBinder(OutageTypeForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST, method = RequestMethod.GET)
    public ModelAndView getOutageTypeList(final Model model) {
        model.addAttribute(WebConstants.KEY_OUTAGE, outageService.getAllOutageType());

        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_LIST);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_CREATE, method = RequestMethod.GET)
    public ModelAndView createOutageType(
        @ModelAttribute(OutageTypeForm.FORM_NAME) final OutageTypeForm form,
        final BindingResult bindingResult,
        final Model model) {
        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_CREATE, method = RequestMethod.POST)
    public ModelAndView createOutageTypePost(
        @ModelAttribute(OutageTypeForm.FORM_NAME) @Valid final OutageTypeForm form,
        final BindingResult bindingResult,
        final Model model) {
        log.debug(form);

        if (!bindingResult.hasErrors()) {
            final OutageType outageType = form.createOutageType();
            outageService.saveOutageType(outageType);
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST));
        }

        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_EDIT, method = RequestMethod.GET)
    public ModelAndView editOutageType(
        @RequestParam("id") final Long id,
        @ModelAttribute(OutageTypeForm.FORM_NAME) final OutageTypeForm form,
        final BindingResult bindingResult,
        final Model model) {
        final OutageType outageType = outageService.findOutageTypeByPrimaryKey(id);

        if (outageType != null) {
            model.addAttribute(WebConstants.KEY_OUTAGE, outageType);
            form.initialize(outageType);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_EDIT, method = RequestMethod.POST)
    public ModelAndView editOutageTypePost(
        @ModelAttribute(OutageTypeForm.FORM_NAME) @Valid final OutageTypeForm form,
        final BindingResult bindingResult,
        final Model model) {
        log.debug(form);

        final OutageType outageType = form.createOutageType();

        if (!bindingResult.hasErrors()) {
            outageService.saveOutageType(outageType);
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST));
        }

        model.addAttribute(WebConstants.KEY_OUTAGE, outageType);
        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_DELETE, method = RequestMethod.GET)
    public ModelAndView deleteOutageType(
        @RequestParam("id") final Long id,
        @ModelAttribute(OutageTypeForm.FORM_NAME) final OutageTypeForm form,
        final BindingResult bindingResult,
        final Model model) {
        final OutageType outageType = outageService.findOutageTypeByPrimaryKey(id);

        if (outageType != null) {
            model.addAttribute(WebConstants.KEY_OUTAGE, outageType);
            final Long outageTypeId = outageType.getId();
            final List<PlannedOutage> outageList = outageService.getAllPlannedOutagesForType(outageTypeId);
            model.addAttribute(WebConstants.KEY_PLANNED_OUTAGE_TYPE, outageList);
            model.addAttribute("numberOfPlannedOutages", outageList.size());
            form.initialize(outageType);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_DELETE, method = RequestMethod.POST)
    public ModelAndView deleteOutageTypePost(
        @ModelAttribute(OutageTypeForm.FORM_NAME) final OutageTypeForm form,
        final BindingResult bindingResult,
        final Model model) {
        log.debug(form);

        final OutageType outageType = form.createOutageType();
        if (!bindingResult.hasErrors()) {
            outageService.deleteOutageType(outageType.getId());
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST));
        }

        model.addAttribute(WebConstants.KEY_OUTAGE, outageType);
        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_DELETE);
    }
}
