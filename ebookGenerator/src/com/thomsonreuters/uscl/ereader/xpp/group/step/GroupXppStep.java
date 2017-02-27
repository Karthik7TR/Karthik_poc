package com.thomsonreuters.uscl.ereader.xpp.group.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.group.step.BaseGroupStep;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.common.XppBookStep;

@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GROUPEBOOK)
public class GroupXppStep extends BaseGroupStep implements XppBookStep
{
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.group.step.BaseGroupStep
}
