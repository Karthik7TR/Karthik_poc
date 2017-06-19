package com.thomsonreuters.uscl.ereader.xpp.transformation.move.resources.step;

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.copyFileToDirectory;
import static org.apache.commons.io.FileUtils.forceMkdir;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ResourcesFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import org.springframework.batch.core.ExitStatus;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class MoveResourcesToAssembleDirectoryXpp extends BookStepImpl
{
    @Resource(name = "assembleFileSystemTemp")
    private AssembleFileSystem assembleFileSystem;

    @Resource(name = "resourcesFileSystemXpp")
    private ResourcesFileSystem resourcesFileSystem;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        final File titleDirectory = assembleFileSystem.getTitleDirectory(this);
        final File artworkDir = assembleFileSystem.getArtworkDirectory(this);
        final File assetsDir = assembleFileSystem.getAssetsDirectory(this);
        final File documentsDir = assembleFileSystem.getDocumentsDirectory(this);

        forceMkdir(titleDirectory);
        artworkDir.mkdir();
        assetsDir.mkdir();
        documentsDir.mkdir();

        final File destinationArtworkFile = assembleFileSystem.getArtworkFile(this);

        copyFile(resourcesFileSystem.getArtwork(this), destinationArtworkFile);
        copyFileToDirectory(resourcesFileSystem.getDocumentCss(), assetsDir);
        copyFileToDirectory(destinationArtworkFile, assetsDir);
        copyDirectory(resourcesFileSystem.getAssetsDirectory(this), assetsDir);
        copyDirectory(resourcesFileSystem.getDocumentsDirectory(this), documentsDir);

        return ExitStatus.COMPLETED;
    }
}
