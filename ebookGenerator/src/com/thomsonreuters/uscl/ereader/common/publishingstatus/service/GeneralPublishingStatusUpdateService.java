package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

@SavePublishingStatusStrategy
public class GeneralPublishingStatusUpdateService extends BasePublishingStatusUpdateService<BookStep>
{
    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.publishingstatus.service.PublishingStatusUpdateService#savePublishingStats(com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep, com.thomsonreuters.uscl.ereader.stats.PublishingStatus)
     */
    @Override
    public void savePublishingStats(final BookStep step, final PublishingStatus publishStatus)
    {
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(step.getJobInstanceId());
        jobstats.setPublishStatus(getPublishStatusString(step, publishStatus));
        publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
    }
}
