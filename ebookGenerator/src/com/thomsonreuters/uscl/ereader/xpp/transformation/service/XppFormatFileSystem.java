package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.PartFilesIndex;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for XPP transformation steps
 */
public interface XppFormatFileSystem extends FormatFileSystem
{
    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /02_StructureWithMetadata}
     */
    @NotNull
    File getStructureWithMetadataDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /02_StructureWithMetadata}{@code /[material number]}
     */
    @NotNull
    File getStructureWithMetadataBundleDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /02_StructureWithMetadata}{@code /[material number]/*.xml}
     */
    @NotNull
    Map<String, Collection<File>> getStructureWithMetadataFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory 02_StructureWithMetadata}{@code /[material number]}{@code /fileName.main}
     */
    @NotNull
    File getStructureWithMetadataFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String fileName);

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
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /03_Sectionbreaks}
     */
    @NotNull
    File getSectionbreaksDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /03_Sectionbreaks}{@code /[material number]}
     */
    @NotNull
    File getSectionbreaksDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 04_Sectionbreaks}{@code /[material number]}{@code /fileName.type}
     */
    @NotNull
    File getSectionbreaksFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /03_Sectionbreaks}{@code /[material number]}{@code /*.*}
     */
    @NotNull
    Map<String, Collection<File>> getSectionBreaksFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /04_SectionbreaksUp}
     */
    @NotNull
    File getPagebreakesUpDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /04_SectionbreaksUp}{@code /[material number]}
     */
    @NotNull
    File getPagebreakesUpDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 04_SectionbreaksUp}{@code /fileName.type}
     */
    @NotNull
    File getPagebreakesUpFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 04_SectionbreaksUp}{@code /[material number]}{@code /fileName.type}
     */
    @NotNull
    File getPagebreakesUpFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * Bundle name (material number) to list of files related to this bundle.
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getPagebreakesUpDirectory 04_SectionbreaksUp}{@code /[material number]}{@code /fileName.type}
     */
    @NotNull
    Map<String, Collection<File>> getPagebreakesUpFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /05_OriginalParts}
     */
    @NotNull
    File getOriginalPartsDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /05_OriginalParts}{@code /[material number]}
     */
    @NotNull
    File getOriginalPartsDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * Returns map of material number to
     * collection of groups of files with different types united by base name and index
     * like
     * material-number-1 ->
     *          [
     *              baseName1 ->
     *                  [
     *                      1 -> [
     *                          main     -> baseName1_1_main_familyNumber1.part,
     *                          footnote -> baseName1_1_footnote_familyNumber1.part
     *                      ],
     *                      2 -> [
     *                          main     -> baseName1_2_main_familyNumber1.part,
     *                          footnote -> baseName1_2_footnote_familyNumber1.part
     *                      ]
     *                  ],
     *              baseName2 ->
     *                  [
     *                      1 -> [
     *                          main     -> baseName2_1_main_familyNumber2.part,
     *                          footnote -> baseName2_1_footnote_familyNumber2.part
     *                      ]
     *                  ],
     *              ...
     *          ],
     * material-number-2 -> [...]
     *
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPartsDirectory 05_OriginalParts}
     * {@code /[material number]}
     * {@code /fileName_#_type_family-number.part}
     */
    @NotNull
    PartFilesIndex getOriginalPartsFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /06_OriginalPages}
     */
    @NotNull
    File getOriginalPagesDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /06_OriginalPages}{@code /[material number]}
     */
    @NotNull
    File getOriginalPagesDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPagesDirectory 06_OriginalPages}{@code /[material number]}{@code /fileName_#_docFamilyGuid.page}
     */
    @NotNull
    File getOriginalPageFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String fileBaseName, int pageNumber, @NotNull String docFamilyGuid);

    /**
     * Returns map "bundle name (material number) to list of files related to this bundle" from original pages directory.
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPagesDirectory 06_OriginalPages}{@code /[material number]}{@code /fileName_#.page}
     */
    @NotNull
    Map<String, Collection<File>> getOriginalPageFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /07_HtmlPages}
     */
    @NotNull
    File getHtmlPagesDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /07_HtmlPages}{@code /[material number]}
     */
    @NotNull
    File getHtmlPagesDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getHtmlPagesDirectory 07_HtmlPages}{@code /fileName.html}
     */
    @Deprecated
    @NotNull
    File getHtmlPageFile(@NotNull BookStep step, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getHtmlPagesDirectory 07_HtmlPages}{@code /[material number]}{@code /fileName.html}
     */
    @NotNull
    File getHtmlPageFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * Returns map "bundle name (material number) to list of files related to this bundle" from original pages directory.
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getHtmlPagesDirectory 07_HtmlPages}{@code /[material number]}{@code /fileName_#.html}
     */
    @NotNull
    Map<String, Collection<File>> getHtmlPageFiles(@NotNull BookStep step);

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
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /08_Toc}
     */
    @NotNull
    File getTocDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTocDirectory 08_Toc}{@code /tocItemToDocumentIdMap.xml}
     */
    @NotNull
    File getTocItemToDocumentIdMapFile(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTocDirectory 08_Toc}{@code /toc.xml}
     */
    @NotNull
    File getTocFile(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTocDirectory 08_Toc}{/@code materialNumber}{/@code toc_bundleFile}
     */
    @NotNull
    File getBundlePartTocFile(@NotNull String bundleFile, @NotNull String materialNumber, @NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /09_title_metadata}
     */
    @NotNull
    File getTitleMetadataDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTitleDirectory/09_title_metadata}{@code /titleMetadata.xml}
     */
    @NotNull
    File getTitleMetadataFile(@NotNull BookStep step);
}
