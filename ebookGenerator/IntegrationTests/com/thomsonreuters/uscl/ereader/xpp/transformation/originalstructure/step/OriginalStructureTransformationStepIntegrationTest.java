package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
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
    @Resource(name = "originalStructureTransformationTask")
    private OriginalStructureTransformationStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;
    @Autowired
    private BookFileSystem bookFileSystem;

    private File xppDirectory;
    private File xpp;
    private File expextedOriginal;
    private File expextedFootnotes;

    @Before
    public void setUp() throws URISyntaxException, IllegalAccessException
    {
        xpp = new File(OriginalStructureTransformationStepIntegrationTest.class.getResource("sampleXpp.xml").toURI());
        expextedOriginal =
            new File(OriginalStructureTransformationStepIntegrationTest.class.getResource("expected.original").toURI());
        expextedFootnotes = new File(
            OriginalStructureTransformationStepIntegrationTest.class.getResource("expected.footnotes").toURI());
        xppDirectory = new File(bookFileSystem.getWorkDirectory(step), "xppDirectory");
        FieldUtils.writeField(step, "xppDirectory", xppDirectory, true);
    }

    @Test
    public void shouldReturnOriginalXml() throws Exception
    {
        //given
        FileUtils.forceMkdir(xppDirectory);
        FileUtils.copyFileToDirectory(xpp, xppDirectory);
        //when
        step.executeStep();
        //then
        final File original = fileSystem.getOriginalFile(step, "sampleXpp");
        final File footnotes = fileSystem.getFootnotesFile(step, "sampleXpp");
        assertThat(expextedOriginal, hasSameContentAs(original));
        assertThat(expextedFootnotes, hasSameContentAs(footnotes));
    }
}
