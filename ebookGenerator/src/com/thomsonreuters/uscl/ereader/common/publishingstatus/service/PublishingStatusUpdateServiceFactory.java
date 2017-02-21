package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public List<PublishingStatusUpdateService<PublishingStatusUpdateStep>> create(final PublishingStatusUpdateStep step)
    {
        final StatsUpdateTypeEnum[] stepUpdateTypes =
            AnnotationUtils.findAnnotation(step.getClass(), SavePublishingStatus.class).value();
        final Collection<Object> beans =
            applicationContext.getBeansWithAnnotation(SavePublishingStatusService.class).values();
        return getServiceBeans(beans, stepUpdateTypes);
    }

    private List<PublishingStatusUpdateService<PublishingStatusUpdateStep>> getServiceBeans(
        final Collection<Object> beans,
        final StatsUpdateTypeEnum[] stepUpdateTypes)
    {
        final List<PublishingStatusUpdateService<PublishingStatusUpdateStep>> services = new ArrayList<>();
        for (final StatsUpdateTypeEnum updateType : stepUpdateTypes)
        {
            services.add(getServiceBean(updateType, beans));
        }
        return services;
    }

    /**
     * @param updateType
     * @param beans
     * @return
     */
    private PublishingStatusUpdateService<PublishingStatusUpdateStep> getServiceBean(
        final StatsUpdateTypeEnum stepUpdateType,
        final Collection<Object> beans)
    {
        for (final Object bean : beans)
        {
            final PublishingStatusUpdateService<PublishingStatusUpdateStep> service =
                (PublishingStatusUpdateService<PublishingStatusUpdateStep>) bean;
            final StatsUpdateTypeEnum serviceUpdateType =
                AnnotationUtils.findAnnotation(service.getClass(), SavePublishingStatusService.class).value();
            if (stepUpdateType.equals(serviceUpdateType))
            {
                return service;
            }
        }

        throw new BeanCreationException(
            String.format("PublishingStatusUpdateService not found for type %s", stepUpdateType));
    }
}
