package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

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
public final class OriginalStructureTransformationStepIntegrationTest
{
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
    public void shouldReturnOriginalXml() throws Exception
    {
        testOriginalStructureTransformationStep(
            "sampleXpp.DIVXML.xml",
            "expected.main",
            "expected.footnotes",
            "expected Processed Cite Queries");
    }

    @Test
    public void shouldReturnOriginalXmlWithRomanPageNumbers() throws Exception
    {
        testOriginalStructureTransformationStep(
            "sampleXpp_Front_vol_1.DIVXML.xml",
            "expected-roman.main",
            "expected-roman.footnotes",
            "expected Processed Cite Queries roman");
    }

    @After
    public void cleanUpProcessedDir() throws IOException
    {
        final File xppDir = xppGatherFileSystem.getXppBundleMaterialNumberDirectory(step, MATERIAL_NUMBER)
            .toPath()
            .toFile();
        final File processedCiteQueriesDir = fileSystem.getCiteQueryProcessedDirectory(step, MATERIAL_NUMBER);
        FileUtils.forceDelete(xppDir);
        FileUtils.forceDelete(processedCiteQueriesDir);
    }

    private void testOriginalStructureTransformationStep(
        final String sampleFileName,
        final String expectedMainFileName,
        final String expectedFootnoteFileName,
        final String processedCiteQueriesDirName) throws Exception
    {
        //given
        final String expectedMain = FileUtils
            .readFileToString(
                new File(
                    OriginalStructureTransformationStepIntegrationTest.class.getResource(expectedMainFileName).toURI()))
            .replace(REF_PLACE_HOLDER, entitiesDtdFile.getAbsolutePath().replace("\\", "/"));
        final File expectedFootnotes = new File(
            OriginalStructureTransformationStepIntegrationTest.class.getResource(expectedFootnoteFileName).toURI());
        final File expectedProcessedCiteQueriesDir = new File(
            OriginalStructureTransformationStepIntegrationTest.class.getResource(processedCiteQueriesDirName).toURI());
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
        final File processedCiteQueriesDir = fileSystem.getCiteQueryProcessedDirectory(step, MATERIAL_NUMBER);
        assertThat(FileUtils.readFileToString(main), equalTo(expectedMain));
        assertThat(footnotes, hasSameContentAs(expectedFootnotes));
        assertThat(
            processedCiteQueriesDir,
            DirectoryContentMatcher.hasSameContentAs(expectedProcessedCiteQueriesDir, true));
    }
}
