package com.thomsonreuters.uscl.ereader.xpp.archive.step;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.XppSplitBookTitlesAwareStep;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class ArchiveXppStep extends BaseArchiveStep implements XppSplitBookTitlesAwareStep {
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep
}
