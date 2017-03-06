package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
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
        return new File(getOriginalDirectory(step), xppFileName + ".original");
    }
}
