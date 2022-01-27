package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort.SortProperty;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.CurrentSessionUserPreferences;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class BookAuditController {
    private final EBookAuditService auditService;
    private final OutageService outageService;
    private final Validator validator;

    @Autowired
    public BookAuditController(final EBookAuditService auditService, final OutageService outageService,
            @Qualifier("bookAuditFilterFormValidator") final Validator validator) {
        this.auditService = auditService;
        this.outageService = outageService;
        this.validator = validator;
    }

    @InitBinder(BookAuditFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_BOOK_AUDIT_LIST, method = RequestMethod.GET)
    public ModelAndView auditList(final HttpSession httpSession,
            @ModelAttribute(BookAuditFilterForm.FORM_NAME) @Valid BookAuditFilterForm filterForm,
            final BindingResult errors, final Model model) {
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
        if (errors.hasErrors()) {
            log.debug("Incorrect parameters passed to BookAuditFilterForm:\n" + errors.toString());
            filterForm = getUserPreferencesForCurrentSession(httpSession);
        }
        updateUserPreferencesForCurrentSession(filterForm, httpSession);
        model.addAttribute(WebConstants.KEY_PAGINATED_LIST, createPaginatedList(filterForm));
        model.addAttribute(WebConstants.KEY_PAGE_SIZE, filterForm.getObjectsPerPage());

        return new ModelAndView(WebConstants.VIEW_BOOK_AUDIT_LIST);
    }

    private PaginatedList createPaginatedList(final BookAuditFilterForm filterForm) {
        final String action = filterForm.getAction() != null ? filterForm.getAction().toString() : null;
        final EbookAuditFilter bookAuditFilter = new EbookAuditFilter(
                filterForm.getFromDate(),
                filterForm.getToDate(),
                action,
                filterForm.getTitleId(),
                filterForm.getProviewDisplayName(),
                filterForm.getSubmittedBy(),
                filterForm.getBookDefinitionId());
        final EbookAuditSort bookAuditSort = createBookAuditSort(filterForm);
        // Lookup all the EbookAudit objects by their primary key
        final List<EbookAudit> audits = auditService.findEbookAudits(bookAuditFilter, bookAuditSort);
        final int numberOfAudits = auditService.numberEbookAudits(bookAuditFilter);
        // Instantiate the object used by DisplayTag to render a partial list
        return new BookAuditPaginatedList(
                audits,
                numberOfAudits,
                filterForm.getPage(),
                filterForm.getObjectsPerPage(),
                filterForm.getSort(),
                filterForm.isAscendingSort());
    }

    private EbookAuditSort createBookAuditSort(final BookAuditFilterForm filterForm) {
        return new EbookAuditSort(
                SortProperty.valueOf(filterForm.getSort().toString()),
                filterForm.isAscendingSort(),
                filterForm.getPage(),
                filterForm.getObjectsPerPage());
    }

    private void updateUserPreferencesForCurrentSession(final BookAuditFilterForm form, final HttpSession httpSession) {
        final Object preferencesSessionAttribute = httpSession.getAttribute(CurrentSessionUserPreferences.NAME);
        if (preferencesSessionAttribute instanceof CurrentSessionUserPreferences) {
            final CurrentSessionUserPreferences sessionPreferences =
                    (CurrentSessionUserPreferences) preferencesSessionAttribute;
            sessionPreferences.setBookAuditPreferences(form);
        }
    }

    private BookAuditFilterForm getUserPreferencesForCurrentSession(final HttpSession httpSession) {
        final Object preferencesSessionAttribute = httpSession.getAttribute(CurrentSessionUserPreferences.NAME);
        if (preferencesSessionAttribute instanceof CurrentSessionUserPreferences) {
            final CurrentSessionUserPreferences sessionPreferences =
                    (CurrentSessionUserPreferences) preferencesSessionAttribute;
            return sessionPreferences.getBookAuditPreferences();
        }
        return new BookAuditFilterForm();
    }

    /**
     * Handle initial in-bound HTTP get request for specific book audit detail.
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_AUDIT_DETAIL, method = RequestMethod.GET)
    public ModelAndView auditDetail(@RequestParam("id") final Long id, final Model model) {
        final EbookAudit audit = auditService.findEBookAuditByPrimaryKey(id);
        model.addAttribute(WebConstants.KEY_BOOK_AUDIT_DETAIL, audit);

        return new ModelAndView(WebConstants.VIEW_BOOK_AUDIT_DETAIL);
    }
}
