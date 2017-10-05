package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the Step Execution Details page.
 */
@Controller
public class StepExecutionController
{
    private final JobService jobService;
    private final PublishingStatsService publishingStatsService;
    private final OutageService outageService;

    @Autowired
    public StepExecutionController(final JobService jobService,
                                   final PublishingStatsService publishingStatsService,
                                   final OutageService outageService)
    {
        this.jobService = jobService;
        this.publishingStatsService = publishingStatsService;
        this.outageService = outageService;
    }

    /**
     * Set up model for viewing a specific job step.
     * @param jobInstanceId job instance ID  (job defn + when + job params)
     * @param jobExecutionId job execution ID  (represents a single run of a job instance)
     * @param stepExecutionId step execution ID
     */
    @RequestMapping(value = WebConstants.MVC_JOB_STEP_EXECUTION_DETAILS, method = RequestMethod.GET)
    public ModelAndView inboundGet(
        final HttpServletRequest request,
        @RequestParam("jobInstanceId") final Long jobInstanceId, // job instance ID
        @RequestParam("jobExecutionId") final Long jobExecutionId, // job execution ID
        @RequestParam("stepExecutionId") final Long stepExecutionId, // step execution ID
        final Model model)
    {
//		log.debug(">>> jobInstanceId="+jobInstanceId + ",jobExecutionId="+jobExecutionId + ",stepExecutionId="+stepExecutionId);
        final JobInstance jobInstance = jobService.findJobInstance(jobInstanceId);
        final EbookAudit bookInfo = publishingStatsService.findAuditInfoByJobId(jobInstance.getId());
        final StepExecution stepExecution = jobService.findStepExecution(jobExecutionId, stepExecutionId);
        populateModel(model, jobInstance, bookInfo, stepExecution);
        return new ModelAndView(WebConstants.VIEW_JOB_STEP_EXECUTION_DETAILS);
    }

    private void populateModel(
        final Model model,
        final JobInstance jobInstance,
        final EbookAudit bookInfo,
        final StepExecution stepExecution)
    {
        final List<Map.Entry<String, Object>> mapEntryList = createStepExecutionContextMapEntryList(stepExecution);
        model.addAttribute(WebConstants.KEY_JOB_INSTANCE, jobInstance);
        model.addAttribute(WebConstants.KEY_JOB_BOOK_INFO, bookInfo);
        model.addAttribute(WebConstants.KEY_JOB_STEP_EXECUTION, stepExecution);
        model.addAttribute(WebConstants.KEY_JOB_STEP_EXECUTION_CONTEXT_MAP_ENTRIES, mapEntryList);
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
    }

    /**
     * Create a list of map entries that represents all the key/value pairs inside the specified step execution.
     * @param stepExecution the step for whom we want the step execution context map entries.
     * @return a list of map entries inside the step execution context.
     */
    private static List<Map.Entry<String, Object>> createStepExecutionContextMapEntryList(
        final StepExecution stepExecution)
    {
        final List<Map.Entry<String, Object>> list = new ArrayList<>();
        if (stepExecution != null)
        {
            final ExecutionContext execContext = stepExecution.getExecutionContext();
            final Set<Map.Entry<String, Object>> entrySet = execContext.entrySet();
            final Iterator<Map.Entry<String, Object>> entryIterator = entrySet.iterator();
            while (entryIterator.hasNext())
            {
                list.add(entryIterator.next());
            }
        }
        return list;
    }
}
