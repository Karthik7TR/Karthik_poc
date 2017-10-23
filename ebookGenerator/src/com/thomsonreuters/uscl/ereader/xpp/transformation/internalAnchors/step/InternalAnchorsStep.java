package com.thomsonreuters.uscl.ereader.xpp.transformation.internalAnchors.step;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.springframework.beans.factory.annotation.Value;

/**
 * Generates mapping file between anchors and pages where they are.
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class InternalAnchorsStep extends XppTransformationStep {
    @Value("${xpp.anchor.to.document.map.xsl}")
    private File transformToAnchorToDocumentIdMapXsl;

    @Value("${xpp.summary.toc.anchor.to.document.map.xsl}")
    private File transformToAnchorToDocumentIdMapSummaryTocXsl;

    @Override
    public void executeTransformation() throws Exception {
        final List<File> inputFiles = new ArrayList<>();

        for (final Map.Entry<String, Collection<File>> materialFiles : fileSystem.getFiles(this, XppFormatFileSystemDir.SECTIONBREAKS_DIR).entrySet()) {
            inputFiles.addAll(materialFiles.getValue());
            transform(transformToAnchorToDocumentIdMapSummaryTocXsl,
                materialFiles.getValue(),
                fileSystem.getAnchorToDocumentIdMapFile(this, materialFiles.getKey()));
        }

        transform(transformToAnchorToDocumentIdMapXsl, inputFiles, fileSystem.getAnchorToDocumentIdMapFile(this));
    }

    private void transform(final File xsl, final Collection<File> input, final File output) {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(xsl).build();
        final TransformationCommand command = new TransformationCommandBuilder(transformer, output).withInput(input).build();
        transformationService.transform(command);
    }
}
