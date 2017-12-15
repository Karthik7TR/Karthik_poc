package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.filesystem.entity.basefiles.BaseFilesIndex;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
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
public final class SplitOriginalStepTest {
    private static final String MATERIAL_NUMBER = "11111111";
    private static final String BASE_NAME = "sample.DIVXML";
    private static final String MAIN_FILE = "sample.DIVXML.main";
    private static final String FOOTNOTES_FILE = "sample.DIVXML.footnotes";

    @InjectMocks
    private SplitOriginalStep step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private Transformer transformer;
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

    private File columnsUpFootnotes;

    @Before
    public void setUp() throws IOException {
        final File root = temporaryFolder.getRoot();
        originalDirectory = mkdir(root, "originalDirectory");
        original = mkfile(originalDirectory, MAIN_FILE);
        footnotes = mkfile(originalDirectory, FOOTNOTES_FILE);

        final File columnsUpDir = mkdir(root, "ColumnsUp");
        final File columnsUpOriginal = mkfile(columnsUpDir, MAIN_FILE);
        columnsUpFootnotes = mkfile(columnsUpDir, FOOTNOTES_FILE);

        final File moveUpDir = mkdir(root, "MoveUp");
        moveUpOriginal = mkfile(moveUpDir, MAIN_FILE);
        moveUpFootnotes = mkfile(moveUpDir, FOOTNOTES_FILE);

        originalPartsDirectory = new File(root, "originalPartsDirectory");

        given(fileSystem.getFiles(step, SplitOriginalStep.INPUT_DIR_1)).willReturn(getFilesFromBundleStructure(original, footnotes));

        final BaseFilesIndex baseFilesIndex = new BaseFilesIndex();
        baseFilesIndex.put(MATERIAL_NUMBER, BASE_NAME, PartType.MAIN, columnsUpOriginal);
        baseFilesIndex.put(MATERIAL_NUMBER, BASE_NAME, PartType.FOOTNOTE, columnsUpFootnotes);
        given(fileSystem.getBaseFilesIndex(step, SplitOriginalStep.INPUT_DIR_2)).willReturn(baseFilesIndex);

        given(fileSystem.getFile(step, SplitOriginalStep.INPUT_DIR_2, MATERIAL_NUMBER, MAIN_FILE)).willReturn(columnsUpOriginal);
        given(fileSystem.getFile(step, SplitOriginalStep.INPUT_DIR_2, MATERIAL_NUMBER, FOOTNOTES_FILE)).willReturn(columnsUpFootnotes);
        given(fileSystem.getDirectory(step, SplitOriginalStep.INPUT_DIR_2, MATERIAL_NUMBER)).willReturn(columnsUpDir);

        given(fileSystem.getFiles(step, SplitOriginalStep.INPUT_DIR_3)).willReturn(getFilesFromBundleStructure(moveUpOriginal, moveUpFootnotes));
        given(fileSystem.getFile(step, SplitOriginalStep.INPUT_DIR_3, MATERIAL_NUMBER, MAIN_FILE)).willReturn(moveUpOriginal);
        given(fileSystem.getFile(step, SplitOriginalStep.INPUT_DIR_3, MATERIAL_NUMBER, FOOTNOTES_FILE)).willReturn(moveUpFootnotes);
        given(fileSystem.getDirectory(step, SplitOriginalStep.INPUT_DIR_3, MATERIAL_NUMBER)).willReturn(moveUpDir);

        given(fileSystem.getDirectory(step, SplitOriginalStep.OUTPUT_DIR_3, MATERIAL_NUMBER)).willReturn(originalPartsDirectory);

        given(transformerBuilderFactory.create().withXsl(any(File.class)).build()).willReturn(transformer);
    }

    @Test
    public void shouldTransform() throws Exception {
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

        then(transformer).should().setParameter(eq("fileType"), eq(PartType.MAIN.name()));
        then(transformer).should().setParameter(eq("fileType"), eq(PartType.FOOTNOTE.name()));
        then(transformer).should().setParameter(eq("footnotesFile"), eq(columnsUpFootnotes.getAbsolutePath().replaceAll("\\\\", "/")));
    }

    private Map<String, Collection<File>> getFilesFromBundleStructure(final File...files) {
        return Collections.singletonMap(MATERIAL_NUMBER, (Collection<File>) Arrays.asList(files));
    }
}
