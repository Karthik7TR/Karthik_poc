package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Decorate AssembleFileSystem#getAssembleDirectory, to create temporary assemble directory.
 * We creating temporary directory for compatibility with proview, until our assemble directory
 * will be able to be validated by proview.
 *
 * created: 5/22/2017
 * Should be removed when our xpp steps will provide us valid assemble.
 */
@Component("assembleFileSystemTemp")
public class AssembleFileSystemTempDecorator extends AssembleFileSystemImpl
{
    @Override
    public File getAssembleDirectory(@NotNull final BookStep step)
    {
        final File assembleDirectory = super.getAssembleDirectory(step);
        final String assembleDirectoryName = assembleDirectory.getName() + "_temp";
        return new File(assembleDirectory.getParentFile(), assembleDirectoryName);
    }
}
