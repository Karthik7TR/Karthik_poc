package com.thomsonreuters.uscl.ereader.deliver.step;

import java.util.Collection;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.deliver.step.BaseDeliverStep;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.ServiceSplitBookTitlesAwareStep;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;

/**
 * This class is responsible for delivering a generated eBook to ProView.
 */
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.FINALPUBLISH)
public class DeliverToProview extends BaseDeliverStep implements ServiceSplitBookTitlesAwareStep {
    @Resource(name = "docMetadataService")
    private DocMetadataService docMetadataService;

    @Override
    public Collection<String> getTitlesFromService() {
        return docMetadataService.findDistinctSplitTitlesByJobId(getJobInstanceId());
    }
}
