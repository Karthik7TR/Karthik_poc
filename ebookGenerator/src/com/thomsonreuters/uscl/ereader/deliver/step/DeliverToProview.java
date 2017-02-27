package com.thomsonreuters.uscl.ereader.deliver.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.deliver.step.BaseDeliverStep;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.generator.common.GeneratorStep;

/**
 * This class is responsible for delivering a generated eBook to ProView.
 */
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.FINALPUBLISH)
public class DeliverToProview extends BaseDeliverStep implements GeneratorStep
{
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.deliver.step.BaseDeliverStep
}
