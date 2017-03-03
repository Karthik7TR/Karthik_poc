package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.gather.exception.EBookGatherException;
import com.thomsonreuters.uscl.ereader.gather.parsinghandler.TOCXmlHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parses TOC XML files and creates a doc guid list.
 *
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam
 *         Chatterjee</a> u0072938
 */
public class DocMetaDataGuidParserServiceImpl implements DocMetaDataGuidParserService
{
    private static final Logger LOG = LogManager.getLogger(DocMetaDataGuidParserServiceImpl.class);

    /**
     * Reads through all the XML files found in the provided directory and
     * parses out the TOC.xml to create a list of document GUIDs.
     *
     * @param xmlDir
     *            directory that contains the TOC XML file to be parsed
     *
     * @return List of document guids
     * @throws EBookGatherException
     *             if any fatal errors are encountered
     */
    @Override
    public void generateDocGuidList(final File tocFile, final File docGuidsFile) throws EBookGatherException
    {
        Map<String, List<String>> docGuidList = new HashMap<>();

        LOG.info(
            "Parsing out the document guids from TOC xml file of the following XML directory: "
                + tocFile.getAbsolutePath());

        try
        {
            docGuidList = parseXMLFile(tocFile);
            createDocGuidListFile(docGuidsFile, docGuidList);
        }
        catch (final Exception e)
        {
            final String errMessage = "Exception occured while parsing tocFile. Please fix and try again.";
            LOG.error(errMessage);
            throw new EBookGatherException(errMessage, e);
        }

        LOG.info("Parsed out " + docGuidList.size() + " doc guids from the XML files in the provided directory.");

        return;
    }

    /**
     * Parses the provided XML file, extracting the doc guid list and adding
     * them to the supplied GUID list.
     *
     * @param xmlFile
     *            XML file to be parsed
     * @param guidList
     *            List of document GUIDs
     *
     * @throws EBookGatherException
     *             if any parsing issues have been encountered
     */
    protected Map<String, List<String>> parseXMLFile(final File xmlFile) throws EBookGatherException
    {
        final TOCXmlHandler handler = new TOCXmlHandler();
        try
        {
            LOG.debug("Parsing following toc for doc guids: " + xmlFile);

            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();
            final InputStream inputStream = new FileInputStream(xmlFile);
            final Reader reader = new InputStreamReader(inputStream, "UTF-8");
            final InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");
            saxParser.parse(is, handler);
        }
        catch (final FileNotFoundException e)
        {
            final String errMessage = "No XML file was found in specified directory. "
                + "Please verify that the correct XML path was specified.";
            LOG.error(errMessage);
            throw new EBookGatherException(errMessage, e);
        }
        catch (final IOException e)
        {
            final String message =
                "Parser throw an exception while parsing the following file: " + xmlFile.getAbsolutePath();
            LOG.error(message);
            throw new EBookGatherException(message, e);
        }
        catch (final SAXException e)
        {
            final String message =
                "Parser throw an exception while parsing the following file: " + xmlFile.getAbsolutePath();
            LOG.error(message);
            throw new EBookGatherException(message, e);
        }
        catch (final ParserConfigurationException e)
        {
            final String message =
                "ParserConfigurationException thrown while parsing the following file: " + xmlFile.getAbsolutePath();
            LOG.error(message);
            throw new EBookGatherException(message, e);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }

        return handler.getDocGuidList();
    }

    /**
     * Takes in a list of document guids and writes them to the specified file.
     *
     * @param docGuidFile
     *            file to which the list will be written to
     * @param docGuidList
     *            list of image guids to be written
     */
    protected void createDocGuidListFile(final File docGuidFile, final Map<String, List<String>> docGuidList)
        throws EBookGatherException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(docGuidFile)))
        {
            String docGuid = "";
            List<String> tocGuidList = new ArrayList<>();

            final Iterator<String> iterator = docGuidList.keySet().iterator();

            while (iterator.hasNext())
            {
                docGuid = iterator.next();

                if (docGuid != null)
                {
                    if (docGuid.length() < 32 || docGuid.length() > 42)
                    {
                        final String message = "Invalid GUID encountered in the Doc GUID list: " + docGuid;
                        LOG.error(message);
                        throw new EBookGatherException(message);
                    }
                    writer.write(docGuid + ",");
                    tocGuidList = docGuidList.get(docGuid);

                    for (final String tocGuid : tocGuidList)
                    {
                        if (tocGuid != null)
                        {
                            // TOC guild can be more than 44 characters due to
                            // 6digit suffix being added to the TOC after '-'
                            if (tocGuid.length() < 32 || (tocGuid.length() >= 44 && !tocGuid.contains("-")))
                            {
                                final String message = "Invalid Toc GUID encountered in the Doc GUID list: " + tocGuid;
                                LOG.error(message);
                                throw new EBookGatherException(message);
                            }
                        }

                        writer.write(tocGuid + "|");
                    }
                }
                writer.newLine();
            }
        }
        catch (final IOException e)
        {
            final String message = "Could not write to the docGuid list file: " + docGuidFile.getAbsolutePath();
            LOG.error(message, e);
            throw new EBookGatherException(message, e);
        }
    }

    /*
     * public static void main(String[] args) throws Exception {
     *
     * DocMetaDataGuidParserServiceImpl test = new
     * DocMetaDataGuidParserServiceImpl(); test.generateDocGuidList( new File(
     * "C:\\USCLeReader\\toc.xml"), new
     * File("C:\\USCLeReader\\docGuidList.txt")); }
     */
}
