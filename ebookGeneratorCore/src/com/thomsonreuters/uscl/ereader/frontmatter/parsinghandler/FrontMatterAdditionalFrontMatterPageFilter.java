package com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This filter transforms the Title Page Template by filling in any template placeholders
 * with the values retrieved from the eBook definition tables in the database.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterAdditionalFrontMatterPageFilter extends XMLFilterImpl {
    private static final Logger LOG = LogManager.getLogger(FrontMatterAdditionalFrontMatterPageFilter.class);

    /** Names of all the placeholder tags this filter handles */
    private static final String ADDITIONAL_HEADING_ANCHOR_TAG = "frontMatterPlaceholder_AdditionalPageAnchor";
    private static final String ADDITIONAL_TITLE_PAGE_TAG = "frontMatterPlaceholder_additionFrontMatterTitle";
    private static final String SECTIONS_TAG = "frontMatterPlaceholder_sections";

    /** Defines the HTML tags that will be created by the filter */
    private static final String HTML_DIV_TAG = "div";
    private static final String HTML_PARAGRAPH_TAG = "p";
    private static final String HTML_ANCHOR_TAG = "a";

    private static final String HTML_TAG_NAME_ATTRIBUTE = "name";
    private static final String HTML_TAG_CLASS_ATTRIBUTE = "class";
    private static final String HTML_TAG_HREF_ATTRIBUTE = "href";
    private static final String HTML_TAG_BREAK_ATTRIBUTE = "br";

    private static final String CDATA = "CDATA";

    private static final boolean MULTI_LINE_FIELD = true;
    private static final boolean SINGLE_LINE_FIELD = false;
    private static final String SRC_ATTRIBUTE = "src";
    private static final String IMG_ATTRIBUTE = "img";
    private static final String SECTION_HEADING = "section_heading";
    private static final String SECTION_TEXT = "section_text";
    private static final String SECTION_PDF = "section_pdf";
    private static final String TR_IMAGE = "tr_image";

    private FrontMatterPage frontMatterPage;
    private Long FRONT_MATTER_PAGE_ID;
    private boolean isCwBook;
    private Map<String, List<String>> frontMatterPdfImageNames;

    public FrontMatterAdditionalFrontMatterPageFilter(
        final BookDefinition bookDefinition,
        final Long FRONT_MATTER_PAGE_ID,
        final Map<String, List<String>> frontMatterPdfImageNames)
        throws EBookFrontMatterGenerationException {
        this.FRONT_MATTER_PAGE_ID = FRONT_MATTER_PAGE_ID;
        for (final FrontMatterPage fmp : bookDefinition.getFrontMatterPages()) {
            if (fmp.getId().equals(FRONT_MATTER_PAGE_ID)) {
                frontMatterPage = fmp;
                break;
            }
        }

        if (frontMatterPage == null) {
            final String message = "Could not retrieve additional front matter page with id: " + FRONT_MATTER_PAGE_ID;
            LOG.error(message);
            throw new EBookFrontMatterGenerationException(message);
        }
        isCwBook = bookDefinition.isCwBook();
        this.frontMatterPdfImageNames = frontMatterPdfImageNames;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (qName.equalsIgnoreCase(ADDITIONAL_HEADING_ANCHOR_TAG)) {
            final AttributesImpl newAtts = new AttributesImpl();
            newAtts.addAttribute(
                uri,
                HTML_TAG_NAME_ATTRIBUTE,
                HTML_TAG_NAME_ATTRIBUTE,
                CDATA,
                FrontMatterFileName.ADDITIONAL_FRONT_MATTER + FRONT_MATTER_PAGE_ID + FrontMatterFileName.ANCHOR);
            super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
            printText(" ", SINGLE_LINE_FIELD);
        } else if (qName.equalsIgnoreCase(ADDITIONAL_TITLE_PAGE_TAG)) {
            if (frontMatterPage.getPageHeadingLabel() == null) {
                printText(frontMatterPage.getPageTocLabel(), SINGLE_LINE_FIELD);
            } else {
                printText(frontMatterPage.getPageHeadingLabel(), SINGLE_LINE_FIELD);
            }
        }

        else if (qName.equalsIgnoreCase(SECTIONS_TAG)) {
            createSections();
        } else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException {
        super.characters(buf, offset, len);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (qName.equalsIgnoreCase(ADDITIONAL_HEADING_ANCHOR_TAG)) {
            super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
        } else if (qName.equalsIgnoreCase(ADDITIONAL_TITLE_PAGE_TAG) || qName.equalsIgnoreCase(SECTIONS_TAG)) {
            //Remove the placeholder tag
        } else {
            super.endElement(uri, localName, qName);
        }
    }

    private void printText(final String text, final boolean isMultiLineField) throws SAXException {
        if (text != null) {
            if (isMultiLineField) {
                final String[] lines = text.split("\\\r\\\n", -1);
                for (int i = 0; i < lines.length; i++) {
                    if (i == lines.length - 1) {
                        super.characters(lines[i].toCharArray(), 0, lines[i].length());
                    } else {
                        if (lines[i].trim().length() == 0) {
                            super.characters("&nbsp;".toCharArray(), 0, lines[i].length());
                        } else {
                            super.characters(lines[i].toCharArray(), 0, lines[i].length());
                        }

                        final AttributesImpl atts = new AttributesImpl();
                        super.endElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG);
                        super.startElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG, atts);
                    }
                }
            } else {
                super.characters(text.toCharArray(), 0, text.length());
            }
        }
    }

    private void createSections() throws SAXException {
        for (final FrontMatterSection fms : frontMatterPage.getFrontMatterSections()) {
            AttributesImpl newAtts = new AttributesImpl();
            // Add Optional Section Heading
            if (fms.getSectionHeading() != null) {
                newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, SECTION_HEADING);
                super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
                printText(fms.getSectionHeading(), SINGLE_LINE_FIELD);
                super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG);
            }

            // Add Optional Section Text
            if (fms.getSectionText() != null) {
                newAtts = new AttributesImpl();
                newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, SECTION_TEXT);
                super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
                newAtts = new AttributesImpl();
                super.startElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG, newAtts);
                printText(fms.getSectionText(), MULTI_LINE_FIELD);
                super.endElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG);
                super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG);
            }

            //Add optional PDF Section
            if (fms.getPdfs() != null && fms.getPdfs().size() != 0) {
                newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, SECTION_PDF);
                super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);

                for (final FrontMatterPdf fmp : fms.getPdfs()) {
                    newAtts = new AttributesImpl();
                    super.startElement("", HTML_TAG_BREAK_ATTRIBUTE, HTML_TAG_BREAK_ATTRIBUTE, newAtts);
                    super.endElement("", HTML_TAG_BREAK_ATTRIBUTE, HTML_TAG_BREAK_ATTRIBUTE);

                    newAtts = new AttributesImpl();
                    newAtts.addAttribute(
                        "",
                        HTML_TAG_CLASS_ATTRIBUTE,
                        HTML_TAG_CLASS_ATTRIBUTE,
                        CDATA,
                        "section_pdf_hyperlink");
                    final String pdfFileName = fmp.getPdfFilename();
                    // Add er:# and strip .pdf
                    int startOfPDFExtension = pdfFileName.lastIndexOf(".");
                    if (startOfPDFExtension == -1) {
                        startOfPDFExtension = pdfFileName.length();
                    }
                    final String pdfHREF = "er:#" + pdfFileName.substring(0, startOfPDFExtension);
                    newAtts.addAttribute("", HTML_TAG_HREF_ATTRIBUTE, HTML_TAG_HREF_ATTRIBUTE, CDATA, pdfHREF);
                    super.startElement("", HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
                    printText(fmp.getPdfLinkText(), SINGLE_LINE_FIELD);
                    super.endElement("", HTML_ANCHOR_TAG, HTML_ANCHOR_TAG); // end anchor tag for PDF

                    newAtts = new AttributesImpl();
                    super.startElement("", HTML_TAG_BREAK_ATTRIBUTE, HTML_TAG_BREAK_ATTRIBUTE, newAtts);
                    super.endElement("", HTML_TAG_BREAK_ATTRIBUTE, HTML_TAG_BREAK_ATTRIBUTE);

                    if (isCwBook) {
                        frontMatterPdfImageNames.getOrDefault(pdfFileName, Collections.emptyList()).forEach(this::createImageRef);
                    }
                }

                super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG); /// end section_pdf
            }
        }
    }

    private void createImageRef(final String pdfHREF) {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, "er:#" + pdfHREF);
        atts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, TR_IMAGE);
        try {
            super.startElement("", IMG_ATTRIBUTE, IMG_ATTRIBUTE, atts);
            super.endElement("", IMG_ATTRIBUTE, IMG_ATTRIBUTE);
        } catch (SAXException e) {
            throw new EBookException(e);
        }
    }
}
