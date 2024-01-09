package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.service.jsoup.JsoupTransformation;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.nodes.Document;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class JsoupTransformationsStep extends BookStepImpl {
    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Autowired
    private List<JsoupTransformation> conversions;

    @Autowired
    private JsoupService jsoup;

    @Override
    public ExitStatus executeStep() throws Exception {
        final File srcDir = getDir(NortTocCwbFileSystemConstants.FORMAT_PROCESS_PAGES_DIR);
        final File destDir = getDir(NortTocCwbFileSystemConstants.FORMAT_JSOUP_TRANSFORMATION_DIR);
        conversions.forEach(item -> item.preparationsBeforeAll(this));
        Files.list(srcDir.toPath()).forEach(file -> {
            Document document = jsoup.loadDocument(file.toFile());
            conversions.forEach(item -> item.preparations(document));
        });
        Files.list(srcDir.toPath()).forEach(file -> {
            Document document = jsoup.loadDocument(file.toFile());
            document.outputSettings().prettyPrint(false);
            conversions.forEach(item -> item.transform(file.toFile(), document, this));
            jsoup.saveDocument(destDir, file.getFileName().toString(), document);
        });
        conversions.forEach(item -> item.clear(this));
        markDirectoryAsReady(destDir);

        return ExitStatus.COMPLETED;
    }

    private File getDir(final NortTocCwbFileSystemConstants dir) {
        return new File(formatFileSystem.getFormatDirectory(this), dir.getName());
    }

    private void markDirectoryAsReady(final File destDir) {
        getJobExecutionContext().putString(
                JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH,
                destDir.getAbsolutePath());
    }
}
