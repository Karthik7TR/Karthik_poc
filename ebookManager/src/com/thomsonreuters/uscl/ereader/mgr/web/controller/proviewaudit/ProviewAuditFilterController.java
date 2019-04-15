package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditFilterForm.FilterCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
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
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ProviewAuditFilterController extends BaseProviewAuditController {
    private final Validator validator;

    @Autowired
    public ProviewAuditFilterController(
        final ProviewAuditService auditService,
        @Qualifier("proviewAuditFilterFormValidator") final Validator validator) {
        super(auditService);
        this.validator = validator;
    }

    @InitBinder(ProviewAuditFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_PROVIEW_AUDIT_LIST_FILTER_POST, method = RequestMethod.GET)
    public ModelAndView doFilterGet() {
        return new ModelAndView(new RedirectView(WebConstants.MVC_PROVIEW_AUDIT_LIST));
    }

    /**
     * Handle submit/post of a new set of filter criteria.
     */
    @RequestMapping(value = WebConstants.MVC_PROVIEW_AUDIT_LIST_FILTER_POST, method = RequestMethod.POST)
    public ModelAndView doFilterPost(
        final HttpSession httpSession,
        @ModelAttribute(ProviewAuditFilterForm.FORM_NAME) @Valid final ProviewAuditFilterForm filterForm,
        final BindingResult errors,
        final Model model) {
        // Restore state of paging and sorting
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        final ProviewAuditForm auditForm = new ProviewAuditForm();
        auditForm.setObjectsPerPage(pageAndSort.getObjectsPerPage());

        if (FilterCommand.RESET.equals(filterForm.getFilterCommand())) {
            filterForm.initialize();
        }

        pageAndSort.setPageNumber(1);

        setUpModel(filterForm, pageAndSort, httpSession, model);
        model.addAttribute(ProviewAuditForm.FORM_NAME, auditForm);

        return new ModelAndView(WebConstants.VIEW_PROVIEW_AUDIT_LIST);
    }
}
