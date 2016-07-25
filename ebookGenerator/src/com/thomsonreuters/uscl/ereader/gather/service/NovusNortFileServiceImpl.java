/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;

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
	private static int splitTocCount = 0;
	List<String> splitTocGuidList = null;
	private int thresholdValue;
	private boolean findSplitsAgain = false;;
	private List<String> tocGuidList = null;
	private List<String> duplicateTocGuids = null;
	
	public List<String> getDuplicateTocGuids() {
		return duplicateTocGuids;
	}

	public void setDuplicateTocGuids(List<String> duplicateTocGuids) {
		this.duplicateTocGuids = duplicateTocGuids;
	}

	public boolean isFindSplitsAgain() {
		return findSplitsAgain;
	}

	public void setFindSplitsAgain(boolean findSplitsAgain) {
		this.findSplitsAgain = findSplitsAgain;
	}

	public int getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(int thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public List<String> getSplitTocGuidList() {
		return splitTocGuidList;
	}

	public void retrieveNodes(List<RelationshipNode> rootNodes, Writer out,
			int[] counters, int[] iParent, String YYYYMMDDHHmmss, List<ExcludeDocument> excludeDocuments,
			List<ExcludeDocument> copyExcludeDocuments, List<RenameTocEntry> renameTocEntries,
			List<RenameTocEntry> copyRenameTocEntries)
			throws GatherException {

		try {
			if (rootNodes == null || rootNodes.size() == 0)
			{
				String emptyErr = 
				"Failed with no root nodes" ;
				LOG.error(emptyErr);
				GatherException ge = new GatherException(
						emptyErr, 
						GatherResponse.CODE_UNHANDLED_ERROR);
				throw ge;
			}
			Map<String, String> tocGuidDateMap = new HashMap<String, String>();
			printNodes(rootNodes, out, counters, iParent,
					YYYYMMDDHHmmss, tocGuidDateMap, excludeDocuments, copyExcludeDocuments, renameTocEntries, copyRenameTocEntries);
		} 
		catch (GatherException e) {
			LOG.error("Failed with Exception in NORT file");
			throw e;
		}
		catch (Exception e) {
			LOG.error("Failed with Exception in NORT file");
			GatherException ge = new GatherException("NORT Exception ", e,
					GatherResponse.CODE_DATA_ERROR);
			throw ge;
		}

	}

	public boolean printNodes(List<RelationshipNode> rootNodes,
			Writer out, int[] counters, int[] iParent, String YYYYMMDDHHmmss,
			Map<String, String> tocGuidDateMap,	List<ExcludeDocument> excludeDocuments, 
			List<ExcludeDocument> copyExcludeDocuments, List<RenameTocEntry> renameTocEntries,
			List<RenameTocEntry> copyRenameTocEntries) throws GatherException,
			ParseException {
		boolean docFound = true;
		if (rootNodes != null) {
			try {
				List<Boolean> documentsFound = new ArrayList<Boolean>();
				Collections.sort(rootNodes);
				for (RelationshipNode node : rootNodes) {
					documentsFound.add(printNode(node, out, counters, iParent, YYYYMMDDHHmmss, 
							tocGuidDateMap, excludeDocuments, copyExcludeDocuments, renameTocEntries, copyRenameTocEntries));
				}
				
				// Only add MissingDocuments if all nodes return false
				docFound = documentsFound.contains(true);
				
				if (iParent[0] > 0) {
					if (docFound == false) {
						out.write("<MissingDocument></MissingDocument>");
					}
					out.write(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
					out.write("\r\n");
					out.flush();

					iParent[0]--;

				}
			} catch (IOException e) {
				LOG.error("Failed writing TOC from NORT file");
				GatherException ge = new GatherException(
						"Failed writing TOC in NORT ", e,
						GatherResponse.CODE_DATA_ERROR);
				throw ge;
			} catch (ParseException e) {
				LOG.error(e.getMessage());
				GatherException ge = new GatherException(
						"NORT ParseException for start/end node date ", e,
						GatherResponse.CODE_FILE_ERROR);
				throw ge;
			}
		}
		return docFound;
	}

	public boolean printNode(RelationshipNode node, Writer out, int[] counters, int[] iParent, String YYYYMMDDHHmmss,
			Map<String, String> tocGuidDateMap, List<ExcludeDocument> excludeDocuments,
			List<ExcludeDocument> copyExcludeDocuments, List<RenameTocEntry> renameTocEntries,
			List<RenameTocEntry> copyRenameTocEntries) throws GatherException, ParseException {
		boolean docFound = true;
		boolean excludeDocumentFound = false;
		String documentGuid = null;

		// skip empty node or subsection node
		if (node != null && !node.getNodeType().equalsIgnoreCase("subsection")) {
			docFound = false;

			StringBuffer name = new StringBuffer();
			StringBuffer tocGuid = new StringBuffer();
			StringBuffer docGuid = new StringBuffer();
			
			counters[NODECOUNT]++;			
			
			if (StringUtils.isNotBlank(node.getDocumentGuid())) {
				documentGuid = node.getDocumentGuid();
				if ((excludeDocuments != null) &&(excludeDocuments.size() > 0)
						&& (copyExcludeDocuments != null)){
					for (ExcludeDocument excludeDocument : excludeDocuments) {
//						if (excludeDocument.getDocumentGuid().equalsIgnoreCase(documentGuid)) {
						if (StringUtils.containsIgnoreCase(documentGuid, excludeDocument.getDocumentGuid())) {
							excludeDocumentFound = true;
							copyExcludeDocuments.remove(excludeDocument);
							break;
						}
					}
				}
				
				if(!excludeDocumentFound){
					docFound = true;
				}
			}
			
			String guid = node.getNortGuid();
				
			if (!excludeDocumentFound){
				tocGuid.append(EBConstants.TOC_START_GUID_ELEMENT)
				.append(guid)
				.append(counters[NODECOUNT])
				.append(EBConstants.TOC_END_GUID_ELEMENT);
				
				if (node.getLabel() == null ) // Fail with empty Name
				{
					String err = "Failed with empty node Label for guid " + node.getNortGuid();
						LOG.error(err);
				GatherException ge = new GatherException(
						err,
						GatherResponse.CODE_DATA_ERROR);
					throw ge;
				}
				
				String label = StringEscapeUtils.escapeXml(node.getLabel().replaceAll("\\<.*?>", ""));
				
				// Check if name needs to be relabelled
				if ((renameTocEntries != null) &&(renameTocEntries.size() > 0)
						&& (copyRenameTocEntries != null)){
					for (RenameTocEntry renameTocEntry : renameTocEntries) {
						if (StringUtils.containsIgnoreCase(guid, renameTocEntry.getTocGuid())) {
							label = StringEscapeUtils.escapeXml(renameTocEntry.getNewLabel());
							copyRenameTocEntries.remove(renameTocEntry);
							break;
						}
					}
				}
				
				// Normalize label text
				label = NormalizationRulesUtil.applyTableOfContentNormalizationRules(label);
				
				String endDate = node.getEndDateStr();
				String startDate = node.getStartDateStr();
				DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");

				if (Long.valueOf(startDate) > Long.valueOf(YYYYMMDDHHmmss)) {
					Date date = formatter.parse(startDate);
					boolean bAncestorFound = false;

					String startDateFinal = formatterFinal.format(date);

					if (bAncestorFound == false) {
						name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
								.append(EBConstants.TOC_START_NAME_ELEMENT)
								.append(label).append(" (effective ")
								.append(startDateFinal).append(") ")
								.append(EBConstants.TOC_END_NAME_ELEMENT);
						tocGuidDateMap.put(
								tocGuid.toString().replaceAll("\\<.*?>", ""),
								startDate);
					}
				} else if (Long.valueOf(endDate) < Long
						.valueOf("20970101000000")) {

					Date date = formatter.parse(endDate);
					boolean bAncestorFound = false;

					if (bAncestorFound == false) {

						String endDateFinal = formatterFinal.format(date);

						name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
								.append(EBConstants.TOC_START_NAME_ELEMENT)
								.append(label).append(" (end effective ")
								.append(endDateFinal).append(") ")
								.append(EBConstants.TOC_END_NAME_ELEMENT);

						tocGuidDateMap.put(
								tocGuid.toString().replaceAll("\\<.*?>", ""),
								endDate);
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
					if (docFound == false) {
						docGuid.append("<MissingDocument></MissingDocument>");
						docFound = true;
					}
					docGuid.append(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
				} else {
					iParent[0]++;
				}
				// To verify if the provided split Toc/Nort Node exists in the file
				if (splitTocGuidList != null && guid.toString().length() >= 33){
					splitTocCount++;
					String splitNode = StringUtils.substring(guid.toString(), 0,33);
					if( splitTocGuidList.contains(splitNode)){
						if (splitTocCount >= thresholdValue){
							this.findSplitsAgain = true;
						}
						splitTocCount = 0;						
						this.splitTocGuidList.remove(splitNode);
					}
				}
				
				//843012:Update Generator for CWB Reorder Changes. For both TOC and Doc guids a six digit suffix has been added
				//Add only first 33 characters to find the duplicate TOC
				if (guid.toString().length() >= 33){
					String guidForDupCheck = StringUtils.substring(guid.toString(), 0,33);
					
					if(tocGuidList.contains(guidForDupCheck) && !duplicateTocGuids.contains(guidForDupCheck)){
						duplicateTocGuids.add(guidForDupCheck);
					}		
					
					tocGuidList.add(guidForDupCheck);
				}
				
				String payloadFormatted = (name.toString() + tocGuid.toString() + docGuid.toString());

				try {
					out.write(payloadFormatted);
					out.write("\r\n");
					out.flush();
				} catch (IOException e) {
					LOG.debug(e.getMessage());
					GatherException ge = new GatherException(
							"Failed writing to NORT TOC ", e,
							GatherResponse.CODE_FILE_ERROR);
					throw ge;
				}
			}
			
			List<RelationshipNode> nortNodes = node.getChildNodes();
			if (nortNodes != null && nortNodes.size() > 0) {
				printNodes(nortNodes, out, counters, iParent, YYYYMMDDHHmmss, tocGuidDateMap, 
						excludeDocuments, copyExcludeDocuments, renameTocEntries, copyRenameTocEntries);
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
	public GatherResponse findTableOfContents(List<RelationshipNode> rootNodes, File nortXmlFile, 
			Date cutoffDate, List<ExcludeDocument> excludeDocuments, List<RenameTocEntry> renameTocEntries, List<String> splitTocGuidList, int thresholdValue)
			throws GatherException {
		
		Writer out = null;
		int[] counters = { 0, 0, 0, 0 };
		int[] iParent = { 0 };
		String publishStatus = "TOC NORT Step Completed";
		GatherResponse gatherResponse = new GatherResponse();

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		
		ArrayList<ExcludeDocument> copyExcludDocs = null;
		
		if(splitTocGuidList != null){
			this.splitTocGuidList = splitTocGuidList;
		}
		
		tocGuidList = new ArrayList<String>();
		duplicateTocGuids = new ArrayList<String>();
		
		this.thresholdValue = thresholdValue;
		
		// Make a copy of the original excluded documents to check that all have been accounted for
		if (excludeDocuments != null) {
			copyExcludDocs = new ArrayList<ExcludeDocument>(Arrays.asList(new ExcludeDocument[excludeDocuments.size()]));
		}
		
		ArrayList<RenameTocEntry> copyRenameTocs = null;
		
		// Make a copy of the original rename toc entries to check that all have been accounted for
		if(renameTocEntries != null) {
			copyRenameTocs = new ArrayList<RenameTocEntry>(Arrays.asList(new RenameTocEntry[renameTocEntries.size()]));
		}

		try {
			
			if (excludeDocuments != null) {
				Collections.copy(copyExcludDocs, excludeDocuments);
			}
			
			if (renameTocEntries != null) {
				Collections.copy(copyRenameTocs, renameTocEntries);
			}
			
			// TODO: fix after Test1 testing.
			String YYYYMMDDHHmmss;
			if (cutoffDate == null) {
				YYYYMMDDHHmmss = formatter.format(date);
			} else {
				YYYYMMDDHHmmss = formatter.format(cutoffDate);
			}

			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(nortXmlFile.getPath()), "UTF-8"));
			out.write(EBConstants.TOC_XML_ELEMENT);
			out.write(EBConstants.TOC_START_EBOOK_ELEMENT);

			retrieveNodes(rootNodes, out, counters, iParent, YYYYMMDDHHmmss, excludeDocuments, copyExcludDocs, renameTocEntries, copyRenameTocs);

			LOG.info(counters[DOCCOUNT] + " documents " + counters[SKIPCOUNT]
					+ " skipped nodes  and " + counters[NODECOUNT] + " nodes");

			out.write(EBConstants.TOC_END_EBOOK_ELEMENT);
			out.flush();
			out.close();
			LOG.debug("Done with Nort.");
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage());
			GatherException ge = new GatherException(
					"NORT UTF-8 encoding error ", e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus = "CWB TOC Step Failed UTF-8 encoding error";
			throw ge;
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage());
			GatherException ge = new GatherException("NORT File not found ", e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus = "CWB TOC Step Failed File not found";
			throw ge;
		} catch (IOException e) {
			LOG.error(e.getMessage());
			GatherException ge = new GatherException("NORT IOException ", e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus = "CWB TOC Step Failed IOException";
			throw ge;
		} catch (GatherException e) {
			LOG.error(e.getMessage());
			publishStatus = "CWB TOC Step Failed GatherException";
			throw e;
		} finally {
			try {
				out.close();
				if ((copyExcludDocs != null) && (copyExcludDocs.size() > 0)) {
					StringBuffer unaccountedExcludedDocs = new StringBuffer();
					for (ExcludeDocument excludeDocument : copyExcludDocs) {
						unaccountedExcludedDocs.append(excludeDocument.getDocumentGuid() + ",");
					}
					GatherException ge = new GatherException(
							"Not all Excluded Docs are accounted for and those are " + unaccountedExcludedDocs.toString()
					);
					publishStatus = "CWB TOC Step Failed with Not all Excluded Docs accounted for error";
					throw ge;				
				}
				if ((copyRenameTocs != null) && (copyRenameTocs.size() > 0)) {
					StringBuffer unaccountedRenameTocs = new StringBuffer();
					for (RenameTocEntry renameTocEntry : copyRenameTocs) {
						unaccountedRenameTocs.append(renameTocEntry.getTocGuid() + ",");
					}
					GatherException ge = new GatherException(
							"Not all Rename TOCs are accounted for and those are " + unaccountedRenameTocs.toString()
					);
					publishStatus = "CWB TOC Step Failed with Not all Rename TOCs accounted for error";
					throw ge;				
				}
			} catch (IOException e) {
				LOG.error(e.getMessage());
				GatherException ge = new GatherException(
						"CWB TOC Cannot close toc.xml ", e,
						GatherResponse.CODE_FILE_ERROR);
				publishStatus = "CWB TOC Step Failed Cannot close toc.xml";
				throw ge;
			} catch (Exception e) {
				LOG.error(e.getMessage());
				GatherException ge = new GatherException(
						"Failure in createToc() ", e,
						GatherResponse.CODE_DATA_ERROR);
				throw ge;
			}finally {
				gatherResponse.setDocCount(counters[DOCCOUNT]);
				gatherResponse.setNodeCount(counters[NODECOUNT]);
				gatherResponse.setSkipCount(counters[SKIPCOUNT]);
				gatherResponse.setRetryCount(counters[RETRYCOUNT]);
				gatherResponse.setPublishStatus(publishStatus);
				gatherResponse.setDuplicateTocGuids(this.duplicateTocGuids);
				gatherResponse.setSplitTocGuidList(this.splitTocGuidList);
			}
		}

		return gatherResponse;
	}
}
