package com.thomsonreuters.uscl.ereader.format.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.format.parsinghandler.SplitBookTocFilter;
import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.ioutil.EntityDecodedOutputStream;
import com.thomsonreuters.uscl.ereader.ioutil.EntityEncodedInputStream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SplitBookTocParseServiceImpl implements SplitBookTocParseService
{
    private static final Logger LOG = LogManager.getLogger(SplitBookTocParseServiceImpl.class);

    @Override
    public Map<String, DocumentInfo> generateSplitBookToc(
        final InputStream tocXml,
        final OutputStream splitTocXml,
        final List<String> splitTocGuidList,
        final String splitTitleId)
    {
        try
        {
            // Obtain a new instance of a SAXParserFactory.
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            final SAXParser saxParser = factory.newSAXParser();
            final XMLReader xmlReader = saxParser.getXMLReader();
            final SplitBookTocFilter splitBookTocFilter = new SplitBookTocFilter();
            splitBookTocFilter.setParent(xmlReader);
            final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
            final Serializer serializer = SerializerFactory.getSerializer(props);
            serializer.setOutputStream(new EntityDecodedOutputStream(splitTocXml, true));

            splitBookTocFilter.setContentHandler(serializer.asContentHandler());
            splitBookTocFilter.setSplitTocGuidList(splitTocGuidList);
            splitBookTocFilter.setSplitTilteId(splitTitleId);
            splitBookTocFilter.parse(new InputSource(new EntityEncodedInputStream(tocXml)));

            if (splitBookTocFilter.getWrongSplitTocNode().size() > 0)
            {
                final StringBuffer eMessage = new StringBuffer("Split occured at an incorrect level. ");
                int i = 1;
                for (final String tocGuid : splitBookTocFilter.getWrongSplitTocNode())
                {
                    if (i == splitBookTocFilter.getWrongSplitTocNode().size())
                    {
                        eMessage.append(tocGuid);
                    }
                    else
                    {
                        eMessage.append(tocGuid + ", ");
                    }
                    i++;
                }
                throw new RuntimeException(eMessage.toString());
            }

            return splitBookTocFilter.getDocumentInfoMap();
        }

        catch (final SAXException e)
        {
            throw new RuntimeException("SAXException occurred while generating splitEbook toc file.", e);
        }
        catch (final ParserConfigurationException e)
        {
            throw new RuntimeException("Failed to configure SAX Parser when generating splitEbook toc file.", e);
        }
        catch (final IOException e)
        {
            throw new RuntimeException("An IOException occurred while generating the splitEbook toc file.", e);
        }
    }
}
