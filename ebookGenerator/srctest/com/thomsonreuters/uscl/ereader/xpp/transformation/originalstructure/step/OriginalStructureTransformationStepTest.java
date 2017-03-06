package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
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
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class OriginalStructureTransformationStepTest
{
    @InjectMocks
    private OriginalStructureTransformationStep step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilder transformerBuilder;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File transformToOriginalXsl;
    private File xppDirectory;
    private File incorrectXppDirectory;
    private File xppFile;

    @Before
    public void setUp() throws IOException
    {
        incorrectXppDirectory = new File("");
        xppDirectory = temporaryFolder.getRoot();
        xppFile = new File(xppDirectory, "xpp.xml");
        xppFile.createNewFile();
        transformToOriginalXsl = new File(xppDirectory, "some.xsl");
        given(fileSystem.getOriginalDirectory(step)).willReturn(xppDirectory);
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
        FieldUtils.writeField(step, "transformToOriginalXsl", transformToOriginalXsl, true);
        //when
        step.executeStep();
        //then
        then(transformationService).should().transform((Transformer) any(), eq(xppFile), (File) any());
    }
}
