package com.thomsonreuters.uscl.ereader.notification.service;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilder;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilderFactory;
import com.thomsonreuters.uscl.ereader.notification.step.SendEmailNotificationStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class SendNotificationEmailBuilderFactory implements EmailBuilderFactory
{
    @Autowired
    private ApplicationContext applicationContext;
    @Resource(name = "sendNotificationTask")
    private SendEmailNotificationStep step;

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilderFactory#create()
     */
    @Override
    public EmailBuilder create()
    {
        if (step.getBookDefinition().isSplitBook())
        {
            return (EmailBuilder) applicationContext.getBean("splitBookGeneratorNotificationEmailBuilder");
        }
        else if (isBigToc())
        {
            return (EmailBuilder) applicationContext.getBean("bigTocGeneratorNotificationEmailBuilder");
        }
        return (EmailBuilder) applicationContext.getBean("defaultGeneratorNotificationEmailBuilder");
    }

    private boolean isBigToc()
    {
        final Integer tocNodeCount = step.getTocNodeCount();
        final Integer thresholdValue = step.getThresholdValue();
        if (tocNodeCount == null || thresholdValue == null)
        {
            throw new RuntimeException(
                "Gather TOC Node Count = "
                    + tocNodeCount
                    + " and cannot be compared with was Threshold Value = "
                    + thresholdValue);
        }
        return tocNodeCount >= thresholdValue;
    }
}
