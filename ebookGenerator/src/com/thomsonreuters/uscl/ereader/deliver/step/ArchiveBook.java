package com.thomsonreuters.uscl.ereader.deliver.step;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.generator.common.GeneratorStep;

/**
 * This class is responsible for archiving the created book artifact.
 * Only performed if this is the production ("prod") environment.
 * The last delivered major and minor number version of the file is archived.
 */
@SavePublishingStatusPolicy
public class ArchiveBook extends BaseArchiveStep implements GeneratorStep
{
    //implementation is nested from @see com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep
}
