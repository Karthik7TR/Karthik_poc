package com.thomsonreuters.uscl.ereader.xpp.assemble.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.assemble.step.BaseAssembleStep;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.XppSplitBookTitlesAwareStep;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy({StatsUpdateTypeEnum.ASSEMBLEDOC, StatsUpdateTypeEnum.TITLEDOC})
public class AssembleXppStep extends BaseAssembleStep implements XppSplitBookTitlesAwareStep {
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.assemble.step.BaseAssembleStep
}
