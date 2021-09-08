package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;

@SavePublishingStatusStrategy(StatsUpdateTypeEnum.PREPARE_SOURCES)
public class PrepareSourcesPublishingStatusUpdateService extends BasePublishingStatusUpdateService<BookStep>{

    @Override
    public void savePublishingStats(BookStep step, PublishingStatus publishStatus) {

    }
}
