package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.format.service.ReorderFootnotesService;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class ProcessPages extends BookStepImpl {
    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Resource(name = "gatherFileSystem")
    private GatherFileSystem gatherFileSystem;

    @Autowired
    private ReorderFootnotesService reorderFootnotesService;

    @Override
    public ExitStatus executeStep() throws Exception {
        final File srcGatherDir = getGatherDir(NortTocCwbFileSystemConstants.GATHER_DOCS_DIR);
        final File srcDir = getDir(NortTocCwbFileSystemConstants.FORMAT_HTML_WRAPPER_DIR);
        final File destDir = getDir(NortTocCwbFileSystemConstants.FORMAT_PROCESS_PAGES_DIR);

        if (shouldReorderFootnotes()) {
            reorderFootnotesService.reorderFootnotes(srcGatherDir, srcDir, destDir);
        } else {
            FileUtils.copyDirectory(srcDir, destDir);
        }

        markDirectoryAsReady(destDir);

        return ExitStatus.COMPLETED;
    }

    private File getDir(final NortTocCwbFileSystemConstants dir) {
        return new File(formatFileSystem.getFormatDirectory(this), dir.getName());
    }

    private File getGatherDir(final NortTocCwbFileSystemConstants dir) {
        return new File(gatherFileSystem.getGatherRootDirectory(this), dir.getName());
    }

    private boolean shouldReorderFootnotes() {
        return getJobExecutionPropertyBoolean(JobExecutionKey.PAGE_NUMBERS_EXIST_IN_SOURCE_DOCS) && getBookDefinition().isPrintPageNumbers();
    }

    private void markDirectoryAsReady(final File destDir) {
        getJobExecutionContext().putString(
            JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH,
            destDir.getAbsolutePath());
    }
}