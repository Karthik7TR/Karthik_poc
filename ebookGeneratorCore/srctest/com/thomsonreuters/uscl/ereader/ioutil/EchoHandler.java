package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class EchoHandler extends DefaultHandler
{
    private OutputStream out;

    public EchoHandler(final OutputStream out)
    {
        super();
        this.out = new BufferedOutputStream(out);
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException
    {
        try
        {
            out.write(new String(ch, start, length).getBytes());
            out.flush();
        }
        catch (final Exception e)
        {
            throw new SAXException(e);
        }
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException
    {
        try
        {
            out.write(("</" + qName + ">").getBytes());
            out.flush();
        }
        catch (final Exception e)
        {
            throw new SAXException(e);
        }
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException
    {
        try
        {
            out.write("<".getBytes());
            out.write(localName.getBytes());

            for (int i = 0; i < atts.getLength(); i++)
            {
                if (null != atts.getLocalName(i))
                {
                    if (i != 0)
                    {
                        out.write(" ".getBytes());
                    }

                    out.write(atts.getLocalName(i).getBytes());
                    out.write("=\"".getBytes());
                    out.write(atts.getValue(i).getBytes());
                    out.write("\"".getBytes());
                }
            }

            out.write(">".getBytes());
            out.flush();
        }
        catch (final IOException e)
        {
            throw new SAXException(e);
        }
    }
}
