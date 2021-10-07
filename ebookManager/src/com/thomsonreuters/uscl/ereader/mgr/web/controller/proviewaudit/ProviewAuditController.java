package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditFilterForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.CurrentSessionUserPreferences;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort.SortProperty;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.extern.slf4j.Slf4j;
import org.displaytag.pagination.PaginatedList;
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
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class ProviewAuditController {
    private final Validator validator;
    private final ProviewAuditService auditService;

    @Autowired
    public ProviewAuditController(@Qualifier("proviewAuditFilterFormValidator") final Validator validator,
            final ProviewAuditService auditService) {
        this.validator = validator;
        this.auditService = auditService;
    }

    @InitBinder(ProviewAuditFilterForm.FORM_NAME)
    private void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_AUDIT_LIST, method = RequestMethod.GET)
    public ModelAndView auditList(final HttpSession session, final Model model,
            @ModelAttribute(ProviewAuditFilterForm.FORM_NAME) @Valid final ProviewAuditFilterForm form,
            final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.debug("Incorrect parameters passed to ProviewAuditFilterForm");
        }
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fillPageAndSort(form.getPageAndSort());
        updateUserPreferencesForCurrentSession(form, session);
        model.addAttribute(WebConstants.KEY_PAGINATED_LIST, createPaginatedList(pageAndSort, form));
        model.addAttribute(WebConstants.KEY_PAGE_SIZE, form.getObjectsPerPage());

        return new ModelAndView(WebConstants.VIEW_PROVIEW_AUDIT_LIST);
    }

    private void updateUserPreferencesForCurrentSession(final ProviewAuditFilterForm form, final HttpSession httpSession) {
        final Object preferencesSessionAttribute = httpSession.getAttribute(CurrentSessionUserPreferences.NAME);
        if (preferencesSessionAttribute instanceof CurrentSessionUserPreferences) {
            final CurrentSessionUserPreferences sessionPreferences =
                (CurrentSessionUserPreferences) preferencesSessionAttribute;
            sessionPreferences.setProviewAuditPreferences(form);
        }
    }

    private PageAndSort<DisplayTagSortProperty> fillPageAndSort(final PageAndSort<DisplayTagSortProperty> pageAndSort) {
        if (pageAndSort.getPageNumber() == null) {
            pageAndSort.setPageNumber(1);
        }
        if (pageAndSort.getObjectsPerPage() == null) {
            pageAndSort.setObjectsPerPage(PageAndSort.DEFAULT_ITEMS_PER_PAGE);
        }
        if (pageAndSort.getSortProperty() == null) {
            pageAndSort.setSortProperty(DisplayTagSortProperty.REQUEST_DATE);
        }
        return pageAndSort;
    }

    private PaginatedList createPaginatedList(
            final PageAndSort<DisplayTagSortProperty> pageAndSort,
            final ProviewAuditFilterForm filterForm) {
        final String action = filterForm.getAction() != null ? filterForm.getAction().toString() : null;
        final ProviewAuditFilter auditFilter = new ProviewAuditFilter(
                filterForm.getRequestFromDate(),
                filterForm.getRequestToDate(),
                action,
                filterForm.getTitleId(),
                filterForm.getUsername());
        final ProviewAuditSort auditSort = createBookAuditSort(pageAndSort);

        // Lookup all the ProviewAudit objects that match the filter criteria
        final List<ProviewAudit> audits = auditService.findProviewAudits(auditFilter, auditSort);
        final int numberOfAudits = auditService.numberProviewAudits(auditFilter);

        // Instantiate the object used by DisplayTag to render a partial list
        return new ProviewAuditPaginatedList(
                audits,
                numberOfAudits,
                pageAndSort.getPageNumber(),
                pageAndSort.getObjectsPerPage(),
                pageAndSort.getSortProperty(),
                pageAndSort.isAscendingSort());
    }

    private ProviewAuditSort createBookAuditSort(final PageAndSort<DisplayTagSortProperty> pageAndSort) {
        return new ProviewAuditSort(
                SortProperty.valueOf(pageAndSort.getSortProperty().toString()),
                pageAndSort.isAscendingSort(),
                pageAndSort.getPageNumber(),
                pageAndSort.getObjectsPerPage());
    }
}
