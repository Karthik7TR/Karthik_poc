package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleWebBuildProductType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemImpl;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TransformationToHtmlStepIntegrationTestConfiguration.class)
@ActiveProfiles("IntegrationTests")
public final class TransformationToHtmlStepIntegrationTest {
    private static final String SAMPLE_DIVXML_PAGE = "1-sample_1.DIVXML_0_test.page";
    private static final String SAMPLE_DIVXML_LRRE_PAGE = "1-sample_1_Table_of_LRRE.DIVXML_0_test.page";
    private static final String SAMPLE_DIVXML_INDEX_PAGE = "1-sample_Volume_1_Index.DIVXML_0001_42073593.IndexABSENCEHEARSAY.page";
    private static final String VOLUMES_MAP = "volumesMap.xml";
    private static final String MATERIAL_NUMBER = "11111111";
    private static final String ADDITIONAL_MATERIAL_NUMBER = "11111112";
    private static final String REF_PLACE_HOLDER = "${refPlaceHolder}";
    private static final XppFormatFileSystemDir SOURCE_DIR = XppFormatFileSystemDir.POCKET_PART_LINKS_DIR;

    @Resource(name = "transformToHtmlTask")
    @InjectMocks
    private TransformationToHtmlStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;
    @Value("${xpp.entities.dtd}")
    private File entitiesDtdFile;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    @Mock
    private XppBundle xppBundle;
    @Mock
    private BookDefinition bookDefinition;

    private File original;
    private File originalLrre;
    private File originalIndex;
    private File anchorsFile;
    private File sumTocAnchorsFile;
    private File volumesMap;

    @Before
    public void setUp() throws URISyntaxException {
        org.mockito.MockitoAnnotations.initMocks(this);
        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).thenReturn(Collections.singletonList(xppBundle));
        when(xppBundle.getProductType()).thenReturn("supp");
        when(xppBundle.isPocketPartPublication()).thenReturn(true);
        when(xppBundle.getMaterialNumber()).thenReturn(MATERIAL_NUMBER);
        original = new File(TransformationToHtmlStepIntegrationTest.class.getResource(SAMPLE_DIVXML_PAGE).toURI());
        originalLrre = new File(TransformationToHtmlStepIntegrationTest.class.getResource(SAMPLE_DIVXML_LRRE_PAGE).toURI());
        originalIndex = new File(TransformationToHtmlStepIntegrationTest.class.getResource(SAMPLE_DIVXML_INDEX_PAGE).toURI());
        anchorsFile = new File(TransformationToHtmlStepIntegrationTest.class.getResource(XppFormatFileSystemImpl.ANCHOR_TO_DOCUMENT_ID_MAP_FILE).toURI());
        sumTocAnchorsFile = new File(TransformationToHtmlStepIntegrationTest.class.getResource(MATERIAL_NUMBER + "/" + XppFormatFileSystemImpl.ANCHOR_TO_DOCUMENT_ID_MAP_FILE).toURI());
        volumesMap = new File(TransformationToHtmlStepIntegrationTest.class.getResource(VOLUMES_MAP).toURI());
    }

    @After
    public void clean() throws IOException {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldTransformPartsToHtml() throws Exception {
        //given
        initGiven(false, false);
        final File expected =
            new File(TransformationToHtmlStepIntegrationTest.class.getResource("expected.html").toURI());
        final File expectedLrre =
            new File(TransformationToHtmlStepIntegrationTest.class.getResource("expected-tbl-lrre.html").toURI());
        final File expectedIndex =
            new File(TransformationToHtmlStepIntegrationTest.class.getResource("expected-index.html").toURI());
        //when
        step.executeStep();
        //then
        final File html = fileSystem.getHtmlPageFile(step, MATERIAL_NUMBER, SAMPLE_DIVXML_PAGE);
        assertThat(FileUtils.readFileToString(html), equalTo(getExpectedString(expected)));
        final File htmlLrre = fileSystem.getHtmlPageFile(step, MATERIAL_NUMBER, SAMPLE_DIVXML_LRRE_PAGE);
        assertThat(FileUtils.readFileToString(htmlLrre), equalTo(getExpectedString(expectedLrre)));
        final File htmlIndex = fileSystem.getHtmlPageFile(step, MATERIAL_NUMBER, SAMPLE_DIVXML_INDEX_PAGE);
        assertThat(FileUtils.readFileToString(htmlIndex), equalTo(getExpectedString(expectedIndex)));
    }

    @Test
    public void shouldTransformMultiVolumePartsToHtml() throws Exception {
        //given
        initGiven(true, false);
        final File expectedVol1 = new File(
            TransformationToHtmlStepIntegrationTest.class.getResource("expected-with-prefix-vol1.html").toURI());
        final File expectedVol2 = new File(
            TransformationToHtmlStepIntegrationTest.class.getResource("expected-with-prefix-vol2.html").toURI());
        //when
        step.executeStep();
        //then
        File html = fileSystem.getHtmlPageFile(step, MATERIAL_NUMBER, SAMPLE_DIVXML_PAGE);
        assertThat(FileUtils.readFileToString(html), equalTo(getExpectedString(expectedVol1)));
        html = fileSystem.getHtmlPageFile(step, ADDITIONAL_MATERIAL_NUMBER, SAMPLE_DIVXML_PAGE);
        assertThat(FileUtils.readFileToString(html), equalTo(getExpectedString(expectedVol2)));
    }

    @Test
    public void shouldTransformMultiVolumeSplitPartsToHtml() throws Exception {
        //given
        initGiven(true, true);
        final File expectedVol1 = new File(
            TransformationToHtmlStepIntegrationTest.class.getResource("split/expected-with-prefix-vol1.html").toURI());
        final File expectedVol2 = new File(
            TransformationToHtmlStepIntegrationTest.class.getResource("split/expected-with-prefix-vol2.html").toURI());
        //when
        step.executeStep();
        //then
        File html = fileSystem.getHtmlPageFile(step, MATERIAL_NUMBER, SAMPLE_DIVXML_PAGE);
        assertThat(FileUtils.readFileToString(html), equalTo(getExpectedString(expectedVol1)));
        html = fileSystem.getHtmlPageFile(step, ADDITIONAL_MATERIAL_NUMBER, SAMPLE_DIVXML_PAGE);
        assertThat(FileUtils.readFileToString(html), equalTo(getExpectedString(expectedVol2)));
    }

    private void initGiven(final boolean multiVolume, final boolean isSplit) throws Exception {
        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).willReturn(getXppBundles(multiVolume));

        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.EBOOK_DEFINITON)).willReturn(bookDefinition);
        given(bookDefinition.getPrintComponents()).willReturn(getPrintComponents(multiVolume, isSplit));
        given(bookDefinition.getFullyQualifiedTitleId()).willReturn("uscl/ts/test_book");

        FileUtils.copyFileToDirectory(original, mkdir(fileSystem.getDirectory(step, SOURCE_DIR, MATERIAL_NUMBER)));
        FileUtils.copyFileToDirectory(originalLrre, mkdir(fileSystem.getDirectory(step, SOURCE_DIR, MATERIAL_NUMBER)));
        FileUtils.copyFileToDirectory(originalIndex, mkdir(fileSystem.getDirectory(step, SOURCE_DIR, MATERIAL_NUMBER)));
        if (multiVolume) {
            FileUtils.copyFileToDirectory(volumesMap, mkdir(fileSystem.getDirectory(step, XppFormatFileSystemDir.VOLUMES_MAP_DIR)));
            FileUtils.copyFileToDirectory(
                original,
                mkdir(fileSystem.getDirectory(step, SOURCE_DIR, ADDITIONAL_MATERIAL_NUMBER)));
        }
        final File sumTocAchorsDir = mkdir(fileSystem.getDirectory(step, XppFormatFileSystemDir.ANCHORS_DIR, MATERIAL_NUMBER));
        final File sumTocAchorsDir2 = mkdir(fileSystem.getDirectory(step, XppFormatFileSystemDir.ANCHORS_DIR, ADDITIONAL_MATERIAL_NUMBER));

        if (isSplit) {
            anchorsFile = new File(TransformationToHtmlStepIntegrationTest.class
                .getResource(String.format("split/%s", XppFormatFileSystemImpl.ANCHOR_TO_DOCUMENT_ID_MAP_FILE)).toURI());
        }

        FileUtils.copyFileToDirectory(anchorsFile, sumTocAchorsDir.getParentFile());
        FileUtils.copyFileToDirectory(sumTocAnchorsFile, sumTocAchorsDir);
        FileUtils.copyFileToDirectory(sumTocAnchorsFile, sumTocAchorsDir2);
    }

    private List<XppBundle> getXppBundles(final boolean multivolume) {
        final List<XppBundle> bundles = new ArrayList<>();

        XppBundle bundle = new XppBundle();
        bundle.setMaterialNumber(MATERIAL_NUMBER);
        bundle.setProductType("bound");
        bundle.setOrderedFileList(Arrays.asList("0_Front_vol_1.DIVXML.xml", "Useless_test_file.DIVXML.xml", "1-sample_1.DIVXML_0_test.xml", "1-sample_1_Table_of_LRRE.DIVXML_0_test.xml"));
        bundles.add(bundle);

        if (multivolume) {
            bundle = new XppBundle();
            bundle.setMaterialNumber(ADDITIONAL_MATERIAL_NUMBER);
            bundle.setOrderedFileList(Arrays.asList("Useless_test_file.DIVXML.xml"));
            bundle.setProductType("supp");
            bundle.setWebBuildProductType(XppBundleWebBuildProductType.SUPPLEMENTARY_PAMPHLET);
            bundles.add(bundle);
        }

        return bundles;
    }

    private Set<PrintComponent> getPrintComponents(final boolean multiVolume, final boolean isSplit) {
        final Set<PrintComponent> components = new HashSet<>();
        PrintComponent component = new PrintComponent();
        component.setComponentOrder(1);
        component.setSplitter(false);
        component.setMaterialNumber(MATERIAL_NUMBER);
        components.add(component);

        if (isSplit) {
            component = new PrintComponent();
            component.setComponentOrder(2);
            component.setSplitter(true);
            components.add(component);
        }

        if (multiVolume) {
            component = new PrintComponent();
            component.setComponentOrder(3);
            component.setSplitter(false);
            component.setMaterialNumber(ADDITIONAL_MATERIAL_NUMBER);
            components.add(component);
        }
        return components;
    }

    private String getExpectedString(final File expected) throws IOException {
        return FileUtils.readFileToString(expected)
            .replace(REF_PLACE_HOLDER, entitiesDtdFile.getAbsolutePath().replace("\\", "/"));
    }
}
