package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class XppGatherFileSystemImplTest
{
    private static final String bundlesDir = "/Gather/Bundles";
    private static final String materialNumber1 = "11111111";
    private static final String materialNumber2 = "22222222";
    private static final String bundle1 = "/" + materialNumber1 + "/bundleName1";
    private static final String bundle2 = "/" + materialNumber2 + "/bundleName2";
    private static final String assets = "/assets";
    private static final String xpp = "/XPP";

    private static final String bundleRoot1 = bundlesDir + bundle1;
    private static final String bundleRoot2 = bundlesDir + bundle2;

    @InjectMocks
    private XppGatherFileSystemImpl fileSystem;
    @Mock
    private BookStep step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File xppFile1;
    private File xppFile2;
    private File txtFile;

    @Before
    public void setUp() throws IllegalAccessException, IOException
    {
        FieldUtils.writeField(fileSystem, "xppTempDirectory", temporaryFolder.getRoot(), true);

        final File bundleDir1 = mkdir(temporaryFolder.getRoot(), bundleRoot1);
        final File bundleDir2 = mkdir(temporaryFolder.getRoot(), bundleRoot2);

        mkdir(bundleDir1, assets);
        mkdir(bundleDir2, assets);

        final File xppDir1 = mkdir(bundleDir1, xpp);
        final File xppDir2 = mkdir(bundleDir2, xpp);

        xppFile1 = mkfile(xppDir1, "xppFile.xml");
        xppFile2 = mkfile(xppDir2, "xppFile.xml");
        txtFile = mkfile(xppDir1, "text.txt");
    }

    @Test
    public void shouldReturnXppBundlesDirectory()
    {
        //given
        //when
        final File file = fileSystem.getXppBundlesDirectory(step);
        //then
        assertThat(file, hasPath(bundlesDir));
    }

    @Test
    public void shouldReturnXppBundleContentDirectories()
    {
        //given
        //when
        final Collection<File> dirs = fileSystem.getXppBundleContentDirectories(step);
        //then

        final FileSystemMatcher bundleMatcher1 = new FileSystemMatcher(bundleRoot1);
        final FileSystemMatcher bundleMatcher2 = new FileSystemMatcher(bundleRoot2);

        for (final File dir : dirs)
        {
            assertTrue(bundleMatcher1.matches(dir) || bundleMatcher2.matches(dir));
        }
    }

    @Test
    public void shouldReturnXppAssetsDirectories()
    {
        //given
        //when
        final Collection<String> dirs = fileSystem.getXppAssetsDirectories(step);
        //then

        final FileSystemMatcher bundleMatcher1 = new FileSystemMatcher(bundleRoot1 + assets);
        final FileSystemMatcher bundleMatcher2 = new FileSystemMatcher(bundleRoot2 + assets);

        for (final String dir : dirs)
        {
            assertTrue(bundleMatcher1.matches(new File(dir)) || bundleMatcher2.matches(new File(dir)));
        }
    }

    @Test
    public void shouldReturnXppSourceXmlDirectories()
    {
        //given
        //when
        final Collection<File> dirs = fileSystem.getXppSourceXmlDirectories(step);
        //then

        final FileSystemMatcher bundleMatcher1 = new FileSystemMatcher(bundleRoot1 + xpp);
        final FileSystemMatcher bundleMatcher2 = new FileSystemMatcher(bundleRoot2 + xpp);

        for (final File dir : dirs)
        {
            assertTrue(bundleMatcher1.matches(dir) || bundleMatcher2.matches(dir));
        }
    }

    @Test
    public void shouldReturnXppSourceXmls()
    {
        //given
        //when
//        final Set<File> files = new HashSet<>(fileSystem.getXppSourceXmls(step));
        final Map<String, Collection<File>> sourceXmls = fileSystem.getXppSourceXmls(step);
        //then
        assertTrue(sourceXmls.get(materialNumber1).contains(xppFile1));
        assertFalse(sourceXmls.get(materialNumber1).contains(txtFile));
        assertTrue(sourceXmls.get(materialNumber2).contains(xppFile2));
    }

    private File mkdir(final File base, final String subPath)
    {
        final File newDir = new File(base, subPath);
        newDir.mkdirs();
        return newDir;
    }

    private File mkfile(final File base, final String fileName) throws IOException
    {
        final File newFile = new File(base, fileName);
        newFile.createNewFile();
        return newFile;
    }
}
