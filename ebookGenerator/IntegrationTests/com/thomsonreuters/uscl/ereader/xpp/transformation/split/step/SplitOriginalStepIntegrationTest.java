package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
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
public final class SplitOriginalStepIntegrationTest
{
    private static final String MATERIAL_NUMBER = "11111111";

    @Resource(name = "splitOriginalTask")
    private SplitOriginalStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File main;
    private File footnotes;
    private File expectedMainPart1;
    private File expectedMainPart2;
    private File expectedFootnotesPart1;
    private File expectedFootnotesPart2;

    @Before
    public void setUp() throws URISyntaxException
    {
        main = new File(SplitOriginalStepIntegrationTest.class.getResource("sample.main").toURI());
        footnotes = new File(SplitOriginalStepIntegrationTest.class.getResource("sample.footnotes").toURI());
        expectedMainPart1 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample_1_main.part").toURI());
        expectedMainPart2 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample_2_main.part").toURI());
        expectedFootnotesPart1 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample_1_footnotes.part").toURI());
        expectedFootnotesPart2 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample_2_footnotes.part").toURI());
    }

    @Test
    public void shouldSplitOriginalXml() throws Exception
    {
        //given
        final File originalDirectory = mkdir(fileSystem.getOriginalBundleDirectory(step, MATERIAL_NUMBER));
        FileUtils.copyFileToDirectory(main, originalDirectory);
        FileUtils.copyFileToDirectory(footnotes, originalDirectory);
        //when
        step.executeStep();
        //then
        final File actualMainPart1 = fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "sample", 1, PartType.MAIN);
        final File actualMainPart2 = fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "sample", 2, PartType.MAIN);
        final File actualFootnotesPart1 = fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "sample", 1, PartType.FOOTNOTE);
        final File actualFootnotesPart2 = fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "sample", 2, PartType.FOOTNOTE);
        assertThat(expectedMainPart1, hasSameContentAs(actualMainPart1));
        assertThat(expectedMainPart2, hasSameContentAs(actualMainPart2));
        assertThat(expectedFootnotesPart1, hasSameContentAs(actualFootnotesPart1));
        assertThat(expectedFootnotesPart2, hasSameContentAs(actualFootnotesPart2));
    }
}
