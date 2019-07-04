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
public final class FrontMatterResearchAssistancePageFilterTest {
    private FrontMatterResearchAssistancePageFilter researchAssistancePageFilter;
    private BookDefinition bookDefinition;
    private Serializer serializer;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        bookDefinition = new BookDefinition();
        bookDefinition.setProviewDisplayName("Display TEST Title");
        final List<EbookName> ebookNames = new ArrayList<>();
        final EbookName title = new EbookName();
        title.setBookNameText("TEST Title");
        title.setSequenceNum(1);
        ebookNames.add(title);
        final EbookName edition = new EbookName();
        edition.setBookNameText("TEST Edition");
        edition.setSequenceNum(2);
        ebookNames.add(edition);
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

        researchAssistancePageFilter = new FrontMatterResearchAssistancePageFilter(bookDefinition);
        researchAssistancePageFilter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        researchAssistancePageFilter = null;
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

            researchAssistancePageFilter.setContentHandler(serializer.asContentHandler());
            researchAssistancePageFilter.parse(new InputSource(input));

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
    public void testFrontMatterPlaceholder_researchAssistancePageAnchor() {
        bookDefinition.setPrintPageNumbers(false);
        final String xmlTestStr = "<test><frontMatterPlaceholder_researchAssistancePageAnchor/></test>";
        final String expectedResult = "<test><a name=\""
            + FrontMatterFileName.RESEARCH_ASSISTANCE
            + FrontMatterFileName.ANCHOR
            + "\"> </a></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_researchAssistancePageAnchorWithPageNumber() {
        bookDefinition.setPrintPageNumbers(true);
        final String xmlTestStr = "<test><frontMatterPlaceholder_researchAssistancePageAnchor/></test>";
        final String expectedResult = "<test><?pb label=\"iii\"?><a name=\""
            + FrontMatterFileName.RESEARCH_ASSISTANCE
            + FrontMatterFileName.ANCHOR
            + "\"> </a></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_researchAssistanceEmail() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_researchAssistanceEmail/></test>";
        final String expectedResult = "<test><a class=\"additional_info_email\" "
            + "href=\"mailto:west.ebooksuggestions@thomsonreuters.com?subject="
            + "eBook Questions and Suggestions for Display TEST Title\">"
            + "west.ebooksuggestions@thomsonreuters.com</a></test>";

        testHelper(xmlTestStr, expectedResult);
    }
}
