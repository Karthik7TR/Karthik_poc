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
    private File expectedMainPart3;
    private File expectedMainPart4;

    @Before
    public void setUp() throws URISyntaxException
    {
        main = new File(SplitOriginalStepIntegrationTest.class.getResource("sample.main").toURI());
        footnotes = new File(SplitOriginalStepIntegrationTest.class.getResource("sample.footnotes").toURI());
        expectedMainPart1 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample_1_main.part").toURI());
        expectedMainPart2 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample_2_main.part").toURI());
        expectedMainPart3 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample_3_main.part").toURI());
        expectedMainPart4 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample_4_main.part").toURI());
    }

    @Test
    public void shouldSplitOriginalXml() throws Exception
    {
        //given
        final File originalDirectory = mkdir(fileSystem.getSectionbreaksDirectory(step, MATERIAL_NUMBER));
        FileUtils.copyFileToDirectory(main, originalDirectory);
        FileUtils.copyFileToDirectory(footnotes, originalDirectory);
        //when
        step.executeStep();
        //then
        final File actualMainPart1 = fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "sample", 1, PartType.MAIN);
        final File actualMainPart2 = fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "sample", 2, PartType.MAIN);
        final File actualMainPart3 = fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "sample", 3, PartType.MAIN);
        final File actualMainPart4 = fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "sample", 4, PartType.MAIN);
        assertThat(expectedMainPart1, hasSameContentAs(actualMainPart1));
        assertThat(expectedMainPart2, hasSameContentAs(actualMainPart2));
        assertThat(expectedMainPart3, hasSameContentAs(actualMainPart3));
        assertThat(expectedMainPart4, hasSameContentAs(actualMainPart4));

        //TODO: return checking footnotes when split by structure for footnotes is ready
    }
}
