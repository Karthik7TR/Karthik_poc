package com.thomsonreuters.uscl.ereader.xpp.archive.step;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.common.XppBookStep;

@SavePublishingStatusPolicy
public class ArchiveXppStep extends BaseArchiveStep implements XppBookStep
{
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep
}
