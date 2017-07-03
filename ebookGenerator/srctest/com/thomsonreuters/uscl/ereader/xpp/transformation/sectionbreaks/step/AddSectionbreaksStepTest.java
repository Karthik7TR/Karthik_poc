package com.thomsonreuters.uscl.ereader.xpp.transformation.sectionbreaks.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.TransformationUtil;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddSectionbreaksStepTest
{
    private static final String MATERIAL_NUMBER = "11111111";

    @InjectMocks
    private AddSectionbreaksStep step;
    @Mock
    private XppFormatFileSystem fileSystem;;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private TransformationUtil transformationUtil;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File inputFile;
    private File outputFile;

    @Before
    public void setUp()
    {
        inputFile = mock(File.class);
        outputFile = mock(File.class);

        given(inputFile.getName()).willReturn("1-CHAL_7.DIVXML.main");

        given(fileSystem.getStructureWithMetadataFiles(step)).willReturn(Collections.singletonMap(MATERIAL_NUMBER, (Collection<File>) Arrays.asList(inputFile)));
        given(fileSystem.getSectionbreaksDirectory(step, MATERIAL_NUMBER)).willReturn(new File(temporaryFolder.getRoot(), "02_Sectionbreaks"));
        given(fileSystem.getSectionbreaksFile(any(AddSectionbreaksStep.class), any(String.class), any(String.class))).willReturn(outputFile);

        final TransformerBuilder builder = mock(TransformerBuilder.class);
        given(transformerBuilderFactory.create()).willReturn(builder);
        given(builder.withXsl(any(File.class))).willReturn(builder);
    }

    @Test
    public void shouldTransform() throws Exception
    {
        //given
        given(transformationUtil.shouldSkip(step)).willReturn(false);
        //when
        step.executeStep();
        //then
        then(fileSystem).should().getSectionbreaksDirectory(eq(step), any(String.class));
        then(transformationService).should().transform(any(Transformer.class), eq(inputFile), eq(outputFile));
        then(fileSystem).should().getSectionbreaksFile(eq(step), any(String.class), any(String.class));
    }
}
