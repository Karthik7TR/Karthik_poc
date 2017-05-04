package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

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

    private File originalXml;
    private File originalXmlTwo;

    private File pageXml11;
    private File pageXml12;
    private File pageXml2;

    private File tocMapExpectedSingle;
    private File tocExpectedSingle;
    private File tocMapExpectedMultiple;
    private File tocExpectedMultiple;

    @Before
    public void setUp() throws URISyntaxException
    {
        originalXml = new File(ExtractTocStepIntegrationTest.class.getResource("originalXpp.main").toURI());
        originalXmlTwo = new File(ExtractTocStepIntegrationTest.class.getResource("originalXppTwo.main").toURI());

        pageXml11 = new File(ExtractTocStepIntegrationTest.class.getResource("sampleXpp_1.page").toURI());
        pageXml12 = new File(ExtractTocStepIntegrationTest.class.getResource("sampleXpp_15.page").toURI());
        pageXml2 = new File(ExtractTocStepIntegrationTest.class.getResource("sampleXppTwo_1.page").toURI());

        tocMapExpectedSingle = new File(ExtractTocStepIntegrationTest.class.getResource("tocItemToDocumentIdMapExpectedSingle.xml").toURI());
        tocExpectedSingle = new File(ExtractTocStepIntegrationTest.class.getResource("tocExpectedSingle.xml").toURI());

        tocMapExpectedMultiple = new File(ExtractTocStepIntegrationTest.class.getResource("tocItemToDocumentIdMapExpectedMultiple.xml").toURI());
        tocExpectedMultiple = new File(ExtractTocStepIntegrationTest.class.getResource("tocExpectedMultiple.xml").toURI());
    }

    @After
    public void clean() throws IOException
    {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldCreateTocFileBasedOnSingleOriginalFile() throws Exception
    {
        //given
        final File originalPagesSourceDir = fileSystem.getOriginalPagesDirectory(step);
        FileUtils.forceMkdir(originalPagesSourceDir);
        FileUtils.copyFileToDirectory(pageXml11, originalPagesSourceDir);
        FileUtils.copyFileToDirectory(pageXml12, originalPagesSourceDir);

        final File originalMainSourceDir = fileSystem.getOriginalDirectory(step);
        FileUtils.forceMkdir(originalMainSourceDir);
        FileUtils.copyFileToDirectory(originalXml, originalMainSourceDir);

        //when
        step.executeStep();
        //then
        final File mapActual = fileSystem.getTocItemToDocumentIdMapFile(step);
        assertThat(mapActual, hasSameContentAs(tocMapExpectedSingle));

        final File tocActual = fileSystem.getTocFile(step);
        assertThat(tocActual, hasSameContentAs(tocExpectedSingle));
    }

    @Test
    public void shouldCreateTocFileBasedOnMultipleOriginalFiles() throws Exception
    {
        //given
        final File originalPagesSourceDir = fileSystem.getOriginalPagesDirectory(step);
        FileUtils.forceMkdir(originalPagesSourceDir);
        FileUtils.copyFileToDirectory(pageXml11, originalPagesSourceDir);
        FileUtils.copyFileToDirectory(pageXml12, originalPagesSourceDir);
        FileUtils.copyFileToDirectory(pageXml2, originalPagesSourceDir);

        final File originalMainSourceDir = fileSystem.getOriginalDirectory(step);
        FileUtils.forceMkdir(originalMainSourceDir);
        FileUtils.copyFileToDirectory(originalXml, originalMainSourceDir);
        FileUtils.copyFileToDirectory(originalXmlTwo, originalMainSourceDir);

        //when
        step.executeStep();
        //then
        final File mapActual = fileSystem.getTocItemToDocumentIdMapFile(step);
        assertThat(mapActual, hasSameContentAs(tocMapExpectedMultiple));

        final File tocActual = fileSystem.getTocFile(step);
        assertThat(tocActual, hasSameContentAs(tocExpectedMultiple));
    }

}
