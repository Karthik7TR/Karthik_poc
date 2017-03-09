package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
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
public final class SplitOriginalStepIntegartionTest
{
    @Resource(name = "splitOriginalTask")
    private SplitOriginalStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File original;
    private File part1;
    private File part2;

    @Before
    public void setUp() throws URISyntaxException
    {
        original = new File(SplitOriginalStepIntegartionTest.class.getResource("sample.original").toURI());
        part1 = new File(SplitOriginalStepIntegartionTest.class.getResource("sample_1.part").toURI());
        part2 = new File(SplitOriginalStepIntegartionTest.class.getResource("sample_2.part").toURI());
    }

    @Test
    public void shouldSplitOriginalXml() throws Exception
    {
        //given
        final File originalDirectory = fileSystem.getOriginalDirectory(step);
        FileUtils.forceMkdir(originalDirectory);
        FileUtils.copyFileToDirectory(original, originalDirectory);
        //when
        step.executeStep();
        //then
        final File actualPart1 = fileSystem.getOriginalPartsFile(step, "sample", 1);
        final File actualPart2 = fileSystem.getOriginalPartsFile(step, "sample", 2);
        assertThat(actualPart1, hasSameContentAs(part1));
        assertThat(actualPart2, hasSameContentAs(part2));
    }
}
