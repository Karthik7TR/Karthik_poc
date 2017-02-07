package com.thomsonreuters.uscl.ereader.gather.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.thomsonreuters.uscl.ereader.gather.exception.NortLabelParseException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;

public class NortLabelParser
{
    private static final String HEADING_ELEMENT_NAME = "heading";
    private static final String START_WRAPPER_TAG = "<labels>";
    private static final String END_WRAPPER_TAG = "</labels>";

    private boolean inHeadingElement;
    private XMLInputFactory factory;

    public NortLabelParser()
    {
        factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
    }

    public String parse(final String text) throws NortLabelParseException
    {
        final StringBuffer buffer = new StringBuffer();
        XMLEventReader r = null;

        try (InputStream startInput = new ByteArrayInputStream(START_WRAPPER_TAG.getBytes("UTF-8"));
            InputStream textInput = new ByteArrayInputStream(text.getBytes("UTF-8"));
            InputStream intermediateStream = new SequenceInputStream(startInput, textInput);
            InputStream endInput = new ByteArrayInputStream(END_WRAPPER_TAG.getBytes("UTF-8"));
            InputStream input = new SequenceInputStream(intermediateStream, endInput);)
        {
            r = factory.createXMLEventReader(input, "UTF-8");

            int level = 0;
            while (r.hasNext())
            {
                final XMLEvent event = r.nextEvent();

                if (event.isStartElement())
                {
                    final StartElement element = event.asStartElement();
                    if (element.getName().getLocalPart().equalsIgnoreCase(HEADING_ELEMENT_NAME))
                    {
                        inHeadingElement = true;
                    }
                    else
                    {
                        level++;
                    }
                }

                if (event.isCharacters())
                {
                    final Characters character = event.asCharacters();
                    if (inHeadingElement)
                    {
                        buffer.append(character.getData());
                    }
                    else if (level == 1)
                    {
                        buffer.append(character.getData());
                    }
                }

                if (event.isEndElement())
                {
                    final EndElement element = event.asEndElement();
                    if (element.getName().getLocalPart().equalsIgnoreCase(HEADING_ELEMENT_NAME))
                    {
                        inHeadingElement = false;
                    }
                    else
                    {
                        level--;
                    }
                }
            }
        }
        catch (final Exception e)
        {
            throw new NortLabelParseException("Error while processing label: " + text, e);
        }
        finally
        {
            try
            {
                if (r != null)
                {
                    r.close();
                }
            }
            catch (final XMLStreamException e)
            {
                throw new NortLabelParseException("Failed to close XMLEventReader.", e);
            }
        }

        final CharSequenceTranslator escapeXml =
            StringEscapeUtils.ESCAPE_XML10.with(NumericEntityEscaper.between(0x7f, Integer.MAX_VALUE));
        return escapeXml.translate(buffer);
    }
}
