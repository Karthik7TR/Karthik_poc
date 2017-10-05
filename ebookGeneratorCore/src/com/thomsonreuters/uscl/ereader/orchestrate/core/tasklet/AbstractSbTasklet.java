package com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutageException;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Required;

/**
 * Wrapper designed to handle exceptions thrown from step execution business as a STOPPED exit status for the step and job.
 * This is a specific requirement for eReader.
 */
public abstract class AbstractSbTasklet implements Tasklet {
    private static final Logger LOG = LogManager.getLogger(AbstractSbTasklet.class);

    protected CoreService coreService;
    protected NotificationService notificationService;
    private OutageProcessor outageProcessor;

    /**
     * Implement this method in the concrete subclass.
     * @return the transition name for the step in the form of an ExitStatus.
     * Return ExitStatus.COMPLETED for a normal finish.
     * Returning a custom ExitStatus will always result in the step BatchStatus being set to BatchStatus.COMPLETED.
     */
    public abstract ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception;

    /**
     * Wrapper around the user implemented task logic that hides the repeat and transition calculations away.
     */
    @Override
    public final RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final StepContext stepContext = chunkContext.getStepContext();
        LOG.debug("Step: " + stepContext.getJobName() + "." + stepContext.getStepName());
        final StepExecution stepExecution = stepContext.getStepExecution();
        final JobExecution jobExecution = stepExecution.getJobExecution();
        final long jobInstanceId = jobExecution.getJobInstance().getId();
        final long jobExecutionId = stepExecution.getJobExecutionId();
        ExitStatus stepExitStatus = null;
        try {
            // Check if a planned outage has come into effect, if so, fail this step right at the start
            // with an exit message indicating the interval of the outage.
            final PlannedOutage plannedOutage = outageProcessor.processPlannedOutages();
            if (plannedOutage != null) {
                LOG.debug("Failing job step at start due to planned outage: " + plannedOutage);
                final SimpleDateFormat sdf = new SimpleDateFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN);
                final Exception e = new PlannedOutageException(
                    String.format(
                        "Planned service outage in effect from %s to %s",
                        sdf.format(plannedOutage.getStartTime()),
                        sdf.format(plannedOutage.getEndTime())));
                final StackTraceElement[] stackTraceElementArray = {};
                e.setStackTrace(stackTraceElementArray);
                throw e;
            } else {
                // Execute user defined step logic
                stepExitStatus = executeStep(contribution, chunkContext);
            }
            // Set the step execution exit status (transition) name to what was returned from executeStep() in the subclass
            stepExecution.setExitStatus(stepExitStatus);
        } catch (final PlannedOutageException e) {
            // Just catch and rethrow as not to send an email message
            throw e;
        } catch (final Exception e) {
            String stackTrace = getStackTrace(e);

            stackTrace = "Error Message : " + e.getMessage() + "\nStack Trace is " + stackTrace;
            notificationService.sendNotification(
                getJobExecutionContext(chunkContext),
                getJobParameters(chunkContext),
                stackTrace,
                jobInstanceId,
                jobExecutionId);
            throw e;
        }

        return RepeatStatus.FINISHED;
    }

    /**
     * Retrieves the JobParameters from the Spring Batch ChunkContext.
     *
     * <p>Job Parameters are set at job initialization time, and are immutable afterwards.</p>
     * @param chunkContext the Spring Batch context exposed to the step implementor (to retrieve Job Parameters from)
     * @return the Job Parameters
     */
    protected static JobParameters getJobParameters(final ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution().getJobParameters();
    }

    /**
     * Retrieves the Job ExecutionContext from the Spring Batch ChunkContext.
     *
     * <p>Job Execution Context is where we put information for later steps to use. Each value added to the Job Execution Context is< mutable.</p>
     * @param chunkContext the Spring Batch context exposed to the step implementor (to retrieve Job Execution Context from)
     * @return the Job Execution context
     */
    protected static ExecutionContext getJobExecutionContext(final ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    }

    protected static JobInstance getJobInstance(final ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance();
    }

    /**
     * Retrieves a String property from the provided ExecutionContext. An existence check is performed prior to attempting type-safe retrieval using the Spring Batch API.
     *
     * @param executionContext The ExecutionContext that contains the property to retrieve.
     * @param propertyKey the name of the property.  The key used as an argument to this method should be exposed via the JobExecutionKey class. If deviation is necessary, talk to Chris, Tom and Nirupam.
     *
     * @return the String value corresponding to the provided key.
     */
    protected static String getRequiredStringProperty(
        final ExecutionContext executionContext,
        final String propertyKey) {
        assertPropertyExists(executionContext, propertyKey);
        return executionContext.getString(propertyKey);
    }

    /**
     * Retrieves a int property from the provided ExecutionContext. An existence check is performed prior to attempting type-safe retrieval using the Spring Batch API.
     *
     * @param executionContext The ExecutionContext that contains the property to retrieve.
     * @param propertyKey the name of the property.  The key used as an argument to this method should be exposed via the JobExecutionKey class. If deviation is necessary, talk to Chris, Tom and Nirupam.
     *
     * @return the int value corresponding to the provided key.
     */
    protected static int getRequiredIntProperty(final ExecutionContext executionContext, final String propertyKey) {
        assertPropertyExists(executionContext, propertyKey);
        return executionContext.getInt(propertyKey);
    }

    /**
     * Retrieves a long property from the provided ExecutionContext. An existence check is performed prior to attempting type-safe retrieval using the Spring Batch API.
     *
     * @param executionContext The ExecutionContext that contains the property to retrieve.
     * @param propertyKey the name of the property.  The key used as an argument to this method should be exposed via the JobExecutionKey class. If deviation is necessary, talk to Chris, Tom and Nirupam.
     *
     * @return the long value corresponding to the provided key.
     */
    protected static long getRequiredLongProperty(final ExecutionContext executionContext, final String propertyKey) {
        assertPropertyExists(executionContext, propertyKey);
        return executionContext.getLong(propertyKey);
    }

    /**
     * Retrieves a double property from the provided ExecutionContext. An existence check is performed prior to attempting type-safe retrieval using the Spring Batch API.
     *
     * @param executionContext The ExecutionContext that contains the property to retrieve.
     * @param propertyKey the name of the property.  The key used as an argument to this method should be exposed via the JobExecutionKey class. If deviation is necessary, talk to Chris, Tom and Nirupam.
     *
     * @return the double value corresponding to the provided key.
     */
    protected static double getRequiredDoubleProperty(
        final ExecutionContext executionContext,
        final String propertyKey) {
        assertPropertyExists(executionContext, propertyKey);
        return executionContext.getDouble(propertyKey);
    }

    private static void assertPropertyExists(final ExecutionContext executionContext, final String propertyKey) {
        if (!executionContext.containsKey(propertyKey)) {
            throw new IllegalArgumentException(
                "The required property '"
                    + propertyKey
                    + "' was not present in the execution context, but should have been. "
                    + "This is considered a programming error, please contact development. The job cannot continue until the '"
                    + propertyKey
                    + "' property is present.");
        }
    }

    /**
     * @param aThrowable
     * @return string corresponding to the provided exception's stack trace.
     */
    private String getStackTrace(final Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    @Required
    public void setOutageProcessor(final OutageProcessor service) {
        outageProcessor = service;
    }

    @Required
    public void setCoreService(final CoreService service) {
        coreService = service;
    }

    @Required
    public void setNotificationService(final NotificationService service) {
        notificationService = service;
    }
}
