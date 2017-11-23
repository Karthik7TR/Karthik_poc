package com.thomsonreuters.uscl.ereader.xpp.transformation.internalAnchors.step;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
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

    @Value("${xpp.merge.anchor.to.document.id.maps.xsl}")
    private File mergeAnchorToDocumentIdMapsXsl;

    @Override
    public void executeTransformation() throws Exception {
        final Collection<XppBundle> bundles = getXppBundles();
        final Map<File, Boolean> files = new HashMap<>();

        for (final Map.Entry<String, Collection<File>> materialFiles : fileSystem
            .getFiles(this, XppFormatFileSystemDir.SECTIONBREAKS_DIR).entrySet()) {
            final boolean isPocketPart = bundles.stream()
                .filter(bundle -> bundle.getMaterialNumber().equals(materialFiles.getKey()))
                .findFirst()
                .map(XppBundle::isPocketPartPublication)
                .orElse(false);
            materialFiles.getValue().forEach(materialFile -> files.put(materialFile, isPocketPart));
            transform(
                transformToAnchorToDocumentIdMapSummaryTocXsl,
                materialFiles.getValue(),
                fileSystem.getAnchorToDocumentIdMapFile(this, materialFiles.getKey()),
                isPocketPart);
        }

        final Map<Boolean, List<File>> typeToFilesMap = files.entrySet().stream().collect(
            Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
        typeToFilesMap.entrySet().forEach(entry -> {
            if (!entry.getKey())
                transform(
                    transformToAnchorToDocumentIdMapXsl,
                    entry.getValue(),
                    fileSystem.getAnchorToDocumentIdMapBoundFile(this),
                    false);
            else {
                transform(
                    transformToAnchorToDocumentIdMapXsl,
                    entry.getValue(),
                    fileSystem.getAnchorToDocumentIdMapSupplementFile(this),
                    true);
            }
        });

        final List<File> transformedAnchorFiles = new ArrayList<>(2);
        transformedAnchorFiles.add(fileSystem.getAnchorToDocumentIdMapBoundFile(this));
        transformedAnchorFiles.add(fileSystem.getAnchorToDocumentIdMapSupplementFile(this));
        transform(
            mergeAnchorToDocumentIdMapsXsl,
            getExistingXmls(transformedAnchorFiles),
            fileSystem.getAnchorToDocumentIdMapFile(this),
            false);
    }

    private List<File> getExistingXmls(final List<File> boundAndSupplementXmls) {
        return boundAndSupplementXmls.stream().filter(File::exists).collect(Collectors.toList());
    }

    private void transform(
        final File xsl,
        final Collection<File> input,
        final File output,
        final boolean isPocketPart) {
        final Transformer transformer =
            transformerBuilderFactory.create().withXsl(xsl).withParameter("isPocketPart", isPocketPart).build();
        final TransformationCommand command =
            new TransformationCommandBuilder(transformer, output).withInput(input).build();
        transformationService.transform(command);
    }
}
