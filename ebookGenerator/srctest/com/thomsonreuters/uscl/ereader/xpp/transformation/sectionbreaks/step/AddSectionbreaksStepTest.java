package com.thomsonreuters.uscl.ereader.xpp.transformation.sectionbreaks.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.io.File;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.filesystem.entity.basefiles.BaseFilesIndex;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
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
public final class AddSectionbreaksStepTest
{
    private static final String MAIN_DOCUMENT_WITH_SECTIONBREAKS_PARAM = "mainDocumentWithSectionbreaks";
    private static final String BASE_FILENAME = "1-CHAL_7.DIVXML";
    private static final String MAIN_CONTENT_FILE_PATH = "path";

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
    private Transformer transformer;

    @Mock
    private File inputFile;
    @Mock
    private File outputFile;
    @Mock
    private File footnotesFile;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp()
    {
        given(inputFile.getName()).willReturn("1-CHAL_7.DIVXML.main");

        given(fileSystem.getStructureWithMetadataFilesIndex(step)).willReturn(getFilesIndex());

        given(fileSystem.getSectionbreaksDirectory(step, MATERIAL_NUMBER)).willReturn(new File(temporaryFolder.getRoot(), "02_Sectionbreaks"));
        given(fileSystem.getSectionbreaksFile(any(AddSectionbreaksStep.class), any(String.class), any(String.class))).willReturn(outputFile);
        given(outputFile.getAbsolutePath()).willReturn(MAIN_CONTENT_FILE_PATH);

        final TransformerBuilder builder = mock(TransformerBuilder.class);
        given(transformerBuilderFactory.create()).willReturn(builder);
        given(builder.withXsl(any(File.class))).willReturn(builder);
        given(builder.build()).willReturn(transformer);
    }

    @Test
    public void shouldTransform() throws Exception
    {
        //given
        //when
        step.executeStep();
        //then
        then(fileSystem).should().getSectionbreaksDirectory(eq(step), any(String.class));
        then(transformationService).should(times(2)).transform(any(TransformationCommand.class));
        then(fileSystem).should(times(2)).getSectionbreaksFile(eq(step), any(String.class), any(String.class));
        then(transformer).should().setParameter(eq(MAIN_DOCUMENT_WITH_SECTIONBREAKS_PARAM), eq(MAIN_CONTENT_FILE_PATH));
    }

    private BaseFilesIndex getFilesIndex()
    {
        final BaseFilesIndex baseFilesIndex = new BaseFilesIndex();
        baseFilesIndex.put(MATERIAL_NUMBER, BASE_FILENAME, PartType.MAIN, inputFile);
        baseFilesIndex.put(MATERIAL_NUMBER, BASE_FILENAME, PartType.FOOTNOTE, footnotesFile);
        return baseFilesIndex;
    }
}
