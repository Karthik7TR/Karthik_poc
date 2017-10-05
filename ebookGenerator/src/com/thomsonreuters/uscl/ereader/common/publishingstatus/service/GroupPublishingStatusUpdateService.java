package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.group.step.GroupStep;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

@SavePublishingStatusStrategy(StatsUpdateTypeEnum.GROUPEBOOK)
public class GroupPublishingStatusUpdateService extends BasePublishingStatusUpdateService<GroupStep> {
    @Override
    public void savePublishingStats(final GroupStep step, final PublishingStatus publishStatus) {
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(step.getJobInstanceId());
        jobstats.setPublishStatus(getPublishStatusString(step, publishStatus));
        jobstats.setGroupVersion(step.getGroupVersion());
        publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GROUPEBOOK);
    }
}
