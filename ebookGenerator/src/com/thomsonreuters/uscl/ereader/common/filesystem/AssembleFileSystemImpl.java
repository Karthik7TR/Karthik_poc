package com.thomsonreuters.uscl.ereader.common.filesystem;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.ASSEMBLE_MINOR_VERSIONS_MAPPING_XML_FILE;
import static com.thomsonreuters.uscl.ereader.core.book.util.FileUtils.getDir;
import static com.thomsonreuters.uscl.ereader.core.book.util.FileUtils.getFile;

@Component("assembleFileSystem")
public class AssembleFileSystemImpl implements AssembleFileSystem {
    private static final String COVER_ART_FILE_NAME = "coverArt.PNG";
    private static final String ARTWORK_DIR_NAME = "artwork";
    private static final String ASSETS_DIR_NAME = "assets";
    private static final String TITLE_XML = "title.xml";
    private static final String ASSEMBLE = "Assemble";

    @Resource(name = "bookFileSystem")
    private BookFileSystem bookFileSystem;

    @Override
    public File getAssembleDirectory(@NotNull final BookStep step) {
        return getDir(bookFileSystem.getWorkDirectory(step), ASSEMBLE);
    }

    @Override
    public File getTitleDirectory(@NotNull final BookStep step) {
        return getDir(getAssembleDirectory(step), step.getBookDefinition().getTitleId());
    }

    @Override
    public File getTitleXml(@NotNull final BookStep step) {
        return getFile(getTitleDirectory(step), TITLE_XML);
    }

    @Override
    public File getSplitPartTitleXml(@NotNull final BookStep step, @NotNull final Integer splitPartNumber) {
        return new File(getSplitTitleDirectory(
            step, String.format("%s_pt%s", step.getBookDefinition().getTitleId(), splitPartNumber)), TITLE_XML);
    }

    @Override
    public File getSplitTitleDirectory(@NotNull final BookStep step, @NotNull final String splitTitleId) {
        final String titleId = StringUtils.substringAfterLast(splitTitleId, "/");
        return new File(getAssembleDirectory(step), titleId.isEmpty() ? splitTitleId : titleId);
    }

    @Override
    public File getAssetsDirectory(@NotNull final BookStep step) {
        return getDir(getTitleDirectory(step), ASSETS_DIR_NAME);
    }

    @Override
    public File getSplitPartAssetsDirectory(@NotNull final BookStep step, @NotNull final Integer splitPartNumber) {
        return new File(getSplitTitleDirectory(step,
            String.format("%s_pt%s", step.getBookDefinition().getTitleId(), splitPartNumber)), ASSETS_DIR_NAME);
    }

    @Override
    public File getDocumentsDirectory(@NotNull final BookStep step) {
        return new File(getTitleDirectory(step), DOCUMENTS_DIR_NAME);
    }

    @Override
    public File getSplitPartDocumentsDirectory(@NotNull final BookStep step, @NotNull final String spitTitleId) {
        return new File(getSplitTitleDirectory(step, spitTitleId), DOCUMENTS_DIR_NAME);
    }

    @Override
    public File getAssembledBookFile(@NotNull final BookStep step) {
        final BookDefinition bookDefinition = step.getBookDefinition();
        final String version = step.getBookVersion().getVersionForFilePattern();
        return new File(
            bookFileSystem.getWorkDirectory(step),
            bookDefinition.getTitleId() + version + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
    }

    @Override
    public File getAssembledSplitTitleFile(@NotNull final BookStep step, @NotNull final String splitTitleId) {
        final String titleId = StringUtils.substringAfterLast(splitTitleId, "/");
        final String version = step.getBookVersion().getVersionForFilePattern();
        return new File(
            bookFileSystem.getWorkDirectory(step),
            titleId + version + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
    }

    @Override
    public File getArtworkDirectory(final BookStep step) {
        return getDir(getTitleDirectory(step), ARTWORK_DIR_NAME);
    }

    @Override
    public File getSplitPartArtworkDirectory(final BookStep step, final Integer splitPartNumber) {
        return new File(getSplitTitleDirectory(step,
            String.format("%s_pt%s", step.getBookDefinition().getTitleId(), splitPartNumber)), ARTWORK_DIR_NAME);
    }

    @Override
    public File getArtworkFile(final BookStep step) {
        return new File(getArtworkDirectory(step), COVER_ART_FILE_NAME);
    }

    @Override
    public File getSplitPartArtworkFile(final BookStep step, final Integer splitPartNumber) {
        return new File(getSplitPartArtworkDirectory(step, splitPartNumber), COVER_ART_FILE_NAME);
    }

    @Override
    @NotNull
    public Integer countAssembleDocs(@NotNull final BookStep step) {
        return FileUtils.listFiles(getDocumentsDirectory(step), null, true).size();
    }

    @NotNull
    @Override
    public File getMinorVersionMappingXml(@NotNull final BookStep step) {
        return new File(getAssetsDirectory(step), ASSEMBLE_MINOR_VERSIONS_MAPPING_XML_FILE.getName());
    }
}
