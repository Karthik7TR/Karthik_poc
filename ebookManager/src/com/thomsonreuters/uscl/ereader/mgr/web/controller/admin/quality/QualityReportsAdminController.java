package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.quality;

import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportParams;
import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;
import com.thomsonreuters.uscl.ereader.core.quality.service.QualityReportsAdminService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class QualityReportsAdminController {
    private final QualityReportsAdminService qualityReportAdminService;

    @Autowired
    public QualityReportsAdminController(final QualityReportsAdminService qualityReportAdminService) {
        this.qualityReportAdminService = qualityReportAdminService;
    }


    @RequestMapping(value = WebConstants.MVC_ADMIN_QUALITY_REPORTS, method = RequestMethod.GET)
    public ModelAndView qualityRecipients(
        final Model model, @ModelAttribute("recipient") final QualityReportRecipient recipient) {
        return view(model, Redirect.FALSE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_QUALITY_RECIPIENTS_ADD, method = RequestMethod.POST)
    public ModelAndView qualityRecipientsAdd(
        final Model model,
        @ModelAttribute("recipient") final QualityReportRecipient recipient) {
        qualityReportAdminService.save(recipient.getEmail());
        return view(model, Redirect.TRUE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_QUALITY_RECIPIENTS_DELETE, method = RequestMethod.POST)
    public ModelAndView qualityRecipientsDelete(
        @ModelAttribute("recipient") final QualityReportRecipient recipient,
        final Model model) {
        qualityReportAdminService.delete(recipient.getEmail());
        return view(model, Redirect.TRUE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_QUALITY_SWITCH_APPLY, method = RequestMethod.POST)
    public ModelAndView qualityStepSwitchApply(final Model model, final QualityReportParams form) {
        qualityReportAdminService.changeQualityStepEnableParameter(form.isQualityStepEnabled());
        return view(model, Redirect.TRUE);
    }

    private ModelAndView view(final Model model, final Redirect redirect) {
        model.addAttribute(
            WebConstants.KEY_QUALITY_REPORTS_FORM, qualityReportAdminService.getParams());
        model.addAttribute("deletePath", WebConstants.MVC_ADMIN_QUALITY_RECIPIENTS_DELETE);
        return (redirect == Redirect.TRUE)
            ? new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_QUALITY_REPORTS))
            : new ModelAndView(WebConstants.VIEW_ADMIN_QUALITY_REPORTS_RECIPIENTS);
    }

    private enum Redirect {
        TRUE,
        FALSE
    }
}
