package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.format.service.InlineTocService;
import org.springframework.batch.core.ExitStatus;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class InlineTocStep extends BookStepImpl {
    @Resource(name = "gatherFileSystem")
    private GatherFileSystem gatherFileSystem;

    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Resource
    private InlineTocService inlineTocService;

    @Override
    public ExitStatus executeStep() throws Exception {
        if (getBookDefinition().isInlineTocIncluded()) {
            generateInlineToc();
        }
        return ExitStatus.COMPLETED;
    }

    private void generateInlineToc() {
        final boolean pages = getJobExecutionPropertyBoolean(JobExecutionKey.WITH_PAGE_NUMBERS);
        final File tocXmlFile = gatherFileSystem.getGatherTocFile(this);
        final File outputDir = formatFileSystem.getHtmlWrapperDirectory(this);
        final long jobId = getJobInstanceId();

        final boolean inlineTocGenerated = inlineTocService.generateInlineToc(tocXmlFile, outputDir, pages, jobId);

        if (inlineTocGenerated) {
            setJobExecutionProperty(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
        }
    }
}
