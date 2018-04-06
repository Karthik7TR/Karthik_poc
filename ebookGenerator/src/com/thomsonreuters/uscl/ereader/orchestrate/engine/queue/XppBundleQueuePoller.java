package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import java.util.Date;
import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class XppBundleQueuePoller {
    private static final Logger LOG = LogManager.getLogger(XppBundleQueuePoller.class);
    private static final String FAIL_MESSAGE = "Failed start bundle process request: ";
    private static final String OUTAGE_MESSAGE_TEMPLATE = "A planned outage is in effect until %s";
    private static final String THREAD_POOL_MESSAGE = "There are %d active bundle processing threads running, the maximum allowed is %d.  "
        + "No new jobs will be started until the active count is less than the maximum.";

    private final JmsTemplate jmsTemplate;
    private final OutageProcessor outageProcessor;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final EngineService engineService;

    private String environmentName;

    @Autowired
    public XppBundleQueuePoller(final JmsTemplate jmsTemplate, final OutageProcessor outageProcessor,
                                @Qualifier("springBatchBundleTaskExecutor") final ThreadPoolTaskExecutor threadPoolTaskExecutor,
                                @Qualifier("environmentName") final String environmentName, final EngineService engineService) {
        this.jmsTemplate = jmsTemplate;
        this.outageProcessor = outageProcessor;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.environmentName = environmentName;
        this.engineService = engineService;
    }

    @Scheduled(fixedDelay = 60000)
    public void pollMessageQueue() {
        try {
            /**
             * Do not start bundle processing if:
             * 		1) A planned outage is active, no jobs started until outage is over
             * 		2) The current number of concurrent job threads equals the configured pool size in the ThreadPoolTaskExecutor.
             * 		3) The application step specific rules prevent it from running.
             */
            if (!"workstation".equalsIgnoreCase(environmentName) && isExecutionAvailable()) {
                Optional.ofNullable(getNextMessage())
                    .filter(StringUtils::isNoneBlank)
                    .ifPresent(this::performJobExecution);
            }
        } catch (final Exception e) {
            LOG.error(FAIL_MESSAGE, e);
        }
    }

    private boolean isExecutionAvailable() {
        final int coreThreadPoolSize = threadPoolTaskExecutor.getCorePoolSize();
        final int activeThreads = threadPoolTaskExecutor.getActiveCount();
        final PlannedOutage outage = outageProcessor.processPlannedOutages();

        boolean result = false;
        if (outage == null && activeThreads < coreThreadPoolSize) {
            result = true;
        } else if (outage != null) {
            LOG.debug(String.format(OUTAGE_MESSAGE_TEMPLATE, outage.getEndTime().toString()));
        } else {
            LOG.debug(String.format(THREAD_POOL_MESSAGE, activeThreads, coreThreadPoolSize));
        }
        return result;
    }

    private String getNextMessage() {
        try {
            final TextMessage message = (TextMessage) jmsTemplate.receive();
            return message == null ? StringUtils.EMPTY : message.getText();
        } catch (final JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private void performJobExecution(final String request) {
        // start ebookBundleJob
        final JobParametersBuilder builder = new JobParametersBuilder();
        builder.addDate("timestamp", new Date());
        builder.addParameter(JobParameterKey.KEY_REQUEST_XML, new JobParameter(request));
        builder.addParameter(JobParameterKey.ENVIRONMENT_NAME, new JobParameter(environmentName));
        engineService.runJob(JobParameterKey.JOB_NAME_PROCESS_BUNDLE, builder.toJobParameters());
    }
}
