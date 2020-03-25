package com.thomsonreuters.uscl.ereader.core.book.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.XmlDeclaration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

@RunWith(MockitoJUnitRunner.class)
public final class PageNumberUtilTest {
    private static final String LABEL_VALUE = "1";
    private static final String TARGET = "pb";
    private static final String CONTENT = "label=\"1\"";
    private static final String PAGEBREAK = "page";
    private static final String PAGEBREAK_PROTECTED = "{pagebreak-open no=\"1\" close-pagebreak}";

    @Mock
    private XMLFilterImpl xmlFilter;

    @Test
    public void shouldAddProcessingInstruction() throws SAXException {
        PageNumberUtil.addPageNumber(xmlFilter, true, LABEL_VALUE);
        verify(xmlFilter).processingInstruction(TARGET, CONTENT);
    }

    @Test
    public void shouldNotAddProcessingInstruction() throws SAXException {
        PageNumberUtil.addPageNumber(xmlFilter, false, LABEL_VALUE);
        verify(xmlFilter, never()).processingInstruction(any(), any());
    }

    @Test
    public void shouldCreatePagebreak() {
        final XmlDeclaration node = (XmlDeclaration) PageNumberUtil.createPagebreak(LABEL_VALUE);
        assertEquals(TARGET, node.name());
        assertEquals(LABEL_VALUE, node.attr(PageNumberUtil.LABEL));
    }

    @Test
    public void shouldDetectPagebreak() {
        final XmlDeclaration node = new XmlDeclaration(PAGEBREAK, false);
        assertTrue(PageNumberUtil.isPagebreak(node));
    }

    @Test
    public void shouldProtectPagebreak() {
        final XmlDeclaration node = new XmlDeclaration(PAGEBREAK, false);
        node.attr(PageNumberUtil.LABEL_NO, LABEL_VALUE);
        assertEquals(PAGEBREAK_PROTECTED, PageNumberUtil.protectPagebreak(node));
    }
}
