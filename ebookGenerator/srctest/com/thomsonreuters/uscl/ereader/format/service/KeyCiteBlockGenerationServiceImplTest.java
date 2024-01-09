package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class KeyCiteBlockGenerationServiceImplTest {
    private KeyCiteBlockGenerationServiceImpl service;
    private DocMetadataService mockDocMetadataService;
    private CitationNormalizer mockCitationNormalizer;
    private DocMetadata docMetadata;
    private String titleId;
    private long jobId;
    private String docGuid;

    /**
     * Generic setup for all the tests.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        docMetadata = new DocMetadata();

        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        mockCitationNormalizer = EasyMock.createMock(CitationNormalizer.class);
        service = new KeyCiteBlockGenerationServiceImpl();
        service.setDocMetadataService(mockDocMetadataService);
        service.setHostname("http://www.westlaw.com");
        service.setMudParamRS("ebbb3.0");
        service.setMudParamVR("3.0");
        service.setCitationNormalizer(mockCitationNormalizer);
        titleId = "uscl/an/IMPH";
        jobId = 101;
        docGuid = "I770806320bbb11e1948492503fc0d37f";

        EasyMock.expect(mockCitationNormalizer.normalizeCitation("Title 1 §100")).andReturn("TITLE 1 S100");
        EasyMock.expect(mockCitationNormalizer.normalizeCitation("Title 2 §200")).andReturn("TITLE 2 S200");
        EasyMock.expect(mockCitationNormalizer.normalizeCitation("Title 3 §300")).andReturn("TITLE 3 S300");
        EasyMock.replay(mockCitationNormalizer);
    }

    @Test
    public void testGetKeyCite() {
        expectDocMetadataService(docMetadata);

        InputStream keyCiteStream = null;
        try {
            keyCiteStream = service.getKeyCiteInfo(titleId, jobId, docGuid);
            System.out.println(keyCiteStream);
        } catch (final EBookFormatException e) {
            e.printStackTrace();
        }
        EasyMock.verify(mockDocMetadataService);
        Assert.assertNotNull(keyCiteStream);
    }

    @Test
    public void testSymbolConversion() throws IOException {
        expectDocMetadataService(docMetadata);

        docMetadata.setNormalizedFirstlineCite("Title 1 \u00A7100");
        docMetadata.setFirstlineCite("Title 2 \u00A7200");
        docMetadata.setSecondlineCite("Title 3 \u00A7300");

        InputStream keyCiteStream = null;
        try {
            keyCiteStream = service.getKeyCiteInfo(titleId, jobId, docGuid);

            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            IOUtils.copy(keyCiteStream, output);
            final String keyCite = new String(output.toByteArray());
            System.out.println(keyCite);

            final String expected = "<div id=\"ebookGeneratorKeyciteInfo\" class=\"co_flush x_introPara\">"
                + "<a href=\"http://www.westlaw.com/Search/Results.html?query=kc%3ATITLE+2+S200%3BTITLE+3+S300%3BTITLE+1+"
                + "S100%3B&amp;jurisdiction=ALLCASES&amp;contentType=ALL&amp;startIndex=1&amp;transitionType=Search&amp;"
                + "contextData=(sc.Default)&amp;rs=ebbb3.0&amp;vr=3.0\"><img src=\"er:#keycite\" "
                + "alt=\"KeyCite This Document\"/></a></div>";

            Assert.assertEquals(expected, keyCite);
        } catch (final EBookFormatException e) {
            e.printStackTrace();
        }
        EasyMock.verify(mockDocMetadataService);
        Assert.assertNotNull(keyCiteStream);
    }

    @Test
    public void testGetKeyCiteDefault() throws IOException, EBookFormatException {
        expectDocMetadataService(null);

        final InputStream keyCiteStream = service.getKeyCiteInfo(titleId, jobId, docGuid);
        final String result = IOUtils.toString(keyCiteStream);

        assertEquals(result, StringUtils.EMPTY);
        EasyMock.verify(mockDocMetadataService);
    }

    private void expectDocMetadataService(final DocMetadata docMetadata) {
        EasyMock.expect(mockDocMetadataService.findDocMetadataByPrimaryKey(titleId, jobId, docGuid))
        .andReturn(docMetadata);
        EasyMock.replay(mockDocMetadataService);
    }
}
