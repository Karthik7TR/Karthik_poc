package com.thomsonreuters.uscl.ereader.orchestrate.core.service;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ExecutionContext;

public interface NotificationService {
    void sendNotification(
        ExecutionContext jobExecutionContext,
        JobParameters jobParams,
        String bodyMessage,
        long jobInstanceId,
        long jobExecutionId);
}
