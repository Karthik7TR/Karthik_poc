package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        return new File(getFormatDirectory(step), "01_Original");
    }

    @NotNull
    @Override
    public File getOriginalBundleDirectory(@NotNull final BookStep step, @NotNull final String materialNumber)
    {
        return new File(getOriginalDirectory(step), materialNumber);
    }

    @NotNull
    @Override
    public File getOriginalFile(@NotNull final BookStep step, @NotNull final String materialNumber, @NotNull final String xppFileName)
    {
        final String fileName = FilenameUtils.removeExtension(xppFileName);
        return new File(getOriginalBundleDirectory(step, materialNumber), fileName + ".main");
    }

    @NotNull
    @Override
    public File getOriginalFile(@NotNull final BookStep step, @NotNull final String xppFileName)
    {
        final String fileName = FilenameUtils.removeExtension(xppFileName);
        return new File(getOriginalDirectory(step), fileName + ".main");
    }

    @NotNull
    @Override
    public Collection<File> getOriginalFiles(@NotNull final BookStep step)
    {
        return FileUtils.listFiles(getOriginalDirectory(step), new String[] {"main"}, false);
    }

    @NotNull
    @Override
    public File getFootnotesFile(@NotNull final BookStep step, @NotNull final String materialNumber, @NotNull final String xppFileName)
    {
        final String fileName = FilenameUtils.removeExtension(xppFileName);
        return new File(getOriginalBundleDirectory(step, materialNumber), fileName + ".footnotes");
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

    @NotNull
    @Override
    public Map<String, Collection<File>> getOriginalMainAndFootnoteFiles(@NotNull final BookStep step)
    {
        return getMaterialNumberToFilesMap(getOriginalDirectory(step));
    }

    @NotNull
    @Override
    public File getSectionbreaksDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "02_Sectionbreaks");
    }

    @NotNull
    @Override
    public File getSectionbreaksFile(@NotNull final BookStep step, @NotNull final String name)
    {
        return new File(getSectionbreaksDirectory(step), name);
    }

    @NotNull
    @Override
    public File getPagebreakesUpDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "02_PagebreakesUp");
    }

    @NotNull
    @Override
    public File getPagebreakesUpDirectory(@NotNull final BookStep step, @NotNull final String materialNumber)
    {
        return new File(getFormatDirectory(step), "02_PagebreakesUp/" + materialNumber);
    }

    @NotNull
    @Override
    public File getPagebreakesUpFile(@NotNull final BookStep step, @NotNull final String name)
    {
        return new File(getPagebreakesUpDirectory(step), name);
    }

    @NotNull
    @Override
    public File getPagebreakesUpFile(@NotNull final BookStep step, @NotNull final String materialNumber, @NotNull final String name)
    {
        return new File(getPagebreakesUpDirectory(step), materialNumber + "/" + name);
    }

    @NotNull
    @Override
    public Map<String, Collection<File>> getPagebreakesUpFiles(@NotNull final BookStep step)
    {
        return getMaterialNumberToFilesMap(getPagebreakesUpDirectory(step));
    }

    @NotNull
    @Override
    public File getOriginalPartsDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "03_OriginalParts");
    }

    @NotNull
    @Override
    public File getOriginalPartsDirectory(@NotNull final BookStep step, @NotNull final String materialNumber)
    {
        return new File(getFormatDirectory(step), "03_OriginalParts/" + materialNumber);
    }

    @NotNull
    @Override
    public File getOriginalPartsFile(
        @NotNull final BookStep step,
        @NotNull final String materialNumber,
        @NotNull final String name,
        final int partNumber,
        @NotNull final PartType type)
    {
        final String fileName = FilenameUtils.removeExtension(name);
        final String partFileName = String.format("%s_%s_%s.part", fileName, partNumber, type.getName());
        return new File(getOriginalPartsDirectory(step, materialNumber), partFileName);
    }

    @NotNull
    @Override
    public File getOriginalPagesDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "04_OriginalPages");
    }

    @NotNull
    @Override
    public File getOriginalPagesDirectory(@NotNull final BookStep step, @NotNull final String materialNumber)
    {
        return new File(getFormatDirectory(step), "04_OriginalPages/" + materialNumber);
    }

    @NotNull
    @Override
    public File getOriginalPageFile(@NotNull final BookStep step, @NotNull final String name, final int pageNumber)
    {
        final String fileBaseName = FilenameUtils.removeExtension(name);
        final String fileName = String.format("%s_%d.page", fileBaseName, pageNumber);
        return new File(getOriginalPagesDirectory(step), fileName);
    }

    @NotNull
    @Override
    public File getOriginalPageFile(@NotNull final BookStep step, @NotNull final String materialNumber, @NotNull final String name, final int pageNumber)
    {
        final String fileBaseName = FilenameUtils.removeExtension(name);
        final String fileName = String.format("%s_%d.page", fileBaseName, pageNumber);
        return new File(getOriginalPagesDirectory(step, materialNumber), fileName);
    }

    @NotNull
    @Override
    public File getHtmlPagesDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "05_HtmlPages");
    }

    @NotNull
    @Override
    public File getHtmlPageFile(@NotNull final BookStep step, @NotNull final String name)
    {
        final String fileName = FilenameUtils.removeExtension(name);
        return new File(getHtmlPagesDirectory(step), fileName + ".html");
    }

    @NotNull
    @Override
    public File getAnchorToDocumentIdMapFile(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "anchorToDocumentIdMapFile.txt");
    }

    @NotNull
    @Override
    public File getDocToImageMapFile(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "doc-to-image-manifest.txt");
    }

    @NotNull
    @Override
    public File getTocDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "06_Toc");
    }

    @NotNull
    @Override
    public File getTocItemToDocumentIdMapFile(@NotNull final BookStep step)
    {
        return new File(getTocDirectory(step), "tocItemToDocumentIdMap.xml");
    }

    @NotNull
    @Override
    public File getTocFile(@NotNull final BookStep step)
    {
        return new File(getTocDirectory(step), "toc.xml");
    }

    @NotNull
    @Override
    public File getTitleMetadataDirectory(@NotNull final BookStep step)
    {
        return new File(getFormatDirectory(step), "07_title_metadata");
    }

    @NotNull
    @Override
    public File getTitleMetadataFile(@NotNull final BookStep step)
    {
        return new File(getTitleMetadataDirectory(step), "titleMetadata.xml");
    }

    private Map<String, Collection<File>> getMaterialNumberToFilesMap(@NotNull final File root)
    {
        final Map<String, Collection<File>> files = new HashMap<>();
        for (final File sourceDir : root.listFiles())
        {
            if (sourceDir.isDirectory()) //TODO: need this check till use both multibundle and noBundle approaches
            {
                final String materialNumber = sourceDir.getName();
                files.put(materialNumber, Arrays.asList(sourceDir.listFiles()));
            }
        }
        return files;
    }
}
