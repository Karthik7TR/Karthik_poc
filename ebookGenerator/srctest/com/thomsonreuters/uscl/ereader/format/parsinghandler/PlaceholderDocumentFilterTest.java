package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.SAXParserFactory;

import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Test cases for PlaceholderDocumentFilter.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public final class PlaceholderDocumentFilterTest {
    private PlaceholderDocumentFilter placeholderDocumentFilter;
    private InputSource placeholderDocumentTemplate;
    private ByteArrayOutputStream resultStream;

    @Before
    public void setUp() throws Exception {
        final List<String> anchors = new ArrayList<>();
        anchors.add("TestAnchor1");
        anchors.add("TestAnchor2");
        placeholderDocumentFilter = new PlaceholderDocumentFilter("YARR!", "tocGuid", anchors);
        resultStream = new ByteArrayOutputStream();

        placeholderDocumentTemplate = new InputSource(
            new ByteArrayInputStream("<html><head/><body><div><displaytext/></div></body></html>".getBytes()));
        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");

        final Serializer serializer = SerializerFactory.getSerializer(props);
        serializer.setOutputStream(resultStream);
        placeholderDocumentFilter.setContentHandler(serializer.asContentHandler());
    }

    @After
    public void tearDown() {
        //Intentionally left blank
    }

    /**
     * Tests replacement of the &lt;displaytext&gt; element with the corresponding text.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void testPlaceholderDocumentFilterHappyPath() throws Exception {
        final String expectedXml = "<html><head/><body><div>YARR!</div></body></html>";
        placeholderDocumentFilter.setParent(SAXParserFactory.newInstance().newSAXParser().getXMLReader());
        placeholderDocumentFilter.parse(placeholderDocumentTemplate);
        Assert.assertArrayEquals(expectedXml.getBytes(), resultStream.toByteArray());
    }

    /**
     * This test confirms that XML which does not contain the &lt;displaytext&gt; element comes out unmodified.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void testIdentityTransform() throws Exception {
        final String expectedXml = "<html><head/><body><div>YARR!</div></body></html>";
        placeholderDocumentFilter.setParent(SAXParserFactory.newInstance().newSAXParser().getXMLReader());
        placeholderDocumentFilter.parse(new InputSource(new ByteArrayInputStream(expectedXml.getBytes())));
        Assert.assertArrayEquals(expectedXml.getBytes(), resultStream.toByteArray());
    }
}
