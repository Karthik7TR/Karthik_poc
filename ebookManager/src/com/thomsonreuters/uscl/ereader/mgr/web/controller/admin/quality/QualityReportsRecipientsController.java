package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.quality;

import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;
import com.thomsonreuters.uscl.ereader.core.quality.service.QualityReportsRecipientService;
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
public class QualityReportsRecipientsController {
    @Autowired
    private QualityReportsRecipientService qualityReportRecipientService;

    @RequestMapping(value = WebConstants.MVC_ADMIN_QUALITY_RECIPIENTS_VIEW, method = RequestMethod.GET)
    public ModelAndView qualityRecipients(
        final Model model,
        @ModelAttribute("recipient") final QualityReportRecipient recipient) {
        return view(model, Redirect.FALSE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_QUALITY_RECIPIENTS_ADD, method = RequestMethod.POST)
    public ModelAndView qualityRecipientsAdd(
        final Model model,
        @ModelAttribute("recipient") final QualityReportRecipient recipient) {
        qualityReportRecipientService.save(recipient.getEmail());
        return view(model, Redirect.TRUE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_QUALITY_RECIPIENTS_DELETE, method = RequestMethod.POST)
    public ModelAndView qualityRecipientsDelete(
        @ModelAttribute("recipient") final QualityReportRecipient recipient,
        final Model model) {
        qualityReportRecipientService.delete(recipient.getEmail());
        return view(model, Redirect.TRUE);
    }

    private ModelAndView view(final Model model, final Redirect redirect) {
        model.addAttribute(
            WebConstants.KEY_QUALITY_REPORTS_RECIPIENTS,
            qualityReportRecipientService.getAll()
                .stream()
                .map(QualityReportRecipient::getEmail)
                .collect(Collectors.toList()));
        model.addAttribute("deletePath", WebConstants.MVC_ADMIN_QUALITY_RECIPIENTS_DELETE);
        return (redirect == Redirect.TRUE)
            ? new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_QUALITY_RECIPIENTS_VIEW))
            : new ModelAndView(WebConstants.VIEW_ADMIN_QUALITY_REPORTS_RECIPIENTS);
    }

    private enum Redirect {
        TRUE,
        FALSE
    }
}
