package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import java.util.Collection;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatus;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Factory class to create {@link com.thomsonreuters.uscl.ereader.common.publishingstatus.service.PublishingStatusUpdateService} depending on context
 */
public class PublishingStatusUpdateServiceFactory
{
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Returns publishing status service specific for step
     */
    public PublishingStatusUpdateService<PublishingStatusUpdateStep> create(final PublishingStatusUpdateStep step)
    {
        final SavePublishingStatus annotation =
            AnnotationUtils.findAnnotation(step.getClass(), SavePublishingStatus.class);
        final StatsUpdateTypeEnum value = annotation.value();
        final Collection<Object> beans = applicationContext.getBeansWithAnnotation(SavePublishingStatus.class).values();
        return getServiceBean(beans, value);
    }

    private PublishingStatusUpdateService<PublishingStatusUpdateStep> getServiceBean(
        final Collection<Object> beans,
        final StatsUpdateTypeEnum stepUpdateType)
    {
        for (final Object bean : beans)
        {
            if (!(bean instanceof PublishingStatusUpdateService))
            {
                continue;
            }

            final PublishingStatusUpdateService<PublishingStatusUpdateStep> service =
                (PublishingStatusUpdateService<PublishingStatusUpdateStep>) bean;
            final StatsUpdateTypeEnum serviceUpdateType =
                AnnotationUtils.findAnnotation(service.getClass(), SavePublishingStatus.class).value();
            if (serviceUpdateType.equals(stepUpdateType))
            {
                return service;
            }
        }
        throw new BeanCreationException(
            String.format("PublishingStatusUpdateService for update type %s not found", stepUpdateType));
    }
}
