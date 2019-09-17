package com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Test various FrontMatterTitlePageFilter data scenarios.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public final class FrontMatterTitlePageFilterTest {
    private FrontMatterTitlePageFilter titlePageFilter;
    private BookDefinition bookDefinition;
    private Serializer serializer;
    private SAXParser saxParser;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        saxParser = factory.newSAXParser();

        bookDefinition = new BookDefinition();

        final List<EbookName> ebookNames = new ArrayList<>();
        final EbookName title = new EbookName();
        title.setBookNameText("TEST Title");
        title.setSequenceNum(1);
        ebookNames.add(title);

        final EbookName edition = new EbookName();
        edition.setBookNameText("TEST Edition");
        edition.setSequenceNum(2);
        ebookNames.add(edition);

        final EbookName series = new EbookName();
        series.setBookNameText("TEST Series");
        series.setSequenceNum(3);
        ebookNames.add(series);
        bookDefinition.setEbookNames(ebookNames);

        bookDefinition.setCurrency("Currency Test");

        final List<Author> authors = new ArrayList<>();
        final Author author1 = new Author();
        author1.setAuthorFirstName("John");
        author1.setAuthorLastName("Tester1");
        author1.setAuthorAddlText("Pirate, Arrrrrrrrrrr");
        author1.setSequenceNum(1);
        authors.add(author1);

        final Author author2 = new Author();
        author2.setAuthorFirstName("Joel");
        author2.setAuthorLastName("Tester2");
        author2.setAuthorAddlText("Also a \r\nPirate, Arrrrrrrrrrr");
        author2.setSequenceNum(2);
        authors.add(author2);

        final Author author3 = new Author();
        author3.setAuthorFirstName("Ender");
        author3.setAuthorLastName("Tester3");
        author3.setAuthorAddlText("Another \r\nPirate, Arrrrrrrrrrr");
        author3.setSequenceNum(3);
        authors.add(author3);

        bookDefinition.setAuthors(authors);
        bookDefinition.setIsAuthorDisplayVertical(true);
        bookDefinition.setFrontMatterTheme("AAJ Press");

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        titlePageFilter = null;
    }

    /**
     * Helper method that sets up the repeating pieces of each test and modifies the Table
     * values that are returned along with the input and output.
     *
     * @param inputXML input string for the test.
     * @param expectedResult the expected output for the specified input string.
     */
    private void testHelper(final String inputXML, final String expectedResult) {
        testHelper(inputXML, expectedResult, false);
    }

    private void testHelper(final String inputXML, final String expectedResult, final boolean withPagebreaks) {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;
        try {
            input = new ByteArrayInputStream(inputXML.getBytes());
            output = new ByteArrayOutputStream();

            titlePageFilter = new FrontMatterTitlePageFilter(bookDefinition, withPagebreaks);
            titlePageFilter.setParent(saxParser.getXMLReader());

            serializer.setOutputStream(output);

            titlePageFilter.setContentHandler(serializer.asContentHandler());
            titlePageFilter.parse(new InputSource(input));

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
    public void testFrontMatterPlaceholder_theme() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_theme/></test>";
        final String expectedResult =
            "<test><div class=\"logo\"><img src=\"er:#AAJ_PRESS\" alt=\"AAJ Press logo\"/></div></test>";

        testHelper(xmlTestStr, expectedResult, true);
    }

    @Test
    public void testFrontMatterPlaceholder_themeNone() {
        bookDefinition.setFrontMatterTheme("west");

        final String xmlTestStr = "<test><frontMatterPlaceholder_theme/></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_TitlePageAnchor() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_TitlePageAnchor/></test>";
        final String expectedResult =
            "<test><a name=\"" + FrontMatterFileName.FRONT_MATTER_TITLE + FrontMatterFileName.ANCHOR + "\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_TitlePageAnchorWithPagebreaks() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_TitlePageAnchor/></test>";
        final String expectedResult =
            "<test><?pb label=\"i\"?><a name=\"" + FrontMatterFileName.FRONT_MATTER_TITLE + FrontMatterFileName.ANCHOR + "\"/></test>";

        testHelper(xmlTestStr, expectedResult, true);
    }

    @Test
    public void testFrontMatterPlaceholder_bookname() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_bookname/></test>";
        final String expectedResult = "<test>TEST Title</test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_bookname2() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_bookname2/></test>";
        final String expectedResult = "<test>TEST Edition</test>";

        testHelper(xmlTestStr, expectedResult, true);
    }

    @Test
    public void testFrontMatterPalceholder_bookname3() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_bookname3/></test>";
        final String expectedResult = "<test>TEST Series</test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_currency() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_currency/></test>";
        final String expectedResult = "<test>Currency Test</test>";

        testHelper(xmlTestStr, expectedResult, true);
    }

    @Test
    public void testFrontMatterPlaceholder_authorsVertical() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_authors/></test>";
        final String expectedResult = "<test><div class=\"author1\">John Tester1</div><div class=\"authorInfo\">"
            + "<p>Pirate, Arrrrrrrrrrr</p></div><div class=\"authorNext\">Joel Tester2</div>"
            + "<div class=\"authorInfo\"><p>Also a </p><p>Pirate, Arrrrrrrrrrr</p></div>"
            + "<div class=\"authorNext\">Ender Tester3</div><div class=\"authorInfo\"><p>Another </p><p>Pirate, Arrrrrrrrrrr</p></div></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_authorsHorizontal() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        bookDefinition.setIsAuthorDisplayVertical(false);

        titlePageFilter = new FrontMatterTitlePageFilter(bookDefinition, false);
        titlePageFilter.setParent(saxParser.getXMLReader());

        final String xmlTestStr = "<test><frontMatterPlaceholder_authors/></test>";
        final String expectedResult =
            "<test><div class=\"author1\">John Tester1, Joel Tester2 and Ender Tester3</div></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testOptionalSecondTitleLine() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        final List<EbookName> ebookNames = new ArrayList<>();
        final EbookName title = new EbookName();
        title.setBookNameText("TEST Title");
        title.setSequenceNum(1);
        ebookNames.add(title);

        final EbookName series = new EbookName();
        series.setBookNameText("TEST Series");
        series.setSequenceNum(3);
        ebookNames.add(series);
        bookDefinition.setEbookNames(ebookNames);

        titlePageFilter = new FrontMatterTitlePageFilter(bookDefinition, false);
        titlePageFilter.setParent(saxParser.getXMLReader());

        final String xmlTestStr = "<test><bookname1><frontMatterPlaceholder_bookname/></bookname1>"
            + "<bookname2><frontMatterPlaceholder_bookname2/></bookname2>"
            + "<bookname3><frontMatterPlaceholder_bookname3/></bookname3></test>";
        final String expectedResult =
            "<test><bookname1>TEST Title</bookname1><bookname2/><bookname3>TEST Series</bookname3></test>";

        testHelper(xmlTestStr, expectedResult);
    }
}
