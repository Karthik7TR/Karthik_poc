package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;
import java.util.Collection;
import java.util.Map;

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
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /01_Original}
     */
    @NotNull
    File getOriginalDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /01_Original}{/[material number]}
     */
    @NotNull
    File getOriginalBundleDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 01_Original}{/[material number]}{@code /fileName.main}
     */
    @NotNull
    File getOriginalFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 01_Original}{@code /fileName.main}
     */
    @NotNull
    File getOriginalFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 01_Original}{@code /*.main}
     */
    @NotNull
    Collection<File> getOriginalFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 01_Original}{/[material number]}{@code /fileName.footnotes}
     */
    @NotNull
    File getFootnotesFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 01_Original}{@code /fileName.footnotes}
     */
    @NotNull
    File getFootnotesFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 01_Original}{@code /*.footnotes}
     */
    @NotNull
    Collection<File> getFootnotesFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /01_Original}{/[material number]/*.main|*.footnotes}
     */
    @NotNull
    Map<String, Collection<File>> getOriginalMainAndFootnoteFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /02_Sectionbreaks}
     */
    @NotNull
    File getSectionbreaksDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 02_Sectionbreaks}{@code /fileName.type}
     */
    @NotNull
    File getSectionbreaksFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /02_PagebreakesUp}
     */
    @NotNull
    File getPagebreakesUpDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /02_PagebreakesUp}{/[material number]}
     */
    @NotNull
    File getPagebreakesUpDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 02_PagebreakesUp}{@code /fileName.type}
     */
    @NotNull
    File getPagebreakesUpFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 02_PagebreakesUp}{/[material number]}{@code /fileName.type}
     */
    @NotNull
    File getPagebreakesUpFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * Bundle name (material number) to list of files related to this bundle.
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 02_PagebreakesUp}{/[material number]}{@code /fileName.type}
     */
    @NotNull
    Map<String, Collection<File>> getPagebreakesUpFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /03_OriginalParts}
     */
    @NotNull
    File getOriginalPartsDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /03_OriginalParts}{/[material number]}
     */
    @NotNull
    File getOriginalPartsDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPartsDirectory 03_OriginalParts}
     * {@code /fileName_number_type.part}
     */
    @NotNull
    File getOriginalPartsFile(@NotNull BookStep step, @NotNull String name, int partNumber, @NotNull PartType type);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /04_OriginalPages}
     */
    @NotNull
    File getOriginalPagesDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPagesDirectory 04_OriginalPages}{@code /fileName_#.page}
     */
    @NotNull
    File getOriginalPageFile(@NotNull BookStep step, @NotNull String name, int pageNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /05_HtmlPages}
     */
    @NotNull
    File getHtmlPagesDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getHtmlPagesDirectory 05_HtmlPages}{@code /fileName.html}
     */
    @NotNull
    File getHtmlPageFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getHtmlPagesDirectory}{@code /anchorToDocumentIdMapFile.txt}
     */
    @NotNull
    File getAnchorToDocumentIdMapFile(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getToHtmlDirectory}{@code /doc-to-image-manifest.txt}
     */
    @NotNull
    File getDocToImageMapFile(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /06_Toc}
     */
    @NotNull
    File getTocDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTocDirectory 06_Toc}{@code /tocItemToDocumentIdMap.xml}
     */
    @NotNull
    File getTocItemToDocumentIdMapFile(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTocDirectory 06_Toc}{@code /toc.xml}
     */
    @NotNull
    File getTocFile(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /07_title_metadata}
     */
    @NotNull
    File getTitleMetadataDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTitleDirectory/07_title_metadata}{@code /titleMetadata.xml}
     */
    @NotNull
    File getTitleMetadataFile(@NotNull BookStep step);
}
