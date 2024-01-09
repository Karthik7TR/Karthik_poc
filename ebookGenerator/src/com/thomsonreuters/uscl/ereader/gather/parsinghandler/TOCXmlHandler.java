package com.thomsonreuters.uscl.ereader.gather.parsinghandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class defines the handler that parses the Toc xml to generate a list of
 * GUIDs.
 *
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam
 *         Chatterjee</a> u0072938
 */
public class TOCXmlHandler extends DefaultHandler {
    private Map<String, List<String>> docGuidList = new HashMap<>();
    private List<String> tocGuidList = new ArrayList<>();
    private StringBuffer tempVal;

    private static final String DOCUMENT_GUID_ELEMENT = "DocumentGuid";
    private static final String TOC_GUID_ELEMENT = "Guid";

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXParseException {
        if (qName.equalsIgnoreCase(DOCUMENT_GUID_ELEMENT) || qName.equalsIgnoreCase(TOC_GUID_ELEMENT)) {
            tempVal = new StringBuffer();
        } else {
            tempVal = null;
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (tempVal != null) {
            for (int i = start; i < start + length; i++) {
                tempVal.append(ch[i]);
            }
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (qName.equalsIgnoreCase(DOCUMENT_GUID_ELEMENT)) {
            // add only if there is a guid available
            if (tempVal.length() != 0) {
                // check to see if there is already a toc guid list list for
                // this doc uuid
                final String docGuid = tempVal.toString();
                if (!docGuidList.containsKey(docGuid)) {
                    docGuidList.put(docGuid, tocGuidList);
                } else { // append to the list of exisitng toc guids
                    final List<String> existingTocGuidList = docGuidList.get(docGuid);
                    for (int i = 0; i < existingTocGuidList.size(); i++) {
                        tocGuidList.add(existingTocGuidList.get(i));
                    }
                    docGuidList.remove(docGuid);
                    docGuidList.put(docGuid, tocGuidList);
                }
                tocGuidList = new ArrayList<>();
            }
        }

        if (qName.equalsIgnoreCase(TOC_GUID_ELEMENT)) {
            if (tempVal.length() != 0) {
                // add only if there is a guid available
                tocGuidList.add(tempVal.toString());
            }
        }
    }

    public List<String> getTocGuidList() {
        return tocGuidList;
    }

    public void setTocGuidList(final List<String> tocGuidList) {
        this.tocGuidList = tocGuidList;
    }

    public Map<String, List<String>> getDocGuidList() {
        return docGuidList;
    }

    public void setDocGuidList(final Map<String, List<String>> docGuidList) {
        this.docGuidList = docGuidList;
    }
}
