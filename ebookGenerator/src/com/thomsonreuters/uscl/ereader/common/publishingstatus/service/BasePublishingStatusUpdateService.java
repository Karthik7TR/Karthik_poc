package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public abstract class BasePublishingStatusUpdateService<T extends BookStep>
    implements PublishingStatusUpdateService<T> {
    @Resource(name = "publishingStatsService")
    protected PublishingStatsService publishingStatsService;

    public String getPublishStatusString(final T step, final PublishingStatus publishStatus) {
        return String.format("%s : %s", step.getStepName(), publishStatus);
    }
}
