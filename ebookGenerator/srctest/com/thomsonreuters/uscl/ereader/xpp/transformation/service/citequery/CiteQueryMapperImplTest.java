package com.thomsonreuters.uscl.ereader.xpp.transformation.service.citequery;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.runner.commonsio.FileUtils;
import org.xml.sax.SAXException;

@RunWith(MockitoJUnitRunner.class)
public final class CiteQueryMapperImplTest {
    @InjectMocks
    private CiteQueryMapperImpl sut;

    private File htmlFile;
    private String materialNumber;
    @Mock
    private XppTransformationStep step;
    @Mock
    private XppFormatFileSystem fileSystem;
    private File outputFile;

    @Before
    public void setUp() throws Exception {
        htmlFile = new File(this.getClass().getResource("sample.html").toURI());
        materialNumber = "4815162342";
        outputFile = new File(htmlFile.getParent() + "/output.html");
        outputFile.createNewFile();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(outputFile);
    }

    /**
     * Test method for {@link com.thomsonreuters.uscl.ereader.xpp.transformation.service.citequery.CiteQueryMapperImpl#createMappingFile(java.io.File, java.lang.String, com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep)}.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test
    public void shouldCreateMappingFile() throws ParserConfigurationException, SAXException, IOException {
        //given
        given(fileSystem.getExternalLinksMappingFile(step, materialNumber, htmlFile.getName())).willReturn(outputFile);
        //when
        final String output = sut.createMappingFile(htmlFile, materialNumber, step).getMapFilePath();
        //then
        then(fileSystem).should(times(2)).getExternalLinksMappingFile(step, materialNumber, htmlFile.getName());
        assertThat(output, equalTo("file:///" + outputFile.getPath().replace("\\", "/")));
    }

}
