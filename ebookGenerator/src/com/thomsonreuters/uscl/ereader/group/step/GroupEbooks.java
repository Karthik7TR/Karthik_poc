package com.thomsonreuters.uscl.ereader.group.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.group.step.BaseGroupStep;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GROUPEBOOK)
public class GroupEbooks extends BaseGroupStep {
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.group.step.BaseGroupStep
}
