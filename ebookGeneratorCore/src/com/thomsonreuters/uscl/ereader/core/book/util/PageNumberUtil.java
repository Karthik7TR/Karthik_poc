package com.thomsonreuters.uscl.ereader.core.book.util;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.XmlDeclaration;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public final class PageNumberUtil {
    public static final String PB = "pb";
    public static final String LABEL = "label";
    public static final String PAGEBREAK = "pagebreak";
    private static final String PAGEBREAK_PROTECTED = "{pagebreak-open label=\"%s\" close-pagebreak}";

    private PageNumberUtil() { }

    public static void addPageNumber(final XMLFilterImpl xmlFilter, final boolean withPageNumber, final String pageNumberLabel) throws SAXException {
        if (withPageNumber) {
            xmlFilter.processingInstruction(PB, LABEL + "=\"" + pageNumberLabel + "\"");
        }
    }

    public static Node createPagebreak(final String label) {
        return new XmlDeclaration(PB, false).attr(LABEL, label);
    }

    public static Node convertToProviewPagebreak(final Node pagebreak) {
        return createPagebreak(pagebreak.attr(LABEL));
    }

    public static boolean isPagebreak(final Node node) {
        return node instanceof XmlDeclaration && PAGEBREAK.equals(((XmlDeclaration) node).name());
    }

    public static String protectPagebreak(final Node pagebreak) {
        return String.format(PAGEBREAK_PROTECTED, pagebreak.attr(LABEL));
    }
}
