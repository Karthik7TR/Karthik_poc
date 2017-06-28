package com.thomsonreuters.uscl.ereader.common.service.compress;

import java.io.File;

import org.jetbrains.annotations.NotNull;

/**
 * Iface to work with archives
 */
public interface CompressService
{
    /**
     * Unpack archive
     * @param archie - archive file
     * @param destDir - directory to unpack
     * @param pathToSkip - path to skip in archive
     */
    void decompress(@NotNull File archie, @NotNull File destDir, @NotNull String pathToSkip) throws Exception;

    /**
     * Unpack archive
     * @param archie - archive file
     * @param destDir - directory to unpack
     */
    void decompress(@NotNull File archive, @NotNull File destDir) throws Exception;

    /**
     * Pack archive
     * @param srcDir - directory to pack
     * @param dest - result file
     * @throws Exception
     */
    void compress(@NotNull File srcDir, @NotNull File dest) throws Exception;
}
