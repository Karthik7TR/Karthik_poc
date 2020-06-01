package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianTopicCode;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.CanadianTopicCodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class AddThesaurusToDocumentsStep extends BookStepImpl {
    private static final String DIV = "div";
    private static final String THESAURUS_CLASS = "tr_phrase1";
    private static final String ANCHOR = "a";

    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Autowired
    private JsoupService jsoup;

    @Autowired
    private CanadianTopicCodeService canadianTopicCodeService;

    @Override
    public ExitStatus executeStep() throws Exception {
        final File srcDir = getDir(NortTocCwbFileSystemConstants.FORMAT_PROCESS_PAGES_DIR);
        final File destDir = getDir(NortTocCwbFileSystemConstants.FORMAT_ADD_THESAURUS_TO_DOCUMENTS_DIR);

        if (getJobExecutionPropertyBoolean(JobExecutionKey.WITH_THESAURUS)) {
            Stream.of(Objects.requireNonNull(srcDir.listFiles())).forEach(file ->
                addThesaurusToFile(file, destDir)
            );
            markDirectoryAsReady(destDir);
        }

        return ExitStatus.COMPLETED;
    }

    private void addThesaurusToFile(final File file, final File destDir) {
        final String docUuid = FilenameUtils.removeExtension(file.getName());
        final List<CanadianTopicCode> topicCodes = getTopicCodes(docUuid);

        if (CollectionUtils.isNotEmpty(topicCodes)) {
            final Document doc = jsoup.loadDocument(file);
            doc.outputSettings().prettyPrint(false);

            addThesaurusToDocument(topicCodes, doc);

            jsoup.saveDocument(destDir, file.getName(), doc);
        } else {
            copyFileToDirectory(file, destDir);
        }
    }

    private List<CanadianTopicCode> getTopicCodes(String docUuid) {
        return canadianTopicCodeService.findCanadianTopicCodesForDocument(getJobInstanceId(), docUuid);
    }

    private void addThesaurusToDocument(final List<CanadianTopicCode> topicCodes, final Document document) {
        final Element anchor = getLastOfInitialAnchors(document);
        topicCodes.forEach(topicCode -> {
            Element thesaurusMark = createThesaurusMark(topicCode);
            anchor.after(thesaurusMark);
        });
    }

    private Element getLastOfInitialAnchors(final Document document) {
        Element anchor = document.selectFirst(ANCHOR);
        while (ANCHOR.equals(anchor.nextElementSibling().tagName())) {
            anchor = anchor.nextElementSibling();
        }
        return anchor;
    }

    private Element createThesaurusMark(final CanadianTopicCode topicCode) {
        Element thesaurusMark = new Element(DIV);
        thesaurusMark.addClass(THESAURUS_CLASS);
        thesaurusMark.appendText(topicCode.getTopicKey());
        return thesaurusMark;
    }

    private File getDir(final NortTocCwbFileSystemConstants dir) {
        return new File(formatFileSystem.getFormatDirectory(this), dir.getName());
    }

    private void markDirectoryAsReady(final File destDir) {
        getJobExecutionContext().putString(
                JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH,
                destDir.getAbsolutePath());
    }

    private void copyFileToDirectory(final File file, final File destDir) {
        try {
            FileUtils.copyFileToDirectory(file, destDir);
        } catch (IOException e) {
            throw new EBookException(e);
        }
    }
}
