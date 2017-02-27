package com.thomsonreuters.uscl.ereader.xpp.deliver.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.deliver.step.BaseDeliverStep;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.common.XppBookStep;

@SavePublishingStatusPolicy(StatsUpdateTypeEnum.FINALPUBLISH)
public class DeliverXppStep extends BaseDeliverStep implements XppBookStep
{
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.deliver.step.BaseDeliverStep
}
