package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.eq;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
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
public final class DeliverPublishingStatusUpdateServiceTest {
    @InjectMocks
    private DeliverPublishingStatusUpdateService service;
    @Mock
    private PublishingStatsService publishingStatsService;
    @Mock
    private BookStepImpl step;
    @Captor
    private ArgumentCaptor<PublishingStats> captor;

    @Test
    public void testSavePublishingStats() {
        //given
        given(step.getJobInstanceId()).willReturn(1L);
        given(step.getStepName()).willReturn("stepName");
        //when
        service.savePublishingStats(step, PublishingStatus.FAILED);
        //then
        then(publishingStatsService).should()
            .updatePublishingStats(captor.capture(), eq(StatsUpdateTypeEnum.FINALPUBLISH));

        final PublishingStats stats = captor.getValue();
        assertThat(stats.getJobInstanceId(), is(1L));
        assertThat(stats.getPublishStatus(), is("stepName : FAILED"));
    }
}
