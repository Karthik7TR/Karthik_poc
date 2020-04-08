package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

@SavePublishingStatusStrategy(StatsUpdateTypeEnum.FINALPUBLISH)
public class DeliverPublishingStatusUpdateService extends BasePublishingStatusUpdateService<BookStep> {
    @Override
    public void savePublishingStats(final BookStep step, final PublishingStatus publishStatus) {
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(step.getJobInstanceId());
        jobstats.setPublishStatus(getPublishStatusString(step, publishStatus));
        publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.FINALPUBLISH);
    }
}