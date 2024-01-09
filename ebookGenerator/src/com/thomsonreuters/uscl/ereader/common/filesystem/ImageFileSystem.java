package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for image gather steps of book generation process
 */
public interface ImageFileSystem {
    /**
     * Returns images root directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /Images}
     */
    @NotNull
    File getImageRootDirectory(@NotNull BookStep step);

    /**
     * Returns dynamic images destination directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getImageRootDirectory Images}{@code /Dynamic}
     */
    @NotNull
    File getImageDynamicDirectory(@NotNull BookStep step);

    @NotNull
    File getImageStaticDirectory(@NotNull BookStep step);

    @NotNull
    File getImageStaticManifestFile(@NotNull BookStep step);
}
