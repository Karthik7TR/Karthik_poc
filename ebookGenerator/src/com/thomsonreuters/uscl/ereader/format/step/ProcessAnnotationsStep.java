package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.format.service.TransformDoubleHyphensIntoEmDashesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class ProcessAnnotationsStep extends BookStepImpl {
    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Autowired
    TransformDoubleHyphensIntoEmDashesService transformDoubleHyphensIntoEmDashesService;

    @Override public ExitStatus executeStep() throws Exception {
        final File srcDir = formatFileSystem.getPreprocessDirectory(this);
        final File destDir = formatFileSystem.getProcessAnnotationsDirectory(this);

        final long startTime = System.currentTimeMillis();

        transformDoubleHyphensIntoEmDashesService.transformDoubleHyphensIntoEmDashes(srcDir, destDir);

        final long endTime = System.currentTimeMillis();
        final long operationTime = endTime - startTime;
        log.debug("Processed annotations in {} milliseconds", operationTime);

        return ExitStatus.COMPLETED;
    }
}
