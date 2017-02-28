package com.thomsonreuters.uscl.ereader.common.notification.step;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.notification.service.StepFailureNotificationService;
import com.thomsonreuters.uscl.ereader.common.notification.service.StepFailureNotificationServiceFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Aspect
@Order(2)
public class SendNotificationStepAspect
{
    @Resource(name = "stepFailureNotificationServiceFactory")
    private StepFailureNotificationServiceFactory stepFailureNotificationServiceFactory;

    @Around("execution(* com.thomsonreuters.uscl.ereader.common.notification.step.SendNotificationStep.execute(..))")
    public void around(final ProceedingJoinPoint jp) throws Throwable
    {
        try
        {
            jp.proceed();
        }
        catch (final Exception e)
        {
            final SendNotificationStep step = (SendNotificationStep) jp.getTarget();
            final StepFailureNotificationService<SendNotificationStep> service =
                stepFailureNotificationServiceFactory.create(step);
            service.sendFailureNotification(step, e);
            throw e;
        }
    }
}
