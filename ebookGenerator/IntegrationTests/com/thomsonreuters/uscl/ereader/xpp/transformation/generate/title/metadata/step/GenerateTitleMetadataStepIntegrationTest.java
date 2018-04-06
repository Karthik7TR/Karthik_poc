package com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.proview.Keyword;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleWebBuildProductType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import lombok.SneakyThrows;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = GenerateTitleMetadataStepIntegrationTestConfiguration.class)
@ActiveProfiles("IntegrationTests")
public final class GenerateTitleMetadataStepIntegrationTest {
    private static final String CURRENT_DATE_PLACEHOLDER = "${currentDate}";
    private static final String MATERIAL_NUMBER = "123456";
    private static final String ADDITIONAL_MATERIAL_NUMBER = "123457";
    private static final String SPLIT_MATERIAL_NUMBER = "123458";
    private static final String TITLE_ID = "uscl/gen/title_metadata_integration_test";

    @Resource(name = "generateTitleMetadataTask")
    @InjectMocks
    private BookStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;
    @Resource(name = "assembleFileSystem")
    private AssembleFileSystem assembleFileSystem;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition bookDefinition;

    private File tocFile;
    private File splitTocFile;
    private File coverArtFile;
    private File documentCssFile;
    private File ebookGeneratorCssFile;
    private String expectedTitleFileContent;
    private String expectedTitleMetadataFileContent;
    private String expectedTitleFileContentSecondPart;
    private String expectedTitleMetadataFileContentSecondPart;

    @Before
    public void onSetUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
    }

    private void initTestData(final boolean isSplitBook) {
        initMocksBehavior(isSplitBook);
        initBookDefinitionMockBehavior(isSplitBook);
        initFiles(isSplitBook);
    }

    private void initMocksBehavior(final boolean isSplitBook) {
        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.EBOOK_DEFINITON)).thenReturn(bookDefinition);

        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobParameters()
                .getString(JobParameterKey.BOOK_VERSION_SUBMITTED)).thenReturn("5.0");

        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).thenReturn(getXppBundles(isSplitBook));
    }

    private List<XppBundle> getXppBundles(final boolean isSplitBook) {
        final List<XppBundle> bundles = new ArrayList<>();

        final XppBundle firstBundle = new XppBundle();
        firstBundle.setMaterialNumber(MATERIAL_NUMBER);
        firstBundle.setOrderedFileList(Arrays.asList("Useless_test_file.DIVXML.xml"));
        bundles.add(firstBundle);

        final XppBundle secondBundle = new XppBundle();
        secondBundle.setMaterialNumber(ADDITIONAL_MATERIAL_NUMBER);
        secondBundle.setOrderedFileList(Arrays.asList("Useless_test_file.DIVXML.xml"));
        secondBundle.setProductType("supp");
        secondBundle.setWebBuildProductType(XppBundleWebBuildProductType.SUPPLEMENTARY_PAMPHLET);
        bundles.add(secondBundle);

        if (isSplitBook) {
            final XppBundle splitBundle = new XppBundle();
            splitBundle.setMaterialNumber(SPLIT_MATERIAL_NUMBER);
            splitBundle.setOrderedFileList(Arrays.asList("Useless_test_file.DIVXML.xml"));
            bundles.add(splitBundle);
        }

        return bundles;
    }

    private void initBookDefinitionMockBehavior(final boolean isSplitBook) {
        when(bookDefinition.getMaterialId()).thenReturn("someMaterialId");
        when(bookDefinition.getProviewDisplayName()).thenReturn("Integration test book");
        when(bookDefinition.getAuthors()).thenReturn(Collections.EMPTY_LIST);
        when(bookDefinition.getKeyWords()).thenReturn(getKeywords());
        when(bookDefinition.getCopyright()).thenReturn("©");
        when(bookDefinition.getIsbnNormalized()).thenReturn("9780314943910");
        when(bookDefinition.getFullyQualifiedTitleId()).thenReturn(TITLE_ID);
        when(bookDefinition.getTitleId()).thenReturn("title_metadata_integration_test");
        when(bookDefinition.getAutoUpdateSupportFlag()).thenReturn(true);
        when(bookDefinition.getEnableCopyFeatureFlag()).thenReturn(true);
        when(bookDefinition.getOnePassSsoLinkFlag()).thenReturn(true);
        when(bookDefinition.getSourceType()).thenReturn(SourceType.XPP);
        when(bookDefinition.isSplitBook()).thenReturn(isSplitBook);

        final Set<PrintComponent> printComponents = new HashSet<>();
        PrintComponent component = new PrintComponent();
        component.setComponentOrder(1);
        component.setSplitter(false);
        component.setMaterialNumber(MATERIAL_NUMBER);
        printComponents.add(component);

        component = new PrintComponent();
        component.setComponentOrder(2);
        component.setSplitter(false);
        component.setMaterialNumber(ADDITIONAL_MATERIAL_NUMBER);
        printComponents.add(component);

        if (isSplitBook) {
            component = new PrintComponent();
            component.setComponentOrder(3);
            component.setSplitter(true);
            printComponents.add(component);

            component = new PrintComponent();
            component.setComponentOrder(4);
            component.setSplitter(false);
            component.setMaterialNumber(SPLIT_MATERIAL_NUMBER);
            printComponents.add(component);
        }
        when(bookDefinition.getPrintComponents()).thenReturn(printComponents);
    }

    private List<Keyword> getKeywords() {
        final Keyword publisherKeyword = new Keyword("publisher", "Thomson Reuters Westlaw");
        final Keyword jurisdictionKeyword = new Keyword("jurisdiction", "Alabama");
        return Arrays.asList(publisherKeyword, jurisdictionKeyword);
    }

    @SneakyThrows
    private void initFiles(final boolean isSplitBook) {
        tocFile = loadFileFromResources("toc.xml");
        coverArtFile = loadFileFromResources("coverArt.PNG");
        documentCssFile = loadFileFromResources("document.css");
        ebookGeneratorCssFile = loadFileFromResources("ebook_generator.css");
        if (isSplitBook) {
            splitTocFile = loadFileFromResources("toc_pt2.xml");
        }

        if (isSplitBook) {
            expectedTitleMetadataFileContent = FileUtils.readFileToString(
                new File(GenerateTitleMetadataStepIntegrationTest.class.getResource("expectedSplitTitleMetadata.xml").toURI()))
                .replace(CURRENT_DATE_PLACEHOLDER, DateFormatUtils.format(new Date(), "yyyyMMdd"));
            expectedTitleMetadataFileContentSecondPart = FileUtils.readFileToString(
                new File(GenerateTitleMetadataStepIntegrationTest.class.getResource("expectedSplitTitleMetadata_pt2.xml").toURI()))
                .replace(CURRENT_DATE_PLACEHOLDER, DateFormatUtils.format(new Date(), "yyyyMMdd"));
            expectedTitleFileContent = FileUtils
                .readFileToString(
                new File(GenerateTitleMetadataStepIntegrationTest.class.getResource("expectedSplitTitle.xml").toURI()))
                .replace(CURRENT_DATE_PLACEHOLDER, DateFormatUtils.format(new Date(), "yyyyMMdd"));
            expectedTitleFileContentSecondPart = FileUtils
                .readFileToString(
                new File(GenerateTitleMetadataStepIntegrationTest.class.getResource("expectedSplitTitle_pt2.xml").toURI()))
                .replace(CURRENT_DATE_PLACEHOLDER, DateFormatUtils.format(new Date(), "yyyyMMdd"));
        } else {
            expectedTitleMetadataFileContent = FileUtils.readFileToString(
                new File(GenerateTitleMetadataStepIntegrationTest.class.getResource("expectedTitleMetadata.xml").toURI()))
                .replace(CURRENT_DATE_PLACEHOLDER, DateFormatUtils.format(new Date(), "yyyyMMdd"));
            expectedTitleFileContent = FileUtils
                .readFileToString(
                new File(GenerateTitleMetadataStepIntegrationTest.class.getResource("expectedTitle.xml").toURI()))
                .replace(CURRENT_DATE_PLACEHOLDER, DateFormatUtils.format(new Date(), "yyyyMMdd"));
        }

        final File tocDirectory = fileSystem.getTocDirectory(step);
        FileUtils.forceMkdir(tocDirectory);
        FileUtils.copyFileToDirectory(tocFile, tocDirectory);
        if (isSplitBook) {
            FileUtils.copyFileToDirectory(splitTocFile, tocDirectory);
        }

        File assetsDirectory = assembleFileSystem.getAssetsDirectory(step);
        FileUtils.forceMkdir(assetsDirectory);
        FileUtils.copyFileToDirectory(documentCssFile, assetsDirectory);
        FileUtils.copyFileToDirectory(ebookGeneratorCssFile, assetsDirectory);
        if (isSplitBook) {
            assetsDirectory = assembleFileSystem.getSplitPartAssetsDirectory(step, 2);
            FileUtils.forceMkdir(assetsDirectory);
            FileUtils.copyFileToDirectory(documentCssFile, assetsDirectory);
            FileUtils.copyFileToDirectory(ebookGeneratorCssFile, assetsDirectory);
        }

        File coverArtDirectory = assembleFileSystem.getArtworkFile(step).toPath().getParent().toFile();
        FileUtils.forceMkdir(coverArtDirectory);
        FileUtils.copyFileToDirectory(coverArtFile, coverArtDirectory);
        if (isSplitBook) {
            coverArtDirectory = assembleFileSystem.getSplitPartArtworkFile(step, 2).toPath().getParent().toFile();
            FileUtils.forceMkdir(coverArtDirectory);
            FileUtils.copyFileToDirectory(coverArtFile, coverArtDirectory);
        }

        File bundleDocsDirectory = fileSystem.getExternalLinksDirectory(step, MATERIAL_NUMBER);
        FileUtils.forceMkdir(bundleDocsDirectory);
        FileUtils.copyFileToDirectory(
            loadFileFromResources("Useless_test_file.DIVXML_1_I334acde028b47ft34ed7fcedf0a72426.html"),
            bundleDocsDirectory);
        FileUtils.copyFileToDirectory(
            loadFileFromResources("Useless_test_file.DIVXML_3_I4700e2c0g6kz11e69ed7fcedf0a72426.html"),
            bundleDocsDirectory);

        bundleDocsDirectory = fileSystem.getExternalLinksDirectory(step, ADDITIONAL_MATERIAL_NUMBER);
        FileUtils.forceMkdir(bundleDocsDirectory);
        FileUtils.copyFileToDirectory(
            loadFileFromResources("Useless_test_file.DIVXML_2_I3416j47028b911e69ed7fcedf0a72426.html"),
            bundleDocsDirectory);
        FileUtils.copyFileToDirectory(
            loadFileFromResources("Useless_test_file.DIVXML_4_I4700e2c028b911e69ed7fcedfyt4l426.html"),
            bundleDocsDirectory);

        if (isSplitBook) {
            bundleDocsDirectory = fileSystem.getExternalLinksDirectory(step, SPLIT_MATERIAL_NUMBER);
            FileUtils.copyFileToDirectory(
                loadFileFromResources("Useless_test_file.DIVXML_5_I4700e2c0g6kz11e69ed7fcedf0a72777.html"),
                bundleDocsDirectory);
            FileUtils.copyFileToDirectory(
                loadFileFromResources("Useless_test_file.DIVXML_6_I4700e2c0g6kz11e69ed7fcedf0a72888.html"),
                bundleDocsDirectory);
        }
    }

    @SneakyThrows
    private File loadFileFromResources(final String fileName) {
        return new File(getClass().getResource(fileName).toURI());
    }

    @After
    public void clean() throws IOException {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
        FileUtils.cleanDirectory(assembleFileSystem.getAssembleDirectory(step));
    }

    @Test
    public void testCommonStepExecution() throws Exception {
        //given
        initTestData(false);
        //when
        step.executeStep();
        //then
        assertThat(
            FileUtils.readFileToString(fileSystem.getTitleMetadataFile(step)),
            equalTo(expectedTitleMetadataFileContent));
        assertThat(FileUtils.readFileToString(assembleFileSystem.getTitleXml(step)), equalTo(expectedTitleFileContent));
    }

    @Test
    public void testSplitBookStepExecution() throws Exception {
        //given
        initTestData(true);
        //when
        step.executeStep();
        //then
        assertThat(
            FileUtils.readFileToString(fileSystem.getTitleMetadataFile(step)),
            equalTo(expectedTitleMetadataFileContent));
        assertThat(
            FileUtils.readFileToString(fileSystem.getSplitPartTitleMetadataFile(step, 2)),
            equalTo(expectedTitleMetadataFileContentSecondPart));
        assertThat(FileUtils.readFileToString(assembleFileSystem.getTitleXml(step)), equalTo(expectedTitleFileContent));
        assertThat(FileUtils.readFileToString(
            assembleFileSystem.getSplitPartTitleXml(step, 2)), equalTo(expectedTitleFileContentSecondPart));
    }
}
