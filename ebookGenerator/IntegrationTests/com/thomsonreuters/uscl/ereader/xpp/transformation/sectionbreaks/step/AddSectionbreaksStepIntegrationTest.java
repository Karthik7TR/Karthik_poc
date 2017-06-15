package com.thomsonreuters.uscl.ereader.xpp.transformation.sectionbreaks.step;

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
public class AddSectionbreaksStepIntegrationTest
{
    private static final String MATERIAL_NUMBER = "11111111";

    @Resource(name = "addSectionbreaksTask")
    private AddSectionbreaksStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File originalXml;
    private File originalXmlTwo;

    private File expectedOuput1;
    private File expectedOuput2;

    @Before
    public void setUp() throws URISyntaxException
    {
        originalXml = new File(AddSectionbreaksStepIntegrationTest.class.getResource("originalXpp.main").toURI());
        originalXmlTwo = new File(AddSectionbreaksStepIntegrationTest.class.getResource("originalXppTwo.main").toURI());

        expectedOuput1 = new File(AddSectionbreaksStepIntegrationTest.class.getResource("expectedOutput1.main").toURI());
        expectedOuput2 = new File(AddSectionbreaksStepIntegrationTest.class.getResource("expectedOutput2.main").toURI());
    }

    @After
    public void clean() throws IOException
    {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldCreateOriginalFileWithSectionbreaks() throws Exception
    {
        //given
        final File originalMainSourceDir = fileSystem.getOriginalDirectory(step, MATERIAL_NUMBER);
        FileUtils.forceMkdir(originalMainSourceDir);
        FileUtils.copyFileToDirectory(originalXml, originalMainSourceDir);
        FileUtils.copyFileToDirectory(originalXmlTwo, originalMainSourceDir);

        //when
        step.executeStep();
        //then
        final File actualOuput1 = fileSystem.getSectionbreaksFile(step, MATERIAL_NUMBER, originalXml.getName());
        assertThat(actualOuput1, hasSameContentAs(expectedOuput1));

        final File actualOuput2 = fileSystem.getSectionbreaksFile(step, MATERIAL_NUMBER, originalXmlTwo.getName());
        assertThat(actualOuput2, hasSameContentAs(expectedOuput2));
    }
}
