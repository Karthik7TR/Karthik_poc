package com.thomsonreuters.uscl.ereader.xpp.transformation.internalAnchors.step;

import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.SECTIONBREAKS_DIR;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
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

    @Value("${xpp.merge.anchor.to.document.id.maps.xsl}")
    private File mergeAnchorToDocumentIdMapsXsl;

    @Override
    public void executeTransformation() throws Exception {
        final Map<String, Collection<File>> sourceFiles = fileSystem.getFiles(this, SECTIONBREAKS_DIR);
        final Collection<File> allMapFiles = new ArrayList<>();

        getSplitPartsBundlesMap().forEach((splitPartNumber, bundles) -> {
            bundles.forEach(bundle -> {
                final String materialNumber = bundle.getMaterialNumber();
                final Collection<File> files = sourceFiles.get(materialNumber);

                transform(transformToAnchorToDocumentIdMapSummaryTocXsl, files,
                    fileSystem.getAnchorToDocumentIdMapFile(this, materialNumber), bundle.isPocketPartPublication(),
                    splitPartNumber);

                final File bundleMapFile = fileSystem.getAnchorToDocumentIdMapBoundFile(this, materialNumber);
                transform(transformToAnchorToDocumentIdMapXsl, files, bundleMapFile, bundle.isPocketPartPublication(), splitPartNumber);
                allMapFiles.add(bundleMapFile);
            });
        });

        transform(mergeAnchorToDocumentIdMapsXsl, allMapFiles, fileSystem.getAnchorToDocumentIdMapFile(this), false, 1);
    }

    private void transform(final File xsl, final Collection<File> input,
                           final File output, final boolean isPocketPart,
                           final Integer splitPartNumber) {
        final TransformerBuilder transformerBuilder =
            transformerBuilderFactory.create().withXsl(xsl)
                .withParameter("isPocketPart", isPocketPart);
        if (getSplitPartsBundlesMap().size() > 1) {
            transformerBuilder.withParameter("splitTitleId", getTitleId(splitPartNumber));
        }
        final TransformationCommand command =
            new TransformationCommandBuilder(transformerBuilder.build(), output).withInput(input).build();
        transformationService.transform(command);
    }
}
