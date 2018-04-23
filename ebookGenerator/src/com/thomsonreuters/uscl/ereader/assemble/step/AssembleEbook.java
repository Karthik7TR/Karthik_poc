package com.thomsonreuters.uscl.ereader.assemble.step;

import java.util.Collection;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.assemble.step.BaseAssembleStep;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.ServiceSplitBookTitlesAwareStep;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;

/**
 * Step responsible for assembling an eBook.
 */
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy({StatsUpdateTypeEnum.ASSEMBLEDOC, StatsUpdateTypeEnum.TITLEDOC})
public class AssembleEbook extends BaseAssembleStep implements ServiceSplitBookTitlesAwareStep {
    @Resource(name = "docMetadataService")
    private DocMetadataService docMetadataService;

    @Override
    public Collection<String> getTitlesFromService() {
        return docMetadataService.findDistinctSplitTitlesByJobId(getJobInstanceId());
    }
}
