package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

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
public class ExtractTocStepIntegrationTest
{
    @Resource(name = "extractTocTask")
    private ExtractTocStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File pageXml1;
    private File pageXml2;

    private File originalXml;
    private File tocMapExpected;
    private File tocExpected;

    @Before
    public void setUp() throws URISyntaxException
    {
        pageXml1 = new File(ExtractTocStepIntegrationTest.class.getResource("sampleXpp_1.page").toURI());
        pageXml2 = new File(ExtractTocStepIntegrationTest.class.getResource("sampleXpp_15.page").toURI());

        originalXml = new File(ExtractTocStepIntegrationTest.class.getResource("originalXpp.main").toURI());
        tocMapExpected = new File(ExtractTocStepIntegrationTest.class.getResource("tocItemToDocumentIdMap.xml").toURI());
        tocExpected = new File(ExtractTocStepIntegrationTest.class.getResource("tocExpected.xml").toURI());
    }

    @Test
    public void shouldCreateTocFile() throws Exception
    {
        //given
        final File originalPagesSourceDir = fileSystem.getOriginalPagesDirectory(step);
        FileUtils.forceMkdir(originalPagesSourceDir);
        FileUtils.copyFileToDirectory(pageXml1, originalPagesSourceDir);
        FileUtils.copyFileToDirectory(pageXml2, originalPagesSourceDir);

        final File originalMainSourceDir = fileSystem.getOriginalDirectory(step);
        FileUtils.forceMkdir(originalMainSourceDir);
        FileUtils.copyFileToDirectory(originalXml, originalMainSourceDir);

        //when
        step.executeStep();
        //then
        final File mapActual = fileSystem.getTocItemToDocumentIdMapFile(step);
        assertThat(mapActual, hasSameContentAs(tocMapExpected));

        final File tocActual = fileSystem.getTocFile(step);
        assertThat(tocActual, hasSameContentAs(tocExpected));
    }

}
