package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component("xppFormatFileSystem")
public class XppFormatFileSystemImpl extends FormatFileSystemImpl implements XppFormatFileSystem
{
    @NotNull
    @Override
    public File getOriginalDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "Original");
    }

    @NotNull
    @Override
    public File getOriginalFile(@NotNull final BookStep step, @NotNull final String xppFileName)
    {
        final String fileName = FilenameUtils.removeExtension(xppFileName);
        return new File(getOriginalDirectory(step), fileName + ".original");
    }

    @Override
    public File getPagebreakesUpDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "PagebreakesUp");
    }

    @Override
    public File getPagebreakesUpFile(@NotNull final BookStep step, @NotNull final String name)
    {
        final String fileName = FilenameUtils.removeExtension(name);
        return new File(getPagebreakesUpDirectory(step), fileName + ".pagebreakesUp");
    }

    @NotNull
    @Override
    public File getOriginalPartsDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "OriginalParts");
    }

    @Override
    public File getOriginalPartsFile(@NotNull final BookStep step, @NotNull final String name, final int partNumber)
    {
        final String fileName = FilenameUtils.removeExtension(name);
        return new File(getOriginalPartsDirectory(step), fileName + "_" + partNumber + ".part");
    }
}
