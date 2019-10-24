package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for gather steps of book generation process
 */
public interface GatherFileSystem {
    /**
     * Returns gather root directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /Gather}
     */
    @NotNull
    File getGatherRootDirectory(@NotNull Long jobInstanceId);
    /**
     * Returns gather root directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /Gather}
     */
    @NotNull
    File getGatherRootDirectory(@NotNull BookStep step);
    /**
     * Returns gather toc file:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getGatherRootDirectory Gather}{@code /Toc/toc.xml}
     */
    @NotNull
    File getGatherTocFile(@NotNull BookStep step);
    /**
     * Returns gather toc file:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getGatherRootDirectory Gather}{@code /Toc/indexDocsSingle.xml}
     */
    @NotNull
    File getGatherIndexFile(@NotNull BookStep step);
    /**
     * Returns gather docs directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getGatherRootDirectory Gather}{@code /Docs}
     */
    @NotNull
    File getGatherDocsDirectory(@NotNull BookStep step);
    /**
     * Returns gather docs metadata directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getGatherRootDirectory Gather}{@code /}{@link getGatherDocsDirectory Docs}{@code /Metadata}
     */
    @NotNull
    File getGatherDocsMetadataDirectory(@NotNull BookStep step);
}
