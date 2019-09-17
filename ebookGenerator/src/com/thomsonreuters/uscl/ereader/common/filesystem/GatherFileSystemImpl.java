package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_TOC_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_TOC_FILE;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component("gatherFileSystem")
public class GatherFileSystemImpl implements GatherFileSystem {
    @Resource(name = "bookFileSystem")
    private BookFileSystem bookFileSystem;

    @NotNull
    @Override
    public File getGatherRootDirectory(@NotNull final Long jobInstanceId) {
        return new File(bookFileSystem.getWorkDirectoryByJobId(jobInstanceId), GATHER_DIR.getName());
    }

    @NotNull
    @Override
    public File getGatherRootDirectory(@NotNull final BookStep step) {
        return new File(bookFileSystem.getWorkDirectory(step), GATHER_DIR.getName());
    }

    @NotNull
    @Override
    public File getGatherTocFile(@NotNull final BookStep step) {
        return getGatherRootDirectory(step).toPath().resolve(GATHER_TOC_DIR.getName()).resolve(GATHER_TOC_FILE.getName()).toFile();
    }
}
