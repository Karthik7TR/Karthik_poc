package com.thomsonreuters.uscl.ereader.xpp.transformation.pplinks.step;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.DocumentFile;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.sonar.runner.commonsio.FileUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Adds link placeholders from main files to corresponding pocket part files and back.
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class PocketPartLinksStep extends XppTransformationStep {
    private static final XppFormatFileSystemDir SOURCE_DIR = XppFormatFileSystemDir.ORIGINAL_PAGES_DIR;
    private static final XppFormatFileSystemDir DESTINATION_DIR = XppFormatFileSystemDir.POCKET_PART_LINKS_DIR;

    @Value("${xpp.pplinks.xsl}")
    private File transformToAnchorToDocumentIdMapXsl;

    @Override
    public void executeTransformation() throws Exception {
        final Collection<XppBundle> bundles = getXppBundles();

        final Map<Boolean, Map<String, DocumentFile>> isPocketPartToUuidToFileNameMap =
            getIsPocketPartToUuidToFileNameMap(bundles);

        for (final Map.Entry<Boolean, Map<String, DocumentFile>> ppEntry : isPocketPartToUuidToFileNameMap.entrySet()) {
            final boolean isPP = ppEntry.getKey();

            for (final Map.Entry<String, DocumentFile> document : ppEntry.getValue().entrySet()) {
                final String uuid = document.getKey();
                final boolean anchorFilePresent = Optional.ofNullable(isPocketPartToUuidToFileNameMap.get(!isPP).get(uuid)).isPresent();

                final File file = document.getValue().getFile();
                final String materialNumber = file.getParentFile().getName();

                final File directory = fileSystem.getDirectory(this, DESTINATION_DIR, materialNumber);
                FileUtils.forceMkdir(directory);

                if (anchorFilePresent) {
                    transform(file, new File(directory, file.getName()), isPP, uuid);
                } else {
                    FileUtils.copyFileToDirectory(file, directory);
                }
            }
        }
    }

    /*
     * package visibility is for testing.
     */
    Map<Boolean, Map<String, DocumentFile>> getIsPocketPartToUuidToFileNameMap(final Collection<XppBundle> bundles) {
        final Map<Boolean, Map<String, DocumentFile>> isPocketPartToUuidToFileNameMap = new HashMap<>();
        final Map<String, Collection<File>> partFilesIndex = fileSystem.getFiles(this, SOURCE_DIR);

        for (final Map.Entry<String, Collection<File>> partFilesByMaterialNumber : partFilesIndex.entrySet()) {
            final String materialNumber = partFilesByMaterialNumber.getKey();
            final boolean isPocketPart = isMaterialNumberPocketPart(bundles, materialNumber);
            isPocketPartToUuidToFileNameMap.putIfAbsent(isPocketPart, new HashMap<>());

            for (final File files : partFilesByMaterialNumber.getValue()) {
                final DocumentFile document = new DocumentFile(files);
                isPocketPartToUuidToFileNameMap.get(isPocketPart).putIfAbsent(document.getDocumentName().getDocFamilyUuid(), document);
            }
        }
        return isPocketPartToUuidToFileNameMap;
    }

    private boolean isMaterialNumberPocketPart(
        final Collection<XppBundle> bundles,
        final String materialNumber) {
        return bundles.stream()
            .filter(bundle -> bundle.getMaterialNumber().equals(materialNumber))
            .findFirst()
            .map(XppBundle::isPocketPartPublication)
            .orElse(false);
    }

    private void transform(
        final File input,
        final File output,
        final boolean isPocketPart,
        final String anchorFileName
        ) {
        final Transformer transformer =
            transformerBuilderFactory.create()
                .withXsl(transformToAnchorToDocumentIdMapXsl)
                .withParameter("isPocketPart", isPocketPart)
                .withParameter("docId", anchorFileName).build();
        final TransformationCommand command =
            new TransformationCommandBuilder(transformer, output).withInput(input).build();
        transformationService.transform(command);
    }
}
