package com.thomsonreuters.uscl.ereader.notification.step;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import org.jetbrains.annotations.Nullable;

public interface SendEmailNotificationStep extends BookStep {
    @Nullable
    Integer getTocNodeCount();

    @Nullable
    Integer getThresholdValue();

    PublishingStats getCurrentsStats();

    PublishingStats getPreviousStats();
}
