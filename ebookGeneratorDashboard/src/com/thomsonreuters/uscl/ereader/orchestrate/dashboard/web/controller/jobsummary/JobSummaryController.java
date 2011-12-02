package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobsummary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.thomsonreuters.uscl.ereader.orchestrate.core.engine.EngineConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.SelectOption;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants.SortProperty;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobExecutionVdo;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service.DashboardService;

@Controller
public class JobSummaryController {
	private static final Logger log = Logger.getLogger(JobSummaryController.class);
	
	@Resource(name="environmentName")
	private String environmentName;
	@Autowired
	private JobExplorer jobExplorer;
	@Autowired
	private DashboardService service;
	@Resource(name="jobSummaryFormValidator")
	private Validator validator;
	
	@InitBinder(JobSummaryForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	//====================================================================================
	/**
	 * Handles the GET request page entry to display the most recent job executions.
	 * Queries for all the execution ID's that match the filter and saves the list of them in the HTTP session.
	 * The fully hydrated JobExecution objects are only queried for on a page-by-page basis as the user pages
	 * through the DisplayTag tabular presentation. 
	 */
	@RequestMapping(value=WebConstants.URL_JOB_SUMMARY, method = RequestMethod.GET)
	public ModelAndView jobSummaryGet(HttpSession httpSession,
							   @ModelAttribute(JobSummaryForm.FORM_NAME) JobSummaryForm form,
							   Model model) throws Exception {
		initializeForm(form, httpSession);
log.debug(">>> " + form);
		List<Long> filteredExecutionIds = service.findJobExecutionIds(EngineConstants.JOB_DEFINITION_EBOOK, form.getStartTime(), form.getBatchStatus());
		saveCurrentExecutionIdListOnSession(httpSession, filteredExecutionIds);  // for use in paging/sorting
		PaginatedList paginatedList = createPaginatedList(filteredExecutionIds, 1, form.getItemsPerPage(),
														  SortProperty.START_TIME, false);
		populateModel(model, httpSession, paginatedList);
		return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
	}


	//====================================================================================
	/**
	 * Handles the POST submission of the search filter to limit the number of displayed job executions.
	 * @param form the entered search criteria
	 * @param bindingResult any data binding or validation errors
	 * @param model a map whose key/value pairs end up as http request attributes.
	 * @return the Job Summary page view
	 */
	@RequestMapping(value=WebConstants.URL_JOB_SUMMARY, method = RequestMethod.POST)
	public ModelAndView jobSummaryPost(HttpSession httpSession,
							   @ModelAttribute(JobSummaryForm.FORM_NAME) @Valid JobSummaryForm form,
							   BindingResult bindingResult,
							   Model model) throws Exception {
log.debug(">>> " + form);
		PaginatedList paginatedList = null;
		if (!bindingResult.hasErrors()) {
			httpSession.setAttribute(WebConstants.KEY_SESSION_SUMMARY_FORM, form);  // Save the entered values
			List<Long> filteredExecutionIds = service.findJobExecutionIds(EngineConstants.JOB_DEFINITION_EBOOK, form.getStartTime(), form.getBatchStatus());
			saveCurrentExecutionIdListOnSession(httpSession, filteredExecutionIds);  // for use in paging/sorting
			paginatedList = createPaginatedList(filteredExecutionIds, 1, form.getItemsPerPage(),
											    SortProperty.START_TIME, false);
		}
		populateModel(model, httpSession, paginatedList);
		return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
	}
	
	
	//====================================================================================
	/**
	 * Handles the DisplayTag table external paging and sorting operations.
	 * Assumes a current list of execution ID's on the session that is reused with each paging/sorting operation.
	 * To get a completely up-to-date result set, the page must be refreshed (a new GET) or a new filtered search form must again be submitted (a POST).
	 */
	@RequestMapping(value=WebConstants.URL_JOB_SUMMARY_PAGING, method = RequestMethod.GET)
	public ModelAndView pagingAndSorting(HttpSession httpSession,
							   @ModelAttribute(JobSummaryForm.FORM_NAME) JobSummaryForm form,
							   Model model) throws Exception {
		initializeForm(form, httpSession);
log.debug(">>> " + form);
		// Fetch the current object list from the session
		JobExecutionPaginatedList paginatedList = (JobExecutionPaginatedList) fetchCurrentPaginatedListFromSession(httpSession);
		if (paginatedList == null) {  // Session probably timed-out
			return new ModelAndView("forward:"+WebConstants.URL_JOB_SUMMARY);
		}
			
		if (form.isPagingOperation()) {  // paging operation
			// List<Long>: the list of Execution ID's that match the current search filter as defined on the Summary page
			List<Long> filteredExecutionIds = fetchCurrentExecutionIdListFromSession(httpSession);
			SortProperty sortProperty = SortProperty.valueOf(paginatedList.getSortCriterion());
			paginatedList = createPaginatedList(filteredExecutionIds, form.getPageNumber(), form.getItemsPerPage(),
												sortProperty, paginatedList.isAscendingSort());
		} else {  // sorting operation
			SortProperty sortProperty = (form.getSortProperty() != null) ? form.getSortProperty() : SortProperty.START_TIME;
			boolean ascendingSort = form.isAscendingSort();
			paginatedList.sortList(sortProperty, ascendingSort);
		}
		
		populateModel(model, httpSession, paginatedList);
		return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
	}
	
	/**
	 * Initialize the Summary page form backing object when in-bound on initial page get methods.
	 * Use the previous search values from the form that was saved on the session.
	 * @param form initialized object
	 */
	private static void initializeForm(JobSummaryForm form, HttpSession httpSession) {
		JobSummaryForm sessionForm = (JobSummaryForm) httpSession.getAttribute(WebConstants.KEY_SESSION_SUMMARY_FORM);
		form.setItemsPerPage(JobSummaryForm.DEFAULT_ITEMS_PER_PAGE);
		if (sessionForm == null) { 
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			form.setStartDate(WebConstants.DATE_FORMAT.format(cal.getTime()));  // mm/dd/yyyy
		} else {
			form.copyUserFields(sessionForm);
		}
	}
	
	/**
	 * Create a DisplayTag PaginatedList object from a list of Job Execution id's.
	 * The PaginatedList is used for DisplayTag presentation and to support external paging and sorting.
	 * Each JobExecution object is individually queried by it primary key from the list of id's.
	 * Object Wrapping is as follows:
	 * A JobExecutionVdo wraps a JobExecution.
	 * A JobExecutionPaginatedList (which is a PaginatedList) wraps a List<JobExecutionVdo>.
	 * @param filteredExecutionIds the list of execution primary keys for the records that match the current search filter
	 * @param pageNumber pageNumber of data from within the full list of ID's
	 * @param sortProperty sort on which property
	 * @param ascending true if an ascending sort
	 * @return
	 */
    private JobExecutionPaginatedList createPaginatedList(List<Long> filteredExecutionIds,
    						int pageNumber, int itemsPerPage, SortProperty sortProperty, boolean ascending) {
		
		int allJobExecutionCount = filteredExecutionIds.size(); 

		// Calculate begin and end index for the current page number
		int startIndex = (pageNumber-1) * itemsPerPage;
		int endIndex = startIndex + itemsPerPage;
		endIndex = (endIndex < filteredExecutionIds.size()) ? endIndex : filteredExecutionIds.size();
		
		// Create the partial list of items as a function of the page number (using start and end indicies).
		List<Long> executionIdSubList = filteredExecutionIds.subList(startIndex, endIndex);
		List<JobExecution> jobExecutions = service.findJobExecutionByPrimaryKey(executionIdSubList);

		// Create the paginated list of View Data Objects (wrapping JobExecution) for use by DisplayTag table on the JSP.
		List<JobExecutionVdo> jobExecutionVdos = new ArrayList<JobExecutionVdo>();
		for (JobExecution je : jobExecutions) {
			jobExecutionVdos.add(new JobExecutionVdo(je));
		}
		
		JobExecutionPaginatedList paginatedList = new JobExecutionPaginatedList(
					jobExecutionVdos, allJobExecutionCount, pageNumber, itemsPerPage, sortProperty, ascending);
		return paginatedList;
	}
    
//    private JobExecution createFilter(JobSummaryForm form) {
//		JobExecution filter = new JobExecution((Long) null);
//		filter.setStatus(form.getBatchStatus());
//		filter.setStartTime(form.getStartTime());
////log.debug(filter);
//		return filter;
//    }
    
    /**
     * Fetch the current list (from the last filter search) of execution ID's from the session.
     * @param httpSession the users' current session
     * @return the current list on the session, or null if it is not present
     */
    @SuppressWarnings("unchecked")
    private static List<Long> fetchCurrentExecutionIdListFromSession(HttpSession httpSession) {
    	List<Long> executionIdList = (List<Long>) httpSession.getAttribute(WebConstants.KEY_FILTERED_EXECUTION_IDS);
    	return executionIdList;
    }
    /**
     * Fetch the current list (from the last page or sort) of Job execution VDO's from the session.
     * @param httpSession the users' current session
     * @return the current paginated list on the session, or null if it is not present
     */
    private static PaginatedList fetchCurrentPaginatedListFromSession(HttpSession httpSession) {
    	PaginatedList paginatedList = (PaginatedList) httpSession.getAttribute(WebConstants.KEY_PAGINATED_LIST);
    	return paginatedList;
    }
    private static void saveCurrentExecutionIdListOnSession(HttpSession httpSession, List<Long> filteredExecutionIds) {
    	httpSession.setAttribute(WebConstants.KEY_FILTERED_EXECUTION_IDS, filteredExecutionIds);
    }
    private static void saveCurrentPaginatedListOnSession(HttpSession httpSession, PaginatedList paginatedList) {
    	httpSession.setAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
    }

	private void populateModel(Model model, HttpSession httpSession, PaginatedList paginatedList) {

		/* Get all the unique job names */
		List<String> uniqueJobNames = jobExplorer.getJobNames();		
		List<SelectOption> jobNameOptions = new ArrayList<SelectOption>();
		for (String jobName : uniqueJobNames) {
			jobNameOptions.add(new SelectOption(jobName, jobName));
		}
		
		model.addAttribute(WebConstants.KEY_ENVIRONMENT, environmentName);
		model.addAttribute(WebConstants.KEY_JOB_NAMES, jobNameOptions);
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
		// Store the current JobExecution object list on session
		saveCurrentPaginatedListOnSession(httpSession, paginatedList);
	}
}
