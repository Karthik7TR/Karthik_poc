package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
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
public final class OriginalStructureTransformationStepTest {
    private static final String XPP_DIVXML_XML = "xpp.DIVXML.xml";
    private static final String MATERIAL_NUMBER = "11111111";
    private static final String DTD_FILE_PATH = "some/path";

    @InjectMocks
    private OriginalStructureTransformationStep step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private XppGatherFileSystem xppGatherFileSystem;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock
    private File transformToOriginalXsl;
    @Mock
    private File transformToFootnotesXsl;
    @Mock
    private File citeQueryProcessedFile;
    @Mock
    private File originalFile;
    @Mock
    private File footnotesFile;
    @Mock
    private File entitiesDtdFile;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Captor
    private ArgumentCaptor<TransformationCommand> commandCaptor;

    private File xppDirectory;
    private File xppFile;

    @Before
    public void setUp() throws IOException {
        xppDirectory = temporaryFolder.getRoot();
        final File bundleDir = mkdir(xppDirectory, MATERIAL_NUMBER);
        final File xppDir = mkdir(bundleDir, "/bundleName/XPP");
        xppFile = mkfile(xppDir, XPP_DIVXML_XML);
        given(entitiesDtdFile.getAbsolutePath()).willReturn(DTD_FILE_PATH);
        given(xppGatherFileSystem.getXppSourceXmls(step)).willReturn(getSourceXmlsFromGatherDir());
        given(fileSystem.getOriginalDirectory(step, MATERIAL_NUMBER)).willReturn(bundleDir);
        given(fileSystem.getOriginalFile(step, MATERIAL_NUMBER, XPP_DIVXML_XML)).willReturn(originalFile);
        given(fileSystem.getFootnotesFile(step, MATERIAL_NUMBER, XPP_DIVXML_XML)).willReturn(footnotesFile);
        given(fileSystem.getCiteQueryProcessedFile(step, MATERIAL_NUMBER, XPP_DIVXML_XML))
            .willReturn(citeQueryProcessedFile);
        given(citeQueryProcessedFile.getName()).willReturn(XPP_DIVXML_XML);
    }

    @Test
    public void shouldTransform() throws Exception {
        //given
        //when
        step.executeStep();
        //then
        then(transformationService).should(times(3)).transform(commandCaptor.capture());
        final Iterator<TransformationCommand> iterator = commandCaptor.getAllValues().iterator();
        assertThat(iterator.next().getOutputFile(), is(citeQueryProcessedFile));
        assertThat(iterator.next().getOutputFile(), is(originalFile));
        assertThat(iterator.next().getOutputFile(), is(footnotesFile));
    }

    private Map<String, Collection<File>> getSourceXmlsFromGatherDir() {
        return Collections.singletonMap(MATERIAL_NUMBER, (Collection<File>) Collections.singletonList(xppFile));
    }
}
