package com.thomsonreuters.uscl.ereader.gather.img.util;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

public class ImageMetadataHandler extends XMLFilterImpl
{
    private static final String DPI = "dpi";
    private static final String HEIGHT = "height";
    private static final String WIDTH = "width";
    private static final String DIM_UNITS = "units";

    @NotNull
    private ImgMetadataInfo imgMetadataInfo = new ImgMetadataInfo();
    private StringBuilder charBuffer;

    @NotNull
    public ImgMetadataInfo getImgMetadataInfo()
    {
        return imgMetadataInfo;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException
    {
        charBuffer = new StringBuilder();
        super.startElement(uri, localName, qName, atts);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException
    {
        try
        {
            String value = null;

            if (charBuffer != null)
            {
                value = StringUtils.trim(charBuffer.toString());
                if (qName.contains(DPI))
                {
                    if (StringUtils.isEmpty(value))
                    {
                        imgMetadataInfo.setDpi(Long.valueOf(0));
                    }
                    else
                    {
                        imgMetadataInfo.setDpi(Long.valueOf(value));
                    }
                }
                else if (qName.contains(HEIGHT))
                {
                    imgMetadataInfo.setHeight(Long.valueOf(value));
                }
                else if (qName.contains(WIDTH))
                {
                    imgMetadataInfo.setWidth(Long.valueOf(value));
                }
                else if (qName.contains(DIM_UNITS))
                {
                    imgMetadataInfo.setDimUnit(value);
                }
            }

            charBuffer = null;
        }
        catch (final Exception e)
        {
            final String message = "Exception occured during Novus IMGMetadata parsing endElement. The error message is: "
                + e.getMessage();
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException
    {
        if (charBuffer != null)
        {
            charBuffer.append(new String(ch, start, length));
        }
    }
}
