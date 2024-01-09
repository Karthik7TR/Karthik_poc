package com.thomsonreuters.uscl.ereader.xpp.transformation.footnotes.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CrossPageLinkedFootnotesStepIntegrationTestConfig.class)
@ActiveProfiles("IntegrationTests")
public class CrossPageLinkedFootnotesStepIntegrationTest {
    @Resource(name = "crossPageLinkedFootnotesTask")
    @InjectMocks
    private CrossPageLinkedFootnotesStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private static final String MATERIAL_NUMBER = "11111111";

    private File sourceMain;
    private File sourceFootnotes;
    private File expectedFootnotes;

    @Before
    public void setUp() throws URISyntaxException {
        sourceMain = new File(CrossPageLinkedFootnotesStepIntegrationTest.class.getResource("sample.DIVXML.main").toURI());
        sourceFootnotes = new File(CrossPageLinkedFootnotesStepIntegrationTest.class.getResource("sample.DIVXML.footnotes").toURI());
        expectedFootnotes = new File(CrossPageLinkedFootnotesStepIntegrationTest.class.getResource("expected.sample.DIVXML.footnotes").toURI());
    }

    @After
    public void clean() throws IOException {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldCreateOriginalFileWithSectionbreaks() throws Exception {
        //given
        final File sourceDir = fileSystem.getDirectory(step, XppFormatFileSystemDir.SECTIONBREAKS_DIR, MATERIAL_NUMBER);
        FileUtils.forceMkdir(sourceDir);
        FileUtils.copyFileToDirectory(sourceMain, sourceDir);
        FileUtils.copyFileToDirectory(sourceFootnotes, sourceDir);
        //when
        step.executeStep();
        //then
        final File actualOuput = fileSystem.getFile(step, XppFormatFileSystemDir.CROSS_PAGE_FOOTNOTES, MATERIAL_NUMBER, sourceFootnotes.getName());
        assertThat(actualOuput, hasSameContentAs(expectedFootnotes));
    }
}
