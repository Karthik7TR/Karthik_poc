package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.TitleXMLTOCFilter;
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

/**
 * Updates all the anchor references to include proper document prefixes.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class TitleMetadataAnchorUpdateServiceImpl implements TitleMetadataAnchorUpdateService
{
    private static final Logger LOG = LogManager.getLogger(TitleMetadataAnchorUpdateServiceImpl.class);

    /**
     * Update all the anchor references to match the the format docFamGuid/anchorName.
     *
     * @param srcTitleXML the source title.xml file to be updated.
     * @param trgTitleXML location where the updated file should be generated to.
     * @param docToToc the file that contains mappings of DOC to TOC Guids
     */
    @Override
    public void updateAnchors(final File srcTitleXML, final File trgTitleXML, final File docToToc)
        throws EBookFormatException
    {
        if (srcTitleXML == null || !srcTitleXML.exists())
        {
            throw new IllegalArgumentException("srcTitleXML must be a valid file.");
        }

        FileInputStream inStream = null;
        FileOutputStream outStream = null;

        try
        {
            LOG.debug("Transforming anchor references from Title.xml file: " + srcTitleXML.getAbsolutePath());

            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            final SAXParser saxParser = factory.newSAXParser();

            final TitleXMLTOCFilter tocFilter = new TitleXMLTOCFilter(docToToc);
            tocFilter.setParent(saxParser.getXMLReader());

            final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
            props.setProperty("omit-xml-declaration", "yes");

            final Serializer serializer = SerializerFactory.getSerializer(props);
            outStream = new FileOutputStream(trgTitleXML);
            serializer.setOutputStream(new EntityDecodedOutputStream(outStream));

            tocFilter.setContentHandler(serializer.asContentHandler());

            inStream = new FileInputStream(srcTitleXML);

            tocFilter.parse(new InputSource(new EntityEncodedInputStream(inStream)));
        }
        catch (final IOException e)
        {
            final String errMessage = "Unable to perform IO operations on Title.xml file: "
                + srcTitleXML.getAbsolutePath()
                + " or "
                + trgTitleXML.getAbsolutePath();
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
        catch (final SAXException e)
        {
            final String errMessage = "Encountered a SAX Exception while processing: " + srcTitleXML.getAbsolutePath();
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
        catch (final ParserConfigurationException e)
        {
            final String errMessage =
                "Encountered a SAX Parser Configuration Exception while processing: " + srcTitleXML.getAbsolutePath();
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
        finally
        {
            try
            {
                if (inStream != null)
                {
                    inStream.close();
                }
                if (outStream != null)
                {
                    outStream.close();
                }
            }
            catch (final IOException e)
            {
                LOG.error(
                    "Unable to close files related to the " + srcTitleXML.getAbsolutePath() + " file transformation.",
                    e);
            }
        }

        LOG.debug("Anchors in " + trgTitleXML.getAbsolutePath() + " were successfully transformed.");
    }
}
