package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component("formatFileSystem")
public class FormatFileSystemImpl implements FormatFileSystem {
    private static final String FORMAT_DIR_NAME = "Format";

    @Resource(name = "bookFileSystem")
    private BookFileSystem bookFileSystem;

    @NotNull
    @Override
    public File getFormatDirectory(@NotNull final Long jobInstanceId) {
        return new File(bookFileSystem.getWorkDirectoryByJobId(jobInstanceId), FORMAT_DIR_NAME);
    }

    @NotNull
    @Override
    public File getFormatDirectory(@NotNull final BookStep step) {
        return new File(bookFileSystem.getWorkDirectory(step), FORMAT_DIR_NAME);
    }

    @NotNull
    @Override
    public File getSplitBookDirectory(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), "splitEbook");
    }

    @NotNull
    @Override
    public File getSplitBookInfoFile(@NotNull final BookStep step) {
        return new File(getSplitBookDirectory(step), "splitNodeInfo.txt");
    }

    @NotNull
    @Override
    public File getImageToDocumentManifestFile(@NotNull final BookStep step) {
        return new File(getFormatDirectory(step), "doc-to-image-manifest.txt");
    }
}
