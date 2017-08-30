package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
    private File moveSectionbreaksUpXsl;
    @Mock
    private File splitOriginalXsl;
    @Mock
    private XslTransformationService transformationService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Captor
    private ArgumentCaptor<TransformationCommand> commandCaptor;

    private File originalDirectory;
    private File originalPartsDirectory;
    private File original;
    private File footnotes;

    private File moveUpOriginal;
    private File moveUpFootnotes;

    @Before
    public void setUp() throws IOException
    {
        final File root = temporaryFolder.getRoot();
        originalDirectory = mkdir(root, "originalDirectory");
        original = mkfile(originalDirectory, "original");
        footnotes = mkfile(originalDirectory, "footnotes");

        final File columnsUpDir = mkdir(root, "ColumnsUp");
        final File columnsUpOriginal = mkfile(columnsUpDir, "original");
        final File columnsUpFootnotes = mkfile(columnsUpDir, "footnotes");

        final File moveUpDir = mkdir(root, "MoveUp");
        moveUpOriginal = mkfile(moveUpDir, "original");
        moveUpFootnotes = mkfile(moveUpDir, "footnotes");

        originalPartsDirectory = new File(root, "originalPartsDirectory");

        given(fileSystem.getSectionBreaksFiles(step)).willReturn(getFilesFromBundleStructure(original, footnotes));

        given(fileSystem.getMultiColumnsUpFiles(step)).willReturn(getFilesFromBundleStructure(columnsUpOriginal, columnsUpFootnotes));
        given(fileSystem.getMultiColumnsUpFile(step, MATERIAL_NUMBER, "original")).willReturn(columnsUpOriginal);
        given(fileSystem.getMultiColumnsUpFile(step, MATERIAL_NUMBER, "footnotes")).willReturn(columnsUpFootnotes);
        given(fileSystem.getMultiColumnsUpDirectory(step, MATERIAL_NUMBER)).willReturn(columnsUpDir);

        given(fileSystem.getSectionbreaksUpFiles(step)).willReturn(getFilesFromBundleStructure(moveUpOriginal, moveUpFootnotes));
        given(fileSystem.getSectionbreaksUpFile(step, MATERIAL_NUMBER, "original")).willReturn(moveUpOriginal);
        given(fileSystem.getSectionbreaksUpFile(step, MATERIAL_NUMBER, "footnotes")).willReturn(moveUpFootnotes);
        given(fileSystem.getSectionbreaksUpDirectory(step, MATERIAL_NUMBER)).willReturn(moveUpDir);

        given(fileSystem.getOriginalPartsDirectory(step, MATERIAL_NUMBER)).willReturn(originalPartsDirectory);
    }

    @Test
    public void shouldTransform() throws Exception
    {
        //given
        //when
        step.executeStep();
        //then
        then(transformationService).should(times(6)).transform(commandCaptor.capture());
        final Iterator<TransformationCommand> iterator = commandCaptor.getAllValues().iterator();
        assertThat(iterator.next().getInputFile(), is(original));
        assertThat(iterator.next().getInputFile(), is(footnotes));
        assertThat(iterator.next().getOutputFile(), is(moveUpOriginal));
        assertThat(iterator.next().getOutputFile(), is(moveUpFootnotes));
        assertThat(iterator.next().getOutputFile(), is(originalPartsDirectory));
        assertThat(iterator.next().getOutputFile(), is(originalPartsDirectory));
    }

    private Map<String, Collection<File>> getFilesFromBundleStructure(final File...files)
    {
        return Collections.singletonMap(MATERIAL_NUMBER, (Collection<File>) Arrays.asList(files));
    }
}
