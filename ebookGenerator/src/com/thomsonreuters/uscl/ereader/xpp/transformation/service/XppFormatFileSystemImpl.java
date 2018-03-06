package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.ANCHORS_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.EXTERNAL_LINKS_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.EXTERNAL_LINKS_MAPPING;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.FONTS_CSS_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.HTML_PAGES_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.MULTICOLUMNS_UP_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.ORIGINAL_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.ORIGINAL_PAGES_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.ORIGINAL_PARTS_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.SECTIONBREAKS_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.SECTIONBREAKS_UP_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.SOURCE_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.TITLE_METADATA_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.TOC_DIR;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.basefiles.BaseFilesIndex;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.DocumentFile;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.PartFilesIndex;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step.DocumentName;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component("xppFormatFileSystem")
public class XppFormatFileSystemImpl extends FormatFileSystemImpl implements XppFormatFileSystem {
    public static final String ANCHOR_TO_DOCUMENT_ID_MAP_FILE = "anchorToDocumentIdMapFile.xml";
    public static final String ANCHOR_TO_DOCUMENT_ID_MAP_BOUND_FILE = "anchorToDocumentIdMapBoundFile.xml";
    public static final String ANCHOR_TO_DOCUMENT_ID_MAP_SUPPLEMENT_FILE = "anchorToDocumentIdMapSupplementFile.xml";

    static final String CITE_QUERY_PROCESSED_DIR = "Processed Cite Queries";

    private static final String TITLE_METADATA_FILE = "titleMetadata.xml";
    private static final String TOC_FILE = "toc.xml";
    private static final String MERGED_BUNDLE_TOC_FILE = "toc_merged.xml";
    private static final String DOC_TO_IMAGE_MANIFEST_FILE = "doc-to-image-manifest.txt";

    private static final String HTML = "html";
    private static final String MAIN = "main";
    private static final String CSS = "css";
    private static final String FOOTNOTES = "footnotes";

    @NotNull
    File getStructureWithMetadataDirectory(@NotNull final BookStep step) {
        return getDirectory(step, SOURCE_DIR);
    }

    @NotNull
    @Override
    public File getStructureWithMetadataBundleDirectory(
        @NotNull final BookStep step,
        @NotNull final String materialNumber) {
        return new File(getStructureWithMetadataDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getStructureWithMetadataFiles(@NotNull final BookStep step) {
        return getMaterialNumberToFilesMap(getStructureWithMetadataDirectory(step));
    }

    @NotNull
    @Override
    public BaseFilesIndex getStructureWithMetadataFilesIndex(@NotNull final BookStep step) {
        return getBaseFilesIndex(getStructureWithMetadataDirectory(step));
    }

    @NotNull
    @Override
    public File getStructureWithMetadataFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String fileName) {
        return Paths.get(getStructureWithMetadataDirectory(step).toString(), materialNumber, fileName).toFile();
    }

    @Override
    @NotNull
    public File getFontsCssDirectory(@NotNull final BookStep step) {
        return getDirectory(step, FONTS_CSS_DIR);
    }

    @NotNull
    @Override
    public File getFontsCssDirectory(@NotNull final BookStep step, final String materialNumber) {
        return new File(getFontsCssDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getFontsCssFile(@NotNull final BookStep step, final String materialNumber, final String name) {
        final String fileName = FilenameUtils.removeExtension(name);
        return new File(getFontsCssDirectory(step, materialNumber), fileName + "." + CSS);
    }

    @NotNull
    File getOriginalDirectory(@NotNull final BookStep step) {
        return getDirectory(step, ORIGINAL_DIR);
    }

    @NotNull
    @Override
    public File getOriginalDirectory(@NotNull final BookStep step, @NotNull final String materialNumber) {
        return new File(getOriginalDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getCiteQueryProcessedDirectory(final BookStep step, final String materialNumber) {
        return new File(getOriginalDirectory(step, materialNumber), CITE_QUERY_PROCESSED_DIR);
    }

    @NotNull
    @Override
    public File getCiteQueryProcessedFile(final BookStep step, final String materialNumber, final String name) {
        return new File(getCiteQueryProcessedDirectory(step, materialNumber), name);
    }

    @NotNull
    @Override
    public File getOriginalFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String xppFileName) {
        final String fileName = FilenameUtils.removeExtension(xppFileName);
        return new File(getOriginalDirectory(step, materialNumber), fileName + "." + MAIN);
    }

    @NotNull
    @Override
    public File getFootnotesFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String xppFileName) {
        final String fileName = FilenameUtils.removeExtension(xppFileName);
        return new File(getOriginalDirectory(step, materialNumber), fileName + "." + FOOTNOTES);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getOriginalMainAndFootnoteFiles(@NotNull final BookStep step) {
        final Map<String, Collection<File>> filesMap = getMaterialNumberToFilesMap(getOriginalDirectory(step));
        for (final Map.Entry<String, Collection<File>> entry : filesMap.entrySet()) {
            final Set<File> notDirectories = new HashSet<>();
            for (final File file : entry.getValue()) {
                if (!file.isDirectory())
                    notDirectories.add(file);
            }
            entry.setValue(notDirectories);
        }
        return filesMap;
    }

    @NotNull
    File getSectionbreaksDirectory(@NotNull final BookStep step) {
        return newFile(getFormatDirectory(step), SECTIONBREAKS_DIR);
    }

    @NotNull
    @Override
    public File getSectionbreaksDirectory(@NotNull final BookStep step, @NotNull final String materialNumber) {
        return new File(getSectionbreaksDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getSectionbreaksFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String name) {
        return new File(getSectionbreaksDirectory(step, materialNumber), name);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getSectionBreaksFiles(@NotNull final BookStep step) {
        return getMaterialNumberToFilesMap(getSectionbreaksDirectory(step));
    }

    @NotNull
    File getMultiColumnsUpDirectory(@NotNull final BookStep step) {
        return newFile(getFormatDirectory(step), MULTICOLUMNS_UP_DIR);
    }

    @NotNull
    @Override
    public File getMultiColumnsUpDirectory(@NotNull final BookStep step, @NotNull final String materialNumber) {
        return new File(getMultiColumnsUpDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getMultiColumnsUpFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String name) {
        return new File(getMultiColumnsUpDirectory(step), materialNumber + "/" + name);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getMultiColumnsUpFiles(@NotNull final BookStep step) {
        return getMaterialNumberToFilesMap(getMultiColumnsUpDirectory(step));
    }

    @NotNull
    File getSectionbreaksUpDirectory(@NotNull final BookStep step) {
        return newFile(getFormatDirectory(step), SECTIONBREAKS_UP_DIR);
    }

    @NotNull
    @Override
    public File getSectionbreaksUpDirectory(@NotNull final BookStep step, @NotNull final String materialNumber) {
        return new File(getSectionbreaksUpDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getSectionbreaksUpFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String name) {
        return new File(getSectionbreaksUpDirectory(step), materialNumber + "/" + name);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getSectionbreaksUpFiles(@NotNull final BookStep step) {
        return getMaterialNumberToFilesMap(getSectionbreaksUpDirectory(step));
    }

    @NotNull
    File getOriginalPartsDirectory(@NotNull final BookStep step) {
        return newFile(getFormatDirectory(step), ORIGINAL_PARTS_DIR);
    }

    @NotNull
    @Override
    public File getOriginalPartsDirectory(@NotNull final BookStep step, @NotNull final String materialNumber) {
        return new File(getOriginalPartsDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public PartFilesIndex getOriginalPartsFiles(@NotNull final BookStep step) {
        return getPartFilesIndex(step, ORIGINAL_PARTS_DIR);
    }

    @NotNull
    File getOriginalPagesDirectory(@NotNull final BookStep step) {
        return newFile(getFormatDirectory(step), ORIGINAL_PAGES_DIR);
    }

    @NotNull
    @Override
    public File getOriginalPagesDirectory(@NotNull final BookStep step, @NotNull final String materialNumber) {
        return new File(getOriginalPagesDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getOriginalPageFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String fileBaseName,
        final int pageNumber,
        @NotNull final String docFamilyGuid) {
        return new File(
            getOriginalPagesDirectory(step, materialNumber),
            getPageFileName(fileBaseName, pageNumber, docFamilyGuid));
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getOriginalPageFiles(@NotNull final BookStep step) {
        return getMaterialNumberToFilesMap(getOriginalPagesDirectory(step));
    }

    @NotNull
    @Override
    public File getHtmlPagesDirectory(@NotNull final BookStep step) {
        return newFile(getFormatDirectory(step), HTML_PAGES_DIR);
    }

    @NotNull
    @Override
    public File getHtmlPagesDirectory(@NotNull final BookStep step, @NotNull final String materialNumber) {
        return new File(getHtmlPagesDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getHtmlPageFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String name) {
        final String fileName = FilenameUtils.removeExtension(name);
        return new File(getHtmlPagesDirectory(step, materialNumber), fileName + "." + HTML);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getHtmlPageFiles(@NotNull final BookStep step) {
        return getMaterialNumberToFilesMap(getHtmlPagesDirectory(step));
    }

    @NotNull
    @Override
    public File getExternalLinksDirectory(@NotNull final BookStep step) {
        return newFile(getFormatDirectory(step), EXTERNAL_LINKS_DIR);
    }

    @NotNull
    @Override
    public File getExternalLinksDirectory(@NotNull final BookStep step, @NotNull final String materialNumber) {
        return new File(getExternalLinksDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getExternalLinksMappingDirectory(@NotNull final BookStep step) {
        return newFile(getFormatDirectory(step), EXTERNAL_LINKS_MAPPING);
    }

    @NotNull
    @Override
    public File getExternalLinksMappingDirectory(@NotNull final BookStep step, @NotNull final String materialNumber) {
        return new File(getExternalLinksMappingDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getExternalLinksFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String name) {
        return new File(getExternalLinksDirectory(step, materialNumber), name);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getExternalLinksFiles(@NotNull final BookStep step) {
        return getMaterialNumberToFilesMap(getExternalLinksDirectory(step));
    }

    @NotNull
    @Override
    public File getExternalLinksMappingFile(@NotNull final BookStep step, @NotNull final String materialNumber, @NotNull final String name) {
        return new File(getExternalLinksMappingDirectory(step, materialNumber), name);
    }

    @NotNull
    File getAnchorToDocumentIdMapDirectory(@NotNull final BookStep step) {
        return newFile(getFormatDirectory(step), ANCHORS_DIR);
    }

    @NotNull
    @Override
    public File getAnchorToDocumentIdMapBoundFile(@NotNull final BookStep step) {
        return new File(getAnchorToDocumentIdMapDirectory(step), ANCHOR_TO_DOCUMENT_ID_MAP_BOUND_FILE);
    }

    @NotNull
    @Override
    public File getAnchorToDocumentIdMapSupplementFile(@NotNull final BookStep step) {
        return new File(getAnchorToDocumentIdMapDirectory(step), ANCHOR_TO_DOCUMENT_ID_MAP_SUPPLEMENT_FILE);
    }

    @NotNull
    @Override
    public File getAnchorToDocumentIdMapFile(@NotNull final BookStep step) {
        return new File(getAnchorToDocumentIdMapDirectory(step), ANCHOR_TO_DOCUMENT_ID_MAP_FILE);
    }

    @NotNull
    @Override
    public File getAnchorToDocumentIdMapFile(@NotNull final BookStep step, @NotNull final String materialNumber) {
        return new File(getAnchorToDocumentIdMapDirectory(step) + "/" + materialNumber, ANCHOR_TO_DOCUMENT_ID_MAP_FILE);
    }

    @NotNull
    @Override
    public File getDocToImageMapFile(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), DOC_TO_IMAGE_MANIFEST_FILE);
    }

    @NotNull
    @Override
    public File getIndexBreaksFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String fileName) {
        return getFormatDirectory(step).toPath()
            .resolve("indexBreaks")
            .resolve(materialNumber)
            .resolve("indexbreak-" + fileName)
            .toFile();
    }

    @NotNull
    @Override
    public File getTocDirectory(@NotNull final BookStep step) {
        return newFile(getFormatDirectory(step), TOC_DIR);
    }

    @NotNull
    @Override
    public File getTocFile(@NotNull final BookStep step) {
        return new File(getTocDirectory(step), TOC_FILE);
    }

    @NotNull
    @Override
    public File getBundlePartTocFile(
        @NotNull final String bundleFile,
        @NotNull final String materialNumber,
        @NotNull final BookStep step) {
        return getTocDirectory(step).toPath().resolve(materialNumber).resolve("toc_" + bundleFile).toFile();
    }

    @NotNull
    @Override
    public File getMergedBundleTocFile(@NotNull final String materialNumber, @NotNull final BookStep step) {
        return getTocDirectory(step).toPath().resolve(materialNumber).resolve(MERGED_BUNDLE_TOC_FILE).toFile();
    }

    @NotNull
    @Override
    public File getTitleMetadataDirectory(@NotNull final BookStep step) {
        return newFile(getFormatDirectory(step), TITLE_METADATA_DIR);
    }

    @NotNull
    @Override
    public File getTitleMetadataFile(@NotNull final BookStep step) {
        return new File(getTitleMetadataDirectory(step), TITLE_METADATA_FILE);
    }

    @NotNull
    @Override
    public String getPartFileName(
        @NotNull final String baseFilename,
        @NotNull final int pageNumber,
        @NotNull final PartType type,
        @NotNull final String docFamilyGuid) {
        return String.format("%s_%04d_%s_%s.part", baseFilename, pageNumber, type.getName(), docFamilyGuid);
    }

    @NotNull
    @Override
    public String getPageFileName(
        @NotNull final String baseFilename,
        @NotNull final int pageNumber,
        @NotNull final String docFamilyGuid) {
        return String.format("%s_%04d_%s.page", baseFilename, pageNumber, docFamilyGuid);
    }

    @Override
    @NotNull
    public File getDirectory(@NotNull final BookStep step, @NotNull final XppFormatFileSystemDir dir) {
        return newFile(getFormatDirectory(step), dir);
    }

    @Override
    @NotNull
    public File getDirectory(@NotNull final BookStep step, @NotNull final XppFormatFileSystemDir dir, @NotNull final String materialNumber) {
        return new File(getDirectory(step, dir), materialNumber);
    }

    @Override
    @NotNull
    public File getFile(@NotNull final BookStep step, @NotNull final XppFormatFileSystemDir dir, @NotNull final String materialNumber, @NotNull final String fileName) {
        return Paths.get(getDirectory(step, dir, materialNumber).toString(), fileName).toFile();
    }

    @Override
    @NotNull
    public Map<String, Collection<File>> getFiles(@NotNull final BookStep step, @NotNull final XppFormatFileSystemDir dir) {
        return getMaterialNumberToFilesMap(getDirectory(step, dir));
    }

    @Override
    @NotNull
    public BaseFilesIndex getBaseFilesIndex(@NotNull final BookStep step, @NotNull final XppFormatFileSystemDir dir) {
        return getBaseFilesIndex(getDirectory(step, dir));
    }

    /**
     * Builds index for base files with name [base file name].[part type like 'main' and 'footnotes'].
     * @param baseFilesDir - location of bundles with indexing files.
     */
    @NotNull
    private BaseFilesIndex getBaseFilesIndex(@NotNull final File baseFilesDir) {
        final BaseFilesIndex result = new BaseFilesIndex();
        for (final Map.Entry<String, Collection<File>> dir : getMaterialNumberToFilesMap(baseFilesDir).entrySet()) {
            for (final File file : dir.getValue()) {
                final String baseName = FilenameUtils.removeExtension(file.getName());
                final PartType type = PartType.valueOfByName(FilenameUtils.getExtension(file.getName()));
                result.put(dir.getKey(), baseName, type, file);
            }
        }
        return result;
    }

    @Override
    @NotNull
    public PartFilesIndex getPartFilesIndex(final BookStep step, final XppFormatFileSystemDir dir) {
        final PartFilesIndex result = new PartFilesIndex();
        for (final Map.Entry<String, Collection<File>> materialNumberDir : getMaterialNumberToFilesMap(
            getDirectory(step, dir)).entrySet()) {
            for (final File file : materialNumberDir.getValue()) {
                final DocumentFile documentFile = new DocumentFile(file);
                final DocumentName documentName = documentFile.getDocumentName();
                final String baseName = documentName.getBaseName();
                final PartType type = documentName.getPartType();

                result.put(materialNumberDir.getKey(), baseName, type, documentFile);
            }
        }
        return result;
    }

    @NotNull
    private Map<String, Collection<File>> getMaterialNumberToFilesMap(@NotNull final File root) {
        final Map<String, Collection<File>> files = new HashMap<>();
        for (final File sourceDir : root.listFiles()) {
            final String materialNumber = sourceDir.getName();
            files.put(materialNumber, Arrays.asList(sourceDir.listFiles()));
        }
        return files;
    }

    @NotNull
    private File newFile(final File baseDir, final XppFormatFileSystemDir dir) {
        return new File(baseDir, dir.getDirName());
    }

    @Override
    @NotNull
    public File getVolumesMapFile(@NotNull final BookStep step) {
        return new File(getDirectory(step, XppFormatFileSystemDir.VOLUMES_MAP_DIR), "volumesMap.xml");
    }
}
