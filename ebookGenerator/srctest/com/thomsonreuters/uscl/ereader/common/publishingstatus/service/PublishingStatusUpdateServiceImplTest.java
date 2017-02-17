package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class PublishingStatusUpdateServiceImplTest
{
    @InjectMocks
    private PublishingStatusUpdateServiceImpl service;
    @Mock
    private PublishingStatsService publishingStatsService;
    @Mock
    private EBookAuditService eBookAuditService;
    @Mock
    private BookStep step;
    @Captor
    private ArgumentCaptor<PublishingStats> captor;

    @Test
    public void shouldCreatePublishingStats()
    {
        //given
        given(step.isInitialStep()).willReturn(true);
        given(step.getBookDefinitionId()).willReturn(1L);
        given(step.getStepName()).willReturn("stepName");
        given(eBookAuditService.findEbookAuditByEbookDefId(1L)).willReturn(2L);
        //when
        service.savePublishingStats(step, PublishingStatus.COMPLETED);
        //then
        then(publishingStatsService).should().savePublishingStats(captor.capture());

        final PublishingStats stats = captor.getValue();
        assertThat(stats.getEbookDefId(), is(1L));
        assertThat(stats.getAudit().getAuditId(), is(2L));
        assertThat(stats.getPublishStatus(), is("stepName : COMPLETED"));
    }

    @Test
    public void shouldUpdatePublishingStats()
    {
        //given
        given(step.isInitialStep()).willReturn(false);
        given(step.getJobInstanceId()).willReturn(1L);
        given(step.getStepName()).willReturn("stepName");
        //when
        service.savePublishingStats(step, PublishingStatus.FAILED);
        //then
        then(publishingStatsService).should().updatePublishingStats(captor.capture(), any(StatsUpdateTypeEnum.class));

        final PublishingStats stats = captor.getValue();
        assertThat(stats.getJobInstanceId(), is(1L));
        assertThat(stats.getPublishStatus(), is("stepName : FAILED"));
    }
}
