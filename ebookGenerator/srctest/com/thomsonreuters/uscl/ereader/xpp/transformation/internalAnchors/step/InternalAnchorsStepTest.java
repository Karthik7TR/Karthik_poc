package com.thomsonreuters.uscl.ereader.xpp.transformation.internalAnchors.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class InternalAnchorsStepTest
{
    @InjectMocks
    private InternalAnchorsStep step;
    @Mock
    private XppFormatFileSystem fileSystem;;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private TransformerBuilderFactory transformerBuilderFactory;

    private File mapFile;

    @Before
    public void setUp()
    {
        final File htmlPagesDir = mock(File.class);
        final Map<String, Collection<File>> files = new HashMap<>();
        files.put("111111", Arrays.asList(htmlPagesDir));

        given(htmlPagesDir.listFiles(any(FileFilter.class))).willReturn(new File[] {});
        given(fileSystem.getOriginalPageFiles(step)).willReturn(files);
        given(htmlPagesDir.listFiles()).willReturn(new File[]{});

        final TransformerBuilder builder = mock(TransformerBuilder.class);
        given(transformerBuilderFactory.create()).willReturn(builder);
        given(builder.withXsl(any(File.class))).willReturn(builder);

        mapFile = new File("mapFile");
        given(fileSystem.getAnchorToDocumentIdMapFile(step)).willReturn(mapFile);
    }

    @Test
    public void shouldTransform() throws Exception
    {
        //given
        //when
        step.executeStep();
        //then
        then(transformationService).should().transform((TransformationCommand) any());
        then(fileSystem).should().getOriginalPageFiles(eq(step));
    }
}
