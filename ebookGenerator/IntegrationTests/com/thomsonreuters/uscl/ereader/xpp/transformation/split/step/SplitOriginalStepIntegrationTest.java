package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.entity.PartFilesIndex;
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
    private static final String BASE_FILE_NAME = "sample.DIVXML";

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
        main = new File(SplitOriginalStepIntegrationTest.class.getResource("sample.DIVXML.main").toURI());
        footnotes = new File(SplitOriginalStepIntegrationTest.class.getResource("sample.footnotes").toURI());
        expectedMainPart1 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample.DIVXML_0001_main_I0f72523026a511d99b08a92d7e97623c.part").toURI());
        expectedMainPart2 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample.DIVXML_0002_main_I4a6f120026a611d99b08a92d7e97623c.part").toURI());
        expectedMainPart3 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample.DIVXML_0003_main_I4a6f120026a611d99b08a92d7e97623d.part").toURI());
        expectedMainPart4 =
            new File(SplitOriginalStepIntegrationTest.class.getResource("sample.DIVXML_0004_main_I4a6f120026a611d99b08a92d7e97623e.part").toURI());
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
        final PartFilesIndex partFilesIndex = fileSystem.getOriginalPartsFiles(step);

        final File actualMainPart1 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, 1, PartType.MAIN).getFile();
        final File actualMainPart2 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, 2, PartType.MAIN).getFile();
        final File actualMainPart3 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, 3, PartType.MAIN).getFile();
        final File actualMainPart4 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, 4, PartType.MAIN).getFile();

        assertThat(expectedMainPart1, hasSameContentAs(actualMainPart1));
        assertThat(expectedMainPart2, hasSameContentAs(actualMainPart2));
        assertThat(expectedMainPart3, hasSameContentAs(actualMainPart3));
        assertThat(expectedMainPart4, hasSameContentAs(actualMainPart4));

        //TODO: return checking footnotes when split by structure for footnotes is ready
    }
}
