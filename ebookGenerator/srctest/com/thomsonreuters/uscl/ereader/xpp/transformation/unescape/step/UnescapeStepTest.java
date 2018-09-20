package com.thomsonreuters.uscl.ereader.xpp.transformation.unescape.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
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
public final class UnescapeStepTest {
    private static final String EXTERNAL_LINKS_FILE_NAME = "test.html";
    private static final String MATERIAL_NUMBER = "88005553535";

    @InjectMocks
    private UnescapeStep sut;

    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private XslTransformationService transformationService;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Captor
    private ArgumentCaptor<TransformationCommand> commandCaptor;

    private File xppDirectory;
    private File externalLinksFile;
    @Mock
    private File unescapeFile;

    @Before
    public void setUp() throws IOException {
        xppDirectory = temporaryFolder.getRoot();
        final File bundleDir = mkdir(xppDirectory, MATERIAL_NUMBER);
        final File xppDir = mkdir(bundleDir, XppFormatFileSystemDir.UNESCAPE_DIR.getDirName());
        externalLinksFile = mkfile(xppDir, EXTERNAL_LINKS_FILE_NAME);
        final List<File> files = Stream.of(externalLinksFile)
            .collect(Collectors.toList());
        final Map<String, Collection<File>> mapping = new HashMap<>();
        mapping.put(MATERIAL_NUMBER, files);
        given(fileSystem.getFiles(sut, XppFormatFileSystemDir.SPLIT_ANCHORS_DIR)).willReturn(mapping);
        given(
            fileSystem
                .getFile(sut, XppFormatFileSystemDir.UNESCAPE_DIR, MATERIAL_NUMBER, EXTERNAL_LINKS_FILE_NAME))
                    .willReturn(unescapeFile);
    }

    @Test
    public void shouldTransform() {
        //given
        //when
        sut.executeTransformation();
        //then
        then(transformationService).should()
            .transform(commandCaptor.capture());
        final Iterator<TransformationCommand> iter = commandCaptor.getAllValues()
            .iterator();
        assertThat(iter.next()
            .getOutputFile(), is(unescapeFile));
    }
}
