package com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This filter transforms the Research Assistance Page Template by filling in any template placeholders
 * with the values retrieved from the eBook definition tables in the database.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterResearchAssistancePageFilter extends XMLFilterImpl {
    /** Names of all the placeholder tags this filter handles */
    private static final String RESEARCH_ASSISTANCE_PAGE_ANCHOR_TAG =
        "frontMatterPlaceholder_researchAssistancePageAnchor";
    private static final String EMAIL_TAG = "frontMatterPlaceholder_researchAssistanceEmail";

    /** Defines the HTML tags that will be created by the filter */
    private static final String HTML_ANCHOR_TAG = "a";

    private static final String HTML_TAG_NAME_ATTRIBUTE = "name";
    private static final String HTML_TAG_CLASS_ATTRIBUTE = "class";
    private static final String HTML_TAG_HREF_ATTRIBUTE = "href";

    private static final String CDATA = "CDATA";

    private BookDefinition bookDefinition;

    public FrontMatterResearchAssistancePageFilter(final BookDefinition bookDefinition) {
        this.bookDefinition = bookDefinition;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (qName.equalsIgnoreCase(RESEARCH_ASSISTANCE_PAGE_ANCHOR_TAG)) {
            final AttributesImpl newAtts = new AttributesImpl();
            newAtts.addAttribute(
                uri,
                HTML_TAG_NAME_ATTRIBUTE,
                HTML_TAG_NAME_ATTRIBUTE,
                CDATA,
                FrontMatterFileName.RESEARCH_ASSISTANCE + FrontMatterFileName.ANCHOR);
            super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
            printText(" ");
        } else if (qName.equalsIgnoreCase(EMAIL_TAG)) {
            //<a class="additional_info_hyperlink" href="http://west.thomson.com">west.thomson.com</a>
            final AttributesImpl newAtts = new AttributesImpl();
            newAtts
                .addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "additional_info_email");
            newAtts.addAttribute(
                "",
                HTML_TAG_HREF_ATTRIBUTE,
                HTML_TAG_HREF_ATTRIBUTE,
                CDATA,
                "mailto:west.ebooksuggestions@thomsonreuters.com?subject="
                    + "eBook Questions and Suggestions for "
                    + bookDefinition.getProviewDisplayName());
            super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
            printText("west.ebooksuggestions@thomsonreuters.com");
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
        if (qName.equalsIgnoreCase(RESEARCH_ASSISTANCE_PAGE_ANCHOR_TAG)) {
            super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
        } else if (qName.equalsIgnoreCase(EMAIL_TAG)) {
            super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
        } else {
            super.endElement(uri, localName, qName);
        }
    }

    private void printText(final String text) throws SAXException {
        if (text != null) {
            super.characters(text.toCharArray(), 0, text.length());
        }
    }
}
