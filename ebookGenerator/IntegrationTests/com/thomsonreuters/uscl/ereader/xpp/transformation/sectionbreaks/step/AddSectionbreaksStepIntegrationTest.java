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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AddSectionbreaksStepIntegrationTestConfiguration.class)
@ActiveProfiles("IntegrationTests")
public final class AddSectionbreaksStepIntegrationTest {
    private static final String MATERIAL_NUMBER = "11111111";

    @Resource(name = "addSectionbreaksTask")
    private AddSectionbreaksStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File frontXml;
    private File frontXmlFootnotes;
    private File expectedFrontXml;
    private File expectedFrontXmlFootnotes;

    private File mainXmlOne;
    private File mainXmlOneFootnotes;
    private File expectedMainXmlOne;
    private File expectedMainXmlOneFootnotes;

    private File mainXmlTwo;
    private File mainXmlTwoFootnotes;
    private File expectedMainXmlTwo;
    private File expectedMainXmlTwoFootnotes;

    private File mainXmlThree;
    private File mainXmlThreeFootnotes;
    private File expectedMainXmlThree;

    private File copyColumnsMain;
    private File copyColumnsFootnotes;
    private File expectedCopyColumnsMain;
    private File expectedCopyColumnsFootnotes;

    @Before
    public void setUp() throws URISyntaxException {
        frontXml =
            new File(AddSectionbreaksStepIntegrationTest.class.getResource("0-CHAL_Front_vol_1.DIVXML.main").toURI());
        frontXmlFootnotes = new File(
            AddSectionbreaksStepIntegrationTest.class.getResource("0-CHAL_Front_vol_1.DIVXML.footnotes").toURI());
        expectedFrontXml = new File(
            AddSectionbreaksStepIntegrationTest.class.getResource("expected-0-CHAL_Front_vol_1.DIVXML.main").toURI());
        expectedFrontXmlFootnotes = new File(
            AddSectionbreaksStepIntegrationTest.class.getResource("expected-0-CHAL_Front_vol_1.DIVXML.footnotes")
                .toURI());

        mainXmlOne = new File(AddSectionbreaksStepIntegrationTest.class.getResource("1-CHAL_7.DIVXML.main").toURI());
        mainXmlOneFootnotes =
            new File(AddSectionbreaksStepIntegrationTest.class.getResource("1-CHAL_7.DIVXML.footnotes").toURI());
        expectedMainXmlOne =
            new File(AddSectionbreaksStepIntegrationTest.class.getResource("expected-1-CHAL_7.DIVXML.main").toURI());
        expectedMainXmlOneFootnotes = new File(
            AddSectionbreaksStepIntegrationTest.class.getResource("expected-1-CHAL_7.DIVXML.footnotes").toURI());

        mainXmlTwo =
            new File(AddSectionbreaksStepIntegrationTest.class.getResource("1-CHAL_APX_21.DIVXML.main").toURI());
        mainXmlTwoFootnotes =
            new File(AddSectionbreaksStepIntegrationTest.class.getResource("1-CHAL_APX_21.DIVXML.footnotes").toURI());
        expectedMainXmlTwo = new File(
            AddSectionbreaksStepIntegrationTest.class.getResource("expected-1-CHAL_APX_21.DIVXML.main").toURI());
        expectedMainXmlTwoFootnotes = new File(
            AddSectionbreaksStepIntegrationTest.class.getResource("expected-1-CHAL_APX_21.DIVXML.footnotes").toURI());

        mainXmlThree =
            new File(AddSectionbreaksStepIntegrationTest.class.getResource("1-LAPRACEVID.DIVXML.main").toURI());
        mainXmlThreeFootnotes =
            new File(AddSectionbreaksStepIntegrationTest.class.getResource("1-LAPRACEVID.DIVXML.footnotes").toURI());
        expectedMainXmlThree = new File(
            AddSectionbreaksStepIntegrationTest.class.getResource("expected-1-LAPRACEVID.DIVXML.main").toURI());

        copyColumnsMain =
            new File(AddSectionbreaksStepIntegrationTest.class.getResource("4-COLUMNS_1.DIVXML.main").toURI());
        copyColumnsFootnotes =
            new File(AddSectionbreaksStepIntegrationTest.class.getResource("4-COLUMNS_1.DIVXML.footnotes").toURI());
        expectedCopyColumnsMain = new File(
            AddSectionbreaksStepIntegrationTest.class.getResource("expected-4-COLUMNS_1.DIVXML.main").toURI());
        expectedCopyColumnsFootnotes = new File(
            AddSectionbreaksStepIntegrationTest.class.getResource("expected-4-COLUMNS_1.DIVXML.footnotes").toURI());
    }

    @After
    public void clean() throws IOException {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldCreateOriginalFileWithSectionbreaks() throws Exception {
        //given
        final File originalMainSourceDir = fileSystem.getStructureWithMetadataBundleDirectory(step, MATERIAL_NUMBER);
        FileUtils.forceMkdir(originalMainSourceDir);
        FileUtils.copyFileToDirectory(frontXml, originalMainSourceDir);
        FileUtils.copyFileToDirectory(frontXmlFootnotes, originalMainSourceDir);
        FileUtils.copyFileToDirectory(mainXmlOne, originalMainSourceDir);
        FileUtils.copyFileToDirectory(mainXmlOneFootnotes, originalMainSourceDir);
        FileUtils.copyFileToDirectory(mainXmlTwo, originalMainSourceDir);
        FileUtils.copyFileToDirectory(mainXmlTwoFootnotes, originalMainSourceDir);
        FileUtils.copyFileToDirectory(mainXmlThree, originalMainSourceDir);
        FileUtils.copyFileToDirectory(mainXmlThreeFootnotes, originalMainSourceDir);
        FileUtils.copyFileToDirectory(copyColumnsMain, originalMainSourceDir);
        FileUtils.copyFileToDirectory(copyColumnsFootnotes, originalMainSourceDir);

        //when
        step.executeStep();
        //then
        assertOutputContent(frontXml, expectedFrontXml);
        assertOutputContent(frontXmlFootnotes, expectedFrontXmlFootnotes);
        assertOutputContent(mainXmlOne, expectedMainXmlOne);
        assertOutputContent(mainXmlOneFootnotes, expectedMainXmlOneFootnotes);
        assertOutputContent(mainXmlTwo, expectedMainXmlTwo);
        assertOutputContent(mainXmlTwoFootnotes, expectedMainXmlTwoFootnotes);
        assertOutputContent(mainXmlThree, expectedMainXmlThree);
        assertOutputContent(copyColumnsMain, expectedCopyColumnsMain);
        assertOutputContent(copyColumnsFootnotes, expectedCopyColumnsFootnotes);
    }

    private void assertOutputContent(final File original, final File expected) {
        final File actualOuput = fileSystem.getSectionbreaksFile(step, MATERIAL_NUMBER, original.getName());
        assertThat(actualOuput, hasSameContentAs(expected));
    }
}
