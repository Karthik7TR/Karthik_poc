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
     * Returns gather index toc dir:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getGatherRootDirectory Gather}{@code /indexToc}
     */
    @NotNull
    File getGatherIndexTocDir(@NotNull BookStep step);
    /**
     * Returns gather index docs dir:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getGatherRootDirectory Gather}{@code /indexDocs}
     */
    @NotNull
    File getGatherIndexDocsDir(@NotNull BookStep step);
    /**
     * Returns subdir with name metadata:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getGatherRootDirectory Gather}{@code /[any dir]/metadata}
     */
    @NotNull
    File getGatherMetadataDir(@NotNull File step);

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

    /**
     * Returns gather docs metadata directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getGatherRootDirectory Gather}{@code /}{@code /docs-guids.txt}
     */
    @NotNull
    File getGatherDocGuidsFile(@NotNull BookStep step);
}
