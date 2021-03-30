package com.thomsonreuters.uscl.ereader.core.book.util;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.XmlDeclaration;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.ArrayList;
import java.util.List;

public final class PageNumberUtil {
    public static final String PB = "pb";
    public static final String LABEL = "label";
    public static final String LABEL_NO = "no";
    public static final String PROVIEW_PAGEBREAK_PROCESSING_INSTRUCTION = "<?" + PB + " " + LABEL;
    public static final String PROCESSING_INSTRUCTION_CLOSE = "?>";
    public static final String PAGEBREAK = "page";
    public static final String PAGEBREAK_WRAPPER_OPEN = "{pagebreak-open ";
    public static final String PAGEBREAK_WRAPPER_CLOSE = " close-pagebreak}";
    private static final String PAGEBREAK_PROTECTED = "%sno=\"%s\"%s";

    public static final String PAGEBREAK_WRAPPER_OPEN_WITH_LABEL = PAGEBREAK_WRAPPER_OPEN + LABEL_NO;

    private PageNumberUtil() { }

    public static void addPageNumber(final XMLFilterImpl xmlFilter, final boolean withPageNumber, final String pageNumberLabel) throws SAXException {
        if (withPageNumber) {
            xmlFilter.processingInstruction(PB, LABEL + "=\"" + pageNumberLabel + "\"");
        }
    }

    public static Node createPagebreak(final String label) {
        return new XmlDeclaration(PB, false).attr(LABEL, label);
    }

    public static Node createProviewPagebreak(final String label) {
        return new XmlDeclaration(PB, false).attr(LABEL_NO, label);
    }

    public static Node convertToProviewPagebreak(final Element pagebreak) {
        return createPagebreak(getLabelNo(pagebreak));
    }

    public static boolean isPagebreak(final Node node) {
        return PAGEBREAK.equals(node.nodeName());
    }

    public static String protectPagebreak(final Node pagebreak) {
        return String.format(PAGEBREAK_PROTECTED, PAGEBREAK_WRAPPER_OPEN, getLabelNo(pagebreak), PAGEBREAK_WRAPPER_CLOSE);
    }

    public static String getLabel(final Node pagebreak) {
        return pagebreak.attr(LABEL);
    }

    public static String getLabelNo(final Node pagebreak) {
        return pagebreak.attr(LABEL_NO);
    }

    public static List<Element> parents(final Node pagebreak) {
        List<Element> parents = new ArrayList<>();
        if (pagebreak.hasParent()) {
            Element parent = (Element) pagebreak.parent();
            parents.add(parent);
            parents.addAll(parent.parents());
        }
        return parents;
    }
}
