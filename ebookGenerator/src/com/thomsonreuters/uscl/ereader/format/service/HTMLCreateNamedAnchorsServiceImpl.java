package com.thomsonreuters.uscl.ereader.format.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLIdFilter;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Applies any post transformation on the HTML that needs to be done to cleanup or make
 * the HTML acceptable for ProView.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLCreateNamedAnchorsServiceImpl implements HTMLCreateNamedAnchorsService {
    private static final Logger LOG = LogManager.getLogger(HTMLCreateNamedAnchorsServiceImpl.class);

    private FileHandlingHelper fileHandlingHelper;
    private DocMetadataService docMetadataService;

    public void setfileHandlingHelper(final FileHandlingHelper fileHandlingHelper) {
        this.fileHandlingHelper = fileHandlingHelper;
    }

    public void setdocMetadataService(final DocMetadataService docMetadataService) {
        this.docMetadataService = docMetadataService;
    }

    /**
     * This method applies multiple XMLFilters to the source HTML to apply various
     * post transformation rules to the HTML.
     *
     * @param srcDir source directory that contains the html files
     * @param targetDir target directory where the resulting post transformation files are written to
     * @param title title of the book being published
     * @param jobId the job identifier of the current transformation run
     * @param docToTocMap location of the file that contains the document to TOC mappings
     * @return the number of documents that had post transformations run on them
     *
     * @throws if no source files are found or any parsing/transformation exception are encountered
     */
    @Override
    public int transformHTML(
        final File srcDir,
        final File targetDir,
        final String title,
        final Long jobId,
        final File docToTocMap) throws EBookFormatException {
        if (srcDir == null || !srcDir.isDirectory()) {
            throw new IllegalArgumentException("srcDir must be a directory, not null or a regular file.");
        }

        //retrieve list of all transformed files that need HTML wrappers
        final List<File> htmlFiles = new ArrayList<>();

        try {
            final FileExtensionFilter fileExtFilter = new FileExtensionFilter();
            fileExtFilter.setAcceptedFileExtensions(new String[] {"posttransform"}); // lowercase compare
            fileHandlingHelper.setFilter(fileExtFilter);

            fileHandlingHelper.getFileList(srcDir, htmlFiles);
        } catch (final FileNotFoundException e) {
            final String errMessage = "No html files were found in specified directory. "
                + "Please verify that the correct path was specified.";
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        LOG.info("Fixing named anchors on post transformed files...");

        final DocumentMetadataAuthority documentMetadataAuthority =
            docMetadataService.findAllDocMetadataForTitleByJobId(jobId);
        final Map<String, String> dupGuids = new HashMap<>();

        // Create list of duplicates
        for (final DocMetadata docMeta : documentMetadataAuthority.getAllDocumentMetadata()) {
            //TODO: for each record in the Document Metadata Authority, update it to replace section symbols with lowercase s.
            //There may be other characters that we need to take into account.  There is a XSLT template in SpecialCharacters.xsl.
            if (docMeta.getProviewFamilyUUIDDedup() != null
                && docMeta.getDocFamilyUuid() != null
                && !dupGuids.containsValue(docMeta.getDocFamilyUuid())) {
                dupGuids.put(docMeta.getProViewId(), docMeta.getDocFamilyUuid());
                for (final DocMetadata docMeta2 : documentMetadataAuthority.getAllDocumentMetadata()) {
                    if (docMeta2.getDocFamilyUuid() != null
                        && docMeta2.getDocFamilyUuid().equals(docMeta.getDocFamilyUuid())) {
                        dupGuids.put(docMeta2.getProViewId(), docMeta2.getDocFamilyUuid());
                    }
                }
            }
        }
        final File anchorTargetListFile = new File(srcDir.getAbsolutePath(), "anchorTargetFile");

        final Map<String, Set<String>> targetAnchors = readTargetAnchorFile(anchorTargetListFile);
        final Map<String, Set<String>> dupTargetAnchors = new HashMap<>();
        int numDocs = 0;
        for (final File htmlFile : htmlFiles) {
            transformHTMLFile(
                htmlFile,
                targetDir,
                title,
                jobId,
                documentMetadataAuthority,
                targetAnchors,
                dupGuids,
                dupTargetAnchors);
            numDocs++;
        }

        removeTOCAnchors(docToTocMap, targetAnchors, title, jobId);

        final File anchorTargetUnlinkFile = new File(targetDir.getAbsolutePath(), "anchorTargetUnlinkFile");
        if (targetAnchors != null) {
            createAnchorTargetList(anchorTargetUnlinkFile, targetAnchors);
        }

        final File anchorDupFile = new File(targetDir.getAbsolutePath(), "anchorDupFile");
        createAnchorTargetList(anchorDupFile, dupTargetAnchors);

        LOG.info("Creating Anchor transformations successfully applied to " + numDocs + " files.");
        return numDocs;
    }

    /**
     * This method applies HTMLIdFilter to the passed in source file and generates
     * a new html file in the target directory with the anchors added where currently referenced as ids.
     * It also creates a file that contains anchors that should be unlinked as the source id was not found.
     *
     * @param sourceFile source file to be transformed
     * @param targetDir target directory where the resulting post transformation file is to be written
     * @param titleID title of the book being published
     * @param jobIdentifier identifier of the job that will be used to retrieve the image metadata
     * @param documentMetadataAuthority
     *
     * @throws if any parsing/transformation exception are encountered
     */
    protected void transformHTMLFile(
        final File sourceFile,
        final File targetDir,
        final String titleID,
        final Long jobIdentifier,
        final DocumentMetadataAuthority documentMetadataAuthority,
        final Map<String, Set<String>> targetAnchors,
        final Map<String, String> dupGuids,
        final Map<String, Set<String>> dupTargetAnchors) throws EBookFormatException {
        final String fileName = sourceFile.getName();
        final String guid = fileName.substring(0, fileName.indexOf("."));
        try (FileInputStream inStream = new FileInputStream(sourceFile)) {
            try (FileOutputStream outStream = new FileOutputStream(
                new File(targetDir, fileName.substring(0, fileName.indexOf(".")) + ".postAnchor"))) {
//			LOG.debug("Transforming following html file: " + sourceFile.getAbsolutePath());

                final DocMetadata docMetadata =
                    docMetadataService.findDocMetadataByPrimaryKey(titleID, jobIdentifier, guid);

                final SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                final SAXParser saxParser = factory.newSAXParser();

                final HTMLIdFilter anchorIdFilter = new HTMLIdFilter();
                anchorIdFilter.setParent(saxParser.getXMLReader());
                if (docMetadata != null && docMetadata.getProViewId() != null) {
                    anchorIdFilter.setCurrentGuid(docMetadata.getProViewId());
                    anchorIdFilter.setFamilyGuid(docMetadata.getDocFamilyUuid());
                } else {
                    anchorIdFilter.setCurrentGuid(guid);
                    anchorIdFilter.setFamilyGuid(guid);
                }
                anchorIdFilter.setTargetAnchors(targetAnchors);
                anchorIdFilter.setDupTargetAnchors(dupTargetAnchors);
                anchorIdFilter.setDupGuids(dupGuids);

                final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
                props.setProperty("omit-xml-declaration", "yes");

                final Serializer serializer = SerializerFactory.getSerializer(props);

                serializer.setOutputStream(outStream);

                anchorIdFilter.setContentHandler(serializer.asContentHandler());

                anchorIdFilter.parse(new InputSource(inStream));

                LOG.debug(sourceFile.getAbsolutePath() + " successfully transformed.");
            }
        } catch (final IOException e) {
            final String errMessage = "Unable to perform IO operations related to following source file: " + fileName;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        } catch (final SAXException e) {
            final String errMessage = "Encountered a SAX Exception while processing: " + fileName;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        } catch (final ParserConfigurationException e) {
            final String errMessage = "Encountered a SAX Parser Configuration Exception while processing: " + fileName;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
    }

    protected Map<String, Set<String>> readTargetAnchorFile(final File anchorTargetListFile)
        throws EBookFormatException {
        final Map<String, Set<String>> anchors = new HashMap<>();
        if (anchorTargetListFile.length() == 0) {
            return null;
        } else {
            try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(new FileInputStream(anchorTargetListFile), "UTF-8"))) {
                String input = reader.readLine();
                while (input != null) {
                    final String[] line = input.split(",", -1);
                    if (!line[1].equals("")) {
                        final Set<String> anchorSet = new HashSet<>();
                        final String[] anchorList = line[1].split("\\|");
                        for (final String anchorVal : anchorList) {
                            anchorSet.add(anchorVal);
                        }
                        anchors.put(line[0], anchorSet);
                    } else {
                        final String message = "Please verify that each document GUID in the following file has "
                            + "at least one anchor associated with it: "
                            + anchorTargetListFile.getAbsolutePath();
                        LOG.error(message);
                        throw new EBookFormatException(message);
                    }
                    input = reader.readLine();
                }
                LOG.info("Generated a map for " + anchors.size() + " guids that have anchors.");
            } catch (final IOException e) {
                final String message =
                    "Could not read the DOC guid to anchors file: " + anchorTargetListFile.getAbsolutePath();
                LOG.error(message);
                throw new EBookFormatException(message, e);
            }
        }
        return anchors;
    }

    /**
     * Takes in a list of guids and a set of target anchors and writes them to the specified file.
     *
     * @param anchorTargetListFile file to which the list will be written to
     * @param targetAnchors guids and target anchors for that guid.
     */
    protected void createAnchorTargetList(final File anchorTargetListFile, final Map<String, Set<String>> targetAnchors)
        throws EBookFormatException {
        try (BufferedWriter writer =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(anchorTargetListFile), "UTF-8"))) {
            for (final Entry<String, Set<String>> guidAnchorEntry : targetAnchors.entrySet()) {
                if (guidAnchorEntry.getValue().size() > 0) {
                    writer.write(guidAnchorEntry.getKey());
                    writer.write("|");
                    for (final String anchors : guidAnchorEntry.getValue()) {
                        writer.write(anchors);
                        writer.write(",");
                    }
                    writer.newLine();
                }
            }
            LOG.info(
                targetAnchors.size()
                    + " doc guid anchor references written successfuly to file: "
                    + anchorTargetListFile.getAbsolutePath());
        } catch (final IOException e) {
            final String message =
                "Could not write to the doc guid anchor references file: " + anchorTargetListFile.getAbsolutePath();
            LOG.error(message);
            throw new EBookFormatException(message, e);
        }
    }

    /**
     * Removes the anchors that will be created during the TOC anchor cascade step.
     *
     * @param docTOCMap map that contains all the TOC anchors
     * @param unlinkList list of links that will be unlinked since no anchor was found
     * @param titleID title of the book being published
     * @param jobId identifier of the job that will be used to retrieve the image metadata
     *
     * @throws EBookFormatException encountered issues reading in the TOC anchors from file
     */
    protected void removeTOCAnchors(
        final File docTOCMap,
        final Map<String, Set<String>> unlinkList,
        final String titleID,
        final Long jobId) throws EBookFormatException {
        if (unlinkList != null) {
            final Set<String> tocAnchorSet = new HashSet<>();
            readTOCAnchorList(docTOCMap, tocAnchorSet, titleID, jobId);
            for (final String docId : unlinkList.keySet()) {
                for (final String anchorName : tocAnchorSet) {
                    unlinkList.get(docId).remove(anchorName);
                }
            }
        }
    }

    /**
     * Reads in a list of TOC Guids that are associated to each Doc Guid to ensure the TOC anchors
     * that are generated during the Wrapper step are not unlinked. It then generates a set of
     * anchors that will be created by the later process so they can be removed from the unlink step.
     *
     * @param docGuidsFile file containing the DOC to TOC guid relationships
     * @param toAnchorSet in memory set of anchors that will be generated later
     * @param titleID title of the book being published
     * @param jobId identifier of the job that will be used to retrieve the image metadata
     *
     * @throws EBookFormatException encountered issues reading in the TOC anchors from file
     */
    protected void readTOCAnchorList(
        final File docGuidsFile,
        final Set<String> toAnchorSet,
        final String titleID,
        final Long jobId) throws EBookFormatException {
        try (BufferedReader reader = new BufferedReader(new FileReader(docGuidsFile))) {
            String input = reader.readLine();
            while (input != null) {
                final String[] line = input.split(",", -1);
                if (!line[1].equals("")) {
                    final String[] tocGuids = line[1].split("\\|");
                    final String guid = line[0];

                    final DocMetadata docMetadata =
                        docMetadataService.findDocMetadataByPrimaryKey(titleID, jobId, guid);

                    final String docId;
                    if (docMetadata != null) {
                        docId = docMetadata.getProViewId();
                    } else {
                        docId = guid;
                    }

                    for (final String toc : tocGuids) {
                        final String anchorName = "er:#" + docId + "/" + toc;
                        toAnchorSet.add(anchorName);
                    }
                } else {
                    final String message = "No TOC guid was found for a document. "
                        + "Please verify that each document GUID in the following file has "
                        + "at least one TOC guid associated with it: "
                        + docGuidsFile.getAbsolutePath();
                    LOG.error(message);
                    throw new EBookFormatException(message);
                }
                input = reader.readLine();
            }
        } catch (final IOException e) {
            final String message =
                "Could not read the DOC guid to TOC guid map file: " + docGuidsFile.getAbsolutePath();
            LOG.error(message);
            throw new EBookFormatException(message, e);
        }
    }
}
