package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles removal of the Editors' Notes heading added by WLN style sheets
 *
 *
 * @author <a href="mailto:dong.kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class HTMLEditorNotesFilter extends XMLFilterImpl {
    private boolean delEditorNodeHeading;
    private boolean inEditorNotesBlock;
    private int level;

    protected static final String DIV_TAG = "div";

    public HTMLEditorNotesFilter(final boolean delEditorNodeHeading) {
        this.delEditorNodeHeading = delEditorNodeHeading;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        final String eBookEditorNoteAttribute = atts.getValue("eBookEditorNotes");
        if (inEditorNotesBlock) {
            // Do not process elements
            level++;
        } else if (delEditorNodeHeading
            && StringUtils.isNotBlank(eBookEditorNoteAttribute)
            && eBookEditorNoteAttribute.equals("true")) {
            // Do not process elements
            inEditorNotesBlock = true;
        } else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException {
        //Only remove characters in Editors' Notes block
        if (!inEditorNotesBlock) {
            super.characters(buf, offset, len);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (delEditorNodeHeading && inEditorNotesBlock && level == 0 && qName.equalsIgnoreCase(DIV_TAG)) {
            // Do not process elements
            inEditorNotesBlock = false;
        } else if (inEditorNotesBlock) {
            // Do not process elements
            level--;
        } else {
            super.endElement(uri, localName, qName);
        }
    }
}
