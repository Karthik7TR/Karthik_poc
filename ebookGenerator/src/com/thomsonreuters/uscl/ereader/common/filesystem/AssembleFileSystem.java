package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for assemble step of book generation process
 */
public interface AssembleFileSystem {
    /**
     * Returns assemble directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /Assemble}
     */
    @NotNull
    File getAssembleDirectory(@NotNull BookStep step);

    /**
     * Returns assemble title directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}{@code /}
     * {@link #getAssembleDirectory Assemble}{@code /titleId}
     */
    @NotNull
    File getTitleDirectory(@NotNull BookStep step);

    /**
     * Returns title.xml:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}{@code /}
     * {@link #getAssembleDirectory Assemble}{@code /titleId/title.xml}
     */
    @NotNull
    File getTitleXml(@NotNull BookStep step);

    /**
     * Returns split title directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}{@code /}
     * {@link #getAssembleDirectory Assemble}{@code /splitTitleId}
     */
    @NotNull
    File getSplitTitleDirectory(@NotNull BookStep step, @NotNull String splitTitleId);

    /**
     * Returns assemble assets directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}{@code /}
     * {@link #getAssembleDirectory Assemble}{@code /titleId/assets}
     */
    @NotNull
    File getAssetsDirectory(@NotNull BookStep step);

    /**
     * Returns assemble documents directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}{@code /}
     * {@link #getAssembleDirectory Assemble}{@code /titleId/documents}
     */
    @NotNull
    File getDocumentsDirectory(@NotNull BookStep step);

    /**
     * Returns assembled book file:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /titleId.gz}
     */
    @NotNull
    File getAssembledBookFile(@NotNull BookStep step);

    /**
     * Returns assembled split book file:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /splitTitleId.gz}
     */
    @NotNull
    File getAssembledSplitTitleFile(@NotNull BookStep step, @NotNull String splitTitleId);

    /**
     * Returns assemble documents directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}{@code /}
     * {@link #getAssembleDirectory Assemble}{@code /titleId/artwork}
     */
    File getArtworkDirectory(@NotNull BookStep step);

    /**
     * Returns artwork from assemble directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /titleId/Assemble/artwork/coverArt.png}
     */
    @NotNull
    File getArtworkFile(@NotNull BookStep step);
}
