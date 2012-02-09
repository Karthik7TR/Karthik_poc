/*
* EchoHandler
* 
* Created on: Nov 2, 2010 By: u0009398
* 
* Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved.
*
* Proprietary and Confidential information of TRGR. 
* Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.
*/
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

    public EchoHandler(OutputStream out) throws IOException
    {
        super();
        this.out = new BufferedOutputStream(out);
    }

    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        try
        {
            out.write(new String(ch, start, length).getBytes());
            out.flush();
        }
        catch (Exception e)
        {
            throw new SAXException(e);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException
    {
        try
        {
            out.write(("</" + qName + ">").getBytes());
            out.flush();
        }
        catch (Exception e)
        {
            throw new SAXException(e);
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
        throws SAXException
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
        catch (IOException e)
        {
            throw new SAXException(e);
        }
    }
}