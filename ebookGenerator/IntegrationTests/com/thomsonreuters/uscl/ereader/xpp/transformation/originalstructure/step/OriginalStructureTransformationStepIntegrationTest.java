package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.DirectoryContentMatcher;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OriginalStructureTransformationStepIntegrationTestConfiguration.class)
@ActiveProfiles("IntegrationTests")
public final class OriginalStructureTransformationStepIntegrationTest {
    private static final String MATERIAL_NUMBER = "11111111";
    private static final String REF_PLACE_HOLDER = "${refPlaceHolder}";

    @Resource(name = "originalStructureTransformationTask")
    private OriginalStructureTransformationStep step;
    @Autowired
    private XppGatherFileSystem xppGatherFileSystem;
    @Autowired
    private XppFormatFileSystem fileSystem;
    @Value("${xpp.entities.dtd}")
    private File entitiesDtdFile;

    @Test
    public void shouldReturnOriginalXml() throws Exception {
        testOriginalStructureTransformationStep("sampleXpp.DIVXML.xml", "expected.main", "expected.footnotes");
    }

    @Test
    public void shouldReturnOriginalXmlWithRomanPageNumbers() throws Exception {
        testOriginalStructureTransformationStep(
            "sampleXpp_Front_vol_1.DIVXML.xml",
            "expected-roman.main",
            "expected-roman.footnotes");
    }

    @Test
    public void shouldTransformColumns() throws Exception {
        testOriginalStructureTransformationStep(
            "sampleColumns.DIVXML.xml",
            "expectedColumns.main",
            "expectedColumns.footnotes");
    }

    @Test
    public void shouldProcessCiteQueries() throws Exception {
        //given
        final File expectedProcessedCiteQueriesDir = new File(
            OriginalStructureTransformationStepIntegrationTest.class.getResource("expected Processed Cite Queries")
                .toURI());
        final File xpp = new File(
            OriginalStructureTransformationStepIntegrationTest.class.getResource("sampleXpp.DIVXML.xml").toURI());
        final File xppDir = xppGatherFileSystem.getXppBundleMaterialNumberDirectory(step, MATERIAL_NUMBER)
            .toPath()
            .resolve("bundleName")
            .resolve("XPP")
            .toFile();
        FileUtils.forceMkdir(xppDir);
        FileUtils.copyFileToDirectory(xpp, xppDir);
        //when
        step.executeStep();
        final File processedCiteQueriesDir = fileSystem.getCiteQueryProcessedDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(
            processedCiteQueriesDir,
            DirectoryContentMatcher.hasSameContentAs(expectedProcessedCiteQueriesDir, true));
    }

    @After
    public void cleanUpProcessedDir() throws IOException {
        final File xppDir =
            xppGatherFileSystem.getXppBundleMaterialNumberDirectory(step, MATERIAL_NUMBER).toPath().toFile();
        final File processedCiteQueriesDir = fileSystem.getCiteQueryProcessedDirectory(step, MATERIAL_NUMBER);
        FileUtils.forceDelete(xppDir);
        FileUtils.forceDelete(processedCiteQueriesDir);
    }

    private void testOriginalStructureTransformationStep(
        final String sampleFileName,
        final String expectedMainFileName,
        final String expectedFootnoteFileName) throws Exception {
        //given
        final String expectedMain = getFileContent(expectedMainFileName);
        final String expectedFootnotes = getFileContent(expectedFootnoteFileName);
        final File xpp =
            new File(OriginalStructureTransformationStepIntegrationTest.class.getResource(sampleFileName).toURI());
        final File xppDir = xppGatherFileSystem.getXppBundleMaterialNumberDirectory(step, MATERIAL_NUMBER)
            .toPath()
            .resolve("bundleName")
            .resolve("XPP")
            .toFile();
        FileUtils.forceMkdir(xppDir);
        FileUtils.copyFileToDirectory(xpp, xppDir);
        //when
        step.executeStep();
        //then
        final File main = fileSystem.getOriginalFile(step, MATERIAL_NUMBER, sampleFileName);
        final File footnotes = fileSystem.getFootnotesFile(step, MATERIAL_NUMBER, sampleFileName);
        assertThat(FileUtils.readFileToString(main), equalTo(expectedMain));
        assertThat(FileUtils.readFileToString(footnotes), equalTo(expectedFootnotes));
    }

    private String getFileContent(final String fileName) throws URISyntaxException, IOException {
        final File file =
            new File(OriginalStructureTransformationStepIntegrationTest.class.getResource(fileName).toURI());
        return FileUtils.readFileToString(file)
            .replace(REF_PLACE_HOLDER, entitiesDtdFile.getAbsolutePath().replace("\\", "/"));
    }
}
