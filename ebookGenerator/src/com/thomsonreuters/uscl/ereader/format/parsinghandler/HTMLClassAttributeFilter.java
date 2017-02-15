package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles class attributes on all tags and removes any CSS classes after the first
 * one specified.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLClassAttributeFilter extends XMLFilterImpl
{
    @Override
    public void startElement(final String uri, final String localName, final String qName, Attributes atts)
        throws SAXException
    {
        if (atts != null)
        {
            String classAtt = atts.getValue("class");
            if (classAtt != null && classAtt.contains(" "))
            {
                classAtt = classAtt.substring(0, classAtt.indexOf(" "));

                final AttributesImpl newAtts = new AttributesImpl(atts);
                newAtts.removeAttribute(newAtts.getIndex("class"));
                newAtts.addAttribute("", "", "class", "CDATA", classAtt);

                atts = newAtts;
            }
        }

        super.startElement(uri, localName, qName, atts);
    }
}
