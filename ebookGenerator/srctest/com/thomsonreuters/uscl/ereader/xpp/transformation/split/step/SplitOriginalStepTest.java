package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.TransformationUtil;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class SplitOriginalStepTest
{
    @InjectMocks
    private SplitOriginalStep step;
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
    private TransformationUtil transformationUtil;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File originalDirectory;
    private File originalPartsDirectory;
    private File original;
    private File footnotes;

    @Before
    public void setUp() throws IOException
    {
        final File root = temporaryFolder.getRoot();
        originalDirectory = new File(root, "originalDirectory");
        FileUtils.forceMkdir(originalDirectory);
        original = new File(originalDirectory, "original");
        original.createNewFile();
        footnotes = new File(originalDirectory, "footnotes");
        footnotes.createNewFile();

        final File moveUpDir = new File(root, "MoveUp");
        FileUtils.forceMkdir(moveUpDir);
        new File(moveUpDir, "original").createNewFile();
        new File(moveUpDir, "footnotes").createNewFile();

        originalPartsDirectory = new File(root, "originalPartsDirectory");

        given(fileSystem.getOriginalDirectory(step)).willReturn(originalDirectory);
        given(fileSystem.getPagebreakesUpDirectory(step)).willReturn(moveUpDir);
        given(fileSystem.getOriginalPartsDirectory(step)).willReturn(originalPartsDirectory);
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
        then(transformationService).should().transform(any(Transformer.class), eq(original), any(File.class));
        then(transformationService).should().transform(any(Transformer.class), eq(footnotes), any(File.class));
        then(transformationService).should(times(2))
            .transform(any(Transformer.class), any(File.class), eq(originalPartsDirectory));
    }
}
