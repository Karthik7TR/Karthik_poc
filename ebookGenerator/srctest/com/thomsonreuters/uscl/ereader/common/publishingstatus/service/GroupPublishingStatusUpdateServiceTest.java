package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.group.step.GroupStep;
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
public final class GroupPublishingStatusUpdateServiceTest
{
    @InjectMocks
    private GroupPublishingStatusUpdateService service;
    @Mock
    private PublishingStatsService publishingStatsService;
    @Mock
    private GroupStep step;
    @Captor
    private ArgumentCaptor<PublishingStats> captor;

    @Test
    public void shouldSavePublishingStats()
    {
      //given
        given(step.getJobInstanceId()).willReturn(1L);
        given(step.getStepName()).willReturn("stepName");
        given(step.getGroupVersion()).willReturn(2L);
        //when
        service.savePublishingStats(step, PublishingStatus.FAILED);
        //then
        then(publishingStatsService).should().updatePublishingStats(captor.capture(), any(StatsUpdateTypeEnum.class));

        final PublishingStats stats = captor.getValue();
        assertThat(stats.getJobInstanceId(), is(1L));
        assertThat(stats.getGroupVersion(), is(2L));
        assertThat(stats.getPublishStatus(), is("stepName : FAILED"));
    }
}
