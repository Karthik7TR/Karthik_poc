package com.thomsonreuters.uscl.ereader.xpp.transformation.fonts;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class GenerateFontsCssStepTest {
    private static final String MATERIAL_NUMBER = "11111111";

    @InjectMocks
    private GenerateFontsCssStep step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private XppGatherFileSystem xppGatherFileSystem;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Mock
    private File entitiesDtdFile;
    @Mock
    private File cssFile;

    private File xppDirectory;
    private File xppFile;

    @Before
    public void setUp() throws IOException {
        xppDirectory = temporaryFolder.getRoot();
        final File bundleDir = mkdir(xppDirectory, MATERIAL_NUMBER);
        final File xppDir = mkdir(bundleDir, "/bundleName/XPP");
        xppFile = mkfile(xppDir, "xpp.xml");

        given(xppGatherFileSystem.getXppSourceXmls(step)).willReturn(getSourceXmlsFromGatherDir());
        given(fileSystem.getFontsCssDirectory(step, MATERIAL_NUMBER)).willReturn(bundleDir);
        given(fileSystem.getFontsCssFile(step, MATERIAL_NUMBER, "xpp.xml")).willReturn(cssFile);
    }

    @Test
    public void shouldTransform() throws Exception {
        //given
        //when
        step.executeStep();
        //then
        then(transformationService).should().transform((TransformationCommand) any());
    }

    private Map<String, Collection<File>> getSourceXmlsFromGatherDir() {
        return Collections.singletonMap(MATERIAL_NUMBER, (Collection<File>) Collections.singletonList(xppFile));
    }
}
