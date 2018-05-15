package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

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
        testOriginalStructureTransformationStep(
            "original\\sampleXpp.DIVXML.xml",
            "original\\expected.main",
            "original\\expected.footnotes");
    }

    @Test
    public void shouldReturnOriginalXmlWithRomanPageNumbers() throws Exception {
        testOriginalStructureTransformationStep(
            "roman\\sampleXpp_Front_vol_1.DIVXML.xml",
            "roman\\expected-roman.main",
            "roman\\expected-roman.footnotes");
    }

    @Test
    public void shouldTransformColumns() throws Exception {
        testOriginalStructureTransformationStep(
            "columns\\sampleColumns.DIVXML.xml",
            "columns\\expectedColumns.main",
            "columns\\expectedColumns.footnotes");
    }

    @Test
    public void shouldProcessBlankPages() throws Exception {
        testOriginalStructureTransformationStep(
            "blank\\blank.DIVXML.xml",
            "blank\\blank.expected.main",
            "blank\\blank.expected.footnotes");
    }

    @Test
    public void shouldTransformImageOnFirstFrontmatterPage() throws Exception {
        testOriginalStructureTransformationStep(
            "image\\0-IMAGE_Front_vol_1.DIVXML.xml",
            "image\\image.expected.main",
            "image\\image.expected.footnotes");
    }

    @Test
    public void shouldTransformTable() throws Exception {
        testOriginalStructureTransformationStep(
            "table\\1-CACI_Front_Matter_MEMBERS.DIVXML.xml",
            "table\\table.expected.main",
            "table\\table.expected.footnotes");
    }

    @Test
    public void shouldTransformRutterIndex() throws Exception {
        testOriginalStructureTransformationStep(
            "index\\rutter\\sample.DIVXML.xml",
            "index\\rutter\\expected.main",
            "index\\rutter\\expected.footnotes");
    }

    @Test
    public void shouldProcessCiteQueries() throws Exception {
        //given
        final File expectedProcessedCiteQueriesDir = new File(
            OriginalStructureTransformationStepIntegrationTest.class.getResource("expected Processed Cite Queries")
                .toURI());
        final File xpp = new File(
            OriginalStructureTransformationStepIntegrationTest.class.getResource("original\\sampleXpp.DIVXML.xml").toURI());
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
        final File main = fileSystem.getOriginalFile(step, MATERIAL_NUMBER, getActualFileName(sampleFileName));
        final File footnotes = fileSystem.getFootnotesFile(step, MATERIAL_NUMBER, getActualFileName(sampleFileName));
        assertThat(FileUtils.readFileToString(main), equalTo(expectedMain));
        assertThat(FileUtils.readFileToString(footnotes), equalTo(expectedFootnotes));
    }

    private String getFileContent(final String fileName) throws URISyntaxException, IOException {
        final File file =
            new File(OriginalStructureTransformationStepIntegrationTest.class.getResource(fileName).toURI());
        return FileUtils.readFileToString(file)
            .replace(REF_PLACE_HOLDER, entitiesDtdFile.getAbsolutePath().replace("\\", "/"));
    }

    private String getActualFileName(final String fileName) {
        return Paths.get(fileName)
            .getFileName()
            .toString();
    }
}
