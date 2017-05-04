package com.thomsonreuters.uscl.ereader.xpp.transformation.uniteparts.step;

import static java.util.Arrays.asList;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
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
public final class UnitePagePartsStepTest
{
    @InjectMocks
    private UnitePagePartsStep step;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private TransformationUtil transformationUtil;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private File unitePagePartsXsl;
    @Mock
    private File page;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException
    {
        final File root = temporaryFolder.getRoot();

        final File originalPagesDir = new File(root, "OriginalPages");
        given(fileSystem.getOriginalPagesDirectory(step)).willReturn(originalPagesDir);

        final File originalFile = new File(root, "sample");
        originalFile.createNewFile();
        final File originalFile2 = new File(root, "sampleTwo");
        originalFile2.createNewFile();
        given(fileSystem.getOriginalFiles(step)).willReturn(asList(originalFile, originalFile2));

        final File originalPartsDir = new File(root, "OriginalParts");
        originalPartsDir.mkdirs();
        given(fileSystem.getOriginalPartsDirectory(step)).willReturn(originalPartsDir);
        final File main11 = new File(originalPartsDir, "sample_1_main.part");
        final File footnotes11 = new File(originalPartsDir, "sample_1_footnotes.part");
        final File main12 = new File(originalPartsDir, "sample_2_main.part");
        final File footnotes12 = new File(originalPartsDir, "sample_2_footnotes.part");
        final File main2 = new File(originalPartsDir, "sampleTwo_1_main.part");
        final File footnotes2 = new File(originalPartsDir, "sampleTwo_1_footnotes.part");

        main11.createNewFile();
        footnotes11.createNewFile();
        main12.createNewFile();
        footnotes12.createNewFile();
        main2.createNewFile();
        footnotes2.createNewFile();

        given(fileSystem.getOriginalPartsFile(step, "sample", 1, PartType.MAIN)).willReturn(main11);
        given(fileSystem.getOriginalPartsFile(step, "footnotes", 1, PartType.FOOTNOTE)).willReturn(footnotes11);
    }

    @Test
    public void shouldTransform() throws Exception
    {
        //given
        given(fileSystem.getOriginalPageFile(any(BookStep.class), any(String.class), any(Integer.class))).willReturn(page);
        //when
        step.executeTransformation();
        //then
        then(fileSystem).should(times(2)).getOriginalPartsDirectory(any(BookStep.class));
        then(fileSystem).should(times(6)).getOriginalPartsFile(any(BookStep.class), any(String.class), any(Integer.class), any(PartType.class));
        then(transformationService).should(times(3)).transform(any(Transformer.class), any(List.class), eq(page));
    }
}
