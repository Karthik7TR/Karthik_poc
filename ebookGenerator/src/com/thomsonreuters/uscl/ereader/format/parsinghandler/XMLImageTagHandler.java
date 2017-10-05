package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class defines the handler that parses the XML image.link tags to generate a list of GUIDs.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class XMLImageTagHandler extends DefaultHandler {
    private List<String> guidList;

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXParseException {
        if (qName.equalsIgnoreCase("image.link")) {
            final String targetGuid = atts.getValue("target");
            final String tuuid = atts.getValue("tuuid");

            // TODO: If it is determined order does not matter, change type to HashSet
            if (StringUtils.isNotBlank(targetGuid)) {
                if (!guidList.contains(targetGuid)) {
                    guidList.add(targetGuid);
                }
                // 8/9/2013 - different document pathway puts image guid in tuuid attribute
            } else if (StringUtils.isNotBlank(tuuid)) {
                if (!guidList.contains(tuuid)) {
                    guidList.add(tuuid);
                }
                // Last condition adds one blank into guid list.  This will happen if an image.link element
                // does not have a guid in either target or tuuid attribute.  This will cause the step to fail
                // and have developer look into image.link element to see if a new attribute is used to hold
                // the image guid.
            } else if (!guidList.contains(targetGuid)) {
                guidList.add(targetGuid);
            }
        }
    }

    public void setGuidList(final List<String> aList) {
        guidList = aList;
    }

    public List<String> getGuidList() {
        return guidList;
    }
}
