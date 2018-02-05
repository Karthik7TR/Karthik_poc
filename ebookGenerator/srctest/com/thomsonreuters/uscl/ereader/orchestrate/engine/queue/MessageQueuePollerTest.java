package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.JobParameters;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RunWith(MockitoJUnitRunner.class)
public final class MessageQueuePollerTest {
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

    private XppBundleQueuePoller poller;

    @Test
    public void shouldInterruptIfEnvironmentIsWorkStation() {
        //given
        initPoller("workstation");
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor, never()).processPlannedOutages();
        verify(threadPoolTaskExecutor, never()).getCorePoolSize();
        verify(threadPoolTaskExecutor, never()).getActiveCount();

        verify(mockJmsClient, never()).receiveSingleMessage(mockJmsTemplate, StringUtils.EMPTY);
        verify(engineService, never()).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    @Test
    public void shouldInterruptOutageExist() {
        //given
        initPoller("PROD");
        given(outageProcessor.processPlannedOutages()).willReturn(mock(PlannedOutage.class));
        given(threadPoolTaskExecutor.getCorePoolSize()).willReturn(POOL_SIZE);
        given(threadPoolTaskExecutor.getActiveCount()).willReturn(ACTIVE_THREADS);
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor).processPlannedOutages();
        verify(threadPoolTaskExecutor).getCorePoolSize();
        verify(threadPoolTaskExecutor).getActiveCount();

        verify(mockJmsClient, never()).receiveSingleMessage(mockJmsTemplate, StringUtils.EMPTY);
        verify(engineService, never()).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    @Test
    public void shouldInterruptNoFreeThreads() {
        //given
        initPoller("PROD");
        given(threadPoolTaskExecutor.getCorePoolSize()).willReturn(POOL_SIZE);
        given(threadPoolTaskExecutor.getActiveCount()).willReturn(POOL_SIZE);
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor).processPlannedOutages();
        verify(threadPoolTaskExecutor).getCorePoolSize();
        verify(threadPoolTaskExecutor).getActiveCount();

        verify(mockJmsClient, never()).receiveSingleMessage(mockJmsTemplate, StringUtils.EMPTY);
        verify(engineService, never()).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    @Test
    public void shouldInterruptRequestIsNull() {
        //given
        initPoller("PROD");
        given(threadPoolTaskExecutor.getCorePoolSize()).willReturn(POOL_SIZE);
        given(threadPoolTaskExecutor.getActiveCount()).willReturn(ACTIVE_THREADS);
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor).processPlannedOutages();
        verify(threadPoolTaskExecutor).getCorePoolSize();
        verify(threadPoolTaskExecutor).getActiveCount();

        verify(mockJmsClient).receiveSingleMessage(mockJmsTemplate, StringUtils.EMPTY);
        verify(engineService, never()).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    @Test
    public void shouldInterruptRequestIsEmpty() {
        //given
        initPoller("PROD");
        given(threadPoolTaskExecutor.getCorePoolSize()).willReturn(POOL_SIZE);
        given(threadPoolTaskExecutor.getActiveCount()).willReturn(ACTIVE_THREADS);
        given(mockJmsClient.receiveSingleMessage(mockJmsTemplate, StringUtils.EMPTY)).willReturn(StringUtils.EMPTY);
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor).processPlannedOutages();
        verify(threadPoolTaskExecutor).getCorePoolSize();
        verify(threadPoolTaskExecutor).getActiveCount();

        verify(mockJmsClient).receiveSingleMessage(mockJmsTemplate, StringUtils.EMPTY);
        verify(engineService, never()).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    @Test
    public void shouldSuccessfullyComplete() {
        //given
        initPoller("PROD");
        given(threadPoolTaskExecutor.getCorePoolSize()).willReturn(POOL_SIZE);
        given(threadPoolTaskExecutor.getActiveCount()).willReturn(ACTIVE_THREADS);
        given(mockJmsClient.receiveSingleMessage(mockJmsTemplate, StringUtils.EMPTY)).willReturn("xppBundleMessage");
        //when
        poller.pollMessageQueue();
        //then
        verify(outageProcessor).processPlannedOutages();
        verify(threadPoolTaskExecutor).getCorePoolSize();
        verify(threadPoolTaskExecutor).getActiveCount();

        verify(mockJmsClient).receiveSingleMessage(mockJmsTemplate, StringUtils.EMPTY);
        verify(engineService).runJob(eq(JobParameterKey.JOB_NAME_PROCESS_BUNDLE), any(JobParameters.class));
    }

    private void initPoller(final String environment) {
        poller = new XppBundleQueuePoller(mockJmsClient, mockJmsTemplate, outageProcessor,
            threadPoolTaskExecutor, environment, engineService);
    }
}
