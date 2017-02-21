package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

@SavePublishingStatusService
public class GeneralPublishingStatusUpdateService extends BasePublishingStatusUpdateService
{
    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.publishingstatus.service.PublishingStatusUpdateService#savePublishingStats(com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep, com.thomsonreuters.uscl.ereader.stats.PublishingStatus)
     */
    @Override
    public void savePublishingStats(final BookStepImpl step, final PublishingStatus publishStatus)
    {
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(step.getJobInstanceId());
        jobstats.setPublishStatus(getPublishStatusString(step, publishStatus));
        publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
    }
}
