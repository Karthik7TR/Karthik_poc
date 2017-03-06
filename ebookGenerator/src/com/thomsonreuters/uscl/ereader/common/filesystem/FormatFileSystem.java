package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for transformation steps of book generation process
 */
public interface FormatFileSystem
{
    /**
     * Returns format directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /Format}
     */
    @NotNull
    File getFormatDirectory(@NotNull BookStep step);

    /**
     * Returns split book directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /splitEbook}
     */
    @NotNull
    File getSplitBookDirectory(@NotNull BookStep step);

    /**
     * Returns split book info file:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /}{@link getSplitBookDirectory splitEbook}{@code /splitNodeInfo.txt}
     */
    @NotNull
    File getSplitBookInfoFile(@NotNull BookStep step);
}
