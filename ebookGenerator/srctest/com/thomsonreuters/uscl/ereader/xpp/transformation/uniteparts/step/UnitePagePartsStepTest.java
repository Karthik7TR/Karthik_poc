package com.thomsonreuters.uscl.ereader.xpp.transformation.uniteparts.step;

import static java.util.Arrays.asList;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
    private static final String MATERIAL_NUMBER = "11111111";

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

        final File originalPagesDir = mkdir(root, "OriginalPages", MATERIAL_NUMBER);
        given(fileSystem.getOriginalPagesDirectory(step, MATERIAL_NUMBER)).willReturn(originalPagesDir);
        given(fileSystem.getOriginalPagesDirectory(step)).willReturn(originalPagesDir.getParentFile());

        final File originalFile = mkfile(mkdir(root, "Format/01_Original", MATERIAL_NUMBER), "sample.main");
        final File originalFile2 = mkfile(mkdir(root, "Format/01_Original", MATERIAL_NUMBER), "sampleTwo.main");
        given(fileSystem.getOriginalMainAndFootnoteFiles(step)).willReturn(Collections.singletonMap(MATERIAL_NUMBER, (Collection<File>) asList(originalFile, originalFile2)));

        final File originalPartsDir = mkdir(root, "OriginalParts");
        given(fileSystem.getOriginalPartsDirectory(step, MATERIAL_NUMBER)).willReturn(originalPartsDir);

        final File main11 = mkfile(originalPartsDir, "sample_1_main.part");
        final File footnotes11 = mkfile(originalPartsDir, "sample_1_footnotes.part");
        final File main12 = mkfile(originalPartsDir, "sample_2_main.part");
        final File footnotes12 = mkfile(originalPartsDir, "sample_2_footnotes.part");
        final File main2 = mkfile(originalPartsDir, "sampleTwo_1_main.part");
        final File footnotes2 = mkfile(originalPartsDir, "sampleTwo_1_footnotes.part");

        given(fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "sample", 1, PartType.MAIN)).willReturn(main11);
        given(fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "footnotes", 1, PartType.FOOTNOTE)).willReturn(footnotes11);
        given(fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "sample", 2, PartType.MAIN)).willReturn(main12);
        given(fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "footnotes", 2, PartType.FOOTNOTE)).willReturn(footnotes12);
        given(fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "sampleTwo", 1, PartType.MAIN)).willReturn(main2);
        given(fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, "footnotesTwo", 1, PartType.FOOTNOTE)).willReturn(footnotes2);
    }

    @Test
    public void shouldTransform() throws Exception
    {
        //given
        given(fileSystem.getOriginalPageFile(any(BookStep.class), eq(MATERIAL_NUMBER), any(String.class), any(Integer.class))).willReturn(page);
        //when
        step.executeTransformation();
        //then
        then(fileSystem).should(times(2)).getOriginalPartsDirectory(any(BookStep.class), eq(MATERIAL_NUMBER));

        //TODO: return checking footnotes invocations when split by structure for footnotes is ready
        then(fileSystem).should(times(3)).getOriginalPartsFile(any(BookStep.class), eq(MATERIAL_NUMBER), any(String.class), any(Integer.class), any(PartType.class));
        then(transformationService).should(times(3)).transform(any(Transformer.class), any(List.class), eq(page));
    }
}
