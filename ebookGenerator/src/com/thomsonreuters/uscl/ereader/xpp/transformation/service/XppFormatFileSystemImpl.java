package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;
import java.util.Collection;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.sonar.runner.commonsio.FileUtils;
import org.springframework.stereotype.Component;

@Component("xppFormatFileSystem")
public class XppFormatFileSystemImpl extends FormatFileSystemImpl implements XppFormatFileSystem
{
    @NotNull
    @Override
    public File getOriginalDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "1_Original");
    }

    @NotNull
    @Override
    public File getOriginalFile(@NotNull final BookStep step, @NotNull final String xppFileName)
    {
        final String fileName = FilenameUtils.removeExtension(xppFileName);
        return new File(getOriginalDirectory(step), fileName + ".original");
    }

    @NotNull
    @Override
    public Collection<File> getOriginalFiles(@NotNull final BookStep step)
    {
        return FileUtils.listFiles(getOriginalDirectory(step), new String[] {"original"}, false);
    }

    @NotNull
    @Override
    public File getFootnotesFile(@NotNull final BookStep step, @NotNull final String xppFileName)
    {
        final String fileName = FilenameUtils.removeExtension(xppFileName);
        return new File(getOriginalDirectory(step), fileName + ".footnotes");
    }

    @NotNull
    @Override
    public Collection<File> getFootnotesFiles(@NotNull final BookStep step)
    {
        return FileUtils.listFiles(getOriginalDirectory(step), new String[] {"footnotes"}, false);
    }

    @Override
    public File getPagebreakesUpDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "2_PagebreakesUp");
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
        return new File(getFormatDirectory(step), "3_OriginalParts");
    }

    @NotNull
    @Override
    public File getOriginalPartsFile(@NotNull final BookStep step, @NotNull final String name, final int partNumber)
    {
        final String fileName = FilenameUtils.removeExtension(name);
        return new File(getOriginalPartsDirectory(step), fileName + "_" + partNumber + ".part");
    }

    @NotNull
    @Override
    public File getToHtmlDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "4_ToHtml");
    }

    @NotNull
    @Override
    public File getToHtmlFile(@NotNull final BookStep step, @NotNull final String name)
    {
        final String fileName = FilenameUtils.removeExtension(name);
        return new File(getToHtmlDirectory(step), fileName + ".html");
    }
}
