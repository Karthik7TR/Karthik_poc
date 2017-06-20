package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.DocumentFile;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.PartFilesIndex;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step.DocumentName;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component("xppFormatFileSystem")
public class XppFormatFileSystemImpl extends FormatFileSystemImpl implements XppFormatFileSystem
{
    private static final String ORIGINAL_DIR       = "01_Original";
    private static final String SECTIONBREAKS_DIR  = "02_Sectionbreaks";
    private static final String PAGEBREAKES_UP_DIR = "03_SectionbreaksUp";
    private static final String ORIGINAL_PARTS_DIR = "04_OriginalParts";
    private static final String ORIGINAL_PAGES_DIR = "05_OriginalPages";
    private static final String HTML_PAGES_DIR     = "06_HtmlPages";
    private static final String TOC_DIR            = "07_Toc";
    private static final String TITLE_METADATA_DIR = "08_title_metadata";

    private static final String TITLE_METADATA_FILE = "titleMetadata.xml";
    private static final String TOC_FILE = "toc.xml";
    private static final String TOC_ITEM_TO_DOCUMENT_ID_MAP_FILE = "tocItemToDocumentIdMap.xml";
    private static final String DOC_TO_IMAGE_MANIFEST_FILE = "doc-to-image-manifest.txt";
    private static final String ANCHOR_TO_DOCUMENT_ID_MAP_FILE = "anchorToDocumentIdMapFile.txt";

    private static final String HTML = "html";
    private static final String MAIN = "main";
    private static final String FOOTNOTES = "footnotes";

    @NotNull
    @Override
    public File getOriginalDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), ORIGINAL_DIR);
    }

    @NotNull
    @Override
    public File getOriginalDirectory(@NotNull final BookStep step, @NotNull final String materialNumber)
    {
        return new File(getOriginalDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getOriginalFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String xppFileName)
    {
        final String fileName = FilenameUtils.removeExtension(xppFileName);
        return new File(getOriginalDirectory(step, materialNumber), fileName + "." + MAIN);
    }

    @NotNull
    @Override
    public File getFootnotesFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String xppFileName)
    {
        final String fileName = FilenameUtils.removeExtension(xppFileName);
        return new File(getOriginalDirectory(step, materialNumber), fileName + "." + FOOTNOTES);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getOriginalMainAndFootnoteFiles(@NotNull final BookStep step)
    {
        return getMaterialNumberToFilesMap(getOriginalDirectory(step));
    }

    @NotNull
    @Override
    public File getSectionbreaksDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), SECTIONBREAKS_DIR);
    }

    @NotNull
    @Override
    public File getSectionbreaksDirectory(@NotNull final BookStep step, @NotNull final String materialNumber)
    {
        return new File(getSectionbreaksDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getSectionbreaksFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String name)
    {
        return new File(getSectionbreaksDirectory(step, materialNumber), name);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getSectionBreaksFiles(@NotNull final BookStep step)
    {
        return getMaterialNumberToFilesMap(getSectionbreaksDirectory(step));
    }

    @NotNull
    @Override
    public File getPagebreakesUpDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), PAGEBREAKES_UP_DIR);
    }

    @NotNull
    @Override
    public File getPagebreakesUpDirectory(@NotNull final BookStep step, @NotNull final String materialNumber)
    {
        return new File(getPagebreakesUpDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getPagebreakesUpFile(@NotNull final BookStep step, @NotNull final String name)
    {
        return new File(getPagebreakesUpDirectory(step), name);
    }

    @NotNull
    @Override
    public File getPagebreakesUpFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String name)
    {
        return new File(getPagebreakesUpDirectory(step), materialNumber + "/" + name);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getPagebreakesUpFiles(@NotNull final BookStep step)
    {
        return getMaterialNumberToFilesMap(getPagebreakesUpDirectory(step));
    }

    @NotNull
    @Override
    public File getOriginalPartsDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), ORIGINAL_PARTS_DIR);
    }

    @NotNull
    @Override
    public File getOriginalPartsDirectory(@NotNull final BookStep step, @NotNull final String materialNumber)
    {
        return new File(getOriginalPartsDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public PartFilesIndex getOriginalPartsFiles(@NotNull final BookStep step)
    {
        final PartFilesIndex result = new PartFilesIndex();
        for (final Map.Entry<String, Collection<File>> dir : getMaterialNumberToFilesMap(getOriginalPartsDirectory(step)).entrySet())
        {
            for (final File file : dir.getValue())
            {
                final DocumentFile documentFile = new DocumentFile(file);
                final DocumentName documentName = documentFile.getDocumentName();
                final String baseName = documentName.getBaseName();
                final Integer index = documentName.getOrder();
                final PartType type = documentName.getPartType();

                result.put(dir.getKey(), baseName, index, type, documentFile);
            }
        }
        return result;
    }

    @NotNull
    @Override
    public File getOriginalPagesDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), ORIGINAL_PAGES_DIR);
    }

    @NotNull
    @Override
    public File getOriginalPagesDirectory(@NotNull final BookStep step, @NotNull final String materialNumber)
    {
        return new File(getOriginalPagesDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getOriginalPageFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String fileBaseName,
        final int pageNumber,
        @NotNull final String docFamilyGuid)
    {
        final String fileName = String.format("%s_%04d_%s.page", fileBaseName, pageNumber, docFamilyGuid);
        return new File(getOriginalPagesDirectory(step, materialNumber), fileName);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getOriginalPageFiles(@NotNull final BookStep step)
    {
        return getMaterialNumberToFilesMap(getOriginalPagesDirectory(step));
    }

    @NotNull
    @Override
    public File getHtmlPagesDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), HTML_PAGES_DIR);
    }

    @NotNull
    @Override
    public File getHtmlPagesDirectory(@NotNull final BookStep step, @NotNull final String materialNumber)
    {
        return new File(getHtmlPagesDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getHtmlPageFile(@NotNull final BookStep step, @NotNull final String name)
    {
        final String fileName = FilenameUtils.removeExtension(name);
        return new File(getHtmlPagesDirectory(step), fileName + "." + HTML);
    }

    @NotNull
    @Override
    public File getHtmlPageFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String name)
    {
        final String fileName = FilenameUtils.removeExtension(name);
        return new File(getHtmlPagesDirectory(step, materialNumber), fileName + "." + HTML);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getHtmlPageFiles(@NotNull final BookStep step)
    {
        return getMaterialNumberToFilesMap(getHtmlPagesDirectory(step));
    }

    @NotNull
    @Override
    public File getAnchorToDocumentIdMapFile(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), ANCHOR_TO_DOCUMENT_ID_MAP_FILE);
    }

    @NotNull
    @Override
    public File getDocToImageMapFile(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), DOC_TO_IMAGE_MANIFEST_FILE);
    }

    @NotNull
    @Override
    public File getTocDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), TOC_DIR);
    }

    @NotNull
    @Override
    public File getTocItemToDocumentIdMapFile(@NotNull final BookStep step)
    {
        return new File(getTocDirectory(step), TOC_ITEM_TO_DOCUMENT_ID_MAP_FILE);
    }

    @NotNull
    @Override
    public File getTocFile(@NotNull final BookStep step)
    {
        return new File(getTocDirectory(step), TOC_FILE);
    }

    @NotNull
    @Override
    public File getBundlePartTocFile(
        @NotNull final String bundleFile,
        @NotNull final String materialNumber,
        @NotNull final BookStep step)
    {
        return getTocDirectory(step).toPath().resolve(materialNumber).resolve("toc_" + bundleFile).toFile();
    }

    @NotNull
    @Override
    public File getTitleMetadataDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), TITLE_METADATA_DIR);
    }

    @NotNull
    @Override
    public File getTitleMetadataFile(@NotNull final BookStep step)
    {
        return new File(getTitleMetadataDirectory(step), TITLE_METADATA_FILE);
    }

    private Map<String, Collection<File>> getMaterialNumberToFilesMap(@NotNull final File root)
    {
        final Map<String, Collection<File>> files = new HashMap<>();
        for (final File sourceDir : root.listFiles())
        {
            final String materialNumber = sourceDir.getName();
            files.put(materialNumber, Arrays.asList(sourceDir.listFiles()));
        }
        return files;
    }
}
