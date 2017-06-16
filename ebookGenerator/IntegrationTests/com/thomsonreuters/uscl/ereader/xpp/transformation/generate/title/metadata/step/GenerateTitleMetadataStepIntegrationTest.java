package com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import com.thomsonreuters.uscl.ereader.proview.Keyword;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
public final class GenerateTitleMetadataStepIntegrationTest
{
    private static final String CURRENT_DATE_PLACEHOLDER = "${currentDate}";
    private static final String MATERIAL_NUMBER = "123456";
    private static final String ADDITIONAL_MATERIAL_NUMBER = "123457";

    @Resource(name = "generateTitleMetadataTask")
    @InjectMocks
    private BookStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;
    @Resource(name = "assembleFileSystemTemp")
    private AssembleFileSystem assembleFileSystem;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition bookDefinition;

    private File tocFile;
    private File coverArtFile;
    private File documentCssFile;
    private File ebookGeneratorCssFile;
    private String expectedTitleFileContent;
    private String expectedTitleMetadataFileContent;

    @Before
    public void onSetUp() throws URISyntaxException, IOException
    {
        org.mockito.MockitoAnnotations.initMocks(this);
        initMocksBehavior();
        initBookDefinitionMockBehavior();
        initFiles();
    }

    private void initMocksBehavior()
    {
        when(chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.EBOOK_DEFINITON))
        .thenReturn(bookDefinition);

        when(chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobParameters()
                .getString(JobParameterKey.BOOK_VERSION_SUBMITTED))
        .thenReturn("5.0");

        when(chunkContext
            .getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext()
            .get(JobParameterKey.XPP_BUNDLES))
        .thenReturn(getXppBundles());
    }

    private List<XppBundle> getXppBundles()
    {
        final XppBundle firstBundle = new XppBundle();
        firstBundle.setMaterialNumber(MATERIAL_NUMBER);
        firstBundle.setOrderedFileList(Arrays.asList("Useless_test_file.DIVXML.xml"));

        final XppBundle secondBundle = new XppBundle();
        secondBundle.setMaterialNumber(ADDITIONAL_MATERIAL_NUMBER);
        secondBundle.setOrderedFileList(Arrays.asList("Useless_test_file.DIVXML.xml"));

        return Arrays.asList(firstBundle, secondBundle);
    }

    private void initBookDefinitionMockBehavior()
    {
        when(bookDefinition.getProviewFeatures()).thenReturn(getFeatures());
        when(bookDefinition.getMaterialId()).thenReturn("someMaterialId");
        when(bookDefinition.getProviewDisplayName()).thenReturn("Integration test book");
        when(bookDefinition.getAuthors()).thenReturn(Collections.EMPTY_LIST);
        when(bookDefinition.getKeyWords()).thenReturn(getKeywords());
        when(bookDefinition.getCopyright()).thenReturn("©");
        when(bookDefinition.getIsbnNormalized()).thenReturn("9780314943910");
        when(bookDefinition.getFullyQualifiedTitleId()).thenReturn("uscl/gen/title_metadata_integration_test");
        when(bookDefinition.getTitleId()).thenReturn("title_metadata_integration_test");
    }

    private List<Feature> getFeatures()
    {
        final Feature printFeature = new Feature("Print");
        final Feature autoUpdateFeature = new Feature("AutoUpdate");
        final Feature copyFeature = new Feature("Copy");
        final Feature wwwWestlawFeature = new Feature("OnePassSSO", "www.westlaw.com");
        final Feature nextWestlawFeature = new Feature("OnePassSSO", "next.westlaw.com");
        final Feature pageNosFeature = new Feature("PageNos");
        return Arrays.asList(printFeature, autoUpdateFeature, copyFeature, wwwWestlawFeature, nextWestlawFeature, pageNosFeature);
    }

    private List<Keyword> getKeywords()
    {
        final Keyword publisherKeyword = new Keyword("publisher", "Thomson Reuters Westlaw");
        final Keyword jurisdictionKeyword = new Keyword("jurisdiction", "Alabama");
        return Arrays.asList(publisherKeyword, jurisdictionKeyword);
    }

    private void initFiles() throws URISyntaxException, IOException
    {
        tocFile = loadFileFromResources("toc.xml");
        coverArtFile = loadFileFromResources("coverArt.PNG");
        documentCssFile = loadFileFromResources("document.css");
        ebookGeneratorCssFile = loadFileFromResources("ebook_generator.css");

        expectedTitleFileContent = FileUtils.readFileToString(
            new File(GenerateTitleMetadataStepIntegrationTest.class.getResource("expectedTitle.xml").toURI()))
            .replace(CURRENT_DATE_PLACEHOLDER, DateFormatUtils.format(new Date(), "yyyyMMdd"));
        expectedTitleMetadataFileContent = FileUtils.readFileToString(
            new File(GenerateTitleMetadataStepIntegrationTest.class.getResource("expectedTitleMetadata.xml").toURI()))
            .replace(CURRENT_DATE_PLACEHOLDER, DateFormatUtils.format(new Date(), "yyyyMMdd"));

        final File tocDirectory = fileSystem.getTocDirectory(step);
        FileUtils.forceMkdir(tocDirectory);
        FileUtils.copyFileToDirectory(tocFile, tocDirectory);

        final File assetsDirectory = assembleFileSystem.getAssetsDirectory(step);
        FileUtils.forceMkdir(assetsDirectory);
        FileUtils.copyFileToDirectory(documentCssFile, assetsDirectory);
        FileUtils.copyFileToDirectory(ebookGeneratorCssFile, assetsDirectory);

        final File coverArtDirectory = assembleFileSystem.getArtworkFile(step).toPath().getParent().toFile();
        FileUtils.forceMkdir(coverArtDirectory);
        FileUtils.copyFileToDirectory(coverArtFile, coverArtDirectory);

        File bundleDocsDirectory = fileSystem.getHtmlPagesDirectory(step, MATERIAL_NUMBER);
        FileUtils.forceMkdir(bundleDocsDirectory);
        FileUtils.copyFileToDirectory(loadFileFromResources(
            "Useless_test_file.DIVXML_1_I334acde028b47ft34ed7fcedf0a72426.html"), bundleDocsDirectory);
        FileUtils.copyFileToDirectory(loadFileFromResources(
            "Useless_test_file.DIVXML_3_I4700e2c0g6kz11e69ed7fcedf0a72426.html"), bundleDocsDirectory);

        bundleDocsDirectory = fileSystem.getHtmlPagesDirectory(step, ADDITIONAL_MATERIAL_NUMBER);
        FileUtils.forceMkdir(bundleDocsDirectory);
        FileUtils.copyFileToDirectory(loadFileFromResources(
            "Useless_test_file.DIVXML_2_I3416j47028b911e69ed7fcedf0a72426.html"), bundleDocsDirectory);
        FileUtils.copyFileToDirectory(loadFileFromResources(
            "Useless_test_file.DIVXML_4_I4700e2c028b911e69ed7fcedfyt4l426.html"), bundleDocsDirectory);
    }

    private File loadFileFromResources(final String fileName) throws URISyntaxException
    {
        return new File(getClass().getResource(fileName).toURI());
    }

    @After
    public void clean() throws IOException
    {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
        FileUtils.cleanDirectory(assembleFileSystem.getAssembleDirectory(step));
    }

    @Test
    public void testCommonStepExecution() throws Exception
    {
        step.executeStep();
        assertThat(FileUtils.readFileToString(fileSystem.getTitleMetadataFile(step)), equalTo(expectedTitleMetadataFileContent));
        assertThat(FileUtils.readFileToString(assembleFileSystem.getTitleXml(step)), equalTo(expectedTitleFileContent));
    }
}
