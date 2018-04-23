package com.thomsonreuters.uscl.ereader.deliver.step;

import java.util.Collection;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.ServiceSplitBookTitlesAwareStep;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;

/**
 * This class is responsible for archiving the created book artifact.
 * Only performed if this is the production ("prod") environment.
 * The last delivered major and minor number version of the file is archived.
 */
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy
public class ArchiveBook extends BaseArchiveStep implements ServiceSplitBookTitlesAwareStep {
    @Resource(name = "docMetadataService")
    private DocMetadataService docMetadataService;

    @Override
    public Collection<String> getTitlesFromService() {
        return docMetadataService.findDistinctSplitTitlesByJobId(getJobInstanceId());
    }
}
