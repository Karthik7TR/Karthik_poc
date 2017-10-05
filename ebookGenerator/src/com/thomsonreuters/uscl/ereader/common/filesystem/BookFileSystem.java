package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for book generation process
 */
public interface BookFileSystem {
    /**
     * Returns book workDirectory: {@code /apps/eBookBuilder/environment/data/yyyyMMdd/titleId/jobInstanceId/}
     */
    @NotNull
    File getWorkDirectory(@NotNull BookStep step);
}
