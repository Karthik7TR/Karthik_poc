package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
import org.apache.commons.io.FileUtils;
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

    private File xpp;
    private File expectedMain;
    private File expectedFootnotes;

    @Before
    public void setUp() throws URISyntaxException
    {
        xpp = new File(OriginalStructureTransformationStepIntegrationTest.class.getResource("sampleXpp.xml").toURI());
        expectedMain =
            new File(OriginalStructureTransformationStepIntegrationTest.class.getResource("expected.main").toURI());
        expectedFootnotes = new File(
            OriginalStructureTransformationStepIntegrationTest.class.getResource("expected.footnote").toURI());
    }

    @Test
    public void shouldReturnOriginalXml() throws Exception
    {
        //given
        final File xppDir = xppGatherFileSystem.getXppBundleMaterialNumberDirectory(step, MATERIAL_NUMBER)
            .toPath()
            .resolve("bundleName")
            .resolve("XPP")
            .toFile();
        FileUtils.forceMkdir(xppDir);
        FileUtils.copyFileToDirectory(xpp, xppDir);
        //when
        step.executeStep();
        //then
        final File main = fileSystem.getOriginalFile(step, MATERIAL_NUMBER, "sampleXpp");
        final File footnotes = fileSystem.getFootnotesFile(step, MATERIAL_NUMBER, "sampleXpp");
        assertThat(expectedMain, hasSameContentAs(main));
        assertThat(expectedFootnotes, hasSameContentAs(footnotes));
    }
}
