package com.thomsonreuters.uscl.ereader.core.book.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
        assertEquals(node.name(), TARGET);
        assertEquals(node.attr(PageNumberUtil.LABEL), LABEL_VALUE);
    }
}
