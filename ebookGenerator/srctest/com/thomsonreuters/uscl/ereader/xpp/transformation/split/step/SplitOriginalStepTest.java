package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
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

@RunWith(MockitoJUnitRunner.class)
public final class SplitOriginalStepTest
{
    private static final String MATERIAL_NUMBER = "11111111";
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
        originalDirectory = mkdir(root, "originalDirectory");
        original = mkfile(originalDirectory, "original");
        footnotes = mkfile(originalDirectory, "footnotes");

        final File moveUpDir = mkdir(root, "MoveUp");
        final File moveUpOriginal = mkfile(moveUpDir, "original");
        final File moveUpFootnotes = mkfile(moveUpDir, "footnotes");

        originalPartsDirectory = new File(root, "originalPartsDirectory");

        given(fileSystem.getOriginalMainAndFootnoteFiles(step)).willReturn(getFilesFromBundleStructure(original, footnotes));
        given(fileSystem.getPagebreakesUpFiles(step)).willReturn(getFilesFromBundleStructure(moveUpOriginal, moveUpFootnotes));

        given(fileSystem.getPagebreakesUpDirectory(step, MATERIAL_NUMBER)).willReturn(moveUpDir);
        given(fileSystem.getOriginalPartsDirectory(step, MATERIAL_NUMBER)).willReturn(originalPartsDirectory);
        given(fileSystem.getOriginalPartsDirectory(step)).willReturn(root);
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

    private Map<String, Collection<File>> getFilesFromBundleStructure(final File...files)
    {
        return Collections.singletonMap(MATERIAL_NUMBER, (Collection<File>) Arrays.asList(files));
    }
}
