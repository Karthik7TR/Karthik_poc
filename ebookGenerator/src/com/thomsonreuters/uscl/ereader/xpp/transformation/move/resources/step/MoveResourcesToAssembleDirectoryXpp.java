package com.thomsonreuters.uscl.ereader.xpp.transformation.move.resources.step;

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.copyDirectoryToDirectory;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.copyFileToDirectory;
import static org.apache.commons.io.FileUtils.forceMkdir;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ResourcesFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import lombok.SneakyThrows;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class MoveResourcesToAssembleDirectoryXpp extends XppTransformationStep {
    @Resource(name = "assembleFileSystem")
    private AssembleFileSystem assembleFileSystem;

    @Resource(name = "resourcesFileSystemXpp")
    private ResourcesFileSystem resourcesFileSystem;

    @Override
    public void executeTransformation() throws Exception {
        getSplitPartsBundlesMap().forEach((partNumber, bundles) -> {
            final String titleId = getTitleId(partNumber);

            final File titleDirectory = assembleFileSystem.getSplitTitleDirectory(this, titleId);
            final File artworkDir = getSplitPartFileOrDefault(
                partNumber, assembleFileSystem::getSplitPartArtworkDirectory, assembleFileSystem::getArtworkDirectory);
            final File assetsDir = getSplitPartFileOrDefault(
                partNumber, assembleFileSystem::getSplitPartAssetsDirectory, assembleFileSystem::getAssetsDirectory);
            final File documentsDir = assembleFileSystem.getSplitPartDocumentsDirectory(this, titleId);

            makeDirs(titleDirectory, artworkDir, assetsDir, documentsDir);

            final File destinationArtworkFile = getSplitPartFileOrDefault(
                partNumber, assembleFileSystem::getSplitPartArtworkFile, assembleFileSystem::getArtworkFile);

            copyFiles(destinationArtworkFile, assetsDir, documentsDir, bundles);
        });
    }

    @SneakyThrows
    private void makeDirs(final File titleDirectory, final File artworkDir,
                          final File assetsDir, final File documentsDir) {
        forceMkdir(titleDirectory);
        artworkDir.mkdir();
        assetsDir.mkdir();
        documentsDir.mkdir();
    }

    @SneakyThrows
    private void copyFiles(final File destinationArtworkFile, final File assetsDir,
                           final File documentsDir, final List<XppBundle> bundles) {
        copyFile(resourcesFileSystem.getArtwork(this), destinationArtworkFile);
        copyFileToDirectory(resourcesFileSystem.getDocumentCss(), assetsDir);
        copyFileToDirectory(resourcesFileSystem.getTlrKeyImage(), assetsDir);
        copyFileToDirectory(destinationArtworkFile, assetsDir);
        copyDirectory(resourcesFileSystem.getAssetsDirectory(this), assetsDir);

        for (final XppBundle bundle : bundles) {
            copyDirectoryToDirectory(resourcesFileSystem.getDocumentsDirectory(
                this, bundle.getMaterialNumber()), documentsDir);

            for (final File cssFile : resourcesFileSystem.getFontsCssFiles(this, bundle.getMaterialNumber())) {
                copyFileToDirectory(cssFile, assetsDir);
            }
        }
    }
}
