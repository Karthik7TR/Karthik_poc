package com.thomsonreuters.uscl.ereader.xpp.transformation.unescape.step;

import java.io.File;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Removes double escaping from <span>
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class UnescapeStep extends XppTransformationStep {
    @Value("${xpp.unescape.xsl}")
    private File unescapeXsl;
    @Autowired
    private XppFormatFileSystem fileSystem;

    @Override
    public void executeTransformation() throws Exception {
        final Transformer transformer = transformerBuilderFactory.create()
            .withXsl(unescapeXsl)
            .build();
        fileSystem.getExternalLinksFiles(this)
            .forEach((materialNumber, files) -> files.stream()
                .forEach(file -> transform(materialNumber, file, transformer)));
    }

    private void transform(
        @NotNull final String materialNumber,
        @NotNull final File file,
        @NotNull final Transformer transformer) {
        final TransformationCommand command = new TransformationCommandBuilder(
            transformer,
            fileSystem.getFile(this, XppFormatFileSystemDir.UNESCAPE_DIR, materialNumber, file.getName()))
                .withInput(file)
                .build();
        transformationService.transform(command);
    }
}