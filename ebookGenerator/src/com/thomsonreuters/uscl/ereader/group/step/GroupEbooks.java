package com.thomsonreuters.uscl.ereader.group.step;

import java.io.File;
import java.util.Collection;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.group.step.BaseGroupStep;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.service.splitnode.SplitNodesInfoService;
import com.thomsonreuters.uscl.ereader.common.step.ServiceSplitBookTitlesAwareStep;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GROUPEBOOK)
public class GroupEbooks extends BaseGroupStep implements ServiceSplitBookTitlesAwareStep {
    @Resource(name = "formatFileSystem")
    private FormatFileSystem fileSystem;
    @Resource(name = "splitNodesInfoService")
    private SplitNodesInfoService splitNodesInfoService;

    @Override
    public Collection<String> getTitlesFromService() {
        final File splitBookInfoFile = fileSystem.getSplitBookInfoFile(this);
        final String fullyQualifiedTitleId = getBookDefinition().getFullyQualifiedTitleId();
        return splitNodesInfoService.getTitleIds(splitBookInfoFile, fullyQualifiedTitleId);
    }
}
