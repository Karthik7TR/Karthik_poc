package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.FilterForm.FilterCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.DisplayTagSortProperty;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
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
public class FilterController extends BaseJobSummaryController
{
    private static final Logger log = LogManager.getLogger(FilterController.class);
    private Validator validator;

    @InitBinder(FilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder)
    {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    /**
     * Handle submit/post of a new set of filter criteria.
     */
    @RequestMapping(value = WebConstants.MVC_JOB_SUMMARY_FILTER_POST, method = RequestMethod.POST)
    public ModelAndView doFilterPost(
        final HttpSession httpSession,
        @ModelAttribute(FilterForm.FORM_NAME) @Valid final FilterForm filterForm,
        final BindingResult errors,
        final Model model)
    {
        log.debug(filterForm);
        // Fetch the existing saved list of job execution ID's from the last successful query
        List<Long> jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);

        // Restore state of paging and sorting
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        final JobSummaryForm jobSummaryForm = new JobSummaryForm();
        jobSummaryForm.setObjectsPerPage(pageAndSort.getObjectsPerPage());

        if (FilterCommand.RESET.equals(filterForm.getFilterCommand()))
        {
            filterForm.initialize();
        }

        pageAndSort.setPageNumber(1);
        if (!errors.hasErrors())
        {
            final JobFilter filter = new JobFilter(
                filterForm.getFromDate(),
                filterForm.getToDate(),
                filterForm.getBatchStatus(),
                filterForm.getTitleId(),
                filterForm.getProviewDisplayName(),
                filterForm.getSubmittedBy());
            final JobSort jobSort = createJobSort(pageAndSort.getSortProperty(), pageAndSort.isAscendingSort());
            jobExecutionIds = jobService.findJobExecutions(filter, jobSort);
        }
        setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);
        model.addAttribute(JobSummaryForm.FORM_NAME, jobSummaryForm);

        return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
    }

    @Override
    @Required
    public void setJobService(final JobService service)
    {
        jobService = service;
    }

    @Required
    public void setValidator(final FilterFormValidator validator)
    {
        this.validator = validator;
    }
}
