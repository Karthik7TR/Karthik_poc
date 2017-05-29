package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class OriginalStructureTransformationStepIntegrationTest
{
    private static final String MATERIAL_NUMBER = "11111111";

    @Resource(name = "originalStructureTransformationTask")
    private OriginalStructureTransformationStep step;
    @Autowired
    private XppGatherFileSystem xppGatherFileSystem;
    @Autowired
    private XppFormatFileSystem fileSystem;
    @Autowired
    private BookFileSystem bookFileSystem;

    private File xppDirectory;
    private File xpp;
    private File expectedOriginal;
    private File expectedFootnotes;

    @Before
    public void setUp() throws URISyntaxException, IllegalAccessException
    {
        xpp = new File(OriginalStructureTransformationStepIntegrationTest.class.getResource("sampleXpp.xml").toURI());
        expectedOriginal =
            new File(OriginalStructureTransformationStepIntegrationTest.class.getResource("expected.original").toURI());
        expectedFootnotes = new File(
            OriginalStructureTransformationStepIntegrationTest.class.getResource("expected.footnotes").toURI());
        xppDirectory = new File(bookFileSystem.getWorkDirectory(step), "xppDirectory");
        FieldUtils.writeField(xppGatherFileSystem, "xppTempDirectory", xppDirectory, true);
    }

    @Test
    public void shouldReturnOriginalXml() throws Exception
    {
        //given
        final File xppDir = new File(xppDirectory + "/Gather/Bundles/" + MATERIAL_NUMBER + "/bundleName/XPP");
        FileUtils.forceMkdir(xppDir);
        FileUtils.copyFileToDirectory(xpp, xppDir);
        //when
        step.executeStep();
        //then
        final File original = fileSystem.getOriginalFile(step, MATERIAL_NUMBER, "sampleXpp");
        final File footnotes = fileSystem.getFootnotesFile(step, MATERIAL_NUMBER, "sampleXpp");
        assertThat(expectedOriginal, hasSameContentAs(original));
        assertThat(expectedFootnotes, hasSameContentAs(footnotes));
    }
}
