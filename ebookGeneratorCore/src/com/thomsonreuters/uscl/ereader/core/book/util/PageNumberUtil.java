package com.thomsonreuters.uscl.ereader.core.book.util;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public final class PageNumberUtil {

    private PageNumberUtil() {}

    public static void addPageNumber(final XMLFilterImpl xmlFilter, final BookDefinition bookDefinition, final String pageNumberLabel) throws SAXException {
        if (bookDefinition.isPrintPageNumbers()) {
            xmlFilter.processingInstruction("pb", "label=\"" + pageNumberLabel + "\"");
        }
    }
}
