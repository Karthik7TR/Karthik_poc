package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTask;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

@SavePublishingStatusStrategy(StatsUpdateTypeEnum.GATHERIMAGE)
public class GatherImagePublishingStatusUpdateService extends BasePublishingStatusUpdateService<BookStep>
{

    @Override
    public void savePublishingStats(final BookStep step, final PublishingStatus publishStatus)
    {
        final PublishingStats jobstatsDoc = new PublishingStats();
        jobstatsDoc.setJobInstanceId(step.getJobInstanceId());

        if (step instanceof GatherDynamicImagesTask)
        {
            jobstatsDoc.setGatherImageExpectedCount(((GatherDynamicImagesTask)step).getImageGuidNum());
            jobstatsDoc.setGatherImageRetrievedCount(((GatherDynamicImagesTask)step).getRetrievedCount());
        }
        jobstatsDoc.setPublishStatus(getPublishStatusString(step, publishStatus));
        publishingStatsService.updatePublishingStats(jobstatsDoc, StatsUpdateTypeEnum.GATHERIMAGE);
    }
}
