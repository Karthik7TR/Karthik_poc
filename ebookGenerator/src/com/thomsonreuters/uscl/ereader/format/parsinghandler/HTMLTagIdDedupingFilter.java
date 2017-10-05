package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that de-duping the id's which were causing issues while we tried to publish certain book as needed.
 *
 * @author <a href="mailto:Ravi.Nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */
public class HTMLTagIdDedupingFilter extends XMLFilterImpl {
    private Set<String> anchorIdSet = new HashSet<>();
    private List<String> duplicateIdList = new ArrayList<>();
    private Map<String, Integer> duplicateIdCount = new HashMap<>();
    private String docGuid;

    public HTMLTagIdDedupingFilter(final String docGuid) {
        this.docGuid = docGuid;
    }

    /**
     * @return duplicateIdList contains all the duplicate anchor id's.
     */
    public List<String> getDuplicateIdList() {
        return duplicateIdList;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (atts != null && atts.getValue("id") != null) {
            final String idVal = atts.getValue("id");
            int count = 0;
            if (anchorIdSet.contains(idVal)) {
                if (duplicateIdCount.containsKey(idVal)) {
                    count = duplicateIdCount.get(idVal) + 1;
                }
                duplicateIdCount.put(idVal, count);
                final AttributesImpl newAtts = new AttributesImpl(atts);
                final String newIdVal = idVal + "_eBG_" + count;
                final int i = atts.getIndex("id");
                newAtts.setValue(i, newIdVal);
                duplicateIdList.add(docGuid + ":" + qName + ", " + idVal + ", " + newIdVal);
                super.startElement(uri, localName, qName, newAtts);
            } else {
                anchorIdSet.add(idVal);
                super.startElement(uri, localName, qName, atts);
            }
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
        super.endElement(uri, localName, qName);
    }
}
