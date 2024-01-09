package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_IMAGES_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_IMAGES_DYNAMIC_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_IMAGES_STATIC_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_STATIC_IMAGE_MANIFEST_FILE;
import static com.thomsonreuters.uscl.ereader.core.book.util.FileUtils.getDir;
import static com.thomsonreuters.uscl.ereader.core.book.util.FileUtils.getFile;

@Component("imageFileSystem")
public class ImageFileSystemImpl implements ImageFileSystem {
    @Resource(name = "gatherFileSystem")
    private GatherFileSystem gatherFileSystem;

    @Override
    public File getImageRootDirectory(final BookStep step) {
        return new File(gatherFileSystem.getGatherRootDirectory(step), GATHER_IMAGES_DIR.getName());
    }

    @Override
    public File getImageDynamicDirectory(final BookStep step) {
        return getDir(getImageRootDirectory(step), GATHER_IMAGES_DYNAMIC_DIR.getName());
    }

    @NotNull
    @Override
    public File getImageStaticDirectory(@NotNull final BookStep step) {
        return getDir(getImageRootDirectory(step), GATHER_IMAGES_STATIC_DIR.getName());
    }

    @NotNull
    @Override
    public File getImageStaticManifestFile(@NotNull final BookStep step) {
        return getFile(gatherFileSystem.getGatherRootDirectory(step), GATHER_STATIC_IMAGE_MANIFEST_FILE.getName());
    }
}
