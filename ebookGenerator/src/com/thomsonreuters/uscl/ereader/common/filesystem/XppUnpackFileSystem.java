package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for unpack XPP archive step
 */
public interface XppUnpackFileSystem
{
    /**
     * Returns XPP unpack root directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /XppUnpack}
     */
    @NotNull
    File getXppUnpackDirectory(@NotNull BookStep step);

    /**
     * Returns XPP unpacked images directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getXppUnpackDirectory XppUnpack}{@code /assets}
     */
    @NotNull
    String getXppAssetsDirectory(@NotNull BookStep step);
}
