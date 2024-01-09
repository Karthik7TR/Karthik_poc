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
public final class DocToImageMappingStepIntegrationTest {
    @Resource(name = "createDocToImageMappingTask")
    private DocToImageMappingStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File sourceHtml1;
    private File sourceHtml2;
    private File expected;

    @Before
    public void setUp() throws URISyntaxException {
        sourceHtml1 = new File(DocToImageMappingStepIntegrationTest.class.getResource("sampleXpp_1.html").toURI());
        sourceHtml2 = new File(DocToImageMappingStepIntegrationTest.class.getResource("sampleXpp_2.html").toURI());
        expected =
            new File(DocToImageMappingStepIntegrationTest.class.getResource("expectedDocToImageIdMapFile.txt").toURI());
    }

    @Test
    public void shouldCreateMappingFile() throws Exception {
        //given
        final File externalLinksDirectory = fileSystem.getExternalLinksDirectory(step, "11111111");
        FileUtils.forceMkdir(externalLinksDirectory);
        FileUtils.copyFileToDirectory(sourceHtml1, externalLinksDirectory);
        FileUtils.copyFileToDirectory(sourceHtml2, externalLinksDirectory);
        //when
        step.executeStep();
        //then
        final File anchors = fileSystem.getDocToImageMapFile(step);
        assertThat(anchors, hasSameContentAs(expected));
    }
}
