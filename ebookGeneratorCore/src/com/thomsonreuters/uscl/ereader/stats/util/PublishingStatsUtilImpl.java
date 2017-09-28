package com.thomsonreuters.uscl.ereader.stats.util;

import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import org.springframework.stereotype.Service;

@Service("publishingStatsUtil")
public class PublishingStatsUtilImpl implements PublishingStatsUtil
{
    @Override
    public boolean isPublishedSuccessfully(final String publishStatus)
    {
        return PublishingStats.SUCCESFULL_PUBLISH_STATUS.equalsIgnoreCase(publishStatus)
            || PublishingStats.SEND_EMAIL_COMPLETE.equalsIgnoreCase(publishStatus)
            || PublishingStats.SEND_EMAIL_COMPLETE_XPP.equalsIgnoreCase(publishStatus);
    }
}
