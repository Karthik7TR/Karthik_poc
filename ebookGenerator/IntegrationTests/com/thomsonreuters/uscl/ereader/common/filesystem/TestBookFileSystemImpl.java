package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TestBookFileSystemImpl implements BookFileSystem
{
    private static final Logger LOG = LogManager.getLogger(TestBookFileSystemImpl.class);
    private File tempDir;

    @Override
    public File getWorkDirectory(final BookStep step)
    {
        try
        {
            if (tempDir == null)
            {
                tempDir = Files.createTempDirectory("workDirectory").toFile();
            }
            return tempDir;
        }
        catch (final IOException e)
        {
            LOG.error("", e);
            return null;
        }
    }
}
