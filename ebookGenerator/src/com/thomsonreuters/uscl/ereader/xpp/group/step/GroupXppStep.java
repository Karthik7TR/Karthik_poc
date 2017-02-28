package com.thomsonreuters.uscl.ereader.xpp.group.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.group.step.BaseGroupStep;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GROUPEBOOK)
public class GroupXppStep extends BaseGroupStep
{
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.group.step.BaseGroupStep
}
