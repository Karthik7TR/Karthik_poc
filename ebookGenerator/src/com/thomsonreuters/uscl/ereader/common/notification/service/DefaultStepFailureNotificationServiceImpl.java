package com.thomsonreuters.uscl.ereader.common.notification.service;

import com.thomsonreuters.uscl.ereader.common.notification.step.SendNotificationStep;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Dummy implementation
 */
public class DefaultStepFailureNotificationServiceImpl<T extends SendNotificationStep>
{
    private static final Logger LOG = LogManager.getLogger(DefaultStepFailureNotificationServiceImpl.class);

    void emailFailure(final T step, final Exception e)
    {
        LOG.error(step.toString(), e);
    }
}
