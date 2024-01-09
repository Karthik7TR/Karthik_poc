package com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.addPageNumber;

import java.util.List;
import java.util.Optional;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.core.service.JsoupServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This filter transforms the Copyright Page Template by filling in any template placeholders
 * with the values retrieved from the eBook definition tables in the database.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
@Slf4j
public class FrontMatterCopyrightPageFilter extends XMLFilterImpl {
    private static final String PAGE_NUMBER = "ii";
    /** Names of all the placeholder tags this filter handles */
    private static final String TOC_HEADING_ANCHOR_TAG = "frontMatterPlaceholder_TOCHeadingAnchor";
    private static final String COPYRIGHT_PAGE_ANCHOR_TAG = "frontMatterPlaceholder_CopyrightPageAnchor";
    private static final String COPYRIGHT_TAG = "frontMatterPlaceholder_copyright";
    private static final String COPYRIGHT_PAGE_TEXT_TAG = "frontMatterPlaceholder_copyrightPageText";
    private static final String ISBN_TAG = "frontMatterPlaceholder_copyrightISBN";
    private static final String ISSN_TAG = "frontMatterPlaceholder_copyrightISSN";
    private static final String COPYRIGHT_TRADEMARK_TAG = "frontMatterPlaceholder_copyrightTrademarkLine";

    private static final String ISSN_TEMPLATE = "ISSN %s";

    /** Defines the HTML tags that will be created by the filter */
    private static final String HTML_PARAGRAPH_TAG = "p";
    private static final String HTML_ANCHOR_TAG = "a";

    private static final String HTML_TAG_NAME_ATTRIBUTE = "name";

    private static final String CDATA = "CDATA";

    private static final boolean MULTI_LINE_FIELD = true;
    private static final boolean SINGLE_LINE_FIELD = false;

    private final BookDefinition bookDefinition;
    private final boolean withPageNumber;
    private final JsoupService jsoupService;

    public FrontMatterCopyrightPageFilter(final BookDefinition bookDefinition, final boolean withPageNumber) {
        this.bookDefinition = bookDefinition;
        this.withPageNumber = withPageNumber;
        this.jsoupService = new JsoupServiceImpl();
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (qName.equalsIgnoreCase(TOC_HEADING_ANCHOR_TAG)) {
            addPageNumber(this, withPageNumber, PAGE_NUMBER);
            final AttributesImpl newAtts = new AttributesImpl();
            newAtts.addAttribute(
                uri,
                HTML_TAG_NAME_ATTRIBUTE,
                HTML_TAG_NAME_ATTRIBUTE,
                CDATA,
                FrontMatterFileName.PUBLISHING_INFORMATION + FrontMatterFileName.ANCHOR);
            super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
        } else if (qName.equalsIgnoreCase(COPYRIGHT_PAGE_ANCHOR_TAG)) {
            final AttributesImpl newAtts = new AttributesImpl();
            newAtts.addAttribute(
                uri,
                HTML_TAG_NAME_ATTRIBUTE,
                HTML_TAG_NAME_ATTRIBUTE,
                CDATA,
                FrontMatterFileName.COPYRIGHT + FrontMatterFileName.ANCHOR);
            super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
        } else if (qName.equalsIgnoreCase(COPYRIGHT_TAG)) {
            printHtmlStyledText(uri, bookDefinition.getCopyright());
        } else if (qName.equalsIgnoreCase(COPYRIGHT_PAGE_TEXT_TAG)) {
            printHtmlStyledText(uri, bookDefinition.getCopyrightPageText());
        } else if (qName.equalsIgnoreCase(ISBN_TAG)) {
            printText(bookDefinition.getIsbn(), SINGLE_LINE_FIELD);
        } else if (qName.equalsIgnoreCase(ISSN_TAG)) {
            if (StringUtils.isNotEmpty(bookDefinition.getIssn())) {
                printText(String.format(ISSN_TEMPLATE, bookDefinition.getIssn()), SINGLE_LINE_FIELD);
            }
        } else if (qName.equalsIgnoreCase(COPYRIGHT_TRADEMARK_TAG)) {
            printText(bookDefinition.getAdditionalTrademarkInfo(), MULTI_LINE_FIELD);
        } else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    private void printHtmlStyledText(final String uri, final String htmlStyledText) {
        Optional.ofNullable(htmlStyledText)
                .ifPresent(text -> {
                    final Document document = jsoupService.parseHtml(text);
                    final List<Node> nodes = document.body().childNodes();
                    printNodes(uri, nodes);
                });
    }

    private void printNodes(final String uri, final List<Node> nodes) {
        nodes.forEach(node -> {
            try {
                if (node instanceof Element) {
                    final String tagName = ((Element) node).tagName();
                    super.startElement(uri, tagName, tagName, new AttributesImpl());
                    printNodes(uri, node.childNodes());
                    super.endElement(uri, tagName, tagName);
                } else if (node instanceof TextNode) {
                    printText(((TextNode) node).getWholeText(), MULTI_LINE_FIELD);
                }
            } catch (final SAXException e) {
                log.error("Error occurred during text printing: ", e);
            }
        });
    }

    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException {
        super.characters(buf, offset, len);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (qName.equalsIgnoreCase(TOC_HEADING_ANCHOR_TAG)) {
            super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
        } else if (qName.equalsIgnoreCase(COPYRIGHT_PAGE_ANCHOR_TAG)) {
            super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
        } else if (qName.equalsIgnoreCase(COPYRIGHT_TAG)
            || qName.equalsIgnoreCase(COPYRIGHT_PAGE_TEXT_TAG)
            || qName.equalsIgnoreCase(ISBN_TAG)
            || qName.equalsIgnoreCase(ISSN_TAG)
            || qName.equalsIgnoreCase(COPYRIGHT_TRADEMARK_TAG)) {
            //Remove the placeholder tag
        } else {
            super.endElement(uri, localName, qName);
        }
    }

    private void printText(final String text, final boolean isMultiLineField) throws SAXException {
        if (text != null) {
            if (isMultiLineField) {
                final String[] lines = text.split("\\\r\\\n");
                for (int i = 0; i < lines.length; i++) {
                    if (i == lines.length - 1) {
                        super.characters(lines[i].toCharArray(), 0, lines[i].length());
                    } else {
                        super.characters(lines[i].toCharArray(), 0, lines[i].length());
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
}
