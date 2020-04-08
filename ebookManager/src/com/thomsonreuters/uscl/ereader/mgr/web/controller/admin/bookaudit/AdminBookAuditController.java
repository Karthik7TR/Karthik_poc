package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookaudit;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort.SortProperty;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AdminBookAuditController {
    private final EBookAuditService auditService;
    private final PublishingStatsService publishingStatsService;
    private final Validator validator;

    @Autowired
    public AdminBookAuditController(
        final EBookAuditService auditService,
        final PublishingStatsService publishingStatsService,
        @Qualifier("adminAuditFilterFormValidator") final Validator validator) {
        this.auditService = auditService;
        this.publishingStatsService = publishingStatsService;
        this.validator = validator;
    }

    @InitBinder(AdminAuditFilterForm.FORM_NAME)
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
    @RequestMapping(value = WebConstants.MVC_ADMIN_AUDIT_BOOK_LIST, method = RequestMethod.GET)
    public ModelAndView viewAuditList(final HttpSession httpSession, final Model model) throws Exception {
        AdminAuditFilterForm form = (AdminAuditFilterForm) httpSession.getAttribute(AdminAuditFilterForm.FORM_NAME);

        // Setup form is came from redirect
        if (model.containsAttribute(AdminAuditFilterForm.FORM_NAME)) {
            form = (AdminAuditFilterForm) model.asMap().get(AdminAuditFilterForm.FORM_NAME);
            // Save filter and paging state in the session
            httpSession.setAttribute(AdminAuditFilterForm.FORM_NAME, form);
            setupFilterForm(httpSession, model, form);
        } else if (form != null) {
            // Setup form from previous saved filter form
            setupFilterForm(httpSession, model, form);
        } else {
            // new form
            form = new AdminAuditFilterForm();
        }

        model.addAttribute(AdminAuditFilterForm.FORM_NAME, form);

        return new ModelAndView(WebConstants.VIEW_ADMIN_AUDIT_BOOK_LIST);
    }

    private void setupFilterForm(final HttpSession httpSession, final Model model, final AdminAuditFilterForm form) {
        if (!form.isEmpty()) {
            final PublishingStatsFilter filter =
                new PublishingStatsFilter(form.getTitleId(), form.getProviewDisplayName(), form.getIsbn());
            final PublishingStatsSort sort = new PublishingStatsSort(SortProperty.JOB_SUBMIT_TIMESTAMP, false, 1, 100);
            final List<PublishingStats> publishingStats = publishingStatsService.findPublishingStats(filter, sort);
            model.addAttribute("publishingStats", publishingStats);
        }
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_AUDIT_BOOK_SEARCH, method = RequestMethod.POST)
    public String search(
        @ModelAttribute(AdminAuditFilterForm.FORM_NAME) @Valid final AdminAuditFilterForm form,
        @RequestParam final String submit,
        final BindingResult errors,
        final RedirectAttributes ra) {
        if ("reset".equalsIgnoreCase(submit)) {
            form.initialize();
        }

        ra.addFlashAttribute(AdminAuditFilterForm.FORM_NAME, form);
        return "redirect:/" + WebConstants.MVC_ADMIN_AUDIT_BOOK_LIST;
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_AUDIT_BOOK_MODIFY_ISBN, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ADMIN_AUDIT_BOOK_MODIFY_ISBN)
    public ModelAndView modifyAuditIsbn(
        @ModelAttribute(AdminAuditRecordForm.FORM_NAME) final AdminAuditRecordForm form,
        @RequestParam("id") final Long id,
        final Model model) {
        final EbookAudit audit = auditService.findEBookAuditByPrimaryKey(id);
        if (audit != null) {
            form.setTitleId(audit.getTitleId());
            form.setAuditId(id);
            form.setBookDefinitionId(audit.getEbookDefinitionId());
            form.setLastUpdated(audit.getLastUpdated());
            form.setIsbn(audit.getIsbn());
            form.setProviewDisplayName(audit.getProviewDisplayName());
            model.addAttribute("audit", audit);
            model.addAttribute(AdminAuditRecordForm.FORM_NAME, form);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_AUDIT_BOOK_MODIFY_ISBN);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_AUDIT_BOOK_MODIFY_ISBN, method = RequestMethod.POST)
    public ModelAndView modifyAuditIsbnPost(
        @ModelAttribute(AdminAuditRecordForm.FORM_NAME) @Valid final AdminAuditRecordForm form,
        final BindingResult bindingResult,
        final Model model) {
        if (!bindingResult.hasErrors()) {
            auditService.modifyIsbn(form.getTitleId(), form.getIsbn())
                    .ifPresent(audit -> {
                        // Save audit record to determine user that modified ISBN
                        audit.setAuditId(null);
                        audit.setAuditType(EbookAudit.AUDIT_TYPE.EDIT.toString());
                        audit.setUpdatedBy(UserUtils.getAuthenticatedUserName());
                        audit.setAuditNote("Modify Audit ISBN");
                        auditService.saveEBookAudit(audit);
                    });
            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_AUDIT_BOOK_LIST));
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_AUDIT_BOOK_MODIFY_ISBN);
    }
}