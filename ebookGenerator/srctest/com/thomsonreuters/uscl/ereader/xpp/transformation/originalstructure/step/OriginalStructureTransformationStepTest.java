package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

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

@RunWith(MockitoJUnitRunner.class)
public final class OriginalStructureTransformationStepTest
{
    @InjectMocks
    private OriginalStructureTransformationStep step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock
    private TransformationUtil transformationUtil;
    @Mock
    private File transformToOriginalXsl;
    @Mock
    private File transformToFootnotesXsl;
    @Mock
    private File originalFile;
    @Mock
    private File footnotesFile;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File xppDirectory;
    private File xppFile;

    @Before
    public void setUp() throws IOException
    {
        xppDirectory = temporaryFolder.getRoot();
        xppFile = new File(xppDirectory, "xpp.xml");
        xppFile.createNewFile();
        given(fileSystem.getOriginalDirectory(step)).willReturn(xppDirectory);
        given(fileSystem.getOriginalFile(step, "xpp.xml")).willReturn(originalFile);
        given(fileSystem.getFootnotesFile(step, "xpp.xml")).willReturn(footnotesFile);
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
        FieldUtils.writeField(step, "xppDirectory", xppDirectory, true);
        //when
        step.executeStep();
        //then
        then(transformationService).should().transform((Transformer) any(), eq(xppFile), eq(originalFile));
        then(transformationService).should().transform((Transformer) any(), eq(xppFile), eq(footnotesFile));
    }
}
