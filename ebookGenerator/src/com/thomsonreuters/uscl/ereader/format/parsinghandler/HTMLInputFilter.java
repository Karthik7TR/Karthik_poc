package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various input "<input>" tags and transforms them as needed. Most
 * input tags should be removed but some placeholder tags might be used to inject more
 * relevant data.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLInputFilter extends XMLFilterImpl {
    private boolean keyCitePlaceholder;
    private boolean isInputTag;

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (qName.equalsIgnoreCase("input")) {
            isInputTag = true;
            if (atts != null) {
                final String id = atts.getValue("id");
                final String type = atts.getValue("type");
                if (id != null
                    && id.equalsIgnoreCase("co_keyCiteFlagPlaceHolder")
                    && type != null
                    && type.equalsIgnoreCase("hidden")) {
                    //TODO: Add KeyCite link or display generation based on NPD rules.
                    keyCitePlaceholder = true;
                }
            }
        } else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException {
        if (keyCitePlaceholder) {
            //TODO: Add KeyCite link or display generation based on NPD rules.
        } else if (isInputTag) {
            //Remove anything from within the input tags.
        } else {
            super.characters(buf, offset, len);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (keyCitePlaceholder) {
            keyCitePlaceholder = false;
            isInputTag = false;
            //TODO: Add KeyCite link or display generation based on NPD rules.
        } else if (isInputTag) {
            isInputTag = false;
        } else {
            super.endElement(uri, localName, qName);
        }
    }
}
