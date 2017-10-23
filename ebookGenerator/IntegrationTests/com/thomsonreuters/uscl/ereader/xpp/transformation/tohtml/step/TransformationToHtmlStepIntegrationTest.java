package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemImpl;
import org.apache.commons.io.FileUtils;
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
    private static final String MATERIAL_NUMBER = "11111111";
    private static final String ADDITIONAL_MATERIAL_NUMBER = "11111112";
    private static final String REF_PLACE_HOLDER = "${refPlaceHolder}";

    @Resource(name = "transformToHtmlTask")
    @InjectMocks
    private TransformationToHtmlStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;
    @Value("${xpp.entities.dtd}")
    private File entitiesDtdFile;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    private File original;
    private File anchorsFile;

    @Before
    public void setUp() throws URISyntaxException {
        org.mockito.MockitoAnnotations.initMocks(this);
        original = new File(TransformationToHtmlStepIntegrationTest.class.getResource(SAMPLE_DIVXML_PAGE).toURI());
        anchorsFile = new File(TransformationToHtmlStepIntegrationTest.class.getResource(XppFormatFileSystemImpl.ANCHOR_TO_DOCUMENT_ID_MAP_FILE).toURI());
    }

    @Test
    public void shouldTransformPartsToHtml() throws Exception {
        //given
        initGiven(false);
        final File expected =
            new File(TransformationToHtmlStepIntegrationTest.class.getResource("expected.html").toURI());
        //when
        step.executeStep();
        //then
        final File html = fileSystem.getHtmlPageFile(step, MATERIAL_NUMBER, SAMPLE_DIVXML_PAGE);
        assertThat(FileUtils.readFileToString(html), equalTo(getExpectedString(expected)));
    }

    @Test
    public void shouldTransformMultiVolumePartsToHtml() throws Exception {
        //given
        initGiven(true);
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

    private void initGiven(final boolean multiVolume) throws Exception {
        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).willReturn(getXppBundles(multiVolume));

        FileUtils.copyFileToDirectory(original, mkdir(fileSystem.getOriginalPagesDirectory(step, MATERIAL_NUMBER)));
        if (multiVolume) {
            FileUtils.copyFileToDirectory(
                original,
                mkdir(fileSystem.getOriginalPagesDirectory(step, ADDITIONAL_MATERIAL_NUMBER)));
        }
        FileUtils.copyFileToDirectory(anchorsFile, mkdir(fileSystem.getDirectory(step, XppFormatFileSystemDir.ANCHORS_DIR, MATERIAL_NUMBER)));
        FileUtils.copyFileToDirectory(anchorsFile, mkdir(fileSystem.getDirectory(step, XppFormatFileSystemDir.ANCHORS_DIR, ADDITIONAL_MATERIAL_NUMBER)));
    }

    private List<XppBundle> getXppBundles(final boolean multivolume) {
        final List<XppBundle> bundles = new ArrayList<>();

        XppBundle bundle = new XppBundle();
        bundle.setMaterialNumber(MATERIAL_NUMBER);
        bundle.setOrderedFileList(Arrays.asList("Useless_test_file.DIVXML.xml"));
        bundles.add(bundle);

        if (multivolume) {
            bundle = new XppBundle();
            bundle.setMaterialNumber(ADDITIONAL_MATERIAL_NUMBER);
            bundle.setOrderedFileList(Arrays.asList("Useless_test_file.DIVXML.xml"));
            bundles.add(bundle);
        }

        return bundles;
    }

    private String getExpectedString(final File expected) throws IOException {
        return FileUtils.readFileToString(expected)
            .replace(REF_PLACE_HOLDER, entitiesDtdFile.getAbsolutePath().replace("\\", "/"));
    }
}
