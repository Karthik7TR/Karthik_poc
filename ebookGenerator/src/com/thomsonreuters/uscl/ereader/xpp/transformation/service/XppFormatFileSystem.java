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
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /01_Original}{@code /[material number]}
     */
    @NotNull
    File getOriginalDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 01_Original}{@code /[material number]}{@code /fileName.main}
     */
    @NotNull
    File getOriginalFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 01_Original}{@code /[material number]}{@code /fileName.footnotes}
     */
    @NotNull
    File getFootnotesFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /01_Original}{@code /[material number]/*.main|*.footnotes}
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
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /02_Sectionbreaks}{@code /[material number]}
     */
    @NotNull
    File getSectionbreaksDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 02_Sectionbreaks}{@code /[material number]}{@code /fileName.type}
     */
    @NotNull
    File getSectionbreaksFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /02_Sectionbreaks}{@code /[material number]}{@code /*.*}
     */
    @NotNull
    Map<String, Collection<File>> getSectionBreaksFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /03_SectionbreaksUp}
     */
    @NotNull
    File getPagebreakesUpDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /03_SectionbreaksUp}{@code /[material number]}
     */
    @NotNull
    File getPagebreakesUpDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 03_SectionbreaksUp}{@code /fileName.type}
     */
    @NotNull
    File getPagebreakesUpFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 03_SectionbreaksUp}{@code /[material number]}{@code /fileName.type}
     */
    @NotNull
    File getPagebreakesUpFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * Bundle name (material number) to list of files related to this bundle.
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 03_SectionbreaksUp}{@code /[material number]}{@code /fileName.type}
     */
    @NotNull
    Map<String, Collection<File>> getPagebreakesUpFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /04_OriginalParts}
     */
    @Deprecated
    @NotNull
    File getOriginalPartsDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /04_OriginalParts}{@code /[material number]}
     */
    @NotNull
    File getOriginalPartsDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPartsDirectory 04_OriginalParts}
     * {@code /[material number]}
     * {@code /fileName_number_type.part}
     */
    @NotNull
    File getOriginalPartsFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name, int partNumber, @NotNull PartType type);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /05_OriginalPages}
     */
    @NotNull
    File getOriginalPagesDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /05_OriginalPages}{@code /[material number]}
     */
    @NotNull
    File getOriginalPagesDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPagesDirectory 05_OriginalPages}{@code /fileName_#.page}
     */
    @Deprecated
    @NotNull
    File getOriginalPageFile(@NotNull BookStep step, @NotNull String name, int pageNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPagesDirectory 05_OriginalPages}{@code /[material number]}{@code /fileName_#.page}
     */
    @NotNull
    File getOriginalPageFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name, int pageNumber);

    /**
     * Returns map "bundle name (material number) to list of files related to this bundle" from original pages directory.
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPagesDirectory 05_OriginalPages}{@code /[material number]}{@code /fileName_#.page}
     */
    @NotNull
    Map<String, Collection<File>> getOriginalPageFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /06_HtmlPages}
     */
    @NotNull
    File getHtmlPagesDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /06_HtmlPages}{@code /[material number]}
     */
    @NotNull
    File getHtmlPagesDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getHtmlPagesDirectory 06_HtmlPages}{@code /fileName.html}
     */
    @Deprecated
    @NotNull
    File getHtmlPageFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getHtmlPagesDirectory 06_HtmlPages}{@code /[material number]}{@code /fileName.html}
     */
    @NotNull
    File getHtmlPageFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

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
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /07_Toc}
     */
    @NotNull
    File getTocDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTocDirectory 07_Toc}{@code /tocItemToDocumentIdMap.xml}
     */
    @NotNull
    File getTocItemToDocumentIdMapFile(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTocDirectory 07_Toc}{@code /toc.xml}
     */
    @NotNull
    File getTocFile(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTocDirectory 07_Toc}{/@code materialNumber}{/@code toc_bundleFile}
     */
    @NotNull
    File getBundlePartTocFile(@NotNull String bundleFile, @NotNull String materialNumber, @NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /08_title_metadata}
     */
    @NotNull
    File getTitleMetadataDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTitleDirectory/08_title_metadata}{@code /titleMetadata.xml}
     */
    @NotNull
    File getTitleMetadataFile(@NotNull BookStep step);
}
