package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;

/**
 * Class to update job {@link com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats}
 */
public interface PublishingStatusUpdateService<T extends PublishingStatusUpdateStep> {
    /**
     * Save publish status
     * @param publishStatus status
     */
    void savePublishingStats(T step, PublishingStatus publishStatus);
}
