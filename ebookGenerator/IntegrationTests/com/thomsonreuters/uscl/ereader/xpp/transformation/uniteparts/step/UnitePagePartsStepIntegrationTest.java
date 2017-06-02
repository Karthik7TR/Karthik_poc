package com.thomsonreuters.uscl.ereader.xpp.transformation.uniteparts.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

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
public final class UnitePagePartsStepIntegrationTest
{
    private static final String MATERIAL_NUMBER = "11111111";
    private static final String MATERIAL_NUMBER_2 = "22222222";

    @Resource(name = "unitePagesPartsTask")
    private UnitePagePartsStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File mainPart;
    private File footnotesPart;
    private File mainPartTwo;
    private File footnotesPartTwo;
    private File expected;
    private File expectedTwo;

    @Before
    public void setUp() throws URISyntaxException, IOException
    {
        fileSystem.getOriginalBundleDirectory(step, MATERIAL_NUMBER).mkdirs();
        fileSystem.getOriginalBundleDirectory(step, MATERIAL_NUMBER_2).mkdirs();
        fileSystem.getOriginalFile(step, MATERIAL_NUMBER, "sample").createNewFile();
        fileSystem.getOriginalFile(step, MATERIAL_NUMBER_2, "sampleTwo").createNewFile();
        mainPart = new File(UnitePagePartsStepIntegrationTest.class.getResource("sample_1_main.part").toURI());
        footnotesPart = new File(UnitePagePartsStepIntegrationTest.class.getResource("sample_1_footnotes.part").toURI());
        mainPartTwo = new File(UnitePagePartsStepIntegrationTest.class.getResource("sampleTwo_1_main.part").toURI());
        footnotesPartTwo = new File(UnitePagePartsStepIntegrationTest.class.getResource("sampleTwo_1_footnotes.part").toURI());
        expected = new File(UnitePagePartsStepIntegrationTest.class.getResource("sample_1.page").toURI());
        expectedTwo = new File(UnitePagePartsStepIntegrationTest.class.getResource("sampleTwo_1.page").toURI());
    }

    @Test
    public void shouldUnitePageParts() throws Exception
    {
        //given
        final File originalPartsDirectory = mkdir(fileSystem.getOriginalPartsDirectory(step, MATERIAL_NUMBER));
        final File originalPartsDirectory2 = mkdir(fileSystem.getOriginalPartsDirectory(step, MATERIAL_NUMBER_2));
        FileUtils.copyFileToDirectory(mainPart, originalPartsDirectory);
        FileUtils.copyFileToDirectory(footnotesPart, originalPartsDirectory);
        FileUtils.copyFileToDirectory(mainPartTwo, originalPartsDirectory2);
        FileUtils.copyFileToDirectory(footnotesPartTwo, originalPartsDirectory2);
        //when
        step.executeStep();
        //then
        final File actual = fileSystem.getOriginalPageFile(step, MATERIAL_NUMBER, "sample", 1);
        assertThat(expected, hasSameContentAs(actual));
        final File actualTwo = fileSystem.getOriginalPageFile(step, MATERIAL_NUMBER_2, "sampleTwo", 1);
        assertThat(expectedTwo, hasSameContentAs(actualTwo));
    }
}
