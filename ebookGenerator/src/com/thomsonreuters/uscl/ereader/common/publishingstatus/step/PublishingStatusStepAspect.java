package com.thomsonreuters.uscl.ereader.common.publishingstatus.step;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.publishingstatus.service.PublishingStatusUpdateService;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Aspect
@Order(3)
public class PublishingStatusStepAspect
{
    @Resource(name = "publishingStatusUpdateService")
    private PublishingStatusUpdateService<PublishingStatusUpdateStep> publishingStatusUpdateService;

    @Around("execution(* com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep.execute(..))")
    public void around(final ProceedingJoinPoint jp) throws Throwable
    {
        PublishingStatus publishStatus = PublishingStatus.COMPLETED;
        try
        {
            jp.proceed();
        }
        catch (final Exception e)
        {
            publishStatus = PublishingStatus.FAILED;
            throw e;
        }
        finally
        {
            final PublishingStatusUpdateStep step = (PublishingStatusUpdateStep) jp.getTarget();
            publishingStatusUpdateService.savePublishingStats(step, publishStatus);
        }
    }
}
