package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class TransformationToHtmlStepTest {
    private static final String TEMP_DIVXML_XML = "1-sample_1.DIVXML_0_test.xml";
    private static final String MATERIAL_NUMBER = "11111111";

    @InjectMocks
    private TransformationToHtmlStep step;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock
    private XslTransformationService transformationService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private File transformToHtmlXsl;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File toHtmlFile;
    @Mock
    private File tocUnitsMapFile;

    @Mock
    private TransformerBuilder transformerBuilder;
    @Mock
    private Transformer transformer;

    @Before
    public void setUp() throws IOException {
        final File root = temporaryFolder.getRoot();

        final File originalPagesDir = mkdir(root, "OriginalPages", MATERIAL_NUMBER);
        final File originalFile = mkfile(originalPagesDir, TEMP_DIVXML_XML);

        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).willReturn(getXppBundles());

        given(fileSystem.getOriginalPageFiles(step))
            .willReturn(Collections.singletonMap(MATERIAL_NUMBER, (Collection<File>) Arrays.asList(originalFile)));

        final File toHtmlDirectory = mkdir(root, "toHtmlDirectory", MATERIAL_NUMBER);
        toHtmlFile = new File(toHtmlDirectory, "temp");
        given(fileSystem.getHtmlPagesDirectory(step, MATERIAL_NUMBER)).willReturn(toHtmlDirectory);
        given(fileSystem.getHtmlPageFile(step, MATERIAL_NUMBER, TEMP_DIVXML_XML)).willReturn(toHtmlFile);

        given(tocUnitsMapFile.getAbsolutePath()).willReturn("toc\\Units\\Map\\File\\path");
        given(fileSystem.getAnchorToDocumentIdMapFile(step)).willReturn(tocUnitsMapFile);
        given(fileSystem.getAnchorToDocumentIdMapFile(step, MATERIAL_NUMBER)).willReturn(tocUnitsMapFile);

        given(transformerBuilderFactory.create()).willReturn(transformerBuilder);
        given(transformerBuilder.withXsl(any(File.class))).willReturn(transformerBuilder);
        given(transformerBuilder.withParameter(any(String.class), any())).willReturn(transformerBuilder);
        given(transformerBuilder.build()).willReturn(transformer);
    }

    private List<XppBundle> getXppBundles() {
        final XppBundle firstBundle = new XppBundle();
        firstBundle.setMaterialNumber(MATERIAL_NUMBER);
        firstBundle.setOrderedFileList(Arrays.asList("1-TEST_7.DIVXML.xml"));
        return Arrays.asList(firstBundle);
    }

    @Test
    public void shouldTransform() throws Exception {
        //given
        //when
        step.executeStep();
        //then
        then(transformationService).should().transform(any(TransformationCommand.class));
        then(transformer).should().setParameter(eq("documentUidMapDoc"), any());
        then(transformer).should().setParameter(eq("summaryTocDocumentUidMapDoc"), any());
        then(transformer).should().setParameter(eq("isPocketPart"), any());
    }
}
