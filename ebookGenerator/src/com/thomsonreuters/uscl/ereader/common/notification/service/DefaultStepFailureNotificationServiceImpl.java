package com.thomsonreuters.uscl.ereader.common.notification.service;

import com.thomsonreuters.uscl.ereader.common.notification.step.SendNotificationStep;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Dummy implementation
 */
public class DefaultStepFailureNotificationServiceImpl implements StepFailureNotificationService<SendNotificationStep>
{
    private static final Logger LOG = LogManager.getLogger(DefaultStepFailureNotificationServiceImpl.class);

    @Override
    public void emailFailure(final SendNotificationStep step, final Exception e)
    {
        LOG.error(step.toString(), e);
    }
}
