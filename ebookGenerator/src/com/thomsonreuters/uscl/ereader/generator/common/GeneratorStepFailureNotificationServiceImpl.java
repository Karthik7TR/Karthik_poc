package com.thomsonreuters.uscl.ereader.generator.common;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.notification.service.SendFailureNotificationStrategy;
import com.thomsonreuters.uscl.ereader.common.notification.service.StepFailureNotificationService;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import org.apache.commons.lang3.exception.ExceptionUtils;

@SendFailureNotificationStrategy(FailureNotificationType.GENERATOR)
public class GeneratorStepFailureNotificationServiceImpl implements StepFailureNotificationService<BookStep>
{
    @Resource(name = "generatorNotificationService")
    private NotificationService notificationService;

    @Override
    public void sendFailureNotification(final BookStep step, final Exception e)
    {
        final String message =
            String.format("Error Message : %s%nStack Trace is %n%s", e.getMessage(), ExceptionUtils.getStackTrace(e));
        notificationService.sendNotification(
            step.getJobExecutionContext(),
            step.getJobParameters(),
            message,
            step.getJobInstanceId(),
            step.getJobExecutionId());
    }
}
