package com.thomsonreuters.uscl.ereader.common.notification.service;

import com.thomsonreuters.uscl.ereader.common.notification.step.SendNotificationStep;
import com.thomsonreuters.uscl.ereader.xpp.common.XppBookStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Factory class to create {@link com.thomsonreuters.uscl.ereader.common.notification.service.StepFailureNotificationService} depending on context
 */
public class StepFailureNotificationServiceFactory
{
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Returns notification service specific for step
     */
    @NotNull
    public StepFailureNotificationService<SendNotificationStep> create(final SendNotificationStep step)
    {
        if (step instanceof XppBookStep)
        {
            return (StepFailureNotificationService<SendNotificationStep>) applicationContext
                .getBean("xppStepFailureNotificationService");
        }
        return (StepFailureNotificationService<SendNotificationStep>) applicationContext
            .getBean("defaultStepFailureNotificationService");
    }
}
