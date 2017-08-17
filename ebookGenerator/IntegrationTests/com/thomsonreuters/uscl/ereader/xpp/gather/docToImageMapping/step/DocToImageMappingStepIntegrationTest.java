package com.thomsonreuters.uscl.ereader.xpp.gather.docToImageMapping.step;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DocToImageMappingStepIntegrationTestConfiguration.class)
@ActiveProfiles("IntegrationTests")
public final class DocToImageMappingStepIntegrationTest
{
    @Resource(name = "createDocToImageMappingTask")
    private DocToImageMappingStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File originalHtml1;
    private File originalHtml2;
    private File expected;

    @Before
    public void setUp() throws URISyntaxException
    {
        originalHtml1 = new File(DocToImageMappingStepIntegrationTest.class.getResource("sampleXpp_1.html").toURI());
        originalHtml2 = new File(DocToImageMappingStepIntegrationTest.class.getResource("sampleXpp_2.html").toURI());
        expected = new File(DocToImageMappingStepIntegrationTest.class.getResource("expectedDocToImageIdMapFile.txt").toURI());
    }

    @Test
    public void shouldCreateMappingFile() throws Exception
    {
        //given
        final File originalPartsDirectory = fileSystem.getHtmlPagesDirectory(step, "11111111");
        FileUtils.forceMkdir(originalPartsDirectory);
        FileUtils.copyFileToDirectory(originalHtml1, originalPartsDirectory);
        FileUtils.copyFileToDirectory(originalHtml2, originalPartsDirectory);
        //when
        step.executeStep();
        //then
        final File anchors = fileSystem.getDocToImageMapFile(step);
        assertThat(anchors, hasSameContentAs(expected));
    }
}
