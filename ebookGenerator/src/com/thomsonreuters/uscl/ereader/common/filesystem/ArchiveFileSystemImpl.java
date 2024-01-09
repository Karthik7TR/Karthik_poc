package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component("archiveFileSystem")
public class ArchiveFileSystemImpl implements ArchiveFileSystem {
    @Resource(name = "bookFileSystem")
    private BookFileSystem bookFileSystem;

    @NotNull
    @Override
    public File getArchiveDirectory(@NotNull final BookStep step) {
        return new File(bookFileSystem.getWorkDirectory(step), "archive");
    }

    @NotNull
    @Override
    public File getArchiveVersionDirectory(@NotNull final BookStep step) {
        final Version version = step.getBookVersion();
        final String dirName = version.isNewMajorVersion() ? "major" : "minor";
        return new File(getArchiveDirectory(step), dirName);
    }
}
