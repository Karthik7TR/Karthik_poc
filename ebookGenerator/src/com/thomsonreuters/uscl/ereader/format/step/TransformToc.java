package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.format.service.TransformTocService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class TransformToc extends BookStepImpl {
    @Autowired
    private GatherFileSystem gatherFileSystem;
    @Autowired
    private FormatFileSystem formatFileSystem;
    @Autowired
    private TransformTocService transformTocService;

    @Override
    public ExitStatus executeStep() throws Exception {
        final File toc = gatherFileSystem.getGatherTocFile(this);
        final File destDir = formatFileSystem.getTransformTocDirectory(this);
        transformTocService.transformToc(toc, destDir);
        return ExitStatus.COMPLETED;
    }
}
