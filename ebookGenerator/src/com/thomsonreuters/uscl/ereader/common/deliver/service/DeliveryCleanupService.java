package com.thomsonreuters.uscl.ereader.common.deliver.service;

import com.thomsonreuters.uscl.ereader.common.deliver.step.DeliverStep;

public interface DeliveryCleanupService
{
    /**
     * Remove all published split titles
     */
    void cleanup(DeliverStep step);
}
