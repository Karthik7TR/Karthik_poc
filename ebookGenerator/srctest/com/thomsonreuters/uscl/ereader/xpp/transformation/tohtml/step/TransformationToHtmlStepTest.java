package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

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
public final class TransformationToHtmlStepTest
{
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

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File toHtmlFile;

    @Before
    public void setUp() throws IOException
    {
        final File root = temporaryFolder.getRoot();

        final File originalPartsDir = new File(root, "OriginalParts");
        FileUtils.forceMkdir(originalPartsDir);
        new File(originalPartsDir, "temp").createNewFile();
        given(fileSystem.getOriginalPartsDirectory(step)).willReturn(originalPartsDir);

        final File toHtmlDirectory = new File(root, "toHtmlDirectory");
        toHtmlFile = new File(toHtmlDirectory, "temp");
        given(fileSystem.getToHtmlDirectory(step)).willReturn(toHtmlDirectory);
        given(fileSystem.getToHtmlFile(step, "temp")).willReturn(toHtmlFile);
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
