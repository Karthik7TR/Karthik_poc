package com.thomsonreuters.uscl.ereader.generator.common;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.notification.service.SendFailureNotificationStrategy;
import com.thomsonreuters.uscl.ereader.common.notification.service.StepFailureNotificationService;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import org.apache.commons.lang3.exception.ExceptionUtils;

@SendFailureNotificationStrategy(FailureNotificationType.GENERATOR)
public class GeneratorStepFailureNotificationServiceImpl implements StepFailureNotificationService<BookStep> {
    @Resource(name = "generatorNotificationService")
    private NotificationService notificationService;

    @Override
    public void sendFailureNotification(final BookStep step, final Exception e) {
        if (isExceptionOnGroupStepOccurred(step)) {
            sendGroupStepFailureNotification(step);
        } else {
            sendStepFailureNotification(step, e);
        }
    }

    private void sendStepFailureNotification(final BookStep step, final Exception e) {
        final String message = String.format("Error Message : %s%nStack Trace is %n%s",
                e.getMessage(), ExceptionUtils.getStackTrace(e));
        sendNotification(step, message);
    }

    private void sendGroupStepFailureNotification(final BookStep step) {
        final String message = "The book was published successfully with groupBook step error.";
        sendNotification(step, message);
    }

    private void sendNotification(final BookStep step, final String message) {
        notificationService.sendNotification(step.getJobExecutionContext(), step.getJobParameters(), message,
                step.getJobInstanceId(), step.getJobExecutionId());
    }

    private boolean isExceptionOnGroupStepOccurred(final BookStep step) {
        return step.getJobExecutionPropertyBoolean(JobExecutionKey.EXCEPTION_ON_GROUP_STEP_OCCURRED);
    }
}
