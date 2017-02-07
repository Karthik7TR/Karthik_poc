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
import java.util.Arrays;
import java.util.Collections;
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
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
public class NortServiceImpl implements NortService
{
    private NovusFactory novusFactory;

    private static final Logger LOG = LogManager.getLogger(NortServiceImpl.class);
    private static final int DOCCOUNT = 0;
    private static final int NODECOUNT = 1;
    private static final int SKIPCOUNT = 2;
    private static final int RETRYCOUNT = 3;
    List<String> splitTocGuidList = null;
    private static int splitTocCount = 0;
    private int thresholdValue;
    private boolean findSplitsAgain = false;
    private List<String> tocGuidList = null;
    private List<String> duplicateTocGuids = null;

    private NovusUtility novusUtility;

    private Integer nortRetryCount;

    public List<String> getDuplicateTocGuids()
    {
        return duplicateTocGuids;
    }

    public void setDuplicateTocGuids(final List<String> duplicateTocGuids)
    {
        this.duplicateTocGuids = duplicateTocGuids;
    }

    public boolean isFindSplitsAgain()
    {
        return findSplitsAgain;
    }

    public List<String> getSplitTocGuidList()
    {
        return splitTocGuidList;
    }

    public int getThresholdValue()
    {
        return thresholdValue;
    }

    public void setThresholdValue(final int thresholdValue)
    {
        this.thresholdValue = thresholdValue;
    }

    public void retrieveNodes(
        final NortManager _nortManager,
        final Writer out,
        final int[] counters,
        final int[] iParent,
        final String YYYYMMDDHHmmss,
        final List<ExcludeDocument> excludeDocuments,
        final List<ExcludeDocument> copyExcludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<RenameTocEntry> copyRenameTocEntries) throws GatherException
    {
        NortNode[] nortNodes = null;

        try
        {
            // This is the counter for checking how many Novus retries we
            // are making
            Integer novusNortRetryCounter = 0;
            nortRetryCount = new Integer(novusUtility.getTocRetryCount());
            while (novusNortRetryCounter < nortRetryCount)
            {
                try
                {
                    nortNodes = _nortManager.getRootNodes();
                    break;
                }
                catch (final Exception exception)
                {
                    try
                    {
                        novusNortRetryCounter =
                            novusUtility.handleException(exception, novusNortRetryCounter, nortRetryCount);
                    }
                    catch (final NovusException e)
                    {
                        LOG.error("Failed with Novus Exception in NORT");
                        final GatherException ge =
                            new GatherException("NORT Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
                        throw ge;
                    }
                    catch (final Exception e)
                    {
                        LOG.error("Failed with Exception in NORT");
                        final GatherException ge = new GatherException("NORT Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
                        throw ge;
                    }
                }
            }
            if (nortNodes == null)
            {
                final String emptyErr = "Failed with EMPTY toc.xml for domain "
                    + _nortManager.getDomainDescriptor()
                    + " and filter "
                    + _nortManager.getFilterName();
                LOG.error(emptyErr);
                final GatherException ge = new GatherException(emptyErr, GatherResponse.CODE_UNHANDLED_ERROR);
                throw ge;
            }
            counters[RETRYCOUNT] += novusNortRetryCounter;
            final Map<String, String> tocGuidDateMap = new HashMap<String, String>();
            printNodes(
                nortNodes,
                _nortManager,
                out,
                counters,
                iParent,
                YYYYMMDDHHmmss,
                tocGuidDateMap,
                excludeDocuments,
                copyExcludeDocuments,
                renameTocEntries,
                copyRenameTocEntries);
        }
        catch (final GatherException e)
        {
            LOG.error("Failed with Exception in NORT");
            throw e;
        }
        catch (final Exception e)
        {
            LOG.error("Failed with Exception in NORT");
            final GatherException ge = new GatherException("NORT Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
            throw ge;
        }
    }

    public boolean printNodes(
        final NortNode[] nodes,
        final NortManager _nortManager,
        final Writer out,
        final int[] counters,
        final int[] iParent,
        final String YYYYMMDDHHmmss,
        final Map<String, String> tocGuidDateMap,
        final List<ExcludeDocument> excludeDocuments,
        final List<ExcludeDocument> copyExcludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<RenameTocEntry> copyRenameTocEntries) throws GatherException, ParseException
    {
        boolean docFound = true;
        if (nodes != null)
        {
            try
            {
                final List<Boolean> documentsFound = new ArrayList<Boolean>();
                for (final NortNode node : nodes)
                {
                    documentsFound.add(
                        printNode(
                            node,
                            _nortManager,
                            out,
                            counters,
                            iParent,
                            YYYYMMDDHHmmss,
                            tocGuidDateMap,
                            excludeDocuments,
                            copyExcludeDocuments,
                            renameTocEntries,
                            copyRenameTocEntries));
                    // if (docFound == false)
                    // {
                    // LOG.debug("docFound set false for " + node.getLabel());
                    // }
                }

                // Only add MissingDocuments if all nodes return false
                docFound = documentsFound.contains(true);

                if (iParent[0] > 0)
                {
                    if (docFound == false)
                    {
                        out.write("<MissingDocument></MissingDocument>");
                    }
                    out.write(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
                    out.write("\r\n");
                    out.flush();

                    iParent[0]--;
                }
            }
            catch (final IOException e)
            {
                LOG.error("Failed writing TOC in NORT");
                final GatherException ge =
                    new GatherException("Failed writing TOC in NORT ", e, GatherResponse.CODE_NOVUS_ERROR);
                throw ge;
            }
            catch (final NovusException e)
            {
                LOG.error("Failed with Novus Exception in NORT");
                final GatherException ge = new GatherException("NORT Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
                throw ge;
            }
            catch (final ParseException e)
            {
                LOG.error(e.getMessage());
                final GatherException ge = new GatherException(
                    "NORT ParseException for start/end node date ",
                    e,
                    GatherResponse.CODE_FILE_ERROR);
                throw ge;
            }
            catch (final NortLabelParseException e)
            {
                LOG.error(e.getMessage());
                final GatherException ge =
                    new GatherException("NORT LabelParseException ", e, GatherResponse.CODE_DATA_ERROR);
                throw ge;
            }
        }
        return docFound;
    }

    public boolean printNode(
        final NortNode node,
        final NortManager _nortManager,
        final Writer out,
        final int[] counters,
        final int[] iParent,
        final String YYYYMMDDHHmmss,
        final Map<String, String> tocGuidDateMap,
        final List<ExcludeDocument> excludeDocuments,
        final List<ExcludeDocument> copyExcludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final List<RenameTocEntry> copyRenameTocEntries)
        throws GatherException, NovusException, ParseException, NortLabelParseException
    {
        boolean docFound = true;
        boolean excludeDocumentFound = false;
        String documentGuid = null;

        // skip empty node or subsection node
        if (node != null && !node.getPayloadElement("/n-nortpayload/node-type").equalsIgnoreCase("subsection"))
        {
            docFound = false;

            final StringBuffer name = new StringBuffer();
            final StringBuffer tocGuid = new StringBuffer();
            final StringBuffer docGuid = new StringBuffer();

            counters[NODECOUNT]++;

            if (node.getPayloadElement("/n-nortpayload/n-doc-guid") != null)
            {
                documentGuid = node.getPayloadElement("/n-nortpayload/n-doc-guid").replaceAll("\\<.*?>", "");
                if ((excludeDocuments != null) && (excludeDocuments.size() > 0) && (copyExcludeDocuments != null))
                {
                    for (final ExcludeDocument excludeDocument : excludeDocuments)
                    {
                        if (excludeDocument.getDocumentGuid().equalsIgnoreCase(documentGuid))
                        {
                            excludeDocumentFound = true;
                            copyExcludeDocuments.remove(excludeDocument);
                            break;
                        }
                    }
                }

                if (!excludeDocumentFound)
                {
                    docFound = true;
                }
            }

            final String guid = node.getGuid().replaceAll("\\<.*?>", "");

            if (!excludeDocumentFound)
            {
                tocGuid.append(EBConstants.TOC_START_GUID_ELEMENT)
                    .append(guid)
                    .append(counters[NODECOUNT])
                    .append(EBConstants.TOC_END_GUID_ELEMENT);

                if (node.getLabel() == null) // Fail with empty Name
                {
                    final String err = "Failed with empty node Label for guid " + node.getGuid();
                    LOG.error(err);
                    final GatherException ge = new GatherException(err, GatherResponse.CODE_NOVUS_ERROR);
                    throw ge;
                }

                final String labelRaw = node.getLabel();

                final NortLabelParser labelParser = new NortLabelParser();
                String label = labelParser.parse(labelRaw);
                //String labelHeading = labelRaw.substring(labelRaw.indexOf("<heading>"), labelRaw.indexOf("</heading"));

                // Check if name needs to be relabelled
                if ((renameTocEntries != null) && (renameTocEntries.size() > 0) && (copyRenameTocEntries != null))
                {
                    for (final RenameTocEntry renameTocEntry : renameTocEntries)
                    {
                        if (renameTocEntry.getTocGuid().equalsIgnoreCase(guid))
                        {
                            final CharSequenceTranslator escapeXml =
                                StringEscapeUtils.ESCAPE_XML10.with(NumericEntityEscaper.between(0x7f, Integer.MAX_VALUE));
                            label = escapeXml.translate(renameTocEntry.getNewLabel());
                            copyRenameTocEntries.remove(renameTocEntry);
                            break;
                        }
                    }
                }

                // Normalize label text
                label = NormalizationRulesUtil.applyTableOfContentNormalizationRules(label);

                final String endDate = node.getPayloadElement("/n-nortpayload/n-end-date");
                final String startDate = node.getPayloadElement("/n-nortpayload/n-start-date");
                final DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                final DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");

                if (Long.valueOf(startDate) > Long.valueOf(YYYYMMDDHHmmss))
                {
                    final Date date = formatter.parse(startDate);
                    final boolean bAncestorFound = false;

                    final String startDateFinal = formatterFinal.format(date);

                    // String payload = node.getPayload();
                    // Determine if date is already used by this ancestor
                    // for (String tocAncestor : tocGuidDateMap.keySet())
                    // {
                    // if (payload.contains(tocAncestor) )
                    // {
                    // String ancestorDate = tocGuidDateMap.get(tocAncestor);
                    // if(ancestorDate.equals(startDate))
                    // {
                    // bAncestorFound = true;
                    // name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT).append(EBConstants.TOC_START_NAME_ELEMENT).append(node.getLabel().replaceAll("\\<.*?>","")).append(EBConstants.TOC_END_NAME_ELEMENT);
                    // break;
                    // }
                    //
                    // }
                    // }
                    if (bAncestorFound == false)
                    {
                        name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
                            .append(EBConstants.TOC_START_NAME_ELEMENT)
                            .append(label)
                            .append(" (effective ")
                            .append(startDateFinal)
                            .append(") ")
                            .append(EBConstants.TOC_END_NAME_ELEMENT);
                        tocGuidDateMap.put(tocGuid.toString().replaceAll("\\<.*?>", ""), startDate);
                    }
                }
                else if (Long.valueOf(endDate) < Long.valueOf("20970101000000"))
                {
                    final Date date = formatter.parse(endDate);
                    final boolean bAncestorFound = false;

                    // String payload = node.getPayload();
                    // Determine if date is already used by this ancestor
                    // for (String tocAncestor : tocGuidDateMap.keySet())
                    // {
                    // if (payload.contains(tocAncestor) )
                    // {
                    // String ancestorDate = tocGuidDateMap.get(tocAncestor);
                    // if(ancestorDate.equals(endDate))
                    // {
                    // bAncestorFound = true;
                    // name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT).append(EBConstants.TOC_START_NAME_ELEMENT).append(node.getLabel().replaceAll("\\<.*?>","")).append(EBConstants.TOC_END_NAME_ELEMENT);
                    // break;
                    // }
                    //
                    // }
                    // }
                    if (bAncestorFound == false)
                    {
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
                }
                else
                {
                    name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
                        .append(EBConstants.TOC_START_NAME_ELEMENT)
                        .append(label)
                        .append(EBConstants.TOC_END_NAME_ELEMENT);
                }

                if (docFound)
                {
                    docGuid.append(EBConstants.TOC_START_DOCUMENT_GUID_ELEMENT)
                        .append(documentGuid)
                        .append(EBConstants.TOC_END_DOCUMENT_GUID_ELEMENT);
                    docFound = true;
                    counters[DOCCOUNT]++;
                }

                if (node.getChildrenCount() == 0)
                {
                    if (docFound == false)
                    {
                        docGuid.append("<MissingDocument></MissingDocument>");
                        docFound = true;
                    }
                    docGuid.append(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
                }
                else
                {
                    iParent[0]++;
                }

                // To verify if the provided split Toc/Nort Node exists in the file
                if (splitTocGuidList != null && splitTocGuidList.size() > 0 && guid.toString().length() >= 33)
                {
                    splitTocCount++;
                    final String splitNode = StringUtils.substring(guid.toString(), 0, 33);
                    if (splitTocGuidList.contains(splitNode))
                    {
                        //Check if the split exceeds threshold value
                        if (splitTocCount >= thresholdValue)
                        {
                            findSplitsAgain = true;
                        }
                        splitTocCount = 0;
                        splitTocGuidList.remove(splitNode);
                    }
                }

                //843012:Update Generator for CWB Reorder Changes. For both TOC and Doc guids a six digit suffix has been added
                //Add only first 33 characters to find the duplicate TOC
                if (guid.toString().length() >= 33)
                {
                    final String guidForDupCheck = StringUtils.substring(guid.toString(), 0, 33);

                    if (tocGuidList.contains(guidForDupCheck) && !duplicateTocGuids.contains(guidForDupCheck))
                    {
                        duplicateTocGuids.add(guidForDupCheck);
                    }

                    tocGuidList.add(guidForDupCheck);
                }

                // Example of output:
                // <EBookToc>
                // <Name>Primary Source with Annotations</Name>
                // <DocumentGuid>I175bd1b012bb11dc8c0988fbe4566386</DocumentGuid>

                final String payloadFormatted = (name.toString() + tocGuid.toString() + docGuid.toString());

                // LOG.debug(" document count : " + docCounter + " out of " +
                // counter + " nodes" );

                try
                {
                    out.write(payloadFormatted);
                    out.write("\r\n");
                    out.flush();
                }
                catch (final IOException e)
                {
                    LOG.debug(e.getMessage());
                    final GatherException ge =
                        new GatherException("Failed writing to NORT TOC ", e, GatherResponse.CODE_FILE_ERROR);
                    throw ge;
                }
            }

            NortNode[] nortNodes = null;
            Integer novusNortRetryCounter = 0;
            nortRetryCount = new Integer(novusUtility.getTocRetryCount());
            while (novusNortRetryCounter < nortRetryCount)
            {
                try
                {
                    nortNodes = node.getChildren();
                    break;
                }
                catch (final Exception exception)
                {
                    try
                    {
                        novusNortRetryCounter =
                            novusUtility.handleException(exception, novusNortRetryCounter, nortRetryCount);
                    }
                    catch (final NovusException e)
                    {
                        LOG.error("Failed with Novus Exception in NORT getChildren()");
                        final GatherException ge =
                            new GatherException("NORT Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
                        throw ge;
                    }
                    catch (final GatherException e)
                    {
                        LOG.error("Failed with Exception in NORT");
                        throw e;
                    }
                    catch (final Exception e)
                    {
                        LOG.error("Failed with Exception in NORT  getChildren()");
                        final GatherException ge = new GatherException("NORT Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
                        throw ge;
                    }
                }
            }

            if (nortNodes != null)
            {
                for (int i = 0; i < nortNodes.length; i += 10)
                {
                    final int length = (i + 10 <= nortNodes.length) ? 10 : (nortNodes.length) - i;

                    _nortManager.fillNortNodes(nortNodes, i, length);
                }

                printNodes(
                    nortNodes,
                    _nortManager,
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
        }
        else
        {
            // LOG.debug(" skipping subsection " );
            counters[SKIPCOUNT]++;
        }
        return docFound;
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
        final int thresholdValue) throws GatherException
    {
        NortManager _nortManager = null;
        Writer out = null;
        final int[] counters = {0, 0, 0, 0};
        final int[] iParent = {0};
        String publishStatus = "TOC NORT Step Completed";
        final GatherResponse gatherResponse = new GatherResponse();

        Novus novusObject = null;
        try
        {
            novusObject = novusFactory.createNovus(isFinalStage);
        }
        catch (final NovusException e)
        {
            final GatherException ge = new GatherException(
                "Novus error occurred while creating Novus object " + e,
                GatherResponse.CODE_NOVUS_ERROR);
            throw ge;
        }

        final Date date = new Date();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        List<ExcludeDocument> copyExcludDocs = null;

        this.splitTocGuidList = splitTocGuidList;

        tocGuidList = new ArrayList<String>();
        duplicateTocGuids = new ArrayList<String>();

        this.thresholdValue = thresholdValue;

        // Make a copy of the original excluded documents to check that all have been accounted for
        if (excludeDocuments != null)
        {
            copyExcludDocs =
                new ArrayList<ExcludeDocument>(Arrays.asList(new ExcludeDocument[excludeDocuments.size()]));
        }

        List<RenameTocEntry> copyRenameTocs = null;

        // Make a copy of the original rename toc entries to check that all have been accounted for
        if (renameTocEntries != null)
        {
            copyRenameTocs = new ArrayList<RenameTocEntry>(Arrays.asList(new RenameTocEntry[renameTocEntries.size()]));
        }

        try
        {
            if (excludeDocuments != null)
            {
                Collections.copy(copyExcludDocs, excludeDocuments);
            }

            if (renameTocEntries != null)
            {
                Collections.copy(copyRenameTocs, renameTocEntries);
            }

            // TODO: fix after Test1 testing.
            String YYYYMMDDHHmmss;
            if (cutoffDate == null)
            {
                YYYYMMDDHHmmss = formatter.format(date);
            }
            else
            {
                YYYYMMDDHHmmss = formatter.format(cutoffDate);
            }
            // String YYYYMMDDHHmmss = "20120202111111";

            _nortManager = novusObject.getNortManager();
            _nortManager.setShowChildrenCount(true);
            _nortManager.setDomainDescriptor(domainName);
            _nortManager.setFilterName(expressionFilter, 0);
            _nortManager.setNortVersion(YYYYMMDDHHmmss);
            _nortManager.setShowFutureNodes(true);
            _nortManager.setUseReloadContent(useReloadContent);

            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nortXmlFile.getPath()), "UTF8"));
            out.write(EBConstants.TOC_XML_ELEMENT);
            out.write(EBConstants.TOC_START_EBOOK_ELEMENT);

            retrieveNodes(
                _nortManager,
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
                    + " nodes in the NORT hierarchy for domain "
                    + domainName
                    + " and filter "
                    + expressionFilter);

            out.write(EBConstants.TOC_END_EBOOK_ELEMENT);
            out.flush();
            out.close();
            LOG.debug("Done with Nort.");
        }
        catch (final UnsupportedEncodingException e)
        {
            LOG.error(e.getMessage());
            final GatherException ge = new GatherException("NORT UTF-8 encoding error ", e, GatherResponse.CODE_FILE_ERROR);
            publishStatus = "TOC NORT Step Failed UTF-8 encoding error";
            throw ge;
        }
        catch (final FileNotFoundException e)
        {
            LOG.error(e.getMessage());
            final GatherException ge = new GatherException("NORT File not found ", e, GatherResponse.CODE_FILE_ERROR);
            publishStatus = "TOC NORT Step Failed File not found";
            throw ge;
        }
        catch (final IOException e)
        {
            LOG.error(e.getMessage());
            final GatherException ge = new GatherException("NORT IOException ", e, GatherResponse.CODE_FILE_ERROR);
            publishStatus = "TOC NORT Step Failed IOException";
            throw ge;
        }
        catch (final GatherException e)
        {
            LOG.error(e.getMessage());
            publishStatus = "TOC NORT Step Failed GatherException";
            throw e;
        }
        finally
        {
            try
            {
                out.close();
                if ((copyExcludDocs != null) && (copyExcludDocs.size() > 0))
                {
                    final StringBuffer unaccountedExcludedDocs = new StringBuffer();
                    for (final ExcludeDocument excludeDocument : copyExcludDocs)
                    {
                        unaccountedExcludedDocs.append(excludeDocument.getDocumentGuid() + ",");
                    }
                    final GatherException ge = new GatherException(
                        "Not all Excluded Docs are accounted for and those are " + unaccountedExcludedDocs.toString());
                    publishStatus = "TOC NORT Step Failed with Not all Excluded Docs accounted for error";
                    throw ge;
                }
                if ((copyRenameTocs != null) && (copyRenameTocs.size() > 0))
                {
                    final StringBuffer unaccountedRenameTocs = new StringBuffer();
                    for (final RenameTocEntry renameTocEntry : copyRenameTocs)
                    {
                        unaccountedRenameTocs.append(renameTocEntry.getTocGuid() + ",");
                    }
                    final GatherException ge = new GatherException(
                        "Not all Rename TOCs are accounted for and those are " + unaccountedRenameTocs.toString());
                    publishStatus = "TOC NORT Step Failed with Not all Rename TOCs accounted for error";
                    throw ge;
                }
            }
            catch (final IOException e)
            {
                LOG.error(e.getMessage());
                final GatherException ge =
                    new GatherException("NORT Cannot close toc.xml ", e, GatherResponse.CODE_FILE_ERROR);
                publishStatus = "TOC NORT Step Failed Cannot close toc.xml";
                throw ge;
            }
            catch (final Exception e)
            {
                LOG.error(e.getMessage());
                final GatherException ge = new GatherException("Failure in findTOC() ", e, GatherResponse.CODE_DATA_ERROR);
                throw ge;
            }
            finally
            {
                gatherResponse.setDocCount(counters[DOCCOUNT]);
                gatherResponse.setNodeCount(counters[NODECOUNT]);
                gatherResponse.setSkipCount(counters[SKIPCOUNT]);
                gatherResponse.setRetryCount(counters[RETRYCOUNT]);
                gatherResponse.setPublishStatus(publishStatus);
                gatherResponse.setFindSplitsAgain(findSplitsAgain);
                gatherResponse.setSplitTocGuidList(this.splitTocGuidList);
                gatherResponse.setDuplicateTocGuids(duplicateTocGuids);
            }

            novusObject.shutdownMQ();
        }

        return gatherResponse;
    }

    @Required
    public void setNovusFactory(final NovusFactory factory)
    {
        novusFactory = factory;
    }

    @Required
    public void setNovusUtility(final NovusUtility novusUtil)
    {
        novusUtility = novusUtil;
    }
}
