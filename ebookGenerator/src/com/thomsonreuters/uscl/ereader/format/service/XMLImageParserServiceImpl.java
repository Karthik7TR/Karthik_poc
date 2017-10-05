package com.thomsonreuters.uscl.ereader.format.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.XMLImageTagHandler;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Parses XML files and identifies all image references and prints them to a file.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class XMLImageParserServiceImpl implements XMLImageParserService {
    private static final Logger LOG = LogManager.getLogger(XMLImageParserServiceImpl.class);

    private FileHandlingHelper fileHandlingHelper;

    public void setfileHandlingHelper(final FileHandlingHelper fileHandlingHelper) {
        this.fileHandlingHelper = fileHandlingHelper;
    }

    /**
     * Reads through all the XML files found in the provided directory and parses out all
     * image references and generates the specified file that contains a list of GUIDs for
     * the referenced images.
     *
     * @param xmlDir directory that contains the XML files to be parsed
     * @param imgRef file which will be written that contains a list of all the referenced image GUIDs
     * @param docImageMap file which will be written that contains a list of images embedded in each document
     *
     * @return number of documents processed to generate lists
     * @throws EBookFormatException if any fatal errors are encountered
     */
    @Override
    public int generateImageList(final File xmlDir, final File imgRef, final File docImageMap)
        throws EBookFormatException {
        if (xmlDir == null || !xmlDir.isDirectory()) {
            throw new IllegalArgumentException("xmlDir must be a directory, not null or a regular file.");
        }

        LOG.info(
            "Parsing out image references from XML files out of the following XML directory: "
                + xmlDir.getAbsolutePath());

        final List<File> fileList = new ArrayList<>();

        try {
            fileHandlingHelper.getFileList(xmlDir, fileList);
        } catch (final FileNotFoundException e) {
            final String errMessage = "No XML files were found in specified directory. "
                + "Please verify that the correct XML path was specified.";
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }

        final List<String> guids = new ArrayList<>();
        final Map<String, List<String>> docImgMap = new HashMap<>();
        int numDocsParsed = 0;
        for (final File file : fileList) {
            parseXMLFile(file, guids, docImgMap);
            numDocsParsed++;
        }

        //  createImageList(imgRef, guids);
        createDocToImgMap(docImageMap, docImgMap);

        LOG.info("Parsed out " + guids.size() + " image references from the XML files in the provided directory.");

        return numDocsParsed;
    }

    /**
     * Parses the provided XML file, extracting any image references and adding them to the
     * supplied GUID list.
     *
     * @param xmlFile XML file to be parsed
     * @param guidList Set of GUIDs that new Image GUIDs should be appended to
     * @param docImgMap Map of image GUIDs associated to each document
     *
     * @throws EBookFormatException if any parsing issues have been encountered
     */
    protected void parseXMLFile(
        final File xmlFile,
        final List<String> guidList,
        final Map<String, List<String>> docImgMap) throws EBookFormatException {
        final String docGuid = xmlFile.getName().substring(0, xmlFile.getName().indexOf("."));
        try (FileInputStream xmlStream = new FileInputStream(xmlFile)) {
            //LOG.debug("Parsing following doc for image references: " + xmlFile);
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();

            final XMLImageTagHandler handler = new XMLImageTagHandler();
            final List<String> imgGuids = new ArrayList<>();
            handler.setGuidList(imgGuids);

            saxParser.parse(xmlStream, handler);

            docImgMap.put(docGuid, imgGuids);
            guidList.addAll(imgGuids);

            LOG.debug("Parsed out " + guidList.size() + " image guids from " + xmlFile + ".");
        } catch (final IOException e) {
            final String message =
                "Parser throw an exception while parsing the following file: " + xmlFile.getAbsolutePath();
            LOG.error(message, e);
            throw new EBookFormatException(message, e);
        } catch (final SAXException e) {
            final String message =
                "Parser throw an exception while parsing the following file: " + xmlFile.getAbsolutePath();
            LOG.error(message, e);
            throw new EBookFormatException(message, e);
        } catch (final ParserConfigurationException e) {
            final String message =
                "ParserConfigurationException thrown while parsing the following file: " + xmlFile.getAbsolutePath();
            LOG.error(message, e);
            throw new EBookFormatException(message, e);
        }
    }

    /**
     * Takes in a list of images and writes them to the specified file.
     *
     * @param imgListFile file to which the list will be written to
     * @param imgList set of image guids to be written
     */
    protected void createImageList(final File imgListFile, final List<String> imgList) throws EBookFormatException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(imgListFile))) {
            for (final String guid : imgList) {
                if (guid == null || guid.length() < 30 || guid.length() > 36) {
                    final String message = "Invalid GUID encountered in the Image GUID list: " + guid;
                    LOG.error(message);
                    throw new EBookFormatException(message);
                }

                writer.write(guid);
                writer.newLine();
            }
        } catch (final IOException e) {
            final String message = "Could not write to the Image list file: " + imgListFile.getAbsolutePath();
            LOG.error(message, e);
            throw new EBookFormatException(message, e);
        }
    }

    /**
     * Takes in a map of doc to image associations and writes them to the specified file.
     *
     * @param docToImgMapFile file to which the map will be persisted
     * @param docToImgMap the map that contains all the document to image associations
     */
    protected void createDocToImgMap(final File docToImgMapFile, final Map<String, List<String>> docToImgMap)
        throws EBookFormatException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(docToImgMapFile))) {
            for (final String doc : docToImgMap.keySet()) {
                if (doc == null || doc.length() < 32 || doc.length() > 42) {
                    final String message =
                        "Invalid document GUID encountered in the Document to Image GUID map: " + doc;
                    LOG.error(message);
                    throw new EBookFormatException(message);
                }

                writer.write(doc);
                writer.write("|");

                for (final String imgGuid : docToImgMap.get(doc)) {
                    if (imgGuid == null || imgGuid.length() < 30 || imgGuid.length() > 36) {
                        final String message = "Invalid image GUID encountered in the Document to Image GUID map: "
                            + imgGuid
                            + " for the docuemnt guid "
                            + doc;
                        LOG.error(message);
                        throw new EBookFormatException(message);
                    }

                    writer.write(imgGuid + ",");
                }
                writer.newLine();
            }
        } catch (final IOException e) {
            final String message =
                "Could not write to the Document to Image GUID map file: " + docToImgMapFile.getAbsolutePath();
            LOG.error(message, e);
            throw new EBookFormatException(message, e);
        }
    }
}
