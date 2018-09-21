package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * URL based Spring Batch job control operations for RESTART and STOP.
 */
@Slf4j
@Controller
public class OperationsController {
    private final EngineService engineService;
    private final MessageSourceAccessor messageSourceAccessor;
    private final AppConfigService appConfigService;
    private final OutageProcessor outageProcessor;
    private final Map<String, Collection<String>> jobs;

    @Autowired
    public OperationsController(final EngineService engineService, final MessageSourceAccessor messageSourceAccessor,
                                final AppConfigService appConfigService, final OutageProcessor outageProcessor,
                                final List<FlowJob> jobs) {
        this.engineService = engineService;
        this.messageSourceAccessor = messageSourceAccessor;
        this.appConfigService = appConfigService;
        this.outageProcessor = outageProcessor;
        this.jobs = jobs.stream()
            .collect(Collectors.toMap(FlowJob::getName, FlowJob::getStepNames));
    }

    /** Maximum number of jobs allowed to run concurrently */

    /**
     * Handle REST request to restart an currently stopped or failed job.
     * Only a superuser, or the user who started the job in the first place is allowed to perform this operation.
     * @param jobExecutionId the job execution ID of the job to restart (required).
     * @return the view name which will marshal and return the SimpleRestServiceResponse object.
     */
    @RequestMapping(value = WebConstants.URI_JOB_RESTART, method = RequestMethod.GET)
    public ModelAndView restartJob(@PathVariable final Long jobExecutionId, final Model model) throws Exception {
        final Long jobExecutionIdToRestart = jobExecutionId;
        log.debug("jobExecutionIdToRestart=" + jobExecutionIdToRestart);

        SimpleRestServiceResponse opResponse = null;
        try {
            final PlannedOutage outage = outageProcessor.findPlannedOutageInContainer(new Date());
            if (outage != null) {
                final SimpleDateFormat sdf = new SimpleDateFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN);
                final String message = String.format(
                    "Cannot restart job because we are in a planned service outage until %s",
                    sdf.format(outage.getEndTime()));
                opResponse = new SimpleRestServiceResponse(jobExecutionIdToRestart, false, message);
            } else {
                final Long restartedJobExecutionId = engineService.restartJob(jobExecutionIdToRestart);
                opResponse = new SimpleRestServiceResponse(restartedJobExecutionId);
            }
        } catch (final JobInstanceAlreadyCompleteException e) { // Cannot restart a job that is already finished
            final Object[] args = {jobExecutionIdToRestart.toString()};
            final String message = messageSourceAccessor.getMessage("err.job.instance.already.complete", args);
            opResponse = new SimpleRestServiceResponse(jobExecutionIdToRestart, false, message);
            log.debug(message, e);
        } catch (final Exception e) {
            opResponse = new SimpleRestServiceResponse(jobExecutionIdToRestart, false, e.getMessage());
            log.error("Job RESTART exception", e);
        }
        model.addAttribute(CoreConstants.KEY_SIMPLE_REST_RESPONSE, opResponse);
        return new ModelAndView(CoreConstants.VIEW_SIMPLE_REST_RESPONSE);
    }

    /**
     * Handle REST request to stop an currently execution job.
     * Only a superuser, or the user who started the job in the first place is allowed to perform this operation.
     * @param jobExecutionId the job execution ID of the job to stop (required).
     * @return the view name which will marshal and return the SimpleRestServiceResponse object.
     */
    @RequestMapping(value = WebConstants.URI_JOB_STOP, method = RequestMethod.GET)
    public ModelAndView stopJob(@PathVariable final Long jobExecutionId, final Model model) {
        final Long jobExecutionIdToStop = jobExecutionId;
        log.debug("jobExecutionIdToStop=" + jobExecutionIdToStop);

        SimpleRestServiceResponse opResponse = null;
        try {
            engineService.stopJob(jobExecutionIdToStop);
            opResponse = new SimpleRestServiceResponse(jobExecutionIdToStop);
        } catch (final JobExecutionNotRunningException e) { // Cannot stop a job that is not running
            final Object[] args = {jobExecutionIdToStop.toString()};
            final String message = messageSourceAccessor.getMessage("err.job.execution.not.running", args);
            opResponse = new SimpleRestServiceResponse(jobExecutionIdToStop, false, message);
            log.debug(message, e);
        } catch (final Exception e) {
            opResponse = new SimpleRestServiceResponse(jobExecutionIdToStop, false, e.getMessage());
            log.debug("Job STOP exception: " + e);
        }
        model.addAttribute(CoreConstants.KEY_SIMPLE_REST_RESPONSE, opResponse);
        return new ModelAndView(CoreConstants.VIEW_SIMPLE_REST_RESPONSE);
    }

    @RequestMapping(value = CoreConstants.URI_GET_JOB_THROTTLE_CONFIG, method = RequestMethod.GET)
    public ModelAndView getJobThrottleConfig(final Model model) {
        log.debug(">>>");
        final JobThrottleConfig config = appConfigService.loadJobThrottleConfig();
        model.addAttribute(WebConstants.KEY_JOB_THROTTLE_CONFIG, config);
        return new ModelAndView(WebConstants.VIEW_JOB_THROTTLE_CONFIG_RESPONSE);
    }

    @RequestMapping(value = CoreConstants.URI_GET_MISC_CONFIG, method = RequestMethod.GET)
    public ModelAndView getMiscConfig(final Model model) {
        log.debug(">>>");
        final MiscConfig config = appConfigService.loadMiscConfig();
        model.addAttribute(WebConstants.KEY_MISC_CONFIG, config);
        return new ModelAndView(WebConstants.VIEW_MISC_CONFIG_RESPONSE);
    }

    @RequestMapping(value = WebConstants.URI_GET_STEP_NAMES, method = RequestMethod.GET)
    public void getStepNames(final HttpServletResponse response, @PathVariable final String jobName) {
        log.debug(">>>");

        if (jobs.containsKey(jobName)) {
            try {
                response.getOutputStream().print(String.join(",", jobs.get(jobName)));
                response.setStatus(HttpStatus.OK.value());
            } catch (final IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }
}
