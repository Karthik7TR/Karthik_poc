package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemException;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("xppGatherFileSystem")
public class XppGatherFileSystemImpl extends GatherFileSystemImpl implements XppGatherFileSystem
{
    private static FilenameFilter XML_FILES_FILENAME_FILTER = new FilenameFilter()
    {
        @Override
        public boolean accept(final File dir, final String name)
        {
            return name.toLowerCase().endsWith(".xml");
        }
    };

    //TODO: temp usage to static xpp source directory
    @Value("${xpp.sample.xppTemp.directory}")
    private File xppTempDirectory;

    @Override
    public File getGatherRootDirectory(@NotNull final BookStep step)
    {
        return new File(xppTempDirectory, "Gather");
    }

    @Override
    public File getXppBundlesDirectory(final BookStep step)
    {
        return new File(getGatherRootDirectory(step), "Bundles");
    }

    @Override
    public Collection<File> getXppBundleContentDirectories(final BookStep step)
    {
        final File bundlesDir = getXppBundlesDirectory(step);
        final File[] bundlesMaterialNumberDirs = bundlesDir.listFiles();

        final Collection<File> bundlesContentDirs = new ArrayList<>();
        for (final File bundleMaterialNumberDir : bundlesMaterialNumberDirs)
        {
            bundlesContentDirs.add(extractBundleRootDir(bundleMaterialNumberDir));
        }
        return bundlesContentDirs;
    }

    @Override
    public Collection<String> getXppAssetsDirectories(final BookStep step)
    {
        final Collection<String> xppAssetsDirectories = new ArrayList<>();
        for (final File contentDir : getXppBundleContentDirectories(step))
        {
            final File assetsDir = new File(contentDir, "assets");
            validateExistence(assetsDir);
            xppAssetsDirectories.add(assetsDir.getAbsolutePath());
        }
        return xppAssetsDirectories;
    }

    @Override
    public Collection<File> getXppSourceXmlDirectories(final BookStep step)
    {
        final Collection<File> xppSourceXmlDirectories = new ArrayList<>();
        for (final File contentDir : getXppBundleContentDirectories(step))
        {
            final File xmlDir = new File(contentDir, "XPP");
            validateExistence(xmlDir);
            xppSourceXmlDirectories.add(xmlDir);
        }
        return xppSourceXmlDirectories;
    }

    @Override
    public Map<String, Collection<File>> getXppSourceXmls(final BookStep step)
    {
        final Map<String, Collection<File>> xppSourceXmlDirectories = new HashMap<>();
        for (final File sourceDir : getXppSourceXmlDirectories(step))
        {
            final String materialNumber = sourceDir.getParentFile().getParentFile().getName();
            xppSourceXmlDirectories.put(materialNumber, Arrays.asList(sourceDir.listFiles(XML_FILES_FILENAME_FILTER)));
        }
        return xppSourceXmlDirectories;
    }

    @Override
    @NotNull
    public File getXppBundleMaterialNumberDirectory(@NotNull final BookStep step, @NotNull final String materialNumber)
    {
        return new File(getXppBundlesDirectory(step), materialNumber);
    }

    @Override
    @NotNull
    public List<File> getAllBundleXmls(final BookStep step)
    {
        final List<File> resultList = new ArrayList<>();
        for (final File bundleMaterialNumberDir : getXppBundlesDirectory(step).listFiles())
        {
            resultList.add(new File(bundleMaterialNumberDir, "bundle.xml"));
        }

        return resultList;
    }

    private void validateExistence(final File item)
    {
        if (!item.exists())
        {
            throw new FileSystemException(item.getAbsolutePath() + " doesn't exist.");
        }
    }

    private File extractBundleRootDir(final File bundlesMaterialNumberDir)
    {
        for (final File item : bundlesMaterialNumberDir.listFiles())
        {
            if (item.isDirectory())
            {
                return item;
            }
        }
        throw new FileSystemException(
            bundlesMaterialNumberDir.getAbsolutePath() + " doesn't contain contents directory.");
    }
}
