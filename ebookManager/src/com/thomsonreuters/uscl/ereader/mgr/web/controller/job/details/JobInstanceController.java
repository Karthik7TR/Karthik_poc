package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobExecutionVdo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.StepStartTimeComparator;
import com.thomsonreuters.uscl.ereader.mgr.web.service.job.JobService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the Job Instance Details page.
 */
@Controller
public class JobInstanceController {
    private static final StepStartTimeComparator STEPS_START_TIME_COMPARATOR = new StepStartTimeComparator();

    private final JobService jobService;
    private final PublishingStatsService publishingStatsService;
    private final OutageService outageService;

    @Autowired
    public JobInstanceController(
        final JobService jobService,
        final PublishingStatsService publishingStatsService,
        final OutageService outageService) {
        this.jobService = jobService;
        this.publishingStatsService = publishingStatsService;
        this.outageService = outageService;
    }

    /**
     * Create a aggregated list of StepExecution's from all JobInstance's specified by id.
     * @param jobInstanceId
     * @param model
     * @return the view to the Job Instance Steps page
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_JOB_INSTANCE_DETAILS, method = RequestMethod.GET)
    public ModelAndView inboundGet(@RequestParam("jobInstanceId") final Long jobInstanceId, final Model model)
        throws Exception {
//		log.debug(">>> jobInstanceId="+jobInstanceId);
        final JobInstance jobInstance = (jobInstanceId != null) ? jobService.findJobInstance(jobInstanceId) : null;
        if (jobInstance != null) {
            long totalDurationOfAllExecutions = 0;
            final PublishingStats publishingStats =
                publishingStatsService.findPublishingStatsByJobId(jobInstance.getId());
            final EbookAudit bookInfo = publishingStats.getAudit();
            final List<JobExecution> jobExecutions = jobService.findJobExecutions(jobInstance);
            final List<StepExecution> allJobInstanceSteps = new ArrayList<>();
            for (final JobExecution je : jobExecutions) {
                totalDurationOfAllExecutions += JobSummary.getExecutionDuration(je.getStartTime(), je.getEndTime());
                final Collection<StepExecution> stepExecutions = je.getStepExecutions();
                allJobInstanceSteps.addAll(stepExecutions);
            }

            // Get the job execution for the last step run, used to determine if we can restart the job here
            if (allJobInstanceSteps.size() > 0) {
                Collections.sort(allJobInstanceSteps, STEPS_START_TIME_COMPARATOR); // Descending sort
                final StepExecution lastStepExecution = allJobInstanceSteps.get(0);
                final JobExecution lastJobExecution = lastStepExecution.getJobExecution();
                final JobExecutionVdo vdo = new JobExecutionVdo(lastJobExecution, bookInfo, publishingStats);
                model.addAttribute(WebConstants.KEY_JOB, vdo);
            }

            model.addAttribute(
                WebConstants.KEY_JOB_INSTANCE_DURATION,
                JobSummary.getExecutionDuration(totalDurationOfAllExecutions));
            populateModel(model, jobInstance, bookInfo, allJobInstanceSteps);
        }
        return new ModelAndView(WebConstants.VIEW_JOB_INSTANCE_DETAILS);
    }

    private void populateModel(
        final Model model,
        final JobInstance jobInstance,
        final EbookAudit bookInfo,
        final List<StepExecution> allJobInstanceSteps) {
        model.addAttribute(WebConstants.KEY_JOB_INSTANCE, jobInstance);
        model.addAttribute(WebConstants.KEY_JOB_BOOK_INFO, bookInfo);
        model.addAttribute(WebConstants.KEY_JOB_STEP_EXECUTIONS, allJobInstanceSteps);
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
    }
}
