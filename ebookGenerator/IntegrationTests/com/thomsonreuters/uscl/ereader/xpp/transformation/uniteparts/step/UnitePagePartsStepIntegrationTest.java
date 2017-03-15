package com.thomsonreuters.uscl.ereader.xpp.transformation.uniteparts.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
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
    @Resource(name = "unitePagesPartsTask")
    private UnitePagePartsStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File mainPart;
    private File footnotesPart;
    private File expected;

    @Before
    public void setUp() throws URISyntaxException, IOException
    {
        fileSystem.getOriginalDirectory(step).mkdirs();
        fileSystem.getOriginalFile(step, "sample").createNewFile();
        mainPart = new File(UnitePagePartsStepIntegrationTest.class.getResource("sample_1_main.part").toURI());
        footnotesPart = new File(UnitePagePartsStepIntegrationTest.class.getResource("sample_1_footnotes.part").toURI());
        expected = new File(UnitePagePartsStepIntegrationTest.class.getResource("sample_1.page").toURI());
    }

    @Test
    public void shouldUnitePageParts() throws Exception
    {
        //given
        final File originalPartsDirectory = fileSystem.getOriginalPartsDirectory(step);
        FileUtils.forceMkdir(originalPartsDirectory);
        FileUtils.copyFileToDirectory(mainPart, originalPartsDirectory);
        FileUtils.copyFileToDirectory(footnotesPart, originalPartsDirectory);
        //when
        step.executeStep();
        //then
        final File actual = fileSystem.getOriginalPageFile(step, "sample", 1);
        assertThat(expected, hasSameContentAs(actual));
    }
}
