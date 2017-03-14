package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;
import java.util.Collection;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for XPP transformation steps
 */
public interface XppFormatFileSystem extends FormatFileSystem
{
    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /1_Original}
     */
    @NotNull
    File getOriginalDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 1_Original}{@code /fileName.original}
     */
    @NotNull
    File getOriginalFile(@NotNull BookStep step, @NotNull String xppFileName);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 1_Original}{@code /*.original}
     */
    @NotNull
    Collection<File> getOriginalFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 1_Original}{@code /fileName.footnotes}
     */
    @NotNull
    File getFootnotesFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 1_Original}{@code /*.footnotes}
     */
    @NotNull
    Collection<File> getFootnotesFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /2_PagebreakesUp}
     */
    @NotNull
    File getPagebreakesUpDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 2_PagebreakesUp}{@code /fileName_type.pagebreakesUp}
     */
    @NotNull
    File getPagebreakesUpFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /3_OriginalParts}
     */
    @NotNull
    File getOriginalPartsDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPartsDirectory 3_OriginalParts}
     * {@code /fileName_type_number.part}
     */
    @NotNull
    File getOriginalPartsFile(@NotNull BookStep step, @NotNull String name, @NotNull PartType type, int partNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /4_ToHtml}
     */
    @NotNull
    File getToHtmlDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getToHtmlDirectory 4_ToHtml}{@code /fileName.html}
     */
    @NotNull
    File getToHtmlFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getToHtmlDirectory 4_ToHtml}{@code /anchorToDocumentIdMapFile}
     */
    @NotNull
    File getAnchorToDocumentIdMapFile(@NotNull BookStep step);
}
