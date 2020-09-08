package com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Test various FrontMatterAdditionalFrontMatterPageFilter data scenarios.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public final class FrontMatterAdditionalFrontMatterPageFilterTest {
    private FrontMatterAdditionalFrontMatterPageFilter frontMatterPageFilter;
    private BookDefinition bookDefinition;
    private Serializer serializer;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

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
        bookDefinition.setFullyQualifiedTitleId("uscl/an/test");

        final List<FrontMatterPage> frontMatterPageList = new ArrayList<>();

        //  One PDF with section text
        final FrontMatterPage frontMatterPage4 = new FrontMatterPage();
        frontMatterPage4.setId((long) 40);

        final List<FrontMatterSection> frontMatterSectionsList4 = new ArrayList<>();
        final FrontMatterSection frontMatterSection4 = new FrontMatterSection();
        frontMatterSection4.setId((long) 41);
        frontMatterSection4.setSectionText("Jack Sparrow was here!\r\nElizabeth Swan was too!");
        frontMatterSection4.setSectionHeading("Pirate graffiti");
        frontMatterSection4.setSequenceNum(1);
        // do not set a section heading

        final List<FrontMatterPdf> frontMatterPdfList4 = new ArrayList<>();
        final FrontMatterPdf frontMatterPdfs4 = new FrontMatterPdf();
        frontMatterPdfs4.setId((long) 1);
        frontMatterPdfs4.setPdfFilename("pdfFilename4.pdf");
        frontMatterPdfs4.setPdfLinkText("pdfLinkText4");
        frontMatterPdfs4.setSequenceNum(1);

        frontMatterPdfList4.add(frontMatterPdfs4);

        frontMatterSectionsList4.add(frontMatterSection4);
        frontMatterSection4.setPdfs(frontMatterPdfList4);

        frontMatterPage4.setFrontMatterSections(frontMatterSectionsList4);
        frontMatterPage4.setPageTocLabel("Pirate, Arrrrrrrrrrr Page TOC Label");
        frontMatterPage4.setSequenceNum(2);
        frontMatterPageList.add(frontMatterPage4);

        bookDefinition.setFrontMatterPages(frontMatterPageList);

        frontMatterPageFilter = new FrontMatterAdditionalFrontMatterPageFilter(
            bookDefinition,
            bookDefinition.getFrontMatterPages().get(0).getId(), Collections.emptyMap());
        frontMatterPageFilter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        frontMatterPageFilter = null;
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

            frontMatterPageFilter.setContentHandler(serializer.asContentHandler());
            frontMatterPageFilter.parse(new InputSource(input));

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
    public void testFrontMatterAdditionalFrontMatterAnchor() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_AdditionalPageAnchor/></test>";
        final String expectedResult = "<test><a name=\""
            + FrontMatterFileName.ADDITIONAL_FRONT_MATTER
            + "40"
            + FrontMatterFileName.ANCHOR
            + "\"> </a></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_pageTitleLabel() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_additionFrontMatterTitle/></test>";
        final List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();

        final String expectedResult = "<test>" + fmps.get(0).getPageTocLabel() + "</test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_pageTitleHeader() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_additionFrontMatterTitle/></test>";
        final List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
        final FrontMatterPage frontMatterPage4 = fmps.get(0);
        frontMatterPage4.setPageHeadingLabel("Pirate, Arrrrrrrrrrr Page Heading");

        final String expectedResult = "<test>" + fmps.get(0).getPageHeadingLabel() + "</test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_SectionsWithPDF() {
        final String xmlTestStr = "<frontMatterPlaceholder_sections/>";

        final String expectedResult = "<div class=\"section_heading\">Pirate graffiti</div>"
            + "<div class=\"section_text\"><p>Jack Sparrow was here!</p>"
            + "<p>Elizabeth Swan was too!</p></div>"
            + "<div class=\"section_pdf\"><br/><a class=\"section_pdf_hyperlink\" href=\"er:#pdfFilename4\">pdfLinkText4</a><br/></div>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_SectionsNoPdf() {
        final String xmlTestStr = "<frontMatterPlaceholder_sections/>";

        final String expectedResult = "<div class=\"section_heading\">Pirate graffiti</div>"
            + "<div class=\"section_text\"><p>Jack Sparrow was here!</p>"
            + "<p>Elizabeth Swan was too!</p></div>";

        final List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
        final FrontMatterPage frontMatterPage4 = fmps.get(0);
        for (final FrontMatterSection fms : frontMatterPage4.getFrontMatterSections()) {
            fms.setPdfs(null);
        }
        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_SectionsTwoPdfs() {
        final String xmlTestStr = "<frontMatterPlaceholder_sections/>";

        final String expectedResult = "<div class=\"section_heading\">Pirate graffiti</div>"
            + "<div class=\"section_text\"><p>Jack Sparrow was here!</p>"
            + "<p>Elizabeth Swan was too!</p></div>"
            + "<div class=\"section_pdf\"><br/><a class=\"section_pdf_hyperlink\" href=\"er:#pdfFilename4\">pdfLinkText4</a><br/>"
            + "<br/><a class=\"section_pdf_hyperlink\" href=\"er:#pdfFilename5\">pdfLinkText5</a><br/></div>";

        final List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
        final FrontMatterPage frontMatterPage4 = fmps.get(0);
        for (final FrontMatterSection fms : frontMatterPage4.getFrontMatterSections()) {
            final List<FrontMatterPdf> frontMatterPdfList4 = new ArrayList<>();
            FrontMatterPdf frontMatterPdfs4 = new FrontMatterPdf();
            frontMatterPdfs4.setId((long) 1);
            frontMatterPdfs4.setPdfFilename("pdfFilename4.pdf");
            frontMatterPdfs4.setPdfLinkText("pdfLinkText4");
            frontMatterPdfs4.setSequenceNum(1);
            frontMatterPdfList4.add(frontMatterPdfs4);

            frontMatterPdfs4 = new FrontMatterPdf();
            frontMatterPdfs4.setId((long) 2);
            frontMatterPdfs4.setPdfFilename("pdfFilename5.pdf");
            frontMatterPdfs4.setPdfLinkText("pdfLinkText5");
            frontMatterPdfs4.setSequenceNum(2);
            frontMatterPdfList4.add(frontMatterPdfs4);

            fms.setPdfs(frontMatterPdfList4);
        }

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_NoSectionsOnlyPdf() {
        final String xmlTestStr = "<frontMatterPlaceholder_sections/>";

        final String expectedResult =
            "<div class=\"section_pdf\"><br/><a class=\"section_pdf_hyperlink\" href=\"er:#pdfFilename4\">pdfLinkText4</a><br/></div>";

        final List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
        final FrontMatterPage frontMatterPage4 = fmps.get(0);
        for (final FrontMatterSection fms : frontMatterPage4.getFrontMatterSections()) {
            fms.setSectionText(null);
            fms.setSectionHeading(null);
        }

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_MultipleSectionsMultiReturn() {
        final String xmlTestStr = "<frontMatterPlaceholder_sections/>";

        final String expectedResult = "<div class=\"section_heading\">Pirate graffiti</div>"
            + "<div class=\"section_text\"><p>Jack Sparrow was here!</p>"
            + "<p>Elizabeth Swan was too!</p></div>"
            + "<div class=\"section_pdf\"><br/><a class=\"section_pdf_hyperlink\" href=\"er:#pdfFilename4\">pdfLinkText4</a><br/></div>"
            + "<div class=\"section_heading\">Pirate Stories</div>"
            + "<div class=\"section_text\"><p>BlackBeard's Ghost!</p><p/><p>Scary</p></div>";

        final List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
        final FrontMatterPage frontMatterPage4 = fmps.get(0);
        final List<FrontMatterSection> frontMatterSections4 = frontMatterPage4.getFrontMatterSections();
        final FrontMatterSection frontMatterSectionNew = new FrontMatterSection();
        frontMatterSectionNew.setId((long) 42);
        // add another section and extra newline
        frontMatterSectionNew.setSectionText("BlackBeard's Ghost!\r\n\r\nScary");
        frontMatterSectionNew.setSectionHeading("Pirate Stories");
        frontMatterSectionNew.setSequenceNum(2);

        frontMatterSections4.add(frontMatterSectionNew);
        frontMatterPage4.setFrontMatterSections(frontMatterSections4);

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFrontMatterPlaceholder_AllNulls() {
        final FrontMatterPage page = bookDefinition.getFrontMatterPages().get(0);
        page.setPageTocLabel(null);
        page.getFrontMatterSections().get(0).setSectionHeading(null);
        page.getFrontMatterSections().get(0).setSectionText(null);
        page.getFrontMatterSections().get(0).getPdfs().get(0).setPdfFilename(null);
        page.getFrontMatterSections().get(0).getPdfs().get(0).setPdfLinkText(null);
        final String xmlTestStr = "<test><frontMatterPlaceholder_AdditionalPageAnchor/><frontMatterPlaceholder_additionFrontMatterTitle/><frontMatterPlaceholder_sections/></test>";
        final String expectedResult = "<test><a name=\""
                + FrontMatterFileName.ADDITIONAL_FRONT_MATTER
                + "40"
                + FrontMatterFileName.ANCHOR
                + "\"> </a><div class=\"section_pdf\"><br/><a class=\"section_pdf_hyperlink\" href=\"er:#\"/><br/></div></test>";

        testHelper(xmlTestStr, expectedResult);
    }
}
