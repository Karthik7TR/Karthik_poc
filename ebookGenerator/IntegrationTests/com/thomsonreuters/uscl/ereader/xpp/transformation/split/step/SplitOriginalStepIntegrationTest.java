package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.PartFilesIndex;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@ActiveProfiles("IntegrationTests")
public final class SplitOriginalStepIntegrationTest
{
    private static final String SECTION_1_UUID = "I0f72523026a511d99b08a92d7e97623c";
    private static final String SECTION_2_UUID = "I4a6f120026a611d99b08a92d7e97623c";
    private static final String SECTION_3_UUID = "I4a6f120026a611d99b08a92d7e97623d";
    private static final String SECTION_4_UUID = "I4a6f120026a611d99b08a92d7e97623e";

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

    private File expectedFootnotesPart1;
    private File expectedFootnotesPart2;
    private File expectedFootnotesPart3;
    private File expectedFootnotesPart4;

    @Before
    public void setUp() throws URISyntaxException
    {
        main = new File(SplitOriginalStepIntegrationTest.class.getResource("sample.DIVXML.main").toURI());
        footnotes = new File(SplitOriginalStepIntegrationTest.class.getResource("sample.DIVXML.footnotes").toURI());
        expectedMainPart1 = new File(SplitOriginalStepIntegrationTest.class.getResource(getFileName(1, PartType.MAIN, SECTION_1_UUID)).toURI());
        expectedMainPart2 = new File(SplitOriginalStepIntegrationTest.class.getResource(getFileName(2, PartType.MAIN, SECTION_2_UUID)).toURI());
        expectedMainPart3 = new File(SplitOriginalStepIntegrationTest.class.getResource(getFileName(3, PartType.MAIN, SECTION_3_UUID)).toURI());
        expectedMainPart4 = new File(SplitOriginalStepIntegrationTest.class.getResource(getFileName(4, PartType.MAIN, SECTION_4_UUID)).toURI());

        expectedFootnotesPart1 = new File(SplitOriginalStepIntegrationTest.class.getResource(getFileName(1, PartType.FOOTNOTE, SECTION_1_UUID)).toURI());
        expectedFootnotesPart2 = new File(SplitOriginalStepIntegrationTest.class.getResource(getFileName(2, PartType.FOOTNOTE, SECTION_2_UUID)).toURI());
        expectedFootnotesPart3 = new File(SplitOriginalStepIntegrationTest.class.getResource(getFileName(3, PartType.FOOTNOTE, SECTION_3_UUID)).toURI());
        expectedFootnotesPart4 = new File(SplitOriginalStepIntegrationTest.class.getResource(getFileName(4, PartType.FOOTNOTE, SECTION_4_UUID)).toURI());
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

        final File actualMainPart1 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, SECTION_1_UUID, PartType.MAIN).getFile();
        final File actualMainPart2 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, SECTION_2_UUID, PartType.MAIN).getFile();
        final File actualMainPart3 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, SECTION_3_UUID, PartType.MAIN).getFile();
        final File actualMainPart4 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, SECTION_4_UUID, PartType.MAIN).getFile();

        final File actualFootnotesPart1 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, SECTION_1_UUID, PartType.FOOTNOTE).getFile();
        final File actualFootnotesPart2 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, SECTION_2_UUID, PartType.FOOTNOTE).getFile();
        final File actualFootnotesPart3 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, SECTION_3_UUID, PartType.FOOTNOTE).getFile();
        final File actualFootnotesPart4 = partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, SECTION_4_UUID, PartType.FOOTNOTE).getFile();

        assertThat(expectedMainPart1, hasSameContentAs(actualMainPart1));
        assertThat(expectedMainPart2, hasSameContentAs(actualMainPart2));
        assertThat(expectedMainPart3, hasSameContentAs(actualMainPart3));
        assertThat(expectedMainPart4, hasSameContentAs(actualMainPart4));

        assertThat(expectedFootnotesPart1, hasSameContentAs(actualFootnotesPart1));
        assertThat(expectedFootnotesPart2, hasSameContentAs(actualFootnotesPart2));
        assertThat(expectedFootnotesPart3, hasSameContentAs(actualFootnotesPart3));
        assertThat(expectedFootnotesPart4, hasSameContentAs(actualFootnotesPart4));
    }

    private String getFileName(@NotNull final int pageNumber, @NotNull final PartType type, @NotNull final String docFamilyGuid)
    {
        return fileSystem.getPartFileName(BASE_FILE_NAME, pageNumber, type, docFamilyGuid);
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class SplitOriginalStepIntegrationTestConfiguration
    {
        @Bean(name = "splitOriginalTask")
        public SplitOriginalStep splitOriginalTask()
        {
            return new SplitOriginalStep();
        }
    }
}
