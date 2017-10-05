package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various Anchor "<img>" tags and transforms them as needed.
 *
 * Please reference com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLImageFilterTest for
 * detailed test scenarios that this filter covers.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLImageFilter extends XMLFilterImpl {
    private Set<String> staticImageRefs;
    private boolean isNonStaticImage;
    private boolean isPDFIcon;

    public Set<String> getStaticImageRefs() {
        return staticImageRefs;
    }

    public void setStaticImageRefs(final Set<String> staticImageRefs) {
        this.staticImageRefs = staticImageRefs;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (qName.equalsIgnoreCase("img")) {
            if (atts != null) {
                String src = atts.getValue("src");
                if (atts.getValue("alt") != null && atts.getValue("alt").equalsIgnoreCase("PDF")) {
                    //remove PDF icon images
                    isPDFIcon = true;
                } else if (src != null && src.startsWith("/images/")) {
                    staticImageRefs.add(src.substring(8));
                    src = src.replace(src.substring(0, src.lastIndexOf("/") + 1), "er:#");
                    src = src.substring(0, src.indexOf("."));

                    final AttributesImpl newAtts = new AttributesImpl(atts);
                    newAtts.removeAttribute(newAtts.getIndex("src"));
                    newAtts.addAttribute("", "", "src", "CDATA", src);

                    super.startElement(uri, localName, qName, newAtts);
                } else if (src != null && src.contains("/Link/Document/Blob")) {
                    //remove actual images since the link anchors will be transformed into image tags
                    isNonStaticImage = true;
                } else {
                    throw new SAXException(
                        "Could not retrieve src attribute for img tag. " + "Code should be added to handle these.");
                }
            } else {
                throw new SAXException("Could not retrieve attributes for an image tag.");
            }
        } else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException {
        //Remove any non static image tags and pdf icons
        if (!isNonStaticImage && !isPDFIcon) {
            super.characters(buf, offset, len);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        //Remove any non static image tags
        if (!isNonStaticImage && !isPDFIcon) {
            super.endElement(uri, localName, qName);
        } else if (isNonStaticImage) {
            isNonStaticImage = false;
        } else if (isPDFIcon) {
            isPDFIcon = false;
        }
    }
}
