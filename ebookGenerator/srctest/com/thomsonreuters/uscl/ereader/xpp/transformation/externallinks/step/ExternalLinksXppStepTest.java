package com.thomsonreuters.uscl.ereader.xpp.transformation.externallinks.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.citequery.CiteQueryMapper;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.citequery.CiteQueryMapperResponse;
import org.apache.commons.io.FileUtils;
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
import org.xml.sax.SAXException;

@RunWith(MockitoJUnitRunner.class)
public final class ExternalLinksXppStepTest {
    private static final String HTML_FILE_NAME = "test.html";
    private static final String MATERIAL_NUMBER = "88005553535";

    @InjectMocks
    private ExternalLinksXppStep sut;

    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private XslTransformationService transformationService;
    private Map<String, Collection<File>> htmlPageFiles;
    @Mock
    private Set<Map.Entry<String, Collection<File>>> entrySet;
    @Mock
    private Iterator<Map.Entry<String, Collection<File>>> iterator;
    @Mock
    private Map.Entry<String, Collection<File>> entry;
    private String mappingFilePath = "some/path";
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Captor
    private ArgumentCaptor<TransformationCommand> commandCaptor;
    @Mock
    private FileUtils fileUtils;
    @Mock
    private CiteQueryMapper citeQueryMapper;

    private File xppDirectory;
    @Mock
    private File htmlFile;
    @Mock
    private File externalLinksFile;

    @Before
    public void setUp() throws IOException, ParserConfigurationException, SAXException {
        xppDirectory = temporaryFolder.getRoot();
        final File bundleDir = mkdir(xppDirectory, MATERIAL_NUMBER);
        final File xppDir = mkdir(bundleDir, "/08_HtmlPages");
        htmlFile = mkfile(xppDir, HTML_FILE_NAME);
        htmlPageFiles = new HashMap<>();
        htmlPageFiles.put(MATERIAL_NUMBER, Collections.singletonList(htmlFile));

        given(fileSystem.getHtmlPageFiles(sut)).willReturn(htmlPageFiles);
        given(fileSystem.getExternalLinksFile(sut, MATERIAL_NUMBER, HTML_FILE_NAME)).willReturn(externalLinksFile);
        given(citeQueryMapper.createMappingFile(htmlFile, MATERIAL_NUMBER, sut)).willReturn(new CiteQueryMapperResponse(mappingFilePath));
    }

    @Test
    public void shouldTransform() throws Exception {
        //given
        //when
        sut.executeTransformation();
        //then
        then(transformationService).should().transform(commandCaptor.capture());
        final Iterator<TransformationCommand> iter = commandCaptor.getAllValues().iterator();
        assertThat(iter.next().getOutputFile(), is(externalLinksFile));
    }
}
