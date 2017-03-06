package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for archive step of book generation process
 */
public interface ArchiveFileSystem
{
    /**
     * Returns archive directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /archive}
     */
    @NotNull
    File getArchiveDirectory(@NotNull BookStep step);

    /**
     * Returns archive version directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getArchiveDirectory archive}{@code /[major|minor]}
     */
    @NotNull
    File getArchiveVersionDirectory(@NotNull BookStep step);
}
