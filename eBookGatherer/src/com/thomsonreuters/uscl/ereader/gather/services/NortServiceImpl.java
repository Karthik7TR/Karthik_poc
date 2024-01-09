package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.exception.NortLabelParseException;
import com.thomsonreuters.uscl.ereader.gather.parser.NortLabelParser;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;
import org.springframework.beans.factory.annotation.Required;

import com.westgroup.novus.productapi.NortManager;
import com.westgroup.novus.productapi.NortNode;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

/**
 * This class corresponds with novus helper and gets NORT hierarchy.
 *
 * @author Kirsten Gunn
 *
 */

@Slf4j
public class NortServiceImpl implements NortService {
    private static final String DATE_FORMAT = "yyyyMMddHHmmss";
    private static final String DATE_FORMAT_FINAL = "dd-MMM-yyyy";
    private static final String NORT_PAYLOAD_PATH = "/n-nortpayload";
    private static final String START_DATE_PATH = NORT_PAYLOAD_PATH + "/n-start-date";
    private static final String END_DATE_PATH = NORT_PAYLOAD_PATH + "/n-end-date";
    private static final String DOC_GUID_PATH = NORT_PAYLOAD_PATH + "/n-doc-guid";
    private static final String NODE_TYPE_PATH = NORT_PAYLOAD_PATH + "/node-type";
    private static final String TRIANGULAR_BRACKETS = "\\<.*?>";
    private static final Long MAX_END_DATE = 20970101000000L;
    private static final int DOCCOUNT = 0;
    private static final int NODECOUNT = 1;
    private static final int SKIPCOUNT = 2;
    private static final int RETRYCOUNT = 3;

    private int splitTocCount;

    private NovusFactory novusFactory;

    @Getter
    private List<String> splitTocGuidList;
    @Getter
    @Setter
    private int thresholdValue;
    @Getter
    private boolean findSplitsAgain;
    private List<String> tocGuidList;
    @Getter
    @Setter
    private List<String> duplicateTocGuids;

    private NovusUtility novusUtility;

    private Integer nortRetryCount;

    public void retrieveNodes(
        final NortManager nortManager,
        final Writer out,
        final int[] counters,
        final int[] iParent,
        final String YYYYMMDDHHmmss,
        final List<ExcludeDocument> excludeDocuments,
        final List<ExcludeDocument> copyExcludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<RenameTocEntry> copyRenameTocEntries) throws GatherException {
        NortNode[] nortNodes = null;

        try {
            nortNodes = getRootNodesFromManager(nortManager, counters);
            if (nortNodes == null) {
                final String emptyErr = "Failed with EMPTY toc.xml for domain "
                    + nortManager.getDomainDescriptor()
                    + " and filter "
                    + nortManager.getFilterName();
                log.error(emptyErr);
                throw new GatherException(emptyErr, GatherResponse.CODE_UNHANDLED_ERROR);
            }
            final Map<String, String> tocGuidDateMap = new HashMap<>();
            printNodes(
                nortNodes,
                nortManager,
                out,
                counters,
                iParent,
                YYYYMMDDHHmmss,
                tocGuidDateMap,
                excludeDocuments,
                copyExcludeDocuments,
                renameTocEntries,
                copyRenameTocEntries);
        } catch (final GatherException e) {
            log.error("Failed with Exception in NORT");
            throw e;
        } catch (final Exception e) {
            log.error("Failed with Exception in NORT");
            throw new GatherException("NORT Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
        }
    }

    private NortNode[] getRootNodesFromManager(final NortManager nortManager, final int[] counters)
        throws GatherException {
        NortNode[] nortNodes = null;
        Integer novusNortRetryCounter = 0;
        nortRetryCount = Integer.valueOf(novusUtility.getTocRetryCount());
        while (novusNortRetryCounter < nortRetryCount) {
            try {
                nortNodes = nortManager.getRootNodes();
                break;
            } catch (final Exception exception) {
                novusNortRetryCounter = handleGetRootNodesException(novusNortRetryCounter, exception);
            }
        }
        counters[RETRYCOUNT] += novusNortRetryCounter;
        return nortNodes;
    }

    private Integer handleGetRootNodesException(final Integer novusNortRetryCounter, final Exception exception)
        throws GatherException {
        try {
            return novusUtility.handleException(exception, novusNortRetryCounter, nortRetryCount);
        } catch (final NovusException e) {
            log.error("Failed with Novus Exception in NORT");
            throw new GatherException("NORT Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
        } catch (final Exception e) {
            log.error("Failed with Exception in NORT");
            throw new GatherException("NORT Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
        }
    }

    public boolean printNodes(
        final NortNode[] nodes,
        final NortManager nortManager,
        final Writer out,
        final int[] counters,
        final int[] iParent,
        final String YYYYMMDDHHmmss,
        final Map<String, String> tocGuidDateMap,
        final List<ExcludeDocument> excludeDocuments,
        final List<ExcludeDocument> copyExcludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<RenameTocEntry> copyRenameTocEntries) throws GatherException {
        boolean docFound = true;
        if (nodes != null) {
            try {
                final List<Boolean> documentsFound = new ArrayList<>();
                for (final NortNode node : nodes) {
                    documentsFound.add(
                        printNode(
                            node,
                            nortManager,
                            out,
                            counters,
                            iParent,
                            YYYYMMDDHHmmss,
                            tocGuidDateMap,
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
                log.error("Failed writing TOC in NORT");
                throw new GatherException("Failed writing TOC in NORT ", e, GatherResponse.CODE_NOVUS_ERROR);
            } catch (final NovusException e) {
                log.error("Failed with Novus Exception in NORT");
                throw new GatherException("NORT Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
            } catch (final ParseException e) {
                log.error(e.getMessage());
                throw new GatherException(
                    "NORT ParseException for start/end node date ",
                    e,
                    GatherResponse.CODE_FILE_ERROR);
            } catch (final NortLabelParseException e) {
                log.error(e.getMessage());
                throw new GatherException("NORT LabelParseException ", e, GatherResponse.CODE_DATA_ERROR);
            }
        }
        return docFound;
    }

    public boolean printNode(
        final NortNode node,
        final NortManager nortManager,
        final Writer out,
        final int[] counters,
        final int[] iParent,
        final String YYYYMMDDHHmmss,
        final Map<String, String> tocGuidDateMap,
        final List<ExcludeDocument> excludeDocuments,
        final List<ExcludeDocument> copyExcludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<RenameTocEntry> copyRenameTocEntries)
        throws GatherException, NovusException, ParseException, NortLabelParseException {
        boolean docFound = true;
        boolean excludeDocumentFound = false;
        String documentGuid = null;

        // skip empty node or subsection node
        if (node != null && !node.getPayloadElement(NODE_TYPE_PATH).equalsIgnoreCase("subsection")) {
            docFound = false;

            final StringBuilder name = new StringBuilder();
            final StringBuilder tocGuid = new StringBuilder();
            final StringBuilder docGuid = new StringBuilder();

            counters[NODECOUNT]++;

            if (node.getPayloadElement(DOC_GUID_PATH) != null) {
                documentGuid = node.getPayloadElement(DOC_GUID_PATH).replaceAll(TRIANGULAR_BRACKETS, "");
                if (CollectionUtils.isNotEmpty(excludeDocuments) && copyExcludeDocuments != null) {
                    for (final ExcludeDocument excludeDocument : excludeDocuments) {
                        if (excludeDocument.getDocumentGuid().equalsIgnoreCase(documentGuid)) {
                            excludeDocumentFound = true;
                            copyExcludeDocuments.remove(excludeDocument);
                            break;
                        }
                    }
                }

                if (!excludeDocumentFound) {
                    docFound = true;
                }
            }

            final String guid = node.getGuid().replaceAll(TRIANGULAR_BRACKETS, "");

            if (!excludeDocumentFound) {
                tocGuid.append(EBConstants.TOC_START_GUID_ELEMENT)
                    .append(guid)
                    .append(counters[NODECOUNT])
                    .append(EBConstants.TOC_END_GUID_ELEMENT);

                if (node.getLabel() == null) { // Fail with empty Name
                    final String err = "Failed with empty node Label for guid " + node.getGuid();
                    log.error(err);
                    throw new GatherException(err, GatherResponse.CODE_NOVUS_ERROR);
                }

                final String labelRaw = node.getLabel();

                final NortLabelParser labelParser = new NortLabelParser();
                String label = labelParser.parse(labelRaw);

                // Check if name needs to be relabelled
                if (CollectionUtils.isNotEmpty(renameTocEntries) && copyRenameTocEntries != null) {
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

                final String endDate = node.getPayloadElement(END_DATE_PATH);
                final String startDate = node.getPayloadElement(START_DATE_PATH);
                final DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                final DateFormat formatterFinal = new SimpleDateFormat(DATE_FORMAT_FINAL);

                if (Long.valueOf(startDate) > Long.valueOf(YYYYMMDDHHmmss)) {
                    final Date date = formatter.parse(startDate);
                    final boolean bAncestorFound = false;

                    final String startDateFinal = formatterFinal.format(date);

                    if (!bAncestorFound) {
                        name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
                            .append(EBConstants.TOC_START_NAME_ELEMENT)
                            .append(label)
                            .append(" (effective ")
                            .append(startDateFinal)
                            .append(") ")
                            .append(EBConstants.TOC_END_NAME_ELEMENT);
                        tocGuidDateMap.put(tocGuid.toString().replaceAll(TRIANGULAR_BRACKETS, ""), startDate);
                    }
                } else if (Long.valueOf(endDate) < MAX_END_DATE) {
                    final Date date = formatter.parse(endDate);
                    final boolean bAncestorFound = false;

                    if (!bAncestorFound) {
                        final String endDateFinal = formatterFinal.format(date);

                        name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
                            .append(EBConstants.TOC_START_NAME_ELEMENT)
                            .append(label)
                            .append(" (end effective ")
                            .append(endDateFinal)
                            .append(") ")
                            .append(EBConstants.TOC_END_NAME_ELEMENT);

                        tocGuidDateMap.put(tocGuid.toString().replaceAll(TRIANGULAR_BRACKETS, ""), endDate);
                    }
                } else {
                    name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
                        .append(EBConstants.TOC_START_NAME_ELEMENT)
                        .append(label)
                        .append(EBConstants.TOC_END_NAME_ELEMENT);
                }

                if (docFound) {
                    docGuid.append(EBConstants.TOC_START_DOCUMENT_GUID_ELEMENT)
                        .append(documentGuid)
                        .append(EBConstants.TOC_END_DOCUMENT_GUID_ELEMENT);
                    docFound = true;
                    counters[DOCCOUNT]++;
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

                // To verify if the provided split Toc/Nort Node exists in the file
                if (CollectionUtils.isNotEmpty(splitTocGuidList) && guid.length() >= 33) {
                    splitTocCount++;
                    final String splitNode = StringUtils.substring(guid, 0, 33);
                    if (splitTocGuidList.contains(splitNode)) {
                        //Check if the split exceeds threshold value
                        if (splitTocCount >= thresholdValue) {
                            findSplitsAgain = true;
                        }
                        splitTocCount = 0;
                        splitTocGuidList.remove(splitNode);
                    }
                }

                //843012:Update Generator for CWB Reorder Changes. For both TOC and Doc guids a six digit suffix has been added
                //Add only first 33 characters to find the duplicate TOC
                if (guid.length() >= 33) {
                    final String guidForDupCheck = StringUtils.substring(guid, 0, 33);

                    if (tocGuidList.contains(guidForDupCheck) && !duplicateTocGuids.contains(guidForDupCheck)) {
                        duplicateTocGuids.add(guidForDupCheck);
                    }

                    tocGuidList.add(guidForDupCheck);
                }

                // Example of output:
                // <EBookToc>
                // <Name>Primary Source with Annotations</Name>
                // <DocumentGuid>I175bd1b012bb11dc8c0988fbe4566386</DocumentGuid>

                final String payloadFormatted = (name.toString() + tocGuid.toString() + docGuid.toString());

                try {
                    out.write(payloadFormatted);
                    out.write("\r\n");
                    out.flush();
                } catch (final IOException e) {
                    log.debug(e.getMessage());
                    throw new GatherException("Failed writing to NORT TOC ", e, GatherResponse.CODE_FILE_ERROR);
                }
            }

            final NortNode[] nortNodes = getChildren(node);

            if (nortNodes != null) {
                for (int i = 0; i < nortNodes.length; i += 10) {
                    final int length = (i + 10 <= nortNodes.length) ? 10 : (nortNodes.length) - i;

                    nortManager.fillNortNodes(nortNodes, i, length);
                }

                printNodes(
                    nortNodes,
                    nortManager,
                    out,
                    counters,
                    iParent,
                    YYYYMMDDHHmmss,
                    tocGuidDateMap,
                    excludeDocuments,
                    copyExcludeDocuments,
                    renameTocEntries,
                    copyRenameTocEntries);
                docFound = true;
            }
        } else {
            counters[SKIPCOUNT]++;
        }
        return docFound;
    }

    private NortNode[] getChildren(final NortNode node) throws GatherException {
        Integer novusNortRetryCounter = 0;
        nortRetryCount = Integer.valueOf(novusUtility.getTocRetryCount());
        while (novusNortRetryCounter < nortRetryCount) {
            try {
                return node.getChildren();
            } catch (final Exception exception) {
                novusNortRetryCounter = handleGetChildrenException(novusNortRetryCounter, exception);
            }
        }
        return new NortNode[]{};
    }

    private Integer handleGetChildrenException(final Integer novusNortRetryCounter, final Exception exception)
        throws GatherException {
        try {
            return novusUtility.handleException(exception, novusNortRetryCounter, nortRetryCount);
        } catch (final NovusException e) {
            log.error("Failed with Novus Exception in NORT getChildren()");
            throw new GatherException("NORT Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
        } catch (final GatherException e) {
            log.error("Failed with Exception in NORT");
            throw e;
        } catch (final Exception e) {
            log.error("Failed with Exception in NORT  getChildren()");
            throw new GatherException("NORT Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
        }
    }

    /**
     *
     * Get NORT using domain and expression filter.
     *
     * @throws ParseException
     * @throws Exception
     */
    @Override
    public GatherResponse findTableOfContents(
        final String domainName,
        final String expressionFilter,
        final File nortXmlFile,
        final Date cutoffDate,
        final List<ExcludeDocument> excludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final boolean isFinalStage,
        final boolean useReloadContent,
        final List<String> splitTocGuidList,
        final int thresholdValue) throws GatherException {
        NortManager nortManager = null;
        final int[] counters = {0, 0, 0, 0};
        final int[] iParent = {0};
        final Novus novusObject = createNovusObject(isFinalStage);

        List<ExcludeDocument> copyExcludeDocs = null;
        List<RenameTocEntry> copyRenameTocs = null;

        this.splitTocGuidList = splitTocGuidList;

        tocGuidList = new ArrayList<>();
        duplicateTocGuids = new ArrayList<>();

        this.thresholdValue = thresholdValue;

        try (Writer out =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nortXmlFile.getPath()), "UTF8"))) {

            // Make a copy of the original excluded documents to check that all have been accounted for
            if (excludeDocuments != null) {
                copyExcludeDocs = new ArrayList<>(excludeDocuments);
            }

            // Make a copy of the original rename toc entries to check that all have been accounted for
            if (renameTocEntries != null) {
                copyRenameTocs = new ArrayList<>(renameTocEntries);
            }

            // TODO: fix after Test1 testing.
            final String YYYYMMDDHHmmss = getFormattedDate(cutoffDate);

            nortManager = novusObject.getNortManager();
            nortManager.setShowChildrenCount(true);
            nortManager.setDomainDescriptor(domainName);
            nortManager.setFilterName(expressionFilter, 0);
            nortManager.setNortVersion(YYYYMMDDHHmmss);
            nortManager.setShowFutureNodes(true);
            nortManager.setUseReloadContent(useReloadContent);

            out.write(EBConstants.TOC_XML_ELEMENT);
            out.write(EBConstants.TOC_START_EBOOK_ELEMENT);

            retrieveNodes(
                nortManager,
                out,
                counters,
                iParent,
                YYYYMMDDHHmmss,
                excludeDocuments,
                copyExcludeDocs,
                renameTocEntries,
                copyRenameTocs);

            log.info(
                counters[DOCCOUNT]
                    + " documents "
                    + counters[SKIPCOUNT]
                    + " skipped nodes and "
                    + counters[NODECOUNT]
                    + " nodes in the NORT hierarchy for domain "
                    + domainName
                    + " and filter "
                    + expressionFilter);

            out.write(EBConstants.TOC_END_EBOOK_ELEMENT);
            out.flush();
            log.debug("Done with Nort.");

            checkExcludedDocsAndRenamedTocs(copyExcludeDocs, copyRenameTocs);

            return getGatherResponse(counters);
        } catch (final UnsupportedEncodingException e) {
            log.error(e.getMessage());
            throw new GatherException("NORT UTF-8 encoding error ", e, GatherResponse.CODE_FILE_ERROR);
        } catch (final FileNotFoundException e) {
            log.error(e.getMessage());
            throw new GatherException("NORT File not found ", e, GatherResponse.CODE_FILE_ERROR);
        } catch (final IOException e) {
            log.error(e.getMessage());
            throw new GatherException("NORT IOException ", e, GatherResponse.CODE_FILE_ERROR);
        } catch (final GatherException e) {
            log.error(e.getMessage());
            throw e;
        } catch (final Exception e) {
            log.error(e.getMessage());
            throw new GatherException("Failure in findTOC() ", e, GatherResponse.CODE_DATA_ERROR);
        } finally {
            novusObject.shutdownMQ();
        }
    }

    private String getFormattedDate(final Date cutoffDate) {
        final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        if (cutoffDate == null) {
            return formatter.format(new Date());
        } else {
            return formatter.format(cutoffDate);
        }
    }

    private Novus createNovusObject(final boolean isFinalStage) throws GatherException {
        Novus novusObject = null;
        try {
            novusObject = novusFactory.createNovus(isFinalStage);
        } catch (final NovusException e) {
            throw new GatherException(
                "Novus error occurred while creating Novus object " + e,
                GatherResponse.CODE_NOVUS_ERROR);
        }
        return novusObject;
    }

    private GatherResponse getGatherResponse(final int[] counters) {
        final GatherResponse gatherResponse = new GatherResponse();
        gatherResponse.setDocCount(counters[DOCCOUNT]);
        gatherResponse.setNodeCount(counters[NODECOUNT]);
        gatherResponse.setSkipCount(counters[SKIPCOUNT]);
        gatherResponse.setRetryCount(counters[RETRYCOUNT]);
        gatherResponse.setPublishStatus("TOC NORT Step Completed");
        gatherResponse.setFindSplitsAgain(findSplitsAgain);
        gatherResponse.setSplitTocGuidList(splitTocGuidList);
        gatherResponse.setDuplicateTocGuids(duplicateTocGuids);
        return gatherResponse;
    }

    private void checkExcludedDocsAndRenamedTocs(
        final List<ExcludeDocument> copyExcludeDocs,
        final List<RenameTocEntry> copyRenameTocs) throws GatherException {
        if (CollectionUtils.isNotEmpty(copyExcludeDocs)) {
            final StringBuilder unaccountedExcludedDocs = new StringBuilder();
            for (final ExcludeDocument excludeDocument : copyExcludeDocs) {
                unaccountedExcludedDocs.append(excludeDocument.getDocumentGuid() + ",");
            }
            throw new GatherException("Failure in findTOC(): Not all Excluded Docs are accounted for and those are " + unaccountedExcludedDocs.toString(), GatherResponse.CODE_DATA_ERROR);
        }
        if (CollectionUtils.isNotEmpty(copyRenameTocs)) {
            final StringBuilder unaccountedRenameTocs = new StringBuilder();
            for (final RenameTocEntry renameTocEntry : copyRenameTocs) {
                unaccountedRenameTocs.append(renameTocEntry.getTocGuid() + ",");
            }
            throw new GatherException("Failure in findTOC(): Not all Rename TOCs are accounted for and those are " + unaccountedRenameTocs.toString(), GatherResponse.CODE_DATA_ERROR);
        }
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
