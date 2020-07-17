package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for transformation steps of book generation process
 */
public interface FormatFileSystem {
    /**
     * Returns format directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /Format}
     */
    @NotNull
    File getFormatDirectory(@NotNull Long jobInstanceId);

    /**
     * Returns format directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /Format}
     */
    @NotNull
    File getFormatDirectory(@NotNull BookStep step);

    /**
     * Returns image metadata directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /ImageMetadata}
     */
    @NotNull
    File getImageMetadataDirectory(@NotNull BookStep step);

    /**
     * Returns preprocess directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /Preprocess}
     */
    @NotNull
    File getPreprocessDirectory(@NotNull BookStep step);

    /**
     * Returns process annotations directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /TransformCharSequences}
     */
    @NotNull
    File getTransformCharSequencesDirectory(@NotNull BookStep step);

    /**
     * Returns transformed directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /Transformed}
     */
    @NotNull
    File getTransformedDirectory(@NotNull BookStep step);

    /**
     * Returns html wrapper directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /HTMLWrapper}
     */
    @NotNull
    File getHtmlWrapperDirectory(@NotNull BookStep step);

    /**
     * Returns split book directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /splitEbook}
     */
    @NotNull
    File getSplitBookDirectory(@NotNull BookStep step);

    @NotNull
    File getDocToSplitBook(@NotNull BookStep step);

    /**
     * The path to title.xml within the assemble directory. The GenerateTitleMetadata step writes the title metadata to this file.
     */
    @NotNull
    File getSplitTitleXml(@NotNull BookStep step);

    /**
     * Returns jsoup transformation directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /JsoupTransformations}
     */
    @NotNull
    File getJsoupTransformationsDirectory(@NotNull BookStep step);

    /**
     * Returns split book info file:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /}{@link getSplitBookDirectory splitEbook}{@code /splitNodeInfo.txt}
     */
    @NotNull
    File getSplitBookInfoFile(@NotNull BookStep step);

    /**
     * Returns doc-to-image-manifest.txt file
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /doc-to-image-manifest.txt}
     */
    @NotNull
    File getImageToDocumentManifestFile(@NotNull BookStep step);

    /**
     * Returns FrontMatterHTML dir
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /FrontMatterHTML}
     */
    @NotNull
    File getFrontMatterHtmlDir(@NotNull BookStep step);

    /**
     * Returns FrontMatterPdf dir
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link getFormatDirectory Format}{@code /FrontMatterHTML/FrontMatterPdf}
     */
    @NotNull
    File getFrontMatterPdfImagesDir(@NotNull BookStep step);

    @NotNull
    File getThesaurusXml(@NotNull BookStep step);

    @NotNull
    File getThesaurusStaticFilesDirectory();
}
