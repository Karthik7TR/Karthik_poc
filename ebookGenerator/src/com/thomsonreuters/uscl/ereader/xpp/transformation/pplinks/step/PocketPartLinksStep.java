package com.thomsonreuters.uscl.ereader.xpp.transformation.pplinks.step;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.DocumentFile;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.jetbrains.annotations.NotNull;
import org.sonar.runner.commonsio.FileUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Adds link placeholders from main files to corresponding pocket part files and back.
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class PocketPartLinksStep extends XppTransformationStep {
    private static final XppFormatFileSystemDir SOURCE_DIR = XppFormatFileSystemDir.INTERNAL_LINKS_RECOVERY_DIR;
    private static final XppFormatFileSystemDir DESTINATION_DIR = XppFormatFileSystemDir.POCKET_PART_LINKS_DIR;

    @Value("${xpp.pplinks.xsl}")
    private File transformToAnchorToDocumentIdMapXsl;

    private Map<Boolean, Map<String, DocumentFile>> isPocketPartToUuidToDocumentMap;

    @Override
    public void executeTransformation() throws Exception {
        initMaps();

        isPocketPartToUuidToDocumentMap.forEach((isPP, uuidToDocumentMap) ->
            uuidToDocumentMap.forEach((uuid, document) -> {
                try {
                    transformIfNeedLink(isPP, uuid, document);
                } catch (final IOException e) {
                    throw new EBookException(e);
                }
            })
        );
    }

    private void transformIfNeedLink(final boolean isPP, final String uuid, final DocumentFile document) throws IOException {
        final DocumentFile anchorDoc = isPocketPartToUuidToDocumentMap.getOrDefault(!isPP, Collections.emptyMap()).get(uuid);

        final File file = document.getFile();
        final String materialNumber = file.getParentFile().getName();

        final File directory = fileSystem.getDirectory(this, DESTINATION_DIR, materialNumber);
        FileUtils.forceMkdir(directory);

        if (anchorDoc != null) {
            final String anchorFileMaterialNumber = anchorDoc.getFile().getParentFile().getName();
            final String webBuildProductType = getBundleByMaterial(anchorFileMaterialNumber).getWebBuildProductType().getHumanReadableName();
            transform(file, new File(directory, file.getName()), isPP, webBuildProductType, uuid);
        } else {
            FileUtils.copyFileToDirectory(file, directory);
        }
    }

    private void initMaps() {
        isPocketPartToUuidToDocumentMap = getIsPocketPartToUuidToFileNameMap();
    }

    @NotNull
    private Map<String, XppBundle> getXppBundlesMap() {
        return getXppBundles().stream().collect(Collectors.toMap(XppBundle::getMaterialNumber, Function.identity()));
    }

    /*
     * package visibility is for testing
     */
    Map<Boolean, Map<String, DocumentFile>> getIsPocketPartToUuidToFileNameMap() {
        final Map<Boolean, Map<String, DocumentFile>> isPocketPartToUuidToFileNameMap = new HashMap<>();
        final Map<String, Collection<File>> partFilesIndex = fileSystem.getFiles(this, SOURCE_DIR);

        partFilesIndex.forEach((materialNumber, files) ->
            putToIsPocketPartToUuidToFileNameMap(isPocketPartToUuidToFileNameMap, materialNumber, files)
        );
        return isPocketPartToUuidToFileNameMap;
    }

    private void putToIsPocketPartToUuidToFileNameMap(
        final Map<Boolean, Map<String, DocumentFile>> isPocketPartToUuidToFileNameMap,
        final String materialNumber,
        final Collection<File> files) {
        final boolean isPocketPart = getBundleByMaterial(materialNumber).isPocketPartPublication();

        final Map<String, DocumentFile> map = files.stream().map(DocumentFile::new)
            .collect(Collectors.toMap(
                document -> document.getDocumentName().getDocFamilyUuid(),
                Function.identity()));
        isPocketPartToUuidToFileNameMap.putIfAbsent(isPocketPart, new HashMap<>());
        isPocketPartToUuidToFileNameMap.get(isPocketPart).putAll(map);
    }

    private void transform(
        final File input,
        final File output,
        final boolean isPocketPart,
        final String webBuildProductType,
        final String anchorFileName
        ) {
        final Transformer transformer =
            transformerBuilderFactory.create()
                .withXsl(transformToAnchorToDocumentIdMapXsl)
                .withParameter("isPocketPart", isPocketPart)
                .withParameter("webBuildProductType", webBuildProductType)
                .withParameter("docId", anchorFileName).build();
        final TransformationCommand command =
            new TransformationCommandBuilder(transformer, output).withInput(input).build();
        transformationService.transform(command);
    }
}
