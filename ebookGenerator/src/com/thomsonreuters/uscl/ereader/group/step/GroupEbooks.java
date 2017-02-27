package com.thomsonreuters.uscl.ereader.group.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.group.step.BaseGroupStep;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.generator.common.GeneratorStep;

@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GROUPEBOOK)
public class GroupEbooks extends BaseGroupStep implements GeneratorStep
{
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.group.step.BaseGroupStep
}
