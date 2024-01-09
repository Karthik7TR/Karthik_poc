package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import javax.jms.TextMessage;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.JobStartupThrottleService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.JobParameters;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RunWith(MockitoJUnitRunner.class)
public final class XppBundleQueuePollerTest {
    private static final Integer POOL_SIZE = 5;
    private static final Integer ACTIVE_THREADS = 4;

    @Mock
    private JMSClient mockJmsClient;
    @Mock
    private JmsTemplate mockJmsTemplate;
    @Mock
    private OutageProcessor outageProcessor;
    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Mock
    private EngineService engineService;
    @Mock
    private JobStartupThrottleService jobStartupThrottleService;

    private XppBundleQueuePoller poller;

    @Test
    @SneakyThrows
    public void shouldInterruptIfEnvironmentIsWorkStation() {
        //given
        initPoller("workstation");
        given(jobStartupThrottleService.checkIfnewJobCanbeLaunched()).willReturn(true);
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor, never()).processPlannedOutages();
        verify(threadPoolTaskExecutor, never()).getCorePoolSize();
        verify(threadPoolTaskExecutor, never()).getActiveCount();

        verify(mockJmsTemplate, never()).receive();
        verify(engineService, never()).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    @Test
    @SneakyThrows
    public void shouldInterruptOutageExist() {
        //given
        initPoller("PROD");
        given(outageProcessor.processPlannedOutages()).willReturn(mock(PlannedOutage.class));
        given(threadPoolTaskExecutor.getCorePoolSize()).willReturn(POOL_SIZE);
        given(threadPoolTaskExecutor.getActiveCount()).willReturn(ACTIVE_THREADS);
        given(jobStartupThrottleService.checkIfnewJobCanbeLaunched()).willReturn(true);
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor).processPlannedOutages();
        verify(threadPoolTaskExecutor).getCorePoolSize();
        verify(threadPoolTaskExecutor).getActiveCount();

        verify(mockJmsTemplate, never()).receive();
        verify(engineService, never()).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    @Test
    @SneakyThrows
    public void shouldInterruptNoFreeThreads() {
        //given
        initPoller("PROD");
        given(threadPoolTaskExecutor.getCorePoolSize()).willReturn(POOL_SIZE);
        given(threadPoolTaskExecutor.getActiveCount()).willReturn(POOL_SIZE);
        given(jobStartupThrottleService.checkIfnewJobCanbeLaunched()).willReturn(true);
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor).processPlannedOutages();
        verify(threadPoolTaskExecutor).getCorePoolSize();
        verify(threadPoolTaskExecutor).getActiveCount();

        verify(mockJmsTemplate, never()).receive();
        verify(engineService, never()).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    @Test
    @SneakyThrows
    public void shouldInterruptRequestIsNull() {
        //given
        initPoller("PROD");
        given(threadPoolTaskExecutor.getCorePoolSize()).willReturn(POOL_SIZE);
        given(threadPoolTaskExecutor.getActiveCount()).willReturn(ACTIVE_THREADS);
        given(jobStartupThrottleService.checkIfnewJobCanbeLaunched()).willReturn(true);
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor).processPlannedOutages();
        verify(threadPoolTaskExecutor).getCorePoolSize();
        verify(threadPoolTaskExecutor).getActiveCount();

        verify(mockJmsTemplate).receive();
        verify(engineService, never()).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    @Test
    @SneakyThrows
    public void shouldInterruptRequestIsEmpty() {
        //given
        initPoller("PROD");
        given(threadPoolTaskExecutor.getCorePoolSize()).willReturn(POOL_SIZE);
        given(threadPoolTaskExecutor.getActiveCount()).willReturn(ACTIVE_THREADS);
        final TextMessage emptyMessage = mock(TextMessage.class);
        given(emptyMessage.getText()).willReturn(StringUtils.EMPTY);
        given(mockJmsTemplate.receive()).willReturn(emptyMessage);
        given(jobStartupThrottleService.checkIfnewJobCanbeLaunched()).willReturn(true);
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor).processPlannedOutages();
        verify(threadPoolTaskExecutor).getCorePoolSize();
        verify(threadPoolTaskExecutor).getActiveCount();

        verify(mockJmsTemplate).receive();
        verify(engineService, never()).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    @Test
    @SneakyThrows
    public void shouldInterruptThrottle() {
        //given
        initPoller("PROD");
        given(threadPoolTaskExecutor.getCorePoolSize()).willReturn(POOL_SIZE);
        given(threadPoolTaskExecutor.getActiveCount()).willReturn(ACTIVE_THREADS);
        final TextMessage message = mock(TextMessage.class);
        given(message.getText()).willReturn("xppBundleMessage");
        given(mockJmsTemplate.receive()).willReturn(message);
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor).processPlannedOutages();
        verify(threadPoolTaskExecutor).getCorePoolSize();
        verify(threadPoolTaskExecutor).getActiveCount();

        verify(mockJmsTemplate, never()).receive();
        verify(engineService, never()).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    @Test
    @SneakyThrows
    public void shouldSuccessfullyComplete() {
        //given
        initPoller("PROD");
        given(threadPoolTaskExecutor.getCorePoolSize()).willReturn(POOL_SIZE);
        given(threadPoolTaskExecutor.getActiveCount()).willReturn(ACTIVE_THREADS);
        final TextMessage message = mock(TextMessage.class);
        given(message.getText()).willReturn("xppBundleMessage");
        given(mockJmsTemplate.receive()).willReturn(message);
        given(jobStartupThrottleService.checkIfnewJobCanbeLaunched()).willReturn(true);
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor).processPlannedOutages();
        verify(threadPoolTaskExecutor).getCorePoolSize();
        verify(threadPoolTaskExecutor).getActiveCount();

        verify(mockJmsTemplate).receive();
        verify(engineService).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    private void initPoller(final String environment) {
        poller = new XppBundleQueuePoller(mockJmsTemplate, outageProcessor,
            threadPoolTaskExecutor, environment, engineService, jobStartupThrottleService);
    }
}
