package com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class NovusDocFileParser {
    private static final Logger Log = LogManager.getLogger(NovusDocFileParser.class);

    private static final String CHARSET = "UTF-8"; // explicitly set the character set
    private static final String N_DOCUMENT = "n-document";
    private static final String N_DOCBODY = "n-docbody";
    private static final String N_METADATA = "n-metadata";
    private static final String MD_UUID = "md.uuid";
    private XMLInputFactory xmlInputFactory;
    private XMLOutputFactory xmlOutFactory;
    private XMLEventFactory xmlEventFactory;

    private boolean isDocbody;
    private boolean isMetadata;
    private boolean isDocGuid;
    private boolean foundDocument;
    private String docGuid;
    private String docCollectionName;
    private Map<String, Integer> docGuidsMap;
    private Map<String, Map<Integer, String>> documentLevelMap;
    private File contentDestinationDirectory;
    private File metadataDestinationDirectory;
    private Integer tocSequence;
    private int nortFileLevel;
    private GatherResponse gatherResponse;

    public NovusDocFileParser(
        final String docCollectionName,
        final Map<String, Integer> docGuidsMap,
        final File contentDestinationDirectory,
        final File metadataDestinationDirectory,
        final GatherResponse gatherResponse,
        final Integer tocSequence,
        final int nortFileLevel,
        final Map<String, Map<Integer, String>> documentLevelMap) {
        xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", true);
        xmlOutFactory = XMLOutputFactory.newFactory();
        xmlEventFactory = XMLEventFactory.newInstance();

        this.docCollectionName = docCollectionName;
        this.docGuidsMap = docGuidsMap;
        this.contentDestinationDirectory = contentDestinationDirectory;
        this.metadataDestinationDirectory = metadataDestinationDirectory;
        this.gatherResponse = gatherResponse;
        this.tocSequence = tocSequence;
        this.nortFileLevel = nortFileLevel;
        this.documentLevelMap = documentLevelMap;
    }

    public void parseXML(final File docFile) throws GatherException {
        FileOutputStream docbodyOutput = null;
        FileOutputStream metadataOutput = null;
        XMLEventWriter docbodyWriter = null;
        XMLEventWriter metadataWriter = null;

        int docFoundCounter = 0;
        int metaDocFoundCounter = 0;
        try (FileInputStream input = new FileInputStream(docFile);
            XMLEventReaderClosableWrapper reader = new XMLEventReaderClosableWrapper(xmlInputFactory.createXMLEventReader(input, CHARSET));) {
            while (reader.hasNext()) {
                XMLEvent event = reader.next();

                if (event.isStartElement()) {
                    final StartElement element = event.asStartElement();
                    if (element.getName().getLocalPart().equalsIgnoreCase(N_DOCUMENT)) {
                        final Attribute guid = element.getAttributeByName(QName.valueOf("guid"));
                        docGuid = guid.getValue();

                        if (documentLevelMap.containsKey(docGuid)) {
                            final Map<Integer, String> nortLevelMap = documentLevelMap.get(docGuid);

                            if (nortLevelMap.containsKey(nortFileLevel)) {
                                // duplicate docGuid found and also exists in NORT Level map
                                // use DOC GUID from NORT Level map
                                docGuid = nortLevelMap.get(nortFileLevel);
                            } else {
                                // duplicate docGuid found but DOC GUID does not exist in this NORT Level
                                // generate new docGuid to fix bug: CA Dwyer duplicate doc conflict.  Multiple extracts from
                                // same content set produces same documents with different prelims.  This is a special case.
                                // Duplicate documents within same content set can reuse the same document.
                                if (docGuid.contains("-")) {
                                    docGuid = docGuid + nortFileLevel;
                                } else {
                                    docGuid = docGuid + "-" + nortFileLevel;
                                }

                                nortLevelMap.put(nortFileLevel, docGuid);
                                documentLevelMap.put(docGuid, nortLevelMap);
                            }
                        } else {
                            // First time seeing the document
                            final Map<Integer, String> nortLevelMap = new HashMap<>();
                            nortLevelMap.put(nortFileLevel, docGuid);
                            documentLevelMap.put(docGuid, nortLevelMap);
                        }

                        if (docGuidsMap.containsKey(docGuid)) {
                            foundDocument = true;
                            tocSequence++;
                            docGuidsMap.remove(docGuid);

                            // Create file names
                            final String docbodyBasename = docGuid + EBConstants.XML_FILE_EXTENSION;
                            final String metadataBasename =
                                tocSequence + "-" + docCollectionName + "-" + docGuid + EBConstants.XML_FILE_EXTENSION;

                            docbodyOutput =
                                new FileOutputStream(new File(contentDestinationDirectory, docbodyBasename));
                            metadataOutput =
                                new FileOutputStream(new File(metadataDestinationDirectory, metadataBasename));
                            docbodyWriter = xmlOutFactory.createXMLEventWriter(docbodyOutput, CHARSET);
                            metadataWriter = xmlOutFactory.createXMLEventWriter(metadataOutput, CHARSET);
                        } else {
                            Log.debug("Match Not Found for Novus Document GUID or was already processed: " + docGuid);
                        }
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(N_DOCBODY) && foundDocument) {
                        isDocbody = true;
                        docFoundCounter++;
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(N_METADATA) && foundDocument) {
                        isMetadata = true;
                        metaDocFoundCounter++;
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(MD_UUID) && foundDocument) {
                        isDocGuid = true;
                    }
                }
                // Change Document GUID when not matching docGuid from n-document.
                if (event.isCharacters() && isDocGuid) {
                    final Characters character = event.asCharacters();
                    final String data = character.getData();
                    if (!docGuid.equalsIgnoreCase(data)) {
                        event = xmlEventFactory.createCharacters(docGuid);
                    }
                }

                // write out to files if condition is met
                if (isDocbody) {
                    docbodyWriter.add(event);
                } else if (isMetadata) {
                    metadataWriter.add(event);
                }
                // Needs to be after writers.
                if (event.isEndElement()) {
                    final EndElement element = event.asEndElement();
                    if (element.getName().getLocalPart().equalsIgnoreCase(N_DOCBODY) && isDocbody) {
                        isDocbody = false;
                        docbodyWriter.flush();
                        docbodyWriter.close();
                        docbodyOutput.close();
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(N_METADATA) && isMetadata) {
                        isMetadata = false;
                        metadataWriter.flush();
                        metadataWriter.close();
                        metadataOutput.close();
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(N_DOCUMENT)) {
                        foundDocument = false;
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(MD_UUID)) {
                        isDocGuid = false;
                    }
                }
            }
        } catch (final FileNotFoundException e) {
            throw new GatherException("File Not Found error", e, GatherResponse.CODE_FILE_ERROR);
        } catch (final IOException e) {
            throw new GatherException(
                "File I/O error on document with GUID " + docGuid,
                e,
                GatherResponse.CODE_FILE_ERROR);
        } catch (final XMLStreamException e) {
            throw new GatherException("Streaming doc file error " + docGuid, e, GatherResponse.CODE_FILE_ERROR);
        } catch (final Exception e) {
            throw new GatherException("Error occurred parsing " + docGuid, e, GatherResponse.CODE_FILE_ERROR);
        } finally {
            docFoundCounter += gatherResponse.getDocCount();
            gatherResponse.setDocCount(docFoundCounter); // retrieved doc count
            metaDocFoundCounter += gatherResponse.getDocCount2();
            gatherResponse.setDocCount2(metaDocFoundCounter); // retrieved doc count

            if (docbodyWriter != null) {
                try {
                    docbodyWriter.close();
                } catch (final XMLStreamException e) {
                    Log.error("Error closing docbody writer.", e);
                }
            }
            if (docbodyOutput != null) {
                try {
                    docbodyOutput.close();
                } catch (final IOException e) {
                    Log.error("Error closing docbody output stream.", e);
                }
            }
            if (metadataWriter != null) {
                try {
                    metadataWriter.close();
                } catch (final XMLStreamException e) {
                    Log.error("Error closing metadata writer.", e);
                }
            }
            if (metadataOutput != null) {
                try {
                    metadataOutput.close();
                } catch (final IOException e) {
                    Log.error("Error closing metadata output stream.", e);
                }
            }
        }
    }

    public Integer getTocSequence() {
        return tocSequence;
    }

    public void setTocSequence(final Integer tocSequence) {
        this.tocSequence = tocSequence;
    }

    private static class XMLEventReaderClosableWrapper implements Closeable {
        private XMLEventReader reader;

        XMLEventReaderClosableWrapper(final XMLEventReader reader) {
            this.reader = reader;
        }

        boolean hasNext() {
            return reader.hasNext();
        }

        XMLEvent next() {
            return (XMLEvent) reader.next();
        }

        @Override
        public void close() throws IOException {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final XMLStreamException e) {
                    final String message = "Closing reader doc file error";
                    throw new RuntimeException(message, new GatherException(message, e, GatherResponse.CODE_FILE_ERROR));
                }
            }
        }
    }
}
