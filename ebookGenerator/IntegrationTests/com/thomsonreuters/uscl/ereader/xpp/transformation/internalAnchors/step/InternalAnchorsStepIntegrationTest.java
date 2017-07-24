package com.thomsonreuters.uscl.ereader.xpp.transformation.internalAnchors.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InternalAnchorsStepIntegrationTestConfiguration.class)
@ActiveProfiles("IntegrationTests")
public final class InternalAnchorsStepIntegrationTest
{
    private static final String MATERIAL_NUMBER = "111111";

    @Resource(name = "internalAnchorsTask")
    private InternalAnchorsStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File originalHtml1;
    private File originalHtml2;
    private File expected;

    @Before
    public void setUp() throws URISyntaxException, Exception
    {
        final File originalPagesDirectory = fileSystem.getOriginalPagesDirectory(step, MATERIAL_NUMBER);
        FileUtils.forceMkdir(originalPagesDirectory);

        originalHtml1 = new File(InternalAnchorsStepIntegrationTest.class.getResource("sampleXpp_1.page").toURI());
        originalHtml2 = new File(InternalAnchorsStepIntegrationTest.class.getResource("sampleXpp_2.page").toURI());
        FileUtils.copyFileToDirectory(originalHtml1, originalPagesDirectory);
        FileUtils.copyFileToDirectory(originalHtml2, originalPagesDirectory);

        expected = new File(InternalAnchorsStepIntegrationTest.class.getResource("expectedAnchorToDocumentIdMapFile.xml").toURI());
    }

    @After
    public void onTestComplete() throws IOException
    {
        FileUtils.forceDelete(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldCreateMappingFile() throws Exception
    {
        //given
        //when
        step.executeStep();
        //then
        final File anchors = fileSystem.getAnchorToDocumentIdMapFile(step);
        assertThat(anchors, hasSameContentAs(expected));
    }
}
