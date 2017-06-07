package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import org.jetbrains.annotations.NotNull;

public interface ZipService
{
    /**
     * Unpacks file from
     * @param zipFilePath to
     * @param destDirectory
     * @return directory File with unpacked archive
     */
    @NotNull
    File unzip(@NotNull File zipFilePath, @NotNull File destDirectory);
}
