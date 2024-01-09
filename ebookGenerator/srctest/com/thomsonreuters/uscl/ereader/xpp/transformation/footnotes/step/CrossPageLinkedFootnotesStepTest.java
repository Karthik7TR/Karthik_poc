package com.thomsonreuters.uscl.ereader.xpp.transformation.footnotes.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;

import com.thomsonreuters.uscl.ereader.common.filesystem.entity.basefiles.BaseFilesIndex;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
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
public class CrossPageLinkedFootnotesStepTest {
    private static final String MATERIAL_NUMBER = "11111111";
    @InjectMocks
    private CrossPageLinkedFootnotesStep step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock
    private XslTransformationService transformationService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Captor
    private ArgumentCaptor<TransformationCommand> commandCaptor;

    private static final XppFormatFileSystemDir INPUT_DIR = XppFormatFileSystemDir.SECTIONBREAKS_DIR;
    private static final XppFormatFileSystemDir OUTPUT_DIR = XppFormatFileSystemDir.CROSS_PAGE_FOOTNOTES;

    private File footnotes;
    private File footnotesOutput;
    private final String baseName = "0-WIPRACV7_Front_vol_7.DIVXML";
    private final String mainFileName = baseName + ".main";

    @Before
    public void setUp() throws IOException {
        final File root = temporaryFolder.getRoot();

        final File rootInputDir = mkdir(root, INPUT_DIR.getDirName());
        final File rootOutputDir = mkdir(root, OUTPUT_DIR.getDirName());
        final File inputDir = mkdir(rootInputDir, MATERIAL_NUMBER);
        final File outputDir = mkdir(rootOutputDir, MATERIAL_NUMBER);

        final File main = mkfile(inputDir, mainFileName);
        footnotes = mkfile(inputDir, baseName + ".footnotes");

        final File mainOutput = mkfile(outputDir, main.getName());
        footnotesOutput = mkfile(outputDir, footnotes.getName());

        final BaseFilesIndex inputFiles = new BaseFilesIndex();
        inputFiles.put(MATERIAL_NUMBER, baseName, PartType.MAIN, main);
        inputFiles.put(MATERIAL_NUMBER, baseName, PartType.FOOTNOTE, footnotes);

        given(fileSystem.getBaseFilesIndex(step, INPUT_DIR)).willReturn(inputFiles);
        given(fileSystem.getDirectory(step, OUTPUT_DIR, MATERIAL_NUMBER)).willReturn(new File(rootOutputDir, MATERIAL_NUMBER));
        given(fileSystem.getFile(step, OUTPUT_DIR, MATERIAL_NUMBER, main.getName())).willReturn(mainOutput);
        given(fileSystem.getFile(step, OUTPUT_DIR, MATERIAL_NUMBER, footnotes.getName())).willReturn(footnotesOutput);
    }

    @Test
    public void shouldTransformFootnoteFile() throws Exception {
        //given
        //when
        step.executeStep();
        //then
        then(transformationService).should(times(1)).transform(commandCaptor.capture());
        final TransformationCommand captured = commandCaptor.getAllValues().get(0);
        assertEquals(captured.getInputFile().getAbsolutePath(), footnotes.getAbsolutePath());
        assertEquals(captured.getOutputFile().getAbsolutePath(), footnotesOutput.getAbsolutePath());
    }

}
