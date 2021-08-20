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
public final class FrontMatterCopyrightPageFilterTest {
    private FrontMatterCopyrightPageFilter copyrightPageFilter;
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
        bookDefinition.setEbookNames(ebookNames);
        bookDefinition.setCurrency("Currency Test");
        bookDefinition.setCopyright("&#169; 2012 Thomson Reuters \r\n Test Copyright");
        bookDefinition.setCopyrightPageText(
            "Copyright is not claimed as to any part of the original work prepared "
                + "by a United States Government officer or employee as part of that person's official duties."
                + "\r\nTest paragraph 2.");
        bookDefinition.setIsbn("978-0-314-93983-8");
        bookDefinition.setIssn("1111-1111");
        bookDefinition.setAdditionalTrademarkInfo(
            "McKINNEY'S is registerd in the U.S. Patent and Trademark Office.\r\n"
                + "McKINNEY'S NEW YORK CIVIL PRACTICE LAW AND RULES\r\n"
                + "is a trademark of West Publishing Corporation.");

        final List<Author> authors = new ArrayList<>();
        final Author author1 = new Author();
        author1.setAuthorAddlPreText("Originally authored by");
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

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        copyrightPageFilter = null;
    }

    @Test
    public void testFrontMatterPlaceholder_TOCHeadingAnchor() {
        bookDefinition.setPrintPageNumbers(false);
        final String xmlTestStr = "<test><frontMatterPlaceholder_TOCHeadingAnchor/></test>";
        final String expectedResult = "<test><a name=\""
            + FrontMatterFileName.PUBLISHING_INFORMATION
            + FrontMatterFileName.ANCHOR
            + "\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_TOCHeadingAnchorWithPageNumber() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_TOCHeadingAnchor/></test>";
        final String expectedResult = "<test><?pb label=\"ii\"?><a name=\""
            + FrontMatterFileName.PUBLISHING_INFORMATION
            + FrontMatterFileName.ANCHOR
            + "\"/></test>";

        testHelper(xmlTestStr, expectedResult, true);
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

            copyrightPageFilter = new FrontMatterCopyrightPageFilter(bookDefinition, withPagebreaks);
            copyrightPageFilter.setParent(saxParser.getXMLReader());

            serializer.setOutputStream(output);

            copyrightPageFilter.setContentHandler(serializer.asContentHandler());
            copyrightPageFilter.parse(new InputSource(input));

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
    public void testFrontMatterPlaceholder_CopyrightPageAnchor() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_CopyrightPageAnchor/></test>";
        final String expectedResult =
            "<test><a name=\"" + FrontMatterFileName.COPYRIGHT + FrontMatterFileName.ANCHOR + "\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_copyright() {
        final String xmlTestStr = "<test><p><frontMatterPlaceholder_copyright/></p></test>";
        final String expectedResult = "<test><p>&amp;#169; 2012 Thomson Reuters </p><p> Test Copyright</p></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_copyrightPageText() {
        final String xmlTestStr = "<test><p><frontMatterPlaceholder_copyrightPageText/></p></test>";
        final String expectedResult = "<test><p>Copyright is not claimed as to any part of the original work prepared "
            + "by a United States Government officer or employee as part of that person's official duties."
            + "</p><p>Test paragraph 2.</p></test>";

        testHelper(xmlTestStr, expectedResult, true);
    }

    @Test
    public void testFrontMatterPlaceholder_copyrightISBN() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_copyrightISBN/></test>";
        final String expectedResult = "<test>978-0-314-93983-8</test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_copyrightISSN() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_copyrightISSN/></test>";
        final String expectedResult = "<test>ISSN 1111-1111</test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_copyrightTrademark() {
        final String xmlTestStr =
            "<test><p><frontMatterPlaceholder_copyrightTrademarkLine/></p><p>West's and Westlaw are registered in the U.S. Patent and Trademark Office.</p></test>";
        final String expectedResult =
            "<test><p>McKINNEY'S is registerd in the U.S. Patent and Trademark Office.</p><p>McKINNEY'S NEW YORK CIVIL PRACTICE LAW AND RULES</p><p>is a trademark of West Publishing Corporation.</p><p>West's and Westlaw are registered in the U.S. Patent and Trademark Office.</p></test>";

        testHelper(xmlTestStr, expectedResult);
    }
}
