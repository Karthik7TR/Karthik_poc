package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBookBundles;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public class ExtractTocStepTest
{
    @InjectMocks
    private ExtractTocStep step;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private TransformationUtil transformationUtil;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock
    private File buildTocItemToDocumentIdMapXsl;
    @Mock
    private File extractTocXsl;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File originalFile;
    private File originalFile2;

    @Before
    public void init() throws IOException
    {
        final File root = temporaryFolder.getRoot();
        final File originalPagesDir = new File(root, "04_OriginalPages");
        final File originalDir = new File(root, "01_Original");

        originalDir.mkdirs();
        originalPagesDir.mkdirs();

        given(fileSystem.getOriginalPagesDirectory(step)).willReturn(root);
        given(fileSystem.getTocItemToDocumentIdMapFile(step)).willReturn(new File(root, "tocItemToDocumentIdMap.xml"));
        given(fileSystem.getOriginalDirectory(step)).willReturn(root);

        originalFile = new File(originalDir, "original.main");
        Files.createFile(originalFile.toPath());

        originalFile2 = new File(originalDir, "original2.main");
        Files.createFile(originalFile2.toPath());

        givenBookBundles(chunkContext, Collections.<XppBundle>emptyList());
    }

    @Test
    public void shouldTransformOneFile() throws Exception
    {
        given(fileSystem.getOriginalFiles(any(BookStep.class))).willReturn(Collections.singleton(originalFile));

        step.executeTransformation();

        then(transformationService).should(times(1)).transform(
            any(Transformer.class),
            any(ArrayList.class),
            any(File.class));

        final ArgumentCaptor<Collection<InputStream>> inputStreams = ArgumentCaptor.forClass((Class<Collection<InputStream>>)(Class)Collection.class);
        verify(transformationService).transform(
            any(Transformer.class),
            inputStreams.capture(),
            any(String.class),
            any(File.class));
        assertEquals(1, inputStreams.getValue().size());
    }

    @Test
    public void shouldTransformMultipleFiles() throws Exception
    {
        given(fileSystem.getOriginalFiles(any(BookStep.class))).willReturn(Arrays.asList(originalFile, originalFile2));

        step.executeTransformation();

        final ArgumentCaptor<Collection<InputStream>> inputStreams = ArgumentCaptor.forClass((Class<Collection<InputStream>>)(Class)Collection.class);
        verify(transformationService).transform(
            any(Transformer.class),
            inputStreams.capture(),
            any(String.class),
            any(File.class));
        assertEquals(4, inputStreams.getValue().size());
    }

}
