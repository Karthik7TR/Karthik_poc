package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Parses through the Title.xml file and modifies the "s" attribute of each toc entry
 * to the correct format.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class TitleXMLTOCFilter extends XMLFilterImpl
{
    private static final Logger LOG = LogManager.getLogger(TitleXMLTOCFilter.class);
    private Map<String, String> anchors;

    public TitleXMLTOCFilter(final File anchorMap)
        throws EBookFormatException
    {
        if (anchorMap == null || !anchorMap.exists())
        {
            throw new IllegalArgumentException("File passed into TitleXMLTOCFilter constructor must be a valid file.");
        }

        anchors = new HashMap<>();

        BufferedReader reader = null;
        try
        {
            LOG.info("Reading in TOC anchor map file...");
            int numTocs = 0;
            reader = new BufferedReader(new FileReader(anchorMap));
            String input = reader.readLine();
            while (input != null)
            {
                final String[] line = input.split(",", -1);
                if (!line[1].equals(""))
                {
                    final String[] tocGuids = line[1].split("\\|");
                    for (final String toc : tocGuids)
                    {
                        numTocs++;
                        anchors.put(toc, line[0]);
                    }
                }
                else
                {
                    final String message = "Please verify that each document GUID in the following file has "
                        + "at least one TOC guid associated with it: "
                        + anchorMap.getAbsolutePath();
                    LOG.error(message);
                    throw new EBookFormatException(message);
                }
                input = reader.readLine();
            }
            LOG.info("Generated a map for " + numTocs + " TOCs.");
        }
        catch (final IOException e)
        {
            final String message = "Could not read the DOC guid to TOC guid map file: " + anchorMap.getAbsolutePath();
            LOG.error(message);
            throw new EBookFormatException(message, e);
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (final IOException e)
            {
                LOG.error("Unable to close DOC guid to TOC guid file reader.", e);
            }
        }

        if (anchors.size() == 0)
        {
            final String message = "No TOC to DOC mapping were loaded, please double check that the following"
                + " file is not empty: "
                + anchorMap.getAbsolutePath();
            LOG.error(message);
            throw new EBookFormatException(message);
        }
    }

    /**
     * Returns the build up anchor map that was created from parsing the
     * passed in anchor map file.
     *
     * @return map of TOC Guids to Doc Guids
     */
    public Map<String, String> getTocToDocMapping()
    {
        return anchors;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException
    {
        if (qName.equalsIgnoreCase("entry"))
        {
            if (atts != null)
            {
                final String tocGuid = atts.getValue("s");
                if (tocGuid != null)
                {
                    if (tocGuid.length() > 0)
                    {
                        final AttributesImpl newAtts = new AttributesImpl(atts);

                        newAtts.removeAttribute(newAtts.getIndex("s"));
                        final String docGuid = anchors.get(tocGuid);
                        if (docGuid == null || docGuid.length() < 1)
                        {
                            final String message = "Could not find DOC Guid in mapping file for TOC: " + tocGuid;
                            LOG.error(message);
                            throw new SAXException(message);
                        }
                        final String newAnchorRef = docGuid + "/" + tocGuid;
                        newAtts.addAttribute("", "", "s", "CDATA", newAnchorRef);

                        super.startElement(uri, localName, qName, newAtts);
                    }
                    else
                    {
                        final String message =
                            "Encountered TOC Guid that was empty please take a look at Title.xml file.";
                        LOG.error(message);
                        throw new SAXException(message);
                    }
                }
                else
                {
                    super.startElement(uri, localName, qName, atts);
                }
            }
        }
        else
        {
            super.startElement(uri, localName, qName, atts);
        }
    }
}
