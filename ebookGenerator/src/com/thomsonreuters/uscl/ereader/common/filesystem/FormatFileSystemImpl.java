package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_DOC_TO_IMAGE_MANIFEST_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_EBOOK_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_SPLIT_NODE_INFO_FILE;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component("formatFileSystem")
public class FormatFileSystemImpl implements FormatFileSystem {
    @Resource(name = "bookFileSystem")
    private BookFileSystem bookFileSystem;

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
    public File getSplitBookDirectory(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_SPLIT_EBOOK_DIR.getName());
    }

    @NotNull
    @Override
    public File getSplitBookInfoFile(@NotNull final BookStep step) {
        return new File(getSplitBookDirectory(step), FORMAT_SPLIT_TOC_SPLIT_NODE_INFO_FILE.getName());
    }

    @NotNull
    @Override
    public File getImageToDocumentManifestFile(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), FORMAT_DOC_TO_IMAGE_MANIFEST_FILE.getName());
    }
}
