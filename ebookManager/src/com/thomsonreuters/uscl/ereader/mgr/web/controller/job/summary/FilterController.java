package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.FilterForm.FilterCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.service.job.JobService;
import lombok.extern.slf4j.Slf4j;
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

@Controller
@Slf4j
public class FilterController extends BaseJobSummaryController {
    private final Validator validator;

    @Autowired
    public FilterController(
        final JobService jobService,
        final OutageService outageService,
        @Qualifier("filterFormValidator") final Validator validator) {
        super(jobService, outageService);
        this.validator = validator;
    }

    @InitBinder(FilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    /**
     * Handle submit/post of a new set of filter criteria.
     */
    @RequestMapping(value = WebConstants.MVC_JOB_SUMMARY_FILTER, method = RequestMethod.GET)
    public ModelAndView doFilterGet(
        final HttpSession httpSession,
        @ModelAttribute(FilterForm.FORM_NAME) @Valid final FilterForm filterForm,
        final BindingResult errors,
        final Model model) {
        log.debug(filterForm.toString());
        List<Long> jobExecutionIds;

        // Restore state of paging and sorting
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        final JobSummaryForm jobSummaryForm = new JobSummaryForm();
        jobSummaryForm.setObjectsPerPage(pageAndSort.getObjectsPerPage());

        if (FilterCommand.RESET.equals(filterForm.getFilterCommand())) {
            filterForm.initialize();
        }
        pageAndSort.setPageNumber(1);
        if (!errors.hasErrors()) {
            final JobFilter filter = new JobFilter(
                filterForm.getFromDate(),
                filterForm.getToDate(),
                filterForm.getBatchStatus(),
                filterForm.getTitleId(),
                filterForm.getProviewDisplayName(),
                filterForm.getSubmittedBy());
            final JobSort jobSort = createJobSort(pageAndSort.getSortProperty(), pageAndSort.isAscendingSort());
            jobExecutionIds = jobService.findJobExecutions(filter, jobSort);
        } else {
            jobExecutionIds = Collections.emptyList();
        }
        setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);
        model.addAttribute(JobSummaryForm.FORM_NAME, jobSummaryForm);

        return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
    }
}
