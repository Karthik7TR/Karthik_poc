package com.thomsonreuters.uscl.ereader.xpp.transformation.internalAnchors.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InternalAnchorsStepIntegrationTestConfiguration.class)
@ActiveProfiles("IntegrationTests")
public final class InternalAnchorsStepIntegrationTest {
    private static final String TITLE_ID = "uscl/ts/test_title";
    private static final String MATERIAL_NUMBER = "111111";
    private static final String MATERIAL_NUMBER_2 = "1111112";

    @Resource(name = "internalAnchorsTask")
    @InjectMocks
    private InternalAnchorsStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private XppBundle xppBundleSupplement;
    @Mock
    private XppBundle xppBundleBound;
    @Mock
    private BookDefinition bookDefinition;

    private File source1;
    private File source2;
    private File expected;
    private File expectedSummaryToc;
    private File expectedSummaryToc2;
    private File expectedSupplementAnchors;
    private File expectedBoundAnchors;

    @Before
    public void setUp() throws URISyntaxException, Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        when(bookDefinition.getPrintComponents()).thenReturn(getPrintComponents());
        when(bookDefinition.getFullyQualifiedTitleId()).thenReturn(TITLE_ID);
        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).thenReturn(Arrays.asList(xppBundleSupplement, xppBundleBound));
        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.EBOOK_DEFINITON)).thenReturn(bookDefinition);
        when(xppBundleSupplement.getProductType()).thenReturn("supp");
        when(xppBundleSupplement.isPocketPartPublication()).thenReturn(true);
        when(xppBundleSupplement.getMaterialNumber()).thenReturn(MATERIAL_NUMBER);
        when(xppBundleBound.getProductType()).thenReturn("bound");
        when(xppBundleBound.getMaterialNumber()).thenReturn(MATERIAL_NUMBER_2);

        final File sectionBreaksDirectory =
            fileSystem.getDirectory(step, XppFormatFileSystemDir.SECTIONBREAKS_DIR, MATERIAL_NUMBER);
        final File sectionBreaksDirectory2 =
            fileSystem.getDirectory(step, XppFormatFileSystemDir.SECTIONBREAKS_DIR, MATERIAL_NUMBER_2);
        FileUtils.forceMkdir(sectionBreaksDirectory);
        FileUtils.forceMkdir(sectionBreaksDirectory2);

        source1 = new File(InternalAnchorsStepIntegrationTest.class.getResource("source-1-CHAL_7.DIVXML.main").toURI());
        source2 =
            new File(InternalAnchorsStepIntegrationTest.class.getResource("source-1-CHAL_APX_21.DIVXML.main").toURI());
        FileUtils.copyFileToDirectory(source1, sectionBreaksDirectory);
        FileUtils.copyFileToDirectory(source2, sectionBreaksDirectory2);

        expected = new File(
            InternalAnchorsStepIntegrationTest.class.getResource("expectedAnchorToDocumentIdMapFile.xml").toURI());

        expectedSummaryToc =
            new File(InternalAnchorsStepIntegrationTest.class.getResource("expectedSummaryTocMapFile1.xml").toURI());

        expectedSummaryToc2 =
            new File(InternalAnchorsStepIntegrationTest.class.getResource("expectedSummaryTocMapFile2.xml").toURI());

        expectedSupplementAnchors = new File(
            InternalAnchorsStepIntegrationTest.class.getResource("expectedAnchorToDocumentIdMapSupplementFile.xml")
                .toURI());

        expectedBoundAnchors = new File(
            InternalAnchorsStepIntegrationTest.class.getResource("expectedAnchorToDocumentIdMapBoundFile.xml")
            .toURI());
    }

    private Set<PrintComponent> getPrintComponents() {
        final PrintComponent firstPrintComponent = new PrintComponent();
        firstPrintComponent.setSplitter(false);
        firstPrintComponent.setComponentOrder(1);
        firstPrintComponent.setMaterialNumber(MATERIAL_NUMBER);

        final PrintComponent secondPrintComponent = new PrintComponent();
        secondPrintComponent.setSplitter(false);
        secondPrintComponent.setComponentOrder(2);
        secondPrintComponent.setMaterialNumber(MATERIAL_NUMBER_2);

        return Stream.of(firstPrintComponent, secondPrintComponent).collect(Collectors.toCollection(HashSet::new));
    }

    @After
    public void onTestComplete() throws IOException {
        FileUtils.forceDelete(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldCreateMappingFile() throws Exception {
        //given
        //when
        step.executeStep();
        //then
        final File anchors = fileSystem.getAnchorToDocumentIdMapFile(step);
        assertThat(anchors, hasSameContentAs(expected));

        final File summaryTocAnchors = fileSystem.getAnchorToDocumentIdMapFile(step, MATERIAL_NUMBER);
        assertThat(summaryTocAnchors, hasSameContentAs(expectedSummaryToc));

        final File summaryTocAnchors2 = fileSystem.getAnchorToDocumentIdMapFile(step, MATERIAL_NUMBER_2);
        assertThat(summaryTocAnchors2, hasSameContentAs(expectedSummaryToc2));

        final File supplementAnchors = fileSystem.getAnchorToDocumentIdMapBoundFile(step, MATERIAL_NUMBER);
        assertThat(supplementAnchors, hasSameContentAs(expectedSupplementAnchors));

        final File boundAnchors = fileSystem.getAnchorToDocumentIdMapBoundFile(step, MATERIAL_NUMBER_2);
        assertThat(boundAnchors, hasSameContentAs(expectedBoundAnchors));
    }
}
