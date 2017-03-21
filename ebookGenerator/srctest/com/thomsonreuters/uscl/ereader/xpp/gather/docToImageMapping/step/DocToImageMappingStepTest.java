package com.thomsonreuters.uscl.ereader.xpp.gather.docToImageMapping.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.List;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.TransformationUtil;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DocToImageMappingStepTest
{
    @InjectMocks
    private DocToImageMappingStep step;
    @Mock
    private XppFormatFileSystem fileSystem;;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private TransformationUtil transformationUtil;

    private File mapFile;

    @Before
    public void setUp()
    {
        final File htmlPagesDir = mock(File.class);
        given(fileSystem.getHtmlPagesDirectory(step)).willReturn(htmlPagesDir);
        given(htmlPagesDir.listFiles()).willReturn(new File[]{});

        final TransformerBuilder builder = mock(TransformerBuilder.class);
        given(transformerBuilderFactory.create()).willReturn(builder);
        given(builder.withXsl(any(File.class))).willReturn(builder);

        mapFile = new File("mapFile");
        given(fileSystem.getDocToImageMapFile(step)).willReturn(mapFile);
    }

    @Test
    public void shouldTransform() throws Exception
    {
        //given
        given(transformationUtil.shouldSkip(step)).willReturn(false);
        //when
        step.executeStep();
        //then
        then(transformationService).should().transform(any(Transformer.class), any(List.class), eq(mapFile));
        then(fileSystem).should().getHtmlPagesDirectory(eq(step));
    }
}
