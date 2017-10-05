package com.thomsonreuters.uscl.ereader.gather.service;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Creates toc.xml from the Novus NORT XML from Codes Workbench
 *
 * @author Dong Kim
 *
 */
public class NovusNortFileServiceImpl implements NovusNortFileService {
    private static final Logger LOG = LogManager.getLogger(NovusNortFileServiceImpl.class);
    private static final int DOCCOUNT = 0;
    private static final int NODECOUNT = 1;
    private static final int SKIPCOUNT = 2;
    private static final int RETRYCOUNT = 3;
    private static int splitTocCount;
    private List<String> splitTocGuidList;
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

    @Override
    public boolean isFindSplitsAgain() {
        return findSplitsAgain;
    }

    public void setFindSplitsAgain(final boolean findSplitsAgain) {
        this.findSplitsAgain = findSplitsAgain;
    }

    public int getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(final int thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    @Override
    public List<String> getSplitTocGuidList() {
        return splitTocGuidList;
    }

    public void retrieveNodes(
        final List<RelationshipNode> rootNodes,
        final Writer out,
        final int[] counters,
        final int[] iParent,
        final String YYYYMMDDHHmmss,
        final List<ExcludeDocument> excludeDocuments,
        final List<ExcludeDocument> copyExcludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<RenameTocEntry> copyRenameTocEntries) throws GatherException {
        try {
            if (rootNodes == null || rootNodes.size() == 0) {
                final String emptyErr = "Failed with no root nodes";
                LOG.error(emptyErr);
                final GatherException ge = new GatherException(emptyErr, GatherResponse.CODE_UNHANDLED_ERROR);
                throw ge;
            }
            final Map<String, String> tocGuidDateMap = new HashMap<>();
            printNodes(
                rootNodes,
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
            LOG.error("Failed with Exception in NORT file");
            throw e;
        } catch (final Exception e) {
            LOG.error("Failed with Exception in NORT file");
            final GatherException ge = new GatherException("NORT Exception ", e, GatherResponse.CODE_DATA_ERROR);
            throw ge;
        }
    }

    public boolean printNodes(
        final List<RelationshipNode> rootNodes,
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
        if (rootNodes != null) {
            try {
                final List<Boolean> documentsFound = new ArrayList<>();
                Collections.sort(rootNodes);
                for (final RelationshipNode node : rootNodes) {
                    documentsFound.add(
                        printNode(
                            node,
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
                LOG.error("Failed writing TOC from NORT file");
                final GatherException ge =
                    new GatherException("Failed writing TOC in NORT ", e, GatherResponse.CODE_DATA_ERROR);
                throw ge;
            } catch (final ParseException e) {
                LOG.error(e.getMessage());
                final GatherException ge = new GatherException(
                    "NORT ParseException for start/end node date ",
                    e,
                    GatherResponse.CODE_FILE_ERROR);
                throw ge;
            }
        }
        return docFound;
    }

    public boolean printNode(
        final RelationshipNode node,
        final Writer out,
        final int[] counters,
        final int[] iParent,
        final String YYYYMMDDHHmmss,
        final Map<String, String> tocGuidDateMap,
        final List<ExcludeDocument> excludeDocuments,
        final List<ExcludeDocument> copyExcludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<RenameTocEntry> copyRenameTocEntries) throws GatherException, ParseException {
        boolean docFound = true;
        boolean excludeDocumentFound = false;
        String documentGuid = null;

        // skip empty node or subsection node
        if (node != null && !node.getNodeType().equalsIgnoreCase("subsection")) {
            docFound = false;

            final StringBuffer name = new StringBuffer();
            final StringBuffer tocGuid = new StringBuffer();
            final StringBuffer docGuid = new StringBuffer();

            counters[NODECOUNT]++;

            if (StringUtils.isNotBlank(node.getDocumentGuid())) {
                documentGuid = node.getDocumentGuid();
                if ((excludeDocuments != null) && (excludeDocuments.size() > 0) && (copyExcludeDocuments != null)) {
                    for (final ExcludeDocument excludeDocument : excludeDocuments) {
                        // if (excludeDocument.getDocumentGuid().equalsIgnoreCase(documentGuid)) {
                        if (StringUtils.containsIgnoreCase(documentGuid, excludeDocument.getDocumentGuid())) {
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

            final String guid = node.getNortGuid();

            if (!excludeDocumentFound) {
                tocGuid.append(EBConstants.TOC_START_GUID_ELEMENT)
                    .append(guid)
                    .append(counters[NODECOUNT])
                    .append(EBConstants.TOC_END_GUID_ELEMENT);

                if (node.getLabel() == null) // Fail with empty Name
                {
                    final String err = "Failed with empty node Label for guid " + node.getNortGuid();
                    LOG.error(err);
                    final GatherException ge = new GatherException(err, GatherResponse.CODE_DATA_ERROR);
                    throw ge;
                }

                final CharSequenceTranslator escapeXml =
                    StringEscapeUtils.ESCAPE_XML10.with(NumericEntityEscaper.between(0x7f, Integer.MAX_VALUE));

                String label = escapeXml.translate(node.getLabel().replaceAll("\\<.*?>", ""));

                // Check if name needs to be relabelled
                if ((renameTocEntries != null) && (renameTocEntries.size() > 0) && (copyRenameTocEntries != null)) {
                    for (final RenameTocEntry renameTocEntry : renameTocEntries) {
                        if (StringUtils.containsIgnoreCase(guid, renameTocEntry.getTocGuid())) {
                            label = escapeXml.translate(renameTocEntry.getNewLabel());
                            copyRenameTocEntries.remove(renameTocEntry);
                            break;
                        }
                    }
                }

                // Normalize label text
                label = NormalizationRulesUtil.applyTableOfContentNormalizationRules(label);

                final String endDate = node.getEndDateStr();
                final String startDate = node.getStartDateStr();
                final DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                final DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");

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
                        tocGuidDateMap.put(tocGuid.toString().replaceAll("\\<.*?>", ""), startDate);
                    }
                } else if (Long.valueOf(endDate) < Long.valueOf("20970101000000")) {
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

                        tocGuidDateMap.put(tocGuid.toString().replaceAll("\\<.*?>", ""), endDate);
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

                if (node.getChildNodes().size() == 0) {
                    if (!docFound) {
                        docGuid.append("<MissingDocument></MissingDocument>");
                        docFound = true;
                    }
                    docGuid.append(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
                } else {
                    iParent[0]++;
                }
                // To verify if the provided split Toc/Nort Node exists in the file
                if (splitTocGuidList != null && guid.toString().length() >= 33) {
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

                try {
                    out.write(payloadFormatted);
                    out.write("\r\n");
                    out.flush();
                } catch (final IOException e) {
                    LOG.debug(e.getMessage());
                    final GatherException ge =
                        new GatherException("Failed writing to NORT TOC ", e, GatherResponse.CODE_FILE_ERROR);
                    throw ge;
                }
            }

            final List<RelationshipNode> nortNodes = node.getChildNodes();
            if (nortNodes != null && nortNodes.size() > 0) {
                printNodes(
                    nortNodes,
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
            // LOG.debug(" skipping subsection " );
            counters[SKIPCOUNT]++;
        }
        return docFound;
    }

    /**
     *
     * Create TOC from NORT nodes
     *
     * @throws ParseException
     * @throws Exception
     */
    @Override
    public GatherResponse findTableOfContents(
        final List<RelationshipNode> rootNodes,
        final File nortXmlFile,
        final Date cutoffDate,
        final List<ExcludeDocument> excludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<String> splitTocGuidList,
        final int thresholdValue) throws GatherException {
        final int[] counters = {0, 0, 0, 0};
        final int[] iParent = {0};
        String publishStatus = "TOC NORT Step Completed";
        final GatherResponse gatherResponse = new GatherResponse();

        final Date date = new Date();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        List<ExcludeDocument> copyExcludDocs = null;

        if (splitTocGuidList != null) {
            this.splitTocGuidList = splitTocGuidList;
        }

        tocGuidList = new ArrayList<>();
        duplicateTocGuids = new ArrayList<>();

        this.thresholdValue = thresholdValue;

        // Make a copy of the original excluded documents to check that all have been accounted for
        if (excludeDocuments != null) {
            copyExcludDocs = new ArrayList<>(Arrays.asList(new ExcludeDocument[excludeDocuments.size()]));
        }

        List<RenameTocEntry> copyRenameTocs = null;

        // Make a copy of the original rename toc entries to check that all have been accounted for
        if (renameTocEntries != null) {
            copyRenameTocs = new ArrayList<>(Arrays.asList(new RenameTocEntry[renameTocEntries.size()]));
        }

        try (Writer out =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nortXmlFile.getPath()), "UTF-8"))) {
            if (excludeDocuments != null) {
                Collections.copy(copyExcludDocs, excludeDocuments);
            }

            if (renameTocEntries != null) {
                Collections.copy(copyRenameTocs, renameTocEntries);
            }

            // TODO: fix after Test1 testing.
            final String YYYYMMDDHHmmss;
            if (cutoffDate == null) {
                YYYYMMDDHHmmss = formatter.format(date);
            } else {
                YYYYMMDDHHmmss = formatter.format(cutoffDate);
            }

            ;
            out.write(EBConstants.TOC_XML_ELEMENT);
            out.write(EBConstants.TOC_START_EBOOK_ELEMENT);

            retrieveNodes(
                rootNodes,
                out,
                counters,
                iParent,
                YYYYMMDDHHmmss,
                excludeDocuments,
                copyExcludDocs,
                renameTocEntries,
                copyRenameTocs);

            LOG.info(
                counters[DOCCOUNT]
                    + " documents "
                    + counters[SKIPCOUNT]
                    + " skipped nodes  and "
                    + counters[NODECOUNT]
                    + " nodes");

            out.write(EBConstants.TOC_END_EBOOK_ELEMENT);
            out.flush();
            out.close();
            LOG.debug("Done with Nort.");
        } catch (final UnsupportedEncodingException e) {
            LOG.error(e.getMessage());
            final GatherException ge =
                new GatherException("NORT UTF-8 encoding error ", e, GatherResponse.CODE_FILE_ERROR);
            publishStatus = "CWB TOC Step Failed UTF-8 encoding error";
            throw ge;
        } catch (final FileNotFoundException e) {
            LOG.error(e.getMessage());
            final GatherException ge = new GatherException("NORT File not found ", e, GatherResponse.CODE_FILE_ERROR);
            publishStatus = "CWB TOC Step Failed File not found";
            throw ge;
        } catch (final IOException e) {
            LOG.error(e.getMessage());
            final GatherException ge = new GatherException("NORT IOException ", e, GatherResponse.CODE_FILE_ERROR);
            publishStatus = "CWB TOC Step Failed IOException";
            throw ge;
        } catch (final GatherException e) {
            LOG.error(e.getMessage());
            publishStatus = "CWB TOC Step Failed GatherException";
            throw e;
        } finally {
            try {
                if ((copyExcludDocs != null) && (copyExcludDocs.size() > 0)) {
                    final StringBuffer unaccountedExcludedDocs = new StringBuffer();
                    for (final ExcludeDocument excludeDocument : copyExcludDocs) {
                        unaccountedExcludedDocs.append(excludeDocument.getDocumentGuid() + ",");
                    }
                    final GatherException ge = new GatherException(
                        "Not all Excluded Docs are accounted for and those are " + unaccountedExcludedDocs.toString());
                    publishStatus = "CWB TOC Step Failed with Not all Excluded Docs accounted for error";
                    throw ge;
                }
                if ((copyRenameTocs != null) && (copyRenameTocs.size() > 0)) {
                    final StringBuffer unaccountedRenameTocs = new StringBuffer();
                    for (final RenameTocEntry renameTocEntry : copyRenameTocs) {
                        unaccountedRenameTocs.append(renameTocEntry.getTocGuid() + ",");
                    }
                    final GatherException ge = new GatherException(
                        "Not all Rename TOCs are accounted for and those are " + unaccountedRenameTocs.toString());
                    publishStatus = "CWB TOC Step Failed with Not all Rename TOCs accounted for error";
                    throw ge;
                }
            } catch (final Exception e) {
                LOG.error(e.getMessage());
                final GatherException ge =
                    new GatherException("Failure in createToc() ", e, GatherResponse.CODE_DATA_ERROR);
                throw ge;
            } finally {
                gatherResponse.setDocCount(counters[DOCCOUNT]);
                gatherResponse.setNodeCount(counters[NODECOUNT]);
                gatherResponse.setSkipCount(counters[SKIPCOUNT]);
                gatherResponse.setRetryCount(counters[RETRYCOUNT]);
                gatherResponse.setPublishStatus(publishStatus);
                gatherResponse.setDuplicateTocGuids(duplicateTocGuids);
                gatherResponse.setSplitTocGuidList(this.splitTocGuidList);
            }
        }

        return gatherResponse;
    }
}
