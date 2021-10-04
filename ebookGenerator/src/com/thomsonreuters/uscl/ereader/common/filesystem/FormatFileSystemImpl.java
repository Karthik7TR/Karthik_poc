package com.thomsonreuters.uscl.ereader.common.filesystem;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_DE_DUPPING_ANCHOR_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_DOC_TO_IMAGE_MANIFEST_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_FRONT_MATTER_HTML_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_FRONT_MATTER_PDF_IMAGES_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_HTML_WRAPPER_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_IMAGE_METADATA_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_JSOUP_TRANSFORMATION_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_POST_TRANSFORM_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_PREPROCESS_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_EBOOK_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_EBOOK_SPLIT_TITLE_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_DOC_TO_SPLIT_BOOK_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_SPLIT_NODE_INFO_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_THESAURUS_XML_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_TRANSFORMED_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_TRANSFORM_CHAR_SEQUENCES_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_TRANSFORM_TOC;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.TOC_FILE;
import static com.thomsonreuters.uscl.ereader.core.book.util.FileUtils.getDir;
import static com.thomsonreuters.uscl.ereader.core.book.util.FileUtils.getFile;

@Component("formatFileSystem")
public class FormatFileSystemImpl implements FormatFileSystem {
    @Resource(name = "bookFileSystem")
    private BookFileSystem bookFileSystem;
    @Value("${thesaurus.static.files.dir}")
    private File thesaurusStaticFilesDir;

    @NotNull
    @Override
    public File getFormatDirectory(@NotNull final Long jobInstanceId) {
        return new File(bookFileSystem.getWorkDirectoryByJobId(jobInstanceId), FORMAT_DIR.getName());
    }

    @NotNull
    @Override
    public File getFormatDirectory(@NotNull final BookStep step) {
        return new File(bookFileSystem.getWorkDirectory(step), FORMAT_DIR.getName());
    }

    @NotNull
    @Override
    public File getTransformTocDirectory(@NotNull BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_TRANSFORM_TOC.getName());
    }

    @NotNull
    @Override
    public File getTransformedToc(@NotNull BookStep step) {
        return new File(getTransformTocDirectory(step), TOC_FILE.getName());
    }

    @NotNull
    @Override
    public File getImageMetadataDirectory(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_IMAGE_METADATA_DIR.getName());
    }

    @NotNull
    @Override
    public File getPreprocessDirectory(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_PREPROCESS_DIR.getName());
    }

    @NotNull
    @Override
    public File getTransformCharSequencesDirectory(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_TRANSFORM_CHAR_SEQUENCES_DIR.getName());
    }

    @NotNull
    @Override
    public File getTransformedDirectory(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_TRANSFORMED_DIR.getName());
    }

    @NotNull
    @Override
    public File getPostTransformDirectory(@NotNull final BookStep step) {
        return getDir(getFormatDirectory(step), FORMAT_POST_TRANSFORM_DIR.getName());
    }

    @NotNull
    @Override
    public File getHtmlWrapperDirectory(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_HTML_WRAPPER_DIR.getName());
    }

    @NotNull
    @Override
    public File getSplitBookDirectory(@NotNull final BookStep step) {
        return getDir(getFormatDirectory(step), FORMAT_SPLIT_EBOOK_DIR.getName());
    }

    @NotNull
    @Override
    public File getDocToSplitBook(@NotNull final BookStep step) {
        return getFile(getSplitBookDirectory(step), FORMAT_SPLIT_TOC_DOC_TO_SPLIT_BOOK_FILE.getName());
    }

    @NotNull
    @Override
    public File getSplitTitleXml(@NotNull final BookStep step) {
        return getFile(getSplitBookDirectory(step), FORMAT_SPLIT_EBOOK_SPLIT_TITLE_FILE.getName());
    }

    @NotNull
    @Override
    public File getJsoupTransformationsDirectory(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_JSOUP_TRANSFORMATION_DIR.getName());
    }

    @NotNull
    @Override
    public File getSplitBookInfoFile(@NotNull final BookStep step) {
        return new File(getSplitBookDirectory(step), FORMAT_SPLIT_TOC_SPLIT_NODE_INFO_FILE.getName());
    }

    @NotNull
    @Override
    public File getSplitTocDirectory(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_SPLIT_TOC_DIR.getName());
    }
    @NotNull
    @Override
    public File getSplitTocFile(@NotNull final BookStep step) {
        return new File(getSplitTocDirectory(step), FORMAT_SPLIT_TOC_FILE.getName());
    }

    @NotNull
    @Override
    public File getImageToDocumentManifestFile(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_DOC_TO_IMAGE_MANIFEST_FILE.getName());
    }

    @NotNull
    @Override
    public File getFrontMatterHtmlDir(@NotNull final BookStep step) {
        return getDir(getFormatDirectory(step), FORMAT_FRONT_MATTER_HTML_DIR.getName());
    }

    @NotNull
    @Override
    public File getFrontMatterPdfImagesDir(@NotNull final BookStep step) {
        return new File(getFrontMatterHtmlDir(step), FORMAT_FRONT_MATTER_PDF_IMAGES_DIR.getName());
    }

    @NotNull
    @Override
    public File getThesaurusXml(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_THESAURUS_XML_FILE.getName());
    }

    @NotNull
    @Override
    public File getThesaurusStaticFilesDirectory() {
        return thesaurusStaticFilesDir;
    }

    @NotNull
    @Override
    public File getDeDuppingAnchorFile(@NotNull final BookStep step) {
        return getFile(getFormatDirectory(step), FORMAT_DE_DUPPING_ANCHOR_FILE.getName());
    }


}
