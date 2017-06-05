package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class ExtractTocStepIntegrationTest
{
    private static final String VOL_ONE_MATERIAL_NUMBER = "1111111";
    private static final String VOL_TWO_MATERIAL_NUMBER = "2222222";

    @Resource(name = "extractTocTask")
    @InjectMocks
    private ExtractTocStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File bundleMainContentOriginalFile;
    private File bundleMainContentOriginalAdditionalFile;
    private File expectedMainContentTocFile;
    private File expectedMainContentAdditionalTocFile;
    private File expectedTocFile;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    //TODO: fields below should be removed when books will generate from bundles in XPP pathway.
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
    public void setUp() throws Exception
    {
        org.mockito.MockitoAnnotations.initMocks(this);
        when(chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext()
            .get(JobParameterKey.XPP_BUNDLES)
        ).thenReturn(getBundlesList());

        bundleMainContentOriginalFile = new File(
            ExtractTocStepIntegrationTest.class.getResource("mainContent1.DIVXML.main").toURI());
        bundleMainContentOriginalAdditionalFile = new File(
            ExtractTocStepIntegrationTest.class.getResource("mainContent2.DIVXML.main").toURI());
        expectedMainContentTocFile = new File(
            ExtractTocStepIntegrationTest.class.getResource("expectedMainContent_1_TocFile.xml").toURI());
        expectedMainContentAdditionalTocFile = new File(
            ExtractTocStepIntegrationTest.class.getResource("expectedMainContent_2_TocFile.xml").toURI());
        expectedTocFile = new File(
            ExtractTocStepIntegrationTest.class.getResource("expectedToc.xml").toURI());

        final File bundleVolOneOriginalFilesDir = fileSystem.getOriginalBundleDirectory(step, VOL_ONE_MATERIAL_NUMBER);
        FileUtils.forceMkdir(bundleVolOneOriginalFilesDir);
        FileUtils.copyFileToDirectory(bundleMainContentOriginalFile, bundleVolOneOriginalFilesDir);

        final File bundleVolTwoOriginalFilesDir = fileSystem.getOriginalBundleDirectory(step, VOL_TWO_MATERIAL_NUMBER);
        FileUtils.forceMkdir(bundleVolTwoOriginalFilesDir);
        FileUtils.copyFileToDirectory(bundleMainContentOriginalAdditionalFile, bundleVolTwoOriginalFilesDir);
    }

    private List<XppBundle> getBundlesList()
    {
        final XppBundle volumeOneBundle = new XppBundle();
        volumeOneBundle.setMaterialNumber(VOL_ONE_MATERIAL_NUMBER);
        volumeOneBundle.setOrderedFileList(Arrays.asList("mainContent1.DIVXML.xml"));

        final XppBundle volumeTwoBundle = new XppBundle();
        volumeTwoBundle.setMaterialNumber(VOL_TWO_MATERIAL_NUMBER);
        volumeTwoBundle.setOrderedFileList(Arrays.asList("mainContent2.DIVXML.xml"));

        return Arrays.asList(volumeOneBundle, volumeTwoBundle);
    }

    @After
    public void clean() throws IOException
    {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldCreateTocFileBasedBundleMainContentOriginalFile() throws Exception
    {
        step.executeStep();
        System.out.println(fileSystem.getTocFile(step));
        assertThat(expectedMainContentTocFile, hasSameContentAs(fileSystem.getBundlePartTocFile(
            "mainContent1.DIVXML.xml", VOL_ONE_MATERIAL_NUMBER, step)));
        assertThat(expectedMainContentAdditionalTocFile, hasSameContentAs(fileSystem.getBundlePartTocFile(
            "mainContent2.DIVXML.xml", VOL_TWO_MATERIAL_NUMBER, step)));
        assertThat(expectedTocFile, hasSameContentAs(fileSystem.getTocFile(step)));
    }

    //TODO: code below should be removed when books will generate from bundles in XPP pathway.
    //TODO: test should be removed when books will generate from bundles in XPP pathway.
    @Test
    public void shouldCreateTocFileBasedOnSingleOriginalFile() throws Exception
    {
        initMocksFilesAndDirectories();
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

    //TODO: test should be removed when books will generate from bundles in XPP pathway.
    @Test
    public void shouldCreateTocFileBasedOnMultipleOriginalFiles() throws Exception
    {
        initMocksFilesAndDirectories();
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

    private void initMocksFilesAndDirectories() throws Exception
    {
        org.mockito.MockitoAnnotations.initMocks(this);
        when(chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext()
            .get(JobParameterKey.XPP_BUNDLES)
        ).thenReturn(Collections.emptyList());
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
}
