package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ContentHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.EntityDecodedOutputStream;
import com.thomsonreuters.uscl.ereader.ioutil.EntityEncodedInputStream;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * TitleMetadataService is responsible for retrieving data from various sources in order to produce the title manifest for an eBook.
 *
 * <p>A tight coupling between this service and its collaborators exists due to constraints imposed by ProView on publishers.
 * Should ProView ever support duplicate document IDs in a TOC, multiple TOC entries that refer to the same document ID, or other data scenarios this implementation
 * will probably become much, much simpler.  As it stands at the time of implementation, the collaborators responsible for decisions based on documents
 * within this book are dependencies at this level for reasons of encapsulation, performance, and necessity.</p>
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TitleMetadataServiceImpl implements TitleMetadataService {
    private static final Logger LOG = LogManager.getLogger(TitleMetadataServiceImpl.class);
    private DocMetadataService docMetadataService;
    private PlaceholderDocumentService placeholderDocumentService;
    private FileUtilsFacade fileUtilsFacade;
    private UuidGenerator uuidGenerator;
    private ImageService imageService;

    @Override
    public void generateTitleManifest(
        final OutputStream titleManifest,
        final InputStream tocXml,
        final TitleMetadata titleMetadata,
        final Long jobInstanceId,
        final File documentsDirectory,
        final String altIdDirPath) {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        try {
            saxParserFactory.setNamespaceAware(Boolean.TRUE);
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            final XMLReader xmlReader = saxParser.getXMLReader();

            final Map<String, String> familyGuidMap =
                docMetadataService.findDistinctProViewFamGuidsByJobId(jobInstanceId);

            Map<String, String> altIdMap = new HashMap<>();
            if (titleMetadata.getIsPilotBook()) {
                altIdMap = getAltIdMap(titleMetadata.getTitleId(), altIdDirPath);
            }

            final TitleManifestFilter titleManifestFilter = new TitleManifestFilter(
                titleMetadata,
                familyGuidMap,
                uuidGenerator,
                documentsDirectory,
                fileUtilsFacade,
                placeholderDocumentService,
                altIdMap);
            titleManifestFilter.setParent(xmlReader);

            final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
            props.setProperty("omit-xml-declaration", "yes");

            final Serializer serializer = SerializerFactory.getSerializer(props);
            serializer.setOutputStream(new EntityDecodedOutputStream(titleManifest, true));

            titleManifestFilter.setContentHandler(serializer.asContentHandler());
            titleManifestFilter.parse(new InputSource(new EntityEncodedInputStream(tocXml)));
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException("Failed to configure SAX Parser when generating title manifest.", e);
        } catch (final SAXException e) {
            throw new RuntimeException("A SAXException occurred while generating the title manifest.", e);
        } catch (final IOException e) {
            throw new RuntimeException("An IOException occurred while generating the title manifest.", e);
        }
    }

    /**
     * Generates \Format\splitEbook\splitTitle.xml file for split books with TOC information.
     * This file contains TOC information which is same for all splitBooks
     */
    @Override
    public void generateSplitTitleManifest(
        final OutputStream titleManifest,
        final InputStream tocXml,
        final TitleMetadata titleMetadata,
        final Long jobInstanceId,
        final File transformedDocsDir,
        final String docToSplitBookFile,
        final String splitNodeInfoFile) {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        try {
            saxParserFactory.setNamespaceAware(Boolean.TRUE);
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            final XMLReader xmlReader = saxParser.getXMLReader();

            final Map<String, String> familyGuidMap =
                docMetadataService.findDistinctProViewFamGuidsByJobId(jobInstanceId);

            final Map<String, List<String>> docImageMap = imageService.getDocImageListMap(jobInstanceId);

            final SplitTocManifestFilter splitTocManifestFilter = new SplitTocManifestFilter(
                titleMetadata,
                familyGuidMap,
                uuidGenerator,
                transformedDocsDir,
                fileUtilsFacade,
                placeholderDocumentService,
                docImageMap);
            splitTocManifestFilter.setParent(xmlReader);

            final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
            props.setProperty("omit-xml-declaration", "yes");

            final Serializer serializer = SerializerFactory.getSerializer(props);
            serializer.setOutputStream(new EntityDecodedOutputStream(titleManifest, true));

            splitTocManifestFilter.setContentHandler(serializer.asContentHandler());
            splitTocManifestFilter.parse(new InputSource(new EntityEncodedInputStream(tocXml)));

            final List<Doc> orderedDocuments = splitTocManifestFilter.getOrderedDocuments();
            if (orderedDocuments != null && orderedDocuments.size() > 0) {
                writeDocumentsToFile(orderedDocuments, docToSplitBookFile);
            }

            final List<SplitNodeInfo> splitNodeInfoList = splitTocManifestFilter.getSplitNodeInfoList();
            if (splitNodeInfoList != null && splitNodeInfoList.size() > 0) {
                writeSplitNodeInfoToFile(splitNodeInfoList, splitNodeInfoFile, titleMetadata);
            }
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException("Failed to configure SAX Parser when generating title manifest.", e);
        } catch (final SAXException e) {
            throw new RuntimeException("A SAXException occurred while generating the title manifest.", e);
        } catch (final IOException e) {
            throw new RuntimeException("An IOException occurred while generating the split title manifest.", e);
        }
    }

    /**
     * title.xml file for split books
     */
    @Override
    public void generateTitleXML(
        final TitleMetadata titleMetadata,
        final List<Doc> docList,
        final InputStream splitTitleXMLStream,
        final OutputStream titleManifest,
        final String altIdDirPath) {
        try {
            Map<String, String> altIdMap = new HashMap<>();
            if (titleMetadata.getIsPilotBook()) {
                altIdMap = getAltIdMap(titleMetadata.getTitleId(), altIdDirPath);
            }

            final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(Boolean.TRUE);
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            final XMLReader xmlReader = saxParser.getXMLReader();

            final SplitTitleManifestFilter splitTitleManifestFilter =
                new SplitTitleManifestFilter(titleMetadata, docList, altIdMap);
            splitTitleManifestFilter.setParent(xmlReader);

            final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
            props.setProperty("omit-xml-declaration", "yes");

            final Serializer serializer = SerializerFactory.getSerializer(props);
            serializer.setOutputStream(new EntityDecodedOutputStream(titleManifest, true));

            splitTitleManifestFilter.setContentHandler(serializer.asContentHandler());
            splitTitleManifestFilter.parse(new InputSource(new EntityEncodedInputStream(splitTitleXMLStream)));
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException("Failed to configure SAX Parser when generating title manifest.", e);
        } catch (final SAXException e) {
            throw new RuntimeException("A SAXException occurred while generating the title manifest.", e);
        } catch (final IOException e) {
            throw new RuntimeException("An IOException occurred while generating the title manifest.", e);
        }
    }

    /**
     * Writes the ordered list of documents to file
     * {@link ContentHandler}.
     *
     * @throws SAXException
     *             if the data could not be written.
     */
    protected void writeSplitNodeInfoToFile(
        final List<SplitNodeInfo> splitNodeInfoList,
        final String splitNodeInfoFile,
        final TitleMetadata titleMetadata) throws SAXException, IOException {
        final File docToSplitBookFile = new File(splitNodeInfoFile);

        try (BufferedWriter writer = new BufferedWriter(new FileWriterWithEncoding(docToSplitBookFile, "UTF-8"))) {
            if (splitNodeInfoList.size() > 0) {
                for (final SplitNodeInfo splitNodeInfo : splitNodeInfoList) {
                    writer.append(splitNodeInfo.getSplitNodeGuid());
                    writer.append("|");
                    writer.append(splitNodeInfo.getSplitBookTitle());
                    writer.append("\n");
                }
            }
        } catch (final IOException e) {
            final String message =
                "Could not write out Split Node information to following file: " + docToSplitBookFile.getAbsolutePath();
            LOG.error(message, e);
            throw new IOException(); //EBookFormatException(message, e);
        }
    }

    protected void writeDocumentsToFile(final List<Doc> orderedDocuments, final String docToSplitBookFileName)
        throws IOException {
        final File docToSplitBookFile = new File(docToSplitBookFileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriterWithEncoding(docToSplitBookFile, "UTF-8"))) {
            if (orderedDocuments.size() > 0) {
                for (final Doc document : orderedDocuments) {
                    writer.append(document.getId());
                    writer.append("|");
                    writer.append(document.getSrc());
                    writer.append("|");
                    if (document.getSplitTitlePart() == 0) {
                        document.setSplitTitlePart(1);
                    }
                    writer.append(String.valueOf(document.getSplitTitlePart()));
                    if (document.getImageIdList() != null && document.getImageIdList().size() > 0) {
                        writer.append("|");
                        int i = 0;
                        for (final String img : document.getImageIdList()) {
                            i = i++;
                            writer.append(img);
                            if (i != document.getImageIdList().size()) {
                                writer.append(",");
                            }
                        }
                    }
                    writer.append("\n");
                }
            }
        } catch (final IOException e) {
            final String message =
                "Could not write out ImageMetadata to following file: " + docToSplitBookFile.getAbsolutePath();
            LOG.error(message, e);
            throw new IOException(); //EBookFormatException(message, e);
        }
    }

    /**
     * @param fileName contains altId for corresponding Guid
     * @return a map  (Guid as a Key and altId as a Value)
     */
    protected Map<String, String> getAltIdMap(final String titleId, final String altIdFileDir) {
        final String altIdFileName = titleId.replace("/", "_") + ".csv";

        final File altIdFile = new File(altIdFileDir, altIdFileName);

        final Map<String, String> altIdMap = new HashMap<>();
        String line = null;

        try (BufferedReader stream = new BufferedReader(new FileReader(altIdFile));) {
            while ((line = stream.readLine()) != null) {
                final String[] splitted = line.split(",");
                if (splitted.length >= 2) {
                    if (splitted[1].contains("/")) {
                        splitted[1] = splitted[1].split("/")[0];
                    }
                    altIdMap.put(splitted[1], splitted[0]);
                }
            }
        } catch (final IOException iox) {
            throw new RuntimeException("Unable to find File : " + altIdFile.getAbsolutePath() + " " + iox);
        }

        return altIdMap;
    }

    public void setUuidGenerator(final UuidGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    public void setDocMetadataService(final DocMetadataService docMetadataService) {
        this.docMetadataService = docMetadataService;
    }

    public void setFileUtilsFacade(final FileUtilsFacade fileUtilsFacade) {
        this.fileUtilsFacade = fileUtilsFacade;
    }

    public void setPlaceholderDocumentService(final PlaceholderDocumentService placeholderDocumentService) {
        this.placeholderDocumentService = placeholderDocumentService;
    }

    public ImageService getImageService() {
        return imageService;
    }

    public void setImageService(final ImageService imageService) {
        this.imageService = imageService;
    }
}
