package com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This filter transforms the Title Page Template by filling in any template placeholders
 * with the values retrieved from the eBook definition tables in the database.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterTitlePageFilter extends XMLFilterImpl {
    /** Names of all the placeholder tags this filter handles */
    private static final String TITLE_PAGE_ANCHOR_TAG = "frontMatterPlaceholder_TitlePageAnchor";
    private static final String BOOK_NAME_TAG = "frontMatterPlaceholder_bookname";
    private static final String BOOK_NAME2_TAG = "frontMatterPlaceholder_bookname2";
    private static final String BOOK_NAME3_TAG = "frontMatterPlaceholder_bookname3";
    private static final String CURRENCY_TAG = "frontMatterPlaceholder_currency";
    private static final String AUTHORS_TAG = "frontMatterPlaceholder_authors";
    private static final String THEME_TAG = "frontMatterPlaceholder_theme";

    /** Defines the HTML tags that will be created by the filter */
    private static final String HTML_DIV_TAG = "div";
    private static final String HTML_PARAGRAPH_TAG = "p";
    private static final String HTML_ANCHOR_TAG = "a";
    private static final String HTML_IMG_TAG = "img";

    private static final String HTML_TAG_NAME_ATTRIBUTE = "name";
    private static final String HTML_TAG_CLASS_ATTRIBUTE = "class";
    private static final String HTML_TAG_SRC_ATTRIBUTE = "src";
    private static final String HTML_TAG_ALT_ATTRIBUTE = "alt";

    private static final String CDATA = "CDATA";

    private static final boolean MULTI_LINE_FIELD = true;
    private static final boolean SINGLE_LINE_FIELD = false;
    public static final String AAJ_PRESS_THEME = "AAJ Press";

    private BookDefinition bookDefinition;

    public FrontMatterTitlePageFilter(final BookDefinition bookDefinition) {
        this.bookDefinition = bookDefinition;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (qName.equalsIgnoreCase(TITLE_PAGE_ANCHOR_TAG)) {
            final AttributesImpl newAtts = new AttributesImpl();
            newAtts.addAttribute(
                uri,
                HTML_TAG_NAME_ATTRIBUTE,
                HTML_TAG_NAME_ATTRIBUTE,
                CDATA,
                FrontMatterFileName.FRONT_MATTER_TITLE + FrontMatterFileName.ANCHOR);
            super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
        } else if (qName.equalsIgnoreCase(BOOK_NAME_TAG)) {
            for (final EbookName name : bookDefinition.getEbookNames()) {
                if (name.getSequenceNum() == 1) {
                    printText(name.getBookNameText(), MULTI_LINE_FIELD);
                    break;
                }
            }
        } else if (qName.equalsIgnoreCase(BOOK_NAME2_TAG)) {
            for (final EbookName name : bookDefinition.getEbookNames()) {
                if (name.getSequenceNum() == 2) {
                    printText(name.getBookNameText(), MULTI_LINE_FIELD);
                    break;
                }
            }
        } else if (qName.equalsIgnoreCase(BOOK_NAME3_TAG)) {
            for (final EbookName name : bookDefinition.getEbookNames()) {
                if (name.getSequenceNum() == 3) {
                    printText(name.getBookNameText(), MULTI_LINE_FIELD);
                    break;
                }
            }
        } else if (qName.equalsIgnoreCase(CURRENCY_TAG)) {
            printText(bookDefinition.getCurrency(), MULTI_LINE_FIELD);
        } else if (qName.equalsIgnoreCase(AUTHORS_TAG)) {
            createAuthorSection();
        } else if (qName.equalsIgnoreCase(THEME_TAG)) {
            createFrontMatterThemeSection();
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
        if (qName.equalsIgnoreCase(TITLE_PAGE_ANCHOR_TAG)) {
            super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
        } else if (qName.equalsIgnoreCase(BOOK_NAME_TAG)
            || qName.equalsIgnoreCase(BOOK_NAME2_TAG)
            || qName.equalsIgnoreCase(BOOK_NAME3_TAG)
            || qName.equalsIgnoreCase(CURRENCY_TAG)
            || qName.equalsIgnoreCase(AUTHORS_TAG)
            || qName.equalsIgnoreCase(THEME_TAG)) {
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
                    final String line = lines[i].trim().isEmpty() ? StringUtils.EMPTY : lines[i];
                    super.characters(line.toCharArray(), 0, line.length());
                    if (i != lines.length - 1) {
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

    private void createFrontMatterThemeSection() throws SAXException {
        final String template = bookDefinition.getFrontMatterTheme();

        if (template.equalsIgnoreCase(AAJ_PRESS_THEME)) {
            AttributesImpl newAtts = new AttributesImpl();
            newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "logo");

            super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
            newAtts = new AttributesImpl();
            newAtts.addAttribute("", HTML_TAG_SRC_ATTRIBUTE, HTML_TAG_SRC_ATTRIBUTE, CDATA, "er:#AAJ_PRESS");
            newAtts.addAttribute("", HTML_TAG_ALT_ATTRIBUTE, HTML_TAG_ALT_ATTRIBUTE, CDATA, "AAJ Press logo");
            super.startElement("", HTML_IMG_TAG, HTML_IMG_TAG, newAtts);
            super.endElement("", HTML_IMG_TAG, HTML_IMG_TAG);
            super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG);
        }
    }

    private void createAuthorSection() throws SAXException {
        boolean firstAuthor = true;
        final boolean isAuthorDisplayVertical = bookDefinition.isAuthorDisplayVertical();

        if (isAuthorDisplayVertical) {
            for (final Author author : bookDefinition.getAuthors()) {
                AttributesImpl newAtts = new AttributesImpl();
                if (firstAuthor) {
                    newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "author1");
                    firstAuthor = false;
                } else {
                    newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "authorNext");
                }
                super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
                printText(author.getFullName(), SINGLE_LINE_FIELD);
                super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG);
                newAtts = new AttributesImpl();
                newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "authorInfo");
                super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
                newAtts = new AttributesImpl();
                super.startElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG, newAtts);
                printText(author.getAuthorAddlText(), MULTI_LINE_FIELD);
                newAtts = new AttributesImpl();
                super.endElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG);
                super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG);
            }
        } else {
            final AttributesImpl newAtts = new AttributesImpl();
            newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "author1");
            super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
            int authorNum = 0;
            for (final Author author : bookDefinition.getAuthors()) {
                authorNum++;
                printText(author.getFullName(), SINGLE_LINE_FIELD);
                if (authorNum == bookDefinition.getAuthors().size() - 1) {
                    printText(" and ", SINGLE_LINE_FIELD);
                } else if (authorNum != bookDefinition.getAuthors().size()) {
                    printText(", ", SINGLE_LINE_FIELD);
                }
            }
            super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG);
        }
    }
}
