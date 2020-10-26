package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Test various XMLContentChangerFilterTest data scenarios.
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public final class XMLContentChangerFilterTest {
    private XMLContentChangerFilter contentChangeFilter;
    private Serializer serializer;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        final List<DocumentCurrency> currencies = new ArrayList<>();
        final DocumentCurrency currency = new DocumentCurrency();
        currency.setCurrencyGuid("123456789");
        currency.setNewText("Currency");
        currencies.add(currency);

        final List<DocumentCopyright> copyrights = new ArrayList<>();
        final DocumentCopyright copyright = new DocumentCopyright();
        copyright.setCopyrightGuid("987654321");
        copyright.setNewText("Copyright");
        copyrights.add(copyright);

        final List<DocumentCurrency> copyCurrencies =
            new ArrayList<>(Arrays.asList(new DocumentCurrency[currencies.size()]));
        final List<DocumentCopyright> copyCopyrights =
            new ArrayList<>(Arrays.asList(new DocumentCopyright[copyrights.size()]));
        Collections.copy(copyCurrencies, currencies);
        Collections.copy(copyCopyrights, copyrights);

        contentChangeFilter = new XMLContentChangerFilter(copyrights, copyCopyrights, currencies, copyCurrencies, true);
        contentChangeFilter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        contentChangeFilter = null;
    }

    /**
     * Helper method that sets up the repeating pieces of each test and modifies the Table
     * values that are returned along with the input and output.
     *
     * @param inputXML input string for the test.
     * @param expectedResult the expected output for the specified input string.
     */
    public void testHelper(final String inputXML, final String expectedResult) {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;
        try {
            input = new ByteArrayInputStream(inputXML.getBytes());
            output = new ByteArrayOutputStream();

            serializer.setOutputStream(output);

            contentChangeFilter.setContentHandler(serializer.asContentHandler());
            contentChangeFilter.parse(new InputSource(input));

            final String result = output.toString();

            assertEquals(expectedResult, result);
        } catch (final Exception e) {
            fail("Encountered exception during test: " + e.getMessage());
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (final Exception e) {
                fail("Couldn't clean up resources: " + e.getMessage());
            }
        }
    }

    @Test
    public void testCurrencyValueChange() {
        final String xmlTestStr =
            "<message.block><include.currency n-include_guid=\"123456789\">This is a currency</include.currency></message.block>";
        final String expectedResult =
            "<message.block><include.currency n-include_guid=\"123456789\">Currency</include.currency></message.block>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testCopyrightValueChange() {
        final String xmlTestStr =
            "<message.block><include.copyright n-include_guid=\"987654321\">This is a copyright</include.copyright><page no=\"i\"/></message.block>";
        final String expectedResult =
            "<message.block><include.copyright n-include_guid=\"987654321\">Copyright</include.copyright>{pagebreak-open no=\"i\" close-pagebreak}</message.block>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testCopyrightAndCurrencyChange() {
        final String xmlTestStr =
            "<body><message.block><include.copyright n-include_guid=\"987654321\">This is a copyright</include.copyright><page no=\"i\"/></message.block>"
                + "<message.block><include.currency n-include_guid=\"123456789\">This is a currency</include.currency></message.block></body>";
        final String expectedResult =
            "<body><message.block><include.copyright n-include_guid=\"987654321\">Copyright</include.copyright>{pagebreak-open no=\"i\" close-pagebreak}</message.block>"
                + "<message.block><include.currency n-include_guid=\"123456789\">Currency</include.currency></message.block></body>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testNoChange() {
        final String xmlTestStr =
            "<body><message.block><include.something n-include_guid=\"987654321\">This is a copyright</include.something></message.block>"
                + "<message.block><include.else n-include_guid=\"123456789\">This is a currency</include.else></message.block></body>";
        final String expectedResult = xmlTestStr;

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testDoNotProtectPagebreaks() {
        contentChangeFilter.setProtectPagebreaks(false);
        final String xmlTestStr =
            "<message.block><include.copyright n-include_guid=\"987654321\">This is a copyright</include.copyright><page no=\"i\"/></message.block>";
        final String expectedResult =
            "<message.block><include.copyright n-include_guid=\"987654321\">Copyright</include.copyright></message.block>";

        testHelper(xmlTestStr, expectedResult);
    }
}
