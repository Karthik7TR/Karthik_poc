package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public abstract class BasePublishingStatusUpdateService implements PublishingStatusUpdateService<BookStepImpl>
{
    @Resource(name = "publishingStatsService")
    protected PublishingStatsService publishingStatsService;

    public String getPublishStatusString(final BookStepImpl step, final PublishingStatus publishStatus)
    {
        return String.format("%s : %s", step.getStepName(), publishStatus);
    }
}
