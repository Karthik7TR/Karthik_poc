package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component("gatherFileSystem")
public class GatherFileSystemImpl implements GatherFileSystem {
    private static final String GATHER_DIR_NAME = "Gather";

    @Resource(name = "bookFileSystem")
    private BookFileSystem bookFileSystem;

    @NotNull
    @Override
    public File getGatherRootDirectory(@NotNull final Long jobInstanceId) {
        return new File(bookFileSystem.getWorkDirectoryByJobId(jobInstanceId), GATHER_DIR_NAME);
    }

    @NotNull
    @Override
    public File getGatherRootDirectory(@NotNull final BookStep step) {
        return new File(bookFileSystem.getWorkDirectory(step), GATHER_DIR_NAME);
    }
}
