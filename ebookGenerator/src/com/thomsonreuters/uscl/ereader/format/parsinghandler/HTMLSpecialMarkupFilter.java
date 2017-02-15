package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various HTML special Markup tags and transforms them as needed.
 *
 * The filter changes the following xml elements
 *
 * Input xml element : <del>Tell the Feds Mike drove you away from the bank.</del>
 * Output xml element: <span class=�co_crosshatch�>Tell the Feds Mike drove you away from the bank.</span>
 *
 * @author <a href="mailto:nirupam.chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLSpecialMarkupFilter extends XMLFilterImpl
{
    private boolean isHighlight;
    private boolean isStrikethrough;
    private boolean isDelBlock;

    protected static final String DEL_TAG = "del";
    protected static final String INS_TAG = "ins";
    protected static final String SPAN_TAG = "span";
    protected static final String INS_STYLE = "background-color: #ccffff; color: #000000; text-decoration: none;";

    public HTMLSpecialMarkupFilter(final boolean isHighlight, final boolean isStrikethrough)
    {
        this.isHighlight = isHighlight;
        this.isStrikethrough = isStrikethrough;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException
    {
        if (qName.equalsIgnoreCase(DEL_TAG))
        {
            isDelBlock = true;
            if (isStrikethrough)
            {
                final AttributesImpl newAtts = new AttributesImpl();
                newAtts.addAttribute("", "", "class", "CDATA", "co_crosshatch");
                super.startElement(uri, localName, SPAN_TAG, newAtts);
            }
        }
        else if (qName.equalsIgnoreCase(INS_TAG))
        {
            if (isHighlight)
            {
                final AttributesImpl newAtts = new AttributesImpl();
                newAtts.addAttribute("", "", "style", "CDATA", INS_STYLE);
                super.startElement(uri, localName, SPAN_TAG, newAtts);
            }
            else
            {
                final AttributesImpl newAtts = new AttributesImpl(atts);
                super.startElement(uri, localName, SPAN_TAG, newAtts);
            }
        }
        else
        {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException
    {
        //only write characters not in del tag or if del tag has been set for strikethrough
        if (!isDelBlock || isStrikethrough)
        {
            super.characters(buf, offset, len);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException
    {
        //Remove any del or ins tags and replace with span tag
        if (qName.equalsIgnoreCase(DEL_TAG))
        {
            isDelBlock = false;
            if (isStrikethrough)
            {
                super.endElement(uri, localName, SPAN_TAG);
            }
        }
        else if (qName.equalsIgnoreCase(INS_TAG))
        {
            super.endElement(uri, localName, SPAN_TAG);
        }
        else
        {
            super.endElement(uri, localName, qName);
        }
    }
}
