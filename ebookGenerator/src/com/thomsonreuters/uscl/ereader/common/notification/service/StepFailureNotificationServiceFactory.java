package com.thomsonreuters.uscl.ereader.common.notification.service;

import java.util.Collection;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendNotificationStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

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
        final FailureNotificationType notificationType =
            AnnotationUtils.findAnnotation(step.getClass(), SendFailureNotificationPolicy.class).value();
        final Collection<Object> beans =
            applicationContext.getBeansWithAnnotation(SendFailureNotificationStrategy.class).values();
        return getServiceBean(notificationType, beans);
    }

    private StepFailureNotificationService<SendNotificationStep> getServiceBean(
        final FailureNotificationType notificationType,
        final Collection<Object> beans)
    {
        for (final Object bean : beans)
        {
            final StepFailureNotificationService<SendNotificationStep> service =
                (StepFailureNotificationService<SendNotificationStep>) bean;
            final FailureNotificationType serviceNotificationType =
                AnnotationUtils.findAnnotation(service.getClass(), SendFailureNotificationStrategy.class).value();
            if (notificationType.equals(serviceNotificationType))
            {
                return service;
            }
        }

        throw new BeanCreationException(
            String.format("StepFailureNotificationService not found for type %s", notificationType));
    }
}
