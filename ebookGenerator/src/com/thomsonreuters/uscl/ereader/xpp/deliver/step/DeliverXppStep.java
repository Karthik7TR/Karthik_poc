package com.thomsonreuters.uscl.ereader.xpp.deliver.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.deliver.step.BaseDeliverStepImpl;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatus;
import com.thomsonreuters.uscl.ereader.xpp.common.XppBookStep;

@SavePublishingStatus(StatsUpdateTypeEnum.FINALPUBLISH)
public class DeliverXppStep extends BaseDeliverStepImpl implements XppBookStep
{
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.deliver.step.BaseDeliverStepImpl
}
