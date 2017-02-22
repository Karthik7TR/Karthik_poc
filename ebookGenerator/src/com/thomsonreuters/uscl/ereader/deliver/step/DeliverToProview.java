package com.thomsonreuters.uscl.ereader.deliver.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.deliver.step.BaseDeliverStepImpl;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatus;
import com.thomsonreuters.uscl.ereader.generator.common.GeneratorStep;

/**
 * This class is responsible for delivering a generated eBook to ProView.
 */
@SavePublishingStatus(StatsUpdateTypeEnum.FINALPUBLISH)
public class DeliverToProview extends BaseDeliverStepImpl implements GeneratorStep
{
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.deliver.step.BaseDeliverStepImpl
}
