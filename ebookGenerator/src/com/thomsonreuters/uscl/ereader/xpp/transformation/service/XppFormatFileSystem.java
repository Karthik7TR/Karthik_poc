package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.basefiles.BaseFilesIndex;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.PartFilesIndex;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for XPP transformation steps
 */
public interface XppFormatFileSystem extends FormatFileSystem
{
    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_StructureWithMetadata}{@code /[material number]}
     */
    @NotNull
    File getStructureWithMetadataBundleDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_StructureWithMetadata}{@code /[material number]/*.xml}
     */
    @NotNull
    Map<String, Collection<File>> getStructureWithMetadataFiles(@NotNull BookStep step);

    /**
     * Returns index file based on maps: by material number then by base filename then by content type (main or footnotes).
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_StructureWithMetadata}{@code /[material number]/[Map<MaterialNumber, Map<BaseFilename, Map<PartType, File>>>]}
     */
    @NotNull
    BaseFilesIndex getStructureWithMetadataFilesIndex(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory #_StructureWithMetadata}{@code /[material number]}{@code /fileName.main}
     */
    @NotNull
    File getStructureWithMetadataFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String fileName);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_Css}
     */
    @NotNull
    File getFontsCssDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_Css}{@code /[material number]}
     */
    @NotNull
    File getFontsCssDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getFontsCssDirectory #_Css}{@code /[material number]}{@code /*.css}
     */
    @NotNull
    File getFontsCssFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_Original}{@code /[material number]}
     */
    @NotNull
    File getOriginalDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_Original}{@code /[material number]}{@code /Processed Cite Queries}
     */
    @NotNull
    File getCiteQueryProcessedDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_Original}{@code /[material number]}{@code /Processed Cite Queries}{@code /*DIVXML.xml}
     */
    @NotNull
    File getCiteQueryProcessedFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory #_Original}{@code /[material number]}{@code /fileName.main}
     */
    @NotNull
    File getOriginalFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory #_Original}{@code /[material number]}{@code /fileName.footnotes}
     */
    @NotNull
    File getFootnotesFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_Original}{@code /[material number]/*.main|*.footnotes}
     */
    @NotNull
    Map<String, Collection<File>> getOriginalMainAndFootnoteFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_Sectionbreaks}{@code /[material number]}
     */
    @NotNull
    File getSectionbreaksDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getSectionbreaksUpDirectory #_Sectionbreaks}{@code /[material number]}{@code /fileName.type}
     */
    @NotNull
    File getSectionbreaksFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_Sectionbreaks}{@code /[material number]}{@code /*.*}
     */
    @NotNull
    Map<String, Collection<File>> getSectionBreaksFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_MultiColumnsUp}{@code /[material number]}
     */
    @NotNull
    File getMultiColumnsUpDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getSectionbreaksUpDirectory #_MultiColumnsUp}{@code /[material number]}{@code /fileName.type}
     */
    @NotNull
    File getMultiColumnsUpFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * Bundle name (material number) to list of files related to this bundle.
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getSectionbreaksUpDirectory #_MultiColumnsUp}{@code /[material number]}{@code /fileName.type}
     */
    @NotNull
    Map<String, Collection<File>> getMultiColumnsUpFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_SectionbreaksUp}{@code /[material number]}
     */
    @NotNull
    File getSectionbreaksUpDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getSectionbreaksUpDirectory #_SectionbreaksUp}{@code /[material number]}{@code /fileName.type}
     */
    @NotNull
    File getSectionbreaksUpFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * Bundle name (material number) to list of files related to this bundle.
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getSectionbreaksUpDirectory #_SectionbreaksUp}{@code /[material number]}{@code /fileName.type}
     */
    @NotNull
    Map<String, Collection<File>> getSectionbreaksUpFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_OriginalParts}{@code /[material number]}
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
     * {@code /}{@link getOriginalPartsDirectory #_OriginalParts}
     * {@code /[material number]}
     * {@code /fileName_#_type_family-number.part}
     */
    @NotNull
    PartFilesIndex getOriginalPartsFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_OriginalPages}{@code /[material number]}
     */
    @NotNull
    File getOriginalPagesDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPagesDirectory #_OriginalPages}{@code /[material number]}{@code /fileName_#_docFamilyGuid.page}
     */
    @NotNull
    File getOriginalPageFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String fileBaseName, int pageNumber, @NotNull String docFamilyGuid);

    /**
     * Returns map "bundle name (material number) to list of files related to this bundle" from original pages directory.
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalPagesDirectory #_OriginalPages}{@code /[material number]}{@code /fileName_#.page}
     */
    @NotNull
    Map<String, Collection<File>> getOriginalPageFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_HtmlPages}
     */
    @NotNull
    File getHtmlPagesDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_HtmlPages}{@code /[material number]}
     */
    @NotNull
    File getHtmlPagesDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getHtmlPagesDirectory #_HtmlPages}{@code /[material number]}{@code /fileName.html}
     */
    @NotNull
    File getHtmlPageFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * Returns map "bundle name (material number) to list of files related to this bundle" from html pages directory.
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getHtmlPagesDirectory #_HtmlPages}{@code /[material number]}{@code /fileName_#.html}
     */
    @NotNull
    Map<String, Collection<File>> getHtmlPageFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_ExternalLinks}
     */
    @NotNull
    File getExternalLinksDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_ExternalLinks}{@code /[material number]}
     */
    @NotNull
    File getExternalLinksDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getExternalLinksDirectory #_ExternalLinks}{@code /[material number]}{@code /fileName.html}
     */
    @NotNull
    File getExternalLinksFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * Returns map "bundle name (material number) to list of files related to this bundle" from external links directory.
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getHtmlPagesDirectory #_ExternalLinks}{@code /[material number]}{@code /fileName_#.html}
     */
    @NotNull
    Map<String, Collection<File>> getExternalLinksFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getExternalLinksDirectory #_ExternalLinks_Mapping}{@code /[material number]}
     */
    @NotNull
    File getExternalLinksMappingDirectory(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_ExternalLinks_Mapping}
     */
    @NotNull
    File getExternalLinksMappingDirectory(@NotNull  BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getExternalLinksMappingDirectory #_ExternalLinks_Mapping}{@code /[material number]}{@code /fileName.html}
     */
    @NotNull
    File getExternalLinksMappingFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String name);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /#_Anchors/anchorToDocumentIdMapFile.<materialNumber>.xml}
     */
    @NotNull
    File getAnchorToDocumentIdMapFile(@NotNull BookStep step, @NotNull String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getToHtmlDirectory}{@code /doc-to-image-manifest.txt}
     */
    @NotNull
    File getDocToImageMapFile(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@code indexBreaks}{@code /indexbreak-fileName}
     */
    @NotNull
    File getIndexBreaksFile(@NotNull BookStep step, @NotNull String materialNumber, @NotNull String fileName);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_Toc}
     */
    @NotNull
    File getTocDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTocDirectory #_Toc}{@code /toc.xml}
     */
    @NotNull
    File getTocFile(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTocDirectory #_Toc}{/@code materialNumber}{/@code toc_bundleFile}
     */
    @NotNull
    File getBundlePartTocFile(@NotNull String bundleFile, @NotNull String materialNumber, @NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTocDirectory #_Toc}{/@code materialNumber}{/@code toc_mergedFile}
     */
    @NotNull
    File getMergedBundleTocFile(@NotNull String materialNumber, @NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /#_title_metadata}
     */
    @NotNull
    File getTitleMetadataDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getTitleDirectory/#_title_metadata}{@code /titleMetadata.xml}
     */
    @NotNull
    File getTitleMetadataFile(@NotNull BookStep step);

    /**
     * Utility method to get fileName of .part file by given parameters.
     */
    @NotNull
    String getPartFileName(@NotNull String baseFilename, @NotNull int pageNumber, @NotNull PartType type, @NotNull String docFamilyGuid);

    /**
     * Utility method to get fileName of .page file by given parameters.
     */
    @NotNull
    String getPageFileName(@NotNull String baseFilename, @NotNull int pageNumber, @NotNull String docFamilyGuid);
}
