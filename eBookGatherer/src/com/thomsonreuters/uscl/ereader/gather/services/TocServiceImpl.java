package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;
import org.springframework.beans.factory.annotation.Required;

import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;
import com.westgroup.novus.productapi.TOC;
import com.westgroup.novus.productapi.TOCNode;

/**
 * This class corresponds with novus helper and gets TOC hierarchy.
 *
 * @author Kirsten Gunn
 *
 */

@Slf4j
public class TocServiceImpl implements TocService {
    private NovusFactory novusFactory;

    private NovusUtility novusUtility;

    private Integer tocRetryCount;

    private List<String> splitTocGuidList;
    private static int splitTocCount;
    private int thresholdValue;
    private boolean findSplitsAgain;
    private List<String> tocGuidList;
    private List<String> duplicateTocGuids;

    public List<String> getDuplicateTocGuids() {
        return duplicateTocGuids;
    }

    public void setDuplicateTocGuids(final List<String> duplicateTocGuids) {
        this.duplicateTocGuids = duplicateTocGuids;
    }

    public boolean isFindSplitsAgain() {
        return findSplitsAgain;
    }

    public List<String> getSplitTocGuidList() {
        return splitTocGuidList;
    }

    public int getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(final int thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public void retrieveNodes(
        final String guid,
        final TOC _tocManager,
        final Writer out,
        final int[] counter,
        final int[] docCounter,
        final int[] retryCounter,
        final int[] intParent,
        final List<ExcludeDocument> excludeDocuments,
        final List<ExcludeDocument> copyExcludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<RenameTocEntry> copyRenameTocEntries) throws GatherException {
        TOCNode[] tocNodes = null;
        TOCNode tocNode = null;

        try {
            // This is the counter for checking how many Novus retries we
            // are making
            Integer novusTocRetryCounter = 0;
            tocRetryCount = Integer.valueOf(novusUtility.getTocRetryCount());
            while (novusTocRetryCounter < tocRetryCount) {
                try {
                    tocNode = _tocManager.getNode(guid);
                    break;
                } catch (final Exception exception) {
                    try {
                        novusTocRetryCounter =
                            novusUtility.handleException(exception, novusTocRetryCounter, tocRetryCount);
                    } catch (final Exception e) {
                        log.error("Failed with Novus Exception in TOC");
                        throw new GatherException("TOC Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
                    }
                }
            }
            if (tocNode == null) {
                final String emptyErr =
                    "Failed with EMPTY toc.xml for collection " + _tocManager.getCollection() + " and guid " + guid;
                log.error(emptyErr);
                throw new GatherException(emptyErr, GatherResponse.CODE_UNHANDLED_ERROR);
            }

            retryCounter[0] += novusTocRetryCounter;
            // tocNodes = _tocManager.getRootNodes();

            tocNodes = new TOCNode[1];
            tocNodes[0] = tocNode;
            printNodes(
                tocNodes,
                _tocManager,
                out,
                counter,
                docCounter,
                intParent,
                excludeDocuments,
                copyExcludeDocuments,
                renameTocEntries,
                copyRenameTocEntries);
        } catch (final GatherException e) {
            log.error("Failed with GatherException in TOC");
            throw e;
        } catch (final Exception e) {
            log.debug("Failed with Novus Exception in TOC");
            throw new GatherException("TOC Novus Exception bitch", e, GatherResponse.CODE_NOVUS_ERROR);
        }
    }

    public boolean printNodes(
        final TOCNode[] nodes,
        final TOC _tocManager,
        final Writer out,
        final int[] counter,
        final int[] docCounter,
        final int[] iParent,
        final List<ExcludeDocument> excludeDocuments,
        final List<ExcludeDocument> copyExcludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<RenameTocEntry> copyRenameTocEntries) throws GatherException {
        boolean docFound = true;

        if (nodes != null) {
            try {
                final List<Boolean> documentsFound = new ArrayList<>();
                for (final TOCNode node : nodes) {
                    documentsFound.add(
                        printNode(
                            node,
                            _tocManager,
                            out,
                            counter,
                            docCounter,
                            iParent,
                            excludeDocuments,
                            copyExcludeDocuments,
                            renameTocEntries,
                            copyRenameTocEntries));
                }

                // Only add MissingDocuments if all nodes return false
                docFound = documentsFound.contains(true);

                if (iParent[0] > 0) {
                    if (!docFound) {
                        out.write("<MissingDocument></MissingDocument>");
                    }
                    out.write(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
                    out.write("\r\n");
                    out.flush();

                    iParent[0]--;
                }
            } catch (final IOException e) {
                log.error("Failed writing TOC in TOC");
                throw new GatherException("Failed writing TOC in TOC ", e, GatherResponse.CODE_NOVUS_ERROR);
            }
        }
        return docFound;
    }

    public boolean printNode(
        final TOCNode node,
        final TOC _tocManager,
        final Writer out,
        final int[] counter,
        final int[] docCounter,
        final int[] iParent,
        final List<ExcludeDocument> excludeDocuments,
        final List<ExcludeDocument> copyExcludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<RenameTocEntry> copyRenameTocEntries) throws GatherException {
        boolean docFound = true;
        boolean excludeDocumentFound = false;
        String documentGuid = null;

        if (node != null) {
            documentGuid = node.getDocGuid();
            if (documentGuid != null) {
                documentGuid = documentGuid.replaceAll("\\<.*?>", "");
                if ((excludeDocuments != null) && (excludeDocuments.size() > 0) && (copyExcludeDocuments != null)) {
                    for (final ExcludeDocument excludeDocument : excludeDocuments) {
                        if (excludeDocument.getDocumentGuid().equalsIgnoreCase(documentGuid)) {
                            excludeDocumentFound = true;
                            copyExcludeDocuments.remove(excludeDocument);
                            break;
                        }
                    }
                }
            }

            docFound = false;
            final StringBuffer name = new StringBuffer();
            if (node.getName() == null) // Fail with empty Name
            {
                final String err = "Failed with empty node Name for guid " + node.getGuid();
                log.error(err);
                throw new GatherException(err, GatherResponse.CODE_NOVUS_ERROR);
            }

            if (!excludeDocumentFound) {
                String label = node.getName().replaceAll("\\<.*?>", "");
                final String guid = node.getGuid().replaceAll("\\<.*?>", "");

                // Check if name needs to be relabelled
                if ((renameTocEntries != null) && (renameTocEntries.size() > 0) && (copyRenameTocEntries != null)) {
                    for (final RenameTocEntry renameTocEntry : renameTocEntries) {
                        if (renameTocEntry.getTocGuid().equalsIgnoreCase(guid)) {
                            final CharSequenceTranslator escapeXml = StringEscapeUtils.ESCAPE_XML10
                                .with(NumericEntityEscaper.between(0x7f, Integer.MAX_VALUE));
                            label = escapeXml.translate(renameTocEntry.getNewLabel());
                            copyRenameTocEntries.remove(renameTocEntry);
                            break;
                        }
                    }
                }

                // Normalize label text
                label = NormalizationRulesUtil.applyTableOfContentNormalizationRules(label);

                name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
                    .append(EBConstants.TOC_START_NAME_ELEMENT)
                    .append(label)
                    .append(EBConstants.TOC_END_NAME_ELEMENT);
                final StringBuffer tocGuid = new StringBuffer();
                tocGuid.append(EBConstants.TOC_START_GUID_ELEMENT)
                    .append(guid)
                    .append(EBConstants.TOC_END_GUID_ELEMENT);

                final StringBuffer docGuid = new StringBuffer();

                if (documentGuid != null) {
                    docFound = true;
                    docCounter[0]++;
                    docGuid.append(EBConstants.TOC_START_DOCUMENT_GUID_ELEMENT)
                        .append(documentGuid)
                        .append(EBConstants.TOC_END_DOCUMENT_GUID_ELEMENT);
                }

                if (node.getChildrenCount() == 0) {
                    if (!docFound) {
                        docGuid.append("<MissingDocument></MissingDocument>");
                        docFound = true;
                    }
                    docGuid.append(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
                } else {
                    iParent[0]++;
                }

                // Example of output:
                // <EBookToc>
                // <Name>Primary Source with Annotations</Name>
                // <DocumentGuid>I175bd1b012bb11dc8c0988fbe4566386</DocumentGuid>

                // To verify if the provided split Toc/Nort Node exists in the file
                verifyProvidedSplitNodeExistsInFile(guid);

                //843012:Update Generator for CWB Reorder Changes. For both TOC and Doc guids a six digit suffix has been added
                //Add only first 33 characters to find the duplicate TOC
                if (guid.toString().length() >= 33) {
                    final String guidForDupCheck = StringUtils.substring(guid.toString(), 0, 33);

                    if (tocGuidList.contains(guidForDupCheck) && !duplicateTocGuids.contains(guidForDupCheck)) {
                        duplicateTocGuids.add(guidForDupCheck);
                    }

                    tocGuidList.add(guidForDupCheck);
                }

                final String payloadFormatted = (name.toString() + tocGuid.toString() + docGuid.toString());

                counter[0]++;

                // LOG.debug(" document count : " + docCounter + " out of " +
                // counter + " nodes" );

                try {
                    out.write(payloadFormatted);
                    out.write("\r\n");
                    out.flush();
                } catch (final IOException e) {
                    log.error(e.getMessage());
                    throw new GatherException("Failed writing to TOC ", e, GatherResponse.CODE_FILE_ERROR);
                }
            }

            final TOCNode[] tocNodes = getTocNodes(node);

            if (tocNodes != null) {
                printNodes(
                    tocNodes,
                    _tocManager,
                    out,
                    counter,
                    docCounter,
                    iParent,
                    excludeDocuments,
                    copyExcludeDocuments,
                    renameTocEntries,
                    copyRenameTocEntries);
                docFound = true;
            }
        }
        return docFound;
    }

    private TOCNode[] getTocNodes(final TOCNode node) throws GatherException {
        TOCNode[] tocNodes = null;

        try {
            // This is the counter for checking how many Novus retries we
            // are making
            Integer novusTocRetryCounter = 0;
            tocRetryCount = Integer.valueOf(novusUtility.getTocRetryCount());
            while (novusTocRetryCounter < tocRetryCount) {
                try {
                    tocNodes = node.getChildren();
                    break;
                } catch (final Exception exception) {
                    try {
                        novusTocRetryCounter =
                            novusUtility.handleException(exception, novusTocRetryCounter, tocRetryCount);
                    } catch (final NovusException e) {
                        log.error("Failed with Novus Exception in TOC getChildren()");
                        throw new GatherException("TOC Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
                    } catch (final GatherException e) {
                        log.error("Failed with GatherException in TOC");
                        throw e;
                    } catch (final Exception e) {
                        log.error("Failed with Exception in TOC getChildren()");
                        throw new GatherException("TOC Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
                    }
                }
            }
        } catch (final GatherException e) {
            log.error("Failed with GatherException in TOC");
            throw e;
        } catch (final Exception e) {
            log.error("Failed with Novus Exception in TOC");
            throw new GatherException("TOC Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
        }

        return tocNodes;
    }

    private void verifyProvidedSplitNodeExistsInFile(final String guid) {
        if (splitTocGuidList != null && splitTocGuidList.size() > 0 && guid.toString().length() >= 33) {
            splitTocCount++;
            final String splitNode = StringUtils.substring(guid.toString(), 0, 33);
            if (splitTocGuidList.contains(splitNode)) {
                if (splitTocCount >= thresholdValue) {
                    findSplitsAgain = true;
                }
                splitTocCount = 0;
                splitTocGuidList.remove(splitNode);
            }
        }
    }

    /**
     *
     * Get TOC using domain and expression filter.
     *
     * @throws Exception
     */
    @Override
    public GatherResponse findTableOfContents(
        final String guid,
        final String collectionName,
        final File tocXmlFile,
        final List<ExcludeDocument> excludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final boolean isFinalStage,
        final List<String> splitTocGuidList,
        final int thresholdValue) throws GatherException {
        TOC _tocManager = null;
        final int[] counter = {0};
        final int[] docCounter = {0};
        final int[] retryCounter = {0};
        final int[] iParent = {0};
        final GatherResponse gatherResponse = new GatherResponse();

        String publishStatus = "TOC Step Completed";

        /*** for ebook builder we will always get Collection. ***/
        final String type = EBConstants.COLLECTION_TYPE;

        Novus novusObject = null;
        try {
            novusObject = novusFactory.createNovus(isFinalStage);
        } catch (final NovusException e) {
            throw new GatherException(
                "Novus error occurred while creating Novus object " + e,
                GatherResponse.CODE_NOVUS_ERROR);
        }

        _tocManager = getTocObject(collectionName, type, novusObject);

        List<ExcludeDocument> copyExcludDocs = null;

        this.splitTocGuidList = splitTocGuidList;

        tocGuidList = new ArrayList<>();
        duplicateTocGuids = new ArrayList<>();

        this.thresholdValue = thresholdValue;

        // Make a copy of the original excluded documents to check that all have been accounted for
        if (excludeDocuments != null) {
            copyExcludDocs = new ArrayList<>(Arrays.asList(new ExcludeDocument[excludeDocuments.size()]));
        }

        List<RenameTocEntry> copyRenameTocs = null;

        // Make a copy of the original rename TOC entries to check that all have been accounted for
        if (renameTocEntries != null) {
            copyRenameTocs = new ArrayList<>(Arrays.asList(new RenameTocEntry[renameTocEntries.size()]));
        }

        try (Writer out =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tocXmlFile.getPath()), "UTF8"))) {
            if (excludeDocuments != null) {
                Collections.copy(copyExcludDocs, excludeDocuments);
            }

            if (renameTocEntries != null) {
                Collections.copy(copyRenameTocs, renameTocEntries);
            }
            out.write(EBConstants.TOC_XML_ELEMENT);
            out.write(EBConstants.TOC_START_EBOOK_ELEMENT);

            retrieveNodes(
                guid,
                _tocManager,
                out,
                counter,
                docCounter,
                retryCounter,
                iParent,
                excludeDocuments,
                copyExcludDocs,
                renameTocEntries,
                copyRenameTocs);

            log.info(
                docCounter[0]
                    + " documents and "
                    + counter[0]
                    + " nodes in the TOC hierarchy for guid "
                    + guid
                    + " collection "
                    + collectionName);

            out.write(EBConstants.TOC_END_EBOOK_ELEMENT);
            out.flush();
            log.debug("Done with Toc.");
        } catch (final UnsupportedEncodingException e) {
            log.error(e.getMessage());
            publishStatus = "TOC Step Failed UTF-8 encoding error";
            throw new GatherException("TOC UTF-8 encoding error ", e, GatherResponse.CODE_FILE_ERROR);
        } catch (final FileNotFoundException e) {
            log.error(e.getMessage());
            publishStatus = "TOC Step Failed File not found";
            throw new GatherException("TOC File not found ", e, GatherResponse.CODE_FILE_ERROR);
        } catch (final GatherException e) {
            log.error(e.getMessage());
            publishStatus = "TOC Step Failed GatherException";
            throw e;
        } catch (final Exception e) {
            log.error(e.getMessage());
            publishStatus = "TOC Step Failed IOException";
            throw new GatherException("TOC IOException ", e, GatherResponse.CODE_FILE_ERROR);
        } finally {
            try {
                if ((copyExcludDocs != null) && (copyExcludDocs.size() > 0)) {
                    final StringBuffer unaccountedExcludedDocs = new StringBuffer();
                    for (final ExcludeDocument excludeDocument : copyExcludDocs) {
                        unaccountedExcludedDocs.append(excludeDocument.getDocumentGuid() + ",");
                    }
                    publishStatus = "TOC Step Failed with Not all Excluded Docs accounted for error";
                    throw new GatherException(
                        "Not all Excluded Docs are accounted for and those are " + unaccountedExcludedDocs.toString());
                }
                if ((copyRenameTocs != null) && (copyRenameTocs.size() > 0)) {
                    final StringBuffer unaccountedRenameTocs = new StringBuffer();
                    for (final RenameTocEntry renameTocEntry : copyRenameTocs) {
                        unaccountedRenameTocs.append(renameTocEntry.getTocGuid() + ",");
                    }
                    publishStatus = "TOC Step Failed with Not all Rename TOCs accounted for error";
                    throw new GatherException(
                        "Not all Rename TOCs are accounted for and those are " + unaccountedRenameTocs.toString());
                }
            } catch (final Exception e) {
                log.error(e.getMessage());
                throw new GatherException("Failure in findTOC() ", e, GatherResponse.CODE_DATA_ERROR);
            } finally {
                gatherResponse.setDocCount(docCounter[0]);
                gatherResponse.setNodeCount(counter[0]);
                gatherResponse.setRetryCount(retryCounter[0]);
                gatherResponse.setPublishStatus(publishStatus);
                gatherResponse.setFindSplitsAgain(findSplitsAgain);
                gatherResponse.setSplitTocGuidList(this.splitTocGuidList);
                gatherResponse.setDuplicateTocGuids(duplicateTocGuids);
            }
            novusObject.shutdownMQ();
        }
        return gatherResponse;
    }

    /**
     * Sets required properties to toc object like collection/collectionSet
     * name. and version data if needed in future. as of now we are assuming all
     * the TOC provided would be without version.
     *
     * @param name
     * @param type
     * @param novus
     * @return
     */
    private TOC getTocObject(final String name, final String type, final Novus novus) {
        final TOC toc = novus.getTOC();

        if (!"".equals(type) && type.equalsIgnoreCase(EBConstants.COLLECTION_SET_TYPE)) {
            toc.setCollectionSet(name);
        } else if (!"".equals(type) && type.equalsIgnoreCase(EBConstants.COLLECTION_TYPE)) {
            toc.setCollection(name);
        }
        toc.setShowChildrenCount(true);
        // String dateTime = novusAPIHelper.getCurrentDateTime();
        // toc.setTOCVersion(dateTime);
        return toc;
    }

    @Required
    public void setNovusFactory(final NovusFactory factory) {
        novusFactory = factory;
    }

    @Required
    public void setNovusUtility(final NovusUtility novusUtil) {
        novusUtility = novusUtil;
    }
}
