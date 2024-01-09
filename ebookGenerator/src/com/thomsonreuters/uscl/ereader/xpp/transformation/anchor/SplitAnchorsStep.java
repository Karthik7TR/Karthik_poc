package com.thomsonreuters.uscl.ereader.xpp.transformation.anchor;

import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.EXTERNAL_LINKS_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.SPLIT_ANCHORS_DIR;

import java.io.File;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class SplitAnchorsStep extends XppTransformationStep {
    @Value("${xpp.split.anchors.xsl}")
    private File splitAnchorsXsl;
    @Autowired
    private XppFormatFileSystem fileSystem;

    @Override
    public void executeTransformation() {
        final Transformer transformer = transformerBuilderFactory.create()
                .withXsl(splitAnchorsXsl)
                .build();
        fileSystem.getFiles(this, EXTERNAL_LINKS_DIR)
                .forEach((materialNumber, files) -> files
                        .forEach(file -> transform(materialNumber, file, transformer)));
    }

    private void transform(
            @NotNull final String materialNumber,
            @NotNull final File file,
            @NotNull final Transformer transformer) {
        final TransformationCommand command = new TransformationCommandBuilder(
                transformer,
                fileSystem.getFile(this, SPLIT_ANCHORS_DIR, materialNumber, file.getName()))
                .withInput(file)
                .build();
        transformationService.transform(command);
    }
}
