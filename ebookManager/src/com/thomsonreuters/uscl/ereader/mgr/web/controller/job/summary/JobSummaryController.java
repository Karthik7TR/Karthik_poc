package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.GeneratorRestClient;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage.Type;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details.JobExecutionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.JobCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.service.job.JobService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
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
public class JobSummaryController extends BaseJobSummaryController {
    private static final Logger log = LogManager.getLogger(JobSummaryController.class);

    @Autowired
    private GeneratorRestClient generatorRestClient;
    @Autowired
    @Qualifier("jobSummaryValidator")
    private Validator validator;
    @Autowired
    private MessageSourceAccessor messageSourceAccessor;
    @Autowired
    private JobExecutionController jobExecutionController;

    @Autowired
    public JobSummaryController(final JobService jobService, final OutageService outageService) {
        super(jobService, outageService);
    }

    @InitBinder(JobSummaryForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.setValidator(validator);
    }

    /**
     * Handle initial in-bound HTTP get request to the page.
     * No query string parameters are expected.
     */
    @RequestMapping(value = WebConstants.MVC_JOB_SUMMARY, method = RequestMethod.GET)
    public ModelAndView inboundGet(final HttpSession httpSession, final Model model) {
//		log.debug(">>>");
        final FilterForm filterForm = fetchSavedFilterForm(httpSession); // from session
        final PageAndSort<DisplayTagSortProperty> savedPageAndSort = fetchSavedPageAndSort(httpSession); // from session

        final JobSummaryForm jobSummaryForm = new JobSummaryForm();
        jobSummaryForm.setObjectsPerPage(savedPageAndSort.getObjectsPerPage());

        final JobFilter jobFilter = new JobFilter(
            filterForm.getFromDate(),
            filterForm.getToDate(),
            filterForm.getBatchStatus(),
            filterForm.getTitleId(),
            filterForm.getProviewDisplayName(),
            filterForm.getSubmittedBy());
        final JobSort jobSort = createJobSort(savedPageAndSort.getSortProperty(), savedPageAndSort.isAscendingSort());
        final List<Long> jobExecutionIds = jobService.findJobExecutions(jobFilter, jobSort);

        setUpModel(jobExecutionIds, filterForm, savedPageAndSort, httpSession, model);
        model.addAttribute(JobSummaryForm.FORM_NAME, jobSummaryForm);

        return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
    }

    /**
     * Handle paging and sorting of job list.
     * Handles clicking of column headers to sort, or use of page number navigation links, like prev/next.
     */
    @RequestMapping(value = WebConstants.MVC_JOB_SUMMARY_PAGE_AND_SORT, method = RequestMethod.GET)
    public ModelAndView doPagingAndSorting(
        final HttpSession httpSession,
        @ModelAttribute(JobSummaryForm.FORM_NAME) final JobSummaryForm form,
        final Model model) {
        log.debug(form);
        List<Long> jobExecutionIds = null;
        final FilterForm filterForm = fetchSavedFilterForm(httpSession);
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        form.setObjectsPerPage(pageAndSort.getObjectsPerPage());
        final Integer nextPageNumber = form.getPage();

        // If there was a page=n query string parameter, then we assume we are paging since this
        // parameter is not present on the query string when display tag sorting.
        if (nextPageNumber != null) { // PAGING
            pageAndSort.setPageNumber(nextPageNumber);
            jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);
        } else if (form.getSort() != null) { // SORTING
            pageAndSort.setPageNumber(1);
            pageAndSort.setSortAndAscendingProperties(form.getSort(), form.isAscendingSort());
            // Fetch the job list model
            final JobFilter jobFilter = new JobFilter(
                filterForm.getFromDate(),
                filterForm.getToDate(),
                filterForm.getBatchStatus(),
                filterForm.getTitleId(),
                filterForm.getProviewDisplayName(),
                filterForm.getSubmittedBy());
            final JobSort jobSort = createJobSort(form.getSort(), form.isAscendingSort());
            jobExecutionIds = jobService.findJobExecutions(jobFilter, jobSort);
        } else {
            return new ModelAndView(new RedirectView(WebConstants.MVC_JOB_SUMMARY));
        }
        setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);

        return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
    }

    /**
     * Handle operational buttons that submit a form of selected rows, or when the user changes the number of
     * rows displayed at one time.
     */
    @RequestMapping(value = WebConstants.MVC_JOB_SUMMARY_JOB_OPERATION, method = RequestMethod.POST)
    public ModelAndView stopOrRestartJob(
        final HttpSession httpSession,
        @ModelAttribute(JobSummaryForm.FORM_NAME) @Valid final JobSummaryForm form,
        final BindingResult errors,
        final Model model) {
        log.debug(form);
        final List<InfoMessage> messages = new ArrayList<>();

        if (!errors.hasErrors()) {
            final JobCommand command = form.getJobCommand();
            String mesgCode = null; // resource bundle message key/code
            for (final Long jobExecutionId : form.getJobExecutionIds()) {
                try {
                    SimpleRestServiceResponse restResponse = null;
                    switch (command) {
                    case RESTART_JOB:
                        mesgCode = "job.restart.fail";
                        if (jobExecutionController.authorizedForJobOperation(
                            jobExecutionId,
                            JobExecutionController.LABEL_RESTART,
                            messages)) {
                            restResponse = generatorRestClient.restartJob(jobExecutionId);
                            JobExecutionController.handleRestartJobOperationResponse(
                                messages,
                                jobExecutionId,
                                restResponse,
                                messageSourceAccessor);
                        }
                        break;
                    case STOP_JOB:
                        mesgCode = "job.stop.fail";
                        if (jobExecutionController
                            .authorizedForJobOperation(jobExecutionId, JobExecutionController.LABEL_STOP, messages)) {
                            restResponse = generatorRestClient.stopJob(jobExecutionId);
                            JobExecutionController.handleStopJobOperationResponse(
                                messages,
                                jobExecutionId,
                                restResponse,
                                messageSourceAccessor);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Programming error - Unexpected command: " + command);
                    }
                } catch (final Exception e) {
                    final InfoMessage errorMessage =
                        createRestExceptionMessage(mesgCode, jobExecutionId, e, messageSourceAccessor);
                    log.error(errorMessage.getText(), e);
                    messages.add(errorMessage);
                }
            } // end of for-loop
        }

        // Restore state of paging and sorting
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        // Restore the state of the search filter
        final FilterForm filterForm = fetchSavedFilterForm(httpSession);
        // Fetch the existing session saved list of job execution ID's
        final List<Long> jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);
        // Uncheck all of the form checkboxes
        form.setJobExecutionIds(null);

        setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);
        model.addAttribute(WebConstants.KEY_INFO_MESSAGES, messages); // Informational messages related to success/fail of job stop or restart

        return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
    }

    @RequestMapping(value = WebConstants.MVC_JOB_SUMMARY_CHANGE_ROW_COUNT, method = RequestMethod.GET)
    public ModelAndView handleChangeInItemsToDisplayGet() {
        return new ModelAndView(new RedirectView(WebConstants.MVC_JOB_SUMMARY));
    }

    /**
     * Handle URL request that the number of rows displayed in the job summary table be changed.
     */
    @RequestMapping(value = WebConstants.MVC_JOB_SUMMARY_CHANGE_ROW_COUNT, method = RequestMethod.POST)
    public ModelAndView handleChangeInItemsToDisplay(
        final HttpSession httpSession,
        @ModelAttribute(JobSummaryForm.FORM_NAME) @Valid final JobSummaryForm form,
        final Model model) {
        log.debug(form);
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        pageAndSort.setPageNumber(1); // Always start from first page again once changing row count to avoid index out of bounds
        pageAndSort.setObjectsPerPage(form.getObjectsPerPage()); // Update the new number of items to be shown at one time
        // Restore the state of the search filter
        final FilterForm filterForm = fetchSavedFilterForm(httpSession);
        // Fetch the existing session saved list of job execution ID's
        final List<Long> jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);
        setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);
        return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
    }

    /**
     * Create an new informational message object that encapsulates the error that came from making
     * an job operation request to the ebookGenerator REST service job operations.
     * @param cause the exception thrown
     * @return a new informational message suitable for display
     */
    public static InfoMessage createRestExceptionMessage(
        final String mesgCode,
        final Long id,
        final Throwable cause,
        final MessageSourceAccessor messageSourceAccessor) {
        final String[] args = {id.toString(), cause.getMessage()};
        final String messageText = messageSourceAccessor.getMessage(mesgCode, args);
        return new InfoMessage(Type.ERROR, messageText);
    }
}
