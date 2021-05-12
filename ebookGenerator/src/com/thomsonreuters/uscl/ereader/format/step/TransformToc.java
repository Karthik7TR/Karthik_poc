package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.service.TocHeadersSubstitutionService;
import com.thomsonreuters.uscl.ereader.format.service.TransformTocService;
import com.thomsonreuters.uscl.ereader.gather.metadata.DocMetadataServiceContainer;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.jsoup.nodes.Document;
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
    @Autowired
    private DocMetadataService docMetadataService;
    @Autowired
    private JsoupService jsoup;
    @Autowired
    private TocHeadersSubstitutionService tocHeadersSubstitutionService;

    @Override
    public ExitStatus executeStep() throws Exception {
        final File toc = gatherFileSystem.getGatherTocFile(this);
        final File destDir = formatFileSystem.getTransformTocDirectory(this);
        final Document tocDocument = jsoup.loadDocument(toc);

        transformTocService.transformToc(tocDocument);
        tocHeadersSubstitutionService.substituteTocHeadersWithDates(tocDocument,
                new DocMetadataServiceContainer(docMetadataService,
                    getJobInstanceId(),
                    getBookDefinition().getTitleId()
                ),
                getBookDefinition().isSubstituteTocHeaders());

        jsoup.saveDocument(destDir, toc.getName(), tocDocument);
        return ExitStatus.COMPLETED;
    }
}
