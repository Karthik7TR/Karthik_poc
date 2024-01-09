package com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.addPageNumber;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This filter transforms the WestlawNext Page Template by filling in any template placeholders
 * with the values retrieved from the eBook definition tables in the database.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterWestlawNextPageFilter extends XMLFilterImpl {
    private static final String PAGE_NUMBER = "iv";

    /** Names of all the placeholder tags this filter handles */
    private static final String WESTLAW_NEXT_PAGE_ANCHOR_TAG = "frontMatterPlaceholder_WestlawNextPageAnchor";

    /** Defines the HTML tags that will be created by the filter */
    private static final String HTML_ANCHOR_TAG = "a";

    private static final String HTML_TAG_NAME_ATTRIBUTE = "name";

    private static final String CDATA = "CDATA";

    private boolean withPageNumber;

    public FrontMatterWestlawNextPageFilter(final boolean withPageNumber) {
        this.withPageNumber = withPageNumber;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (qName.equalsIgnoreCase(WESTLAW_NEXT_PAGE_ANCHOR_TAG)) {
            addPageNumber(this, withPageNumber, PAGE_NUMBER);
            final AttributesImpl newAtts = new AttributesImpl();
            newAtts.addAttribute(
                uri,
                HTML_TAG_NAME_ATTRIBUTE,
                HTML_TAG_NAME_ATTRIBUTE,
                CDATA,
                FrontMatterFileName.WESTLAW + FrontMatterFileName.ANCHOR);
            super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
            super.characters(" ".toCharArray(), 0, 1);
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
        if (qName.equalsIgnoreCase(WESTLAW_NEXT_PAGE_ANCHOR_TAG)) {
            super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
        } else {
            super.endElement(uri, localName, qName);
        }
    }
}
