package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.format.service.TransformCharSequencesService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@NoArgsConstructor
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class TransformCharSequencesStep extends BookStepImpl {
    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Autowired
    private TransformCharSequencesService transformCharSequencesService;

    @Override
    public ExitStatus executeStep() throws Exception {
        final File srcDir = formatFileSystem.getPreprocessDirectory(this);
        final File destDir = formatFileSystem.getTransformCharSequencesDirectory(this);
        transformCharSequencesService.transformCharSequences(srcDir, destDir, this.getBookDefinition().isCwBook(),
                this.getBookDefinition().isPrintPageNumbers(),
                getJobExecutionPropertyBoolean(JobExecutionKey.PAGE_VOLUMES_SET));
        return ExitStatus.COMPLETED;
    }
}
