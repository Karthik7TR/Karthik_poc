package com.thomsonreuters.uscl.ereader.xpp.deliver.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.deliver.step.BaseDeliverStep;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.FINALPUBLISH)
public class DeliverXppStep extends BaseDeliverStep {
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.deliver.step.BaseDeliverStep
}
