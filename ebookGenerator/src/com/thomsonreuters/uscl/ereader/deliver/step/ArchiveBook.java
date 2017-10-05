package com.thomsonreuters.uscl.ereader.deliver.step;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;

/**
 * This class is responsible for archiving the created book artifact.
 * Only performed if this is the production ("prod") environment.
 * The last delivered major and minor number version of the file is archived.
 */
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy
public class ArchiveBook extends BaseArchiveStep {
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep
}
