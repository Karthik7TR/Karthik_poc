package com.thomsonreuters.uscl.ereader.assemble.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.assemble.step.BaseAssembleStep;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatus;
import com.thomsonreuters.uscl.ereader.generator.common.GeneratorStep;

/**
 * Step responsible for assembling an eBook.
 */
@SavePublishingStatus({StatsUpdateTypeEnum.ASSEMBLEDOC, StatsUpdateTypeEnum.TITLEDOC})
public class AssembleEbook extends BaseAssembleStep implements GeneratorStep
{
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.assemble.step.BaseAssembleStep
}
