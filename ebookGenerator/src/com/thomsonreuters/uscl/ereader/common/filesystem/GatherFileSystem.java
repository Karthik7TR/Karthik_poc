package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for gather steps of book generation process
 */
public interface GatherFileSystem
{
    /**
     * Returns gather root directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /Gather}
     */
    @NotNull
    File getGatherRootDirectory(@NotNull BookStep step);
}
