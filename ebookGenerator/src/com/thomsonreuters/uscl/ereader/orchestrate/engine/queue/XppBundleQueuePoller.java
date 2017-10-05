package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class XppBundleQueuePoller {
    private static final Logger log = LogManager.getLogger(XppBundleQueuePoller.class);

    private JMSClient jmsClient;
    private JmsTemplate jmsTemplate;

    private JobRegistry jobRegistry;
    private JobLauncher jobLauncher;
    private OutageProcessor outageProcessor;
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private String environmentName;

    @Scheduled(fixedDelay = 60000) // 1 minute
    public void pollMessageQueue() {
        if ("workstation".equals(environmentName))
            return;
        try {
            /**
             * Do not start bundle processing if:
             * 		1) A planned outage is active, no jobs started until outage is over
             * 		2) The current number of concurrent job threads equals the configured pool size in the ThreadPoolTaskExecutor.
             * 		3) The application step specific rules prevent it from running.
             */
            final PlannedOutage outage = outageProcessor.processPlannedOutages();
            if (outage == null) {
                // Core pool size is the task executor pool size, effectively the maximum number of concurrent jobs.
                // This is dynamic and can be changed through the administrative UI.
                final int coreThreadPoolSize = threadPoolTaskExecutor.getCorePoolSize();
                final int activeThreads = threadPoolTaskExecutor.getActiveCount();
                if (activeThreads < coreThreadPoolSize) {
                    final String request = jmsClient.receiveSingleMessage(jmsTemplate, StringUtils.EMPTY);
                    if (StringUtils.isNotEmpty(request)) {
                        // start ebookBundleJob
                        final JobParametersBuilder builder = new JobParametersBuilder();
                        builder.addDate("timestamp", new Date());
                        builder.addParameter(JobParameterKey.KEY_REQUEST_XML, new JobParameter(request));
                        builder.addParameter(JobParameterKey.ENVIRONMENT_NAME, new JobParameter(environmentName));
                        runJob(JobParameterKey.JOB_NAME_PROCESS_BUNDLE, builder.toJobParameters());
                    } else {
                        // log.info("No bundle requests in the message queue.");
                    }
                } else {
                    log.debug(
                        String.format(
                            "There are %d active bundle processing threads running, the maximum allowed is %d.  "
                                + "No new jobs will be started until the active count is less than the maximum.",
                            activeThreads,
                            coreThreadPoolSize));
                }
            } else {
                log.debug(String.format("A planned outage is in effect until %s", outage.getEndTime().toString()));
            }
        } catch (final Exception e) {
            log.error("Failed start bundle process request: ", e);
        }
    }

    private JobExecution runJob(final String jobName, final JobParameters jobParameters) throws Exception {
        // Lookup job object from set of defined collection of jobs
        final Job job = jobRegistry.getJob(jobName);
        if (job == null) {
            throw new IllegalArgumentException("Job definition: " + jobName + " was not found!");
        }

        // Launch the job with the specified set of JobParameters
        log.debug("Launch Job Parameters: " + jobParameters);
        final JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        return jobExecution;
    }

    @Required
    public void setJobRegistry(final JobRegistry jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    public JobRegistry getJobRegistry() {
        return jobRegistry;
    }

    @Required
    public void setJobLauncher(final JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public JobLauncher getJobLauncher() {
        return jobLauncher;
    }

    @Required
    public void setJmsClient(final JMSClient jmsClient) {
        this.jmsClient = jmsClient;
    }

    public JMSClient getJMSClient() {
        return jmsClient;
    }

    @Required
    public void setJmsTemplate(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    @Required
    public void setOutageProcessor(final OutageProcessor outageProcessor) {
        this.outageProcessor = outageProcessor;
    }

    public OutageProcessor getOutageProcessor() {
        return outageProcessor;
    }

    @Required
    public void setThreadPoolTaskExecutor(final ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        return threadPoolTaskExecutor;
    }

    @Required
    public void setEnvironmentName(final String environmentName) {
        this.environmentName = environmentName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }
}
