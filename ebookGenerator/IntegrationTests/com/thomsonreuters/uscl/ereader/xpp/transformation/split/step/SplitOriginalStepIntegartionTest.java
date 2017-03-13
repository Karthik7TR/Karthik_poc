package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class SplitOriginalStepIntegartionTest
{
    @Resource(name = "splitOriginalTask")
    private SplitOriginalStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File original;
    private File footnotes;
    private File expectedOriginalPart1;
    private File expectedOriginalPart2;
    private File expectedFootnotesPart1;
    private File expectedFootnotesPart2;

    @Before
    public void setUp() throws URISyntaxException
    {
        original = new File(SplitOriginalStepIntegartionTest.class.getResource("sample.original").toURI());
        footnotes = new File(SplitOriginalStepIntegartionTest.class.getResource("sample.footnotes").toURI());
        expectedOriginalPart1 =
            new File(SplitOriginalStepIntegartionTest.class.getResource("sample_original_1.part").toURI());
        expectedOriginalPart2 =
            new File(SplitOriginalStepIntegartionTest.class.getResource("sample_original_2.part").toURI());
        expectedFootnotesPart1 =
            new File(SplitOriginalStepIntegartionTest.class.getResource("sample_footnotes_1.part").toURI());
        expectedFootnotesPart2 =
            new File(SplitOriginalStepIntegartionTest.class.getResource("sample_footnotes_2.part").toURI());
    }

    @Test
    public void shouldSplitOriginalXml() throws Exception
    {
        //given
        final File originalDirectory = fileSystem.getOriginalDirectory(step);
        FileUtils.forceMkdir(originalDirectory);
        FileUtils.copyFileToDirectory(original, originalDirectory);
        FileUtils.copyFileToDirectory(footnotes, originalDirectory);
        //when
        step.executeStep();
        //then
        final File actualOriginalPart1 = fileSystem.getOriginalPartsFile(step, "sample", PartType.MAIN, 1);
        final File actualOriginalPart2 = fileSystem.getOriginalPartsFile(step, "sample", PartType.MAIN, 2);
        final File actualFootnotesPart1 = fileSystem.getOriginalPartsFile(step, "sample", PartType.FOOTNOTE, 1);
        final File actualFootnotesPart2 = fileSystem.getOriginalPartsFile(step, "sample", PartType.FOOTNOTE, 2);
        assertThat(expectedOriginalPart1, hasSameContentAs(actualOriginalPart1));
        assertThat(expectedOriginalPart2, hasSameContentAs(actualOriginalPart2));
        assertThat(expectedFootnotesPart1, hasSameContentAs(actualFootnotesPart1));
        assertThat(expectedFootnotesPart2, hasSameContentAs(actualFootnotesPart2));
    }
}
