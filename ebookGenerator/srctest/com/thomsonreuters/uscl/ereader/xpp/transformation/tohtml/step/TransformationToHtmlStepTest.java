package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.TransformationUtil;
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
public final class TransformationToHtmlStepTest
{
    private static final String TEMP_DIVXML_XML = "temp.DIVXML.xml";
    private static final String MATERIAL_NUMBER = "11111111";

    @InjectMocks
    private TransformationToHtmlStep step;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private TransformationUtil transformationUtil;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private File transformToHtmlXsl;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File toHtmlFile;

    @Before
    public void setUp() throws IOException
    {
        final File root = temporaryFolder.getRoot();

        final File originalPagesDir = mkdir(root, "OriginalPages", MATERIAL_NUMBER);
        final File originalFile = mkfile(originalPagesDir, TEMP_DIVXML_XML);

        given(chunkContext
            .getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext()
            .get(JobParameterKey.XPP_BUNDLES))
        .willReturn(getXppBundles());

        given(fileSystem.getOriginalPageFiles(step)).willReturn(Collections.singletonMap(MATERIAL_NUMBER, (Collection<File>) Arrays.asList(originalFile)));

        final File toHtmlDirectory = mkdir(root, "toHtmlDirectory", MATERIAL_NUMBER);
        toHtmlFile = new File(toHtmlDirectory, "temp");
        given(fileSystem.getHtmlPagesDirectory(step, MATERIAL_NUMBER)).willReturn(toHtmlDirectory);
        given(fileSystem.getHtmlPageFile(step, MATERIAL_NUMBER, TEMP_DIVXML_XML)).willReturn(toHtmlFile);

        //TODO: remove when nobundles directory structure is no longer in use
        given(fileSystem.getHtmlPagesDirectory(step)).willReturn(toHtmlDirectory.getParentFile());
    }

    private List<XppBundle> getXppBundles()
    {
        final XppBundle firstBundle = new XppBundle();
        firstBundle.setMaterialNumber(MATERIAL_NUMBER);
        firstBundle.setOrderedFileList(Arrays.asList("Useless_test_file.DIVXML.xml"));
        return Arrays.asList(firstBundle);
    }

    @Test
    public void shouldSkipStepIfNoInputFileFound() throws Exception
    {
        //given
        given(transformationUtil.shouldSkip(step)).willReturn(true);
        //when
        step.executeStep();
        //then
        then(transformationService).should(never()).transform((Transformer) any(), (File) any(), (File) any());
    }

    @Test
    public void shouldTransform() throws Exception
    {
        //given
        //when
        step.executeStep();
        //then
        then(transformationService).should().transform(any(Transformer.class), any(File.class), eq(toHtmlFile));
    }
}
