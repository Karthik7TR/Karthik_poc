package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
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
public final class SplitOriginalStepTest
{
    @InjectMocks
    private SplitOriginalStep step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock
    private File movePagebreakesUpXsl;
    @Mock
    private File splitOriginalXsl;
    @Mock
    private XslTransformationService transformationService;

    @Mock
    private File xppDirectory;
    @Mock
    private File incorrectXppDirectory;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File originalPartsDirectory;

    @Before
    public void setUp() throws IOException
    {
        given(incorrectXppDirectory.exists()).willReturn(false);
        given(xppDirectory.exists()).willReturn(true);

        final File root = temporaryFolder.getRoot();
        final File originalDir = new File(root, "Original");
        FileUtils.forceMkdir(originalDir);
        final File moveUpDir = new File(root, "MoveUp");
        FileUtils.forceMkdir(moveUpDir);
        originalPartsDirectory = new File(root, "OriginalParts");
        given(fileSystem.getOriginalDirectory(step)).willReturn(originalDir);
        given(fileSystem.getPagebreakesUpDirectory(step)).willReturn(moveUpDir);
        given(fileSystem.getOriginalPartsDirectory(step)).willReturn(originalPartsDirectory);
        new File(originalDir, "temp").createNewFile();
        new File(moveUpDir, "temp").createNewFile();
    }

    @Test
    public void shouldSkipStepIfNoInputFileFound() throws Exception
    {
        //given
        FieldUtils.writeField(step, "xppDirectory", incorrectXppDirectory, true);
        //when
        step.executeStep();
        //then
        then(transformationService).should(never()).transform((Transformer) any(), (File) any(), (File) any());
    }

    @Test
    public void shouldTransform() throws Exception
    {
        //given
        FieldUtils.writeField(step, "xppDirectory", xppDirectory, true);
        //when
        step.executeStep();
        //then
        then(transformationService).should()
            .transform(any(Transformer.class), any(File.class), eq(originalPartsDirectory));
    }
}
