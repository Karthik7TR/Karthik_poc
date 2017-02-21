package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.io.File;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyService;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class AssemblePublishingStatusUpdateServiceTest
{
    @InjectMocks
    private AssemblePublishingStatusUpdateService service;
    @Mock
    private EBookAssemblyService assemblyService;
    @Mock
    private PublishingStatusUpdateService<PublishingStatusUpdateStep> generalService;
    @Mock
    private PublishingStatsService publishingStatsService;
    @Mock
    private BookStepImpl step;
    @Mock
    private File dir;

    @Test
    public void shouldCallGeneralServiceIfFailed()
    {
        //given
        //when
        service.savePublishingStats(step, PublishingStatus.FAILED);
        //then
        then(generalService).should().savePublishingStats(step, PublishingStatus.FAILED);
    }

    @Test
    public void shouldSavePublishingStatisIfComplete()
    {
        //given
        given(step.getAssembleDocumentsDirectory()).willReturn(dir);
        given(step.getAssembleAssetsDirectory()).willReturn(dir);
        given(step.getAssembledBookFile()).willReturn(dir);
        //when
        service.savePublishingStats(step, PublishingStatus.COMPLETED);
        //then
        then(publishingStatsService).should()
            .updatePublishingStats(any(PublishingStats.class), eq(StatsUpdateTypeEnum.ASSEMBLEDOC));
    }
}
