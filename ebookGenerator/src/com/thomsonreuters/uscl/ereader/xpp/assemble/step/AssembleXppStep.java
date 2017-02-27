package com.thomsonreuters.uscl.ereader.xpp.assemble.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.assemble.step.BaseAssembleStep;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.common.XppBookStep;

@SavePublishingStatusPolicy({StatsUpdateTypeEnum.ASSEMBLEDOC, StatsUpdateTypeEnum.TITLEDOC})
public class AssembleXppStep extends BaseAssembleStep implements XppBookStep
{
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.assemble.step.BaseAssembleStep
}
