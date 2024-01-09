package com.thomsonreuters.uscl.ereader.common.filesystem;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class TestBookFileSystemImpl implements BookFileSystem {
    private File tempDir;

    @Override
    public File getWorkDirectory(final BookStep step) {
        try {
            if (tempDir == null) {
                tempDir = Files.createTempDirectory("workDirectory").toFile();
            }
            return tempDir;
        } catch (final IOException e) {
            log.error("", e);
            return null;
        }
    }

    @Override
    public File getWorkDirectoryByJobId(final Long jobInstanceId) {
        return getWorkDirectory(null);
    }

    public void reset() {
        tempDir = null;
    }
}
