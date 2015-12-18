/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
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
public class TocServiceImpl implements TocService {

	private NovusFactory novusFactory;

	private NovusUtility novusUtility;

	private static final Logger LOG = Logger.getLogger(TocServiceImpl.class);

	private Integer tocRetryCount;
	
	List<String> splitTocGuidList = null;
	private static int splitTocCount = 0;
	private int thresholdValue;
	private boolean findSplitsAgain = false;
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
	
	public List<String> getSplitTocGuidList() {
		return splitTocGuidList;
	}
	
	public int getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(int thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public void retrieveNodes(String guid, TOC _tocManager, Writer out,
			int[] counter, int[] docCounter, int[] retryCounter, int[] intParent, ArrayList<ExcludeDocument> excludeDocuments,
			ArrayList<ExcludeDocument> copyExcludeDocuments, ArrayList<RenameTocEntry> renameTocEntries, ArrayList<RenameTocEntry> copyRenameTocEntries)
			throws GatherException {

		TOCNode[] tocNodes = null;
		TOCNode tocNode = null;

		try {

			// This is the counter for checking how many Novus retries we
			// are making
			Integer novusTocRetryCounter = 0;
			tocRetryCount = new Integer(novusUtility.getTocRetryCount());
			while (novusTocRetryCounter < tocRetryCount) {
				try {
					tocNode = _tocManager.getNode(guid);
					break;
				} catch (final Exception exception) {
					try {
						novusTocRetryCounter = novusUtility.handleException(
								exception, novusTocRetryCounter, tocRetryCount);
					} catch (NovusException e) {
						LOG.error("Failed with Novus Exception in TOC");
						GatherException ge = new GatherException(
								"TOC Novus Exception ", e,
								GatherResponse.CODE_NOVUS_ERROR);
						throw ge;
					} catch (Exception e) {
						LOG.error("Failed with Novus Exception in TOC");
						GatherException ge = new GatherException(
								"TOC Novus Exception ", e,
								GatherResponse.CODE_NOVUS_ERROR);
						throw ge;
					}
				}
			}
			if (tocNode == null)
			{
				String emptyErr = 
				"Failed with EMPTY toc.xml for collection " + _tocManager.getCollection() + " and guid " + guid ;
				LOG.error(emptyErr);
				GatherException ge = new GatherException(
						emptyErr, 
						GatherResponse.CODE_UNHANDLED_ERROR);
				throw ge;
			}

			retryCounter[0] += novusTocRetryCounter;
			// tocNodes = _tocManager.getRootNodes();

			tocNodes = new TOCNode[1];
			tocNodes[0] = tocNode;
			printNodes(tocNodes, _tocManager, out, counter, docCounter,
					intParent, excludeDocuments, copyExcludeDocuments, renameTocEntries, copyRenameTocEntries);
		}  catch (GatherException e) {
			LOG.error("Failed with GatherException in TOC");
			throw e;
		} catch (Exception e) {
			LOG.debug("Failed with Novus Exception in TOC");
			GatherException ge = new GatherException("TOC Novus Exception ", e,
					GatherResponse.CODE_NOVUS_ERROR);
			throw ge;
		}

	}

	public boolean printNodes(TOCNode[] nodes, TOC _tocManager, Writer out,
			int[] counter, int[] docCounter, int[] iParent, ArrayList<ExcludeDocument> excludeDocuments, ArrayList<ExcludeDocument> copyExcludeDocuments,
			ArrayList<RenameTocEntry> renameTocEntries, ArrayList<RenameTocEntry> copyRenameTocEntries)
			throws GatherException {
		boolean docFound = true;

		if (nodes != null) {
			try {
				List<Boolean> documentsFound = new ArrayList<Boolean>();
				for (TOCNode node : nodes) {
					documentsFound.add(printNode(node, _tocManager, out, counter,
							docCounter, iParent, excludeDocuments, copyExcludeDocuments, renameTocEntries, copyRenameTocEntries));
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
				LOG.error("Failed writing TOC in TOC");
				GatherException ge = new GatherException(
						"Failed writing TOC in TOC ", e,
						GatherResponse.CODE_NOVUS_ERROR);
				throw ge;
			} catch (NovusException e) {
				LOG.error("Failed with Novus Exception in TOC");
				GatherException ge = new GatherException(
						"TOC Novus Exception ", e,
						GatherResponse.CODE_NOVUS_ERROR);
				throw ge;
			}
		}
		return docFound;
	}

	public boolean printNode(TOCNode node, TOC _tocManager, Writer out,
			int[] counter, int[] docCounter, int[] iParent, ArrayList<ExcludeDocument> excludeDocuments, ArrayList<ExcludeDocument> copyExcludeDocuments,
			ArrayList<RenameTocEntry> renameTocEntries, ArrayList<RenameTocEntry> copyRenameTocEntries)
			throws GatherException, NovusException {
		boolean docFound = true;
		boolean excludeDocumentFound = false;
		String documentGuid = null;
		
		if (node != null) {
			documentGuid = node.getDocGuid();
			if (documentGuid != null) {
				documentGuid = documentGuid.replaceAll("\\<.*?>", "");
				if ((excludeDocuments != null) &&(excludeDocuments.size() > 0)
						&& (copyExcludeDocuments != null)){
					for (ExcludeDocument excludeDocument : excludeDocuments) {
						if (excludeDocument.getDocumentGuid().equalsIgnoreCase(documentGuid)) {
							excludeDocumentFound = true;
							copyExcludeDocuments.remove(excludeDocument);
							break;
						} 
					}
				}
			}
			
			docFound = false;
			StringBuffer name = new StringBuffer();
			if (node.getName() == null ) // Fail with empty Name
			{
				String err = "Failed with empty node Name for guid " + node.getGuid();
					LOG.error(err);
			GatherException ge = new GatherException(
					err,
					GatherResponse.CODE_NOVUS_ERROR);
				throw ge;
			}
			
			if (!excludeDocumentFound) {
				String label = node.getName().replaceAll("\\<.*?>", "");
				String guid = node.getGuid().replaceAll("\\<.*?>", "");
				
				// Check if name needs to be relabelled
				if ((renameTocEntries != null) &&(renameTocEntries.size() > 0)
						&& (copyRenameTocEntries != null)){
					for (RenameTocEntry renameTocEntry : renameTocEntries) {
						if (renameTocEntry.getTocGuid().equalsIgnoreCase(guid)) {
							label = StringEscapeUtils.escapeXml(renameTocEntry.getNewLabel());
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
				StringBuffer tocGuid = new StringBuffer();
				tocGuid.append(EBConstants.TOC_START_GUID_ELEMENT)
						.append(guid)
						.append(EBConstants.TOC_END_GUID_ELEMENT);
	
				StringBuffer docGuid = new StringBuffer();
	
				if (documentGuid != null) {
					docFound = true;
					docCounter[0]++;
					docGuid.append(EBConstants.TOC_START_DOCUMENT_GUID_ELEMENT)
							.append(documentGuid)
							.append(EBConstants.TOC_END_DOCUMENT_GUID_ELEMENT);
					}
	
	
				if (node.getChildrenCount() == 0) {
					if (docFound == false) {
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
				if (splitTocGuidList != null && splitTocGuidList.size() > 0 && guid.toString().length() >= 33){
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
				
				if(tocGuidList.contains(guid) && !duplicateTocGuids.contains(guid)){
					duplicateTocGuids.add(guid);
				}
				tocGuidList.add(guid);
	
				String payloadFormatted = (name.toString() + tocGuid.toString() + docGuid
						.toString());
	
				counter[0]++;
	
				// LOG.debug(" document count : " + docCounter + " out of " +
				// counter + " nodes" );
	
				try {
					out.write(payloadFormatted);
					out.write("\r\n");
					out.flush();
				} catch (IOException e) {
					LOG.error(e.getMessage());
					GatherException ge = new GatherException(
							"Failed writing to TOC ", e,
							GatherResponse.CODE_FILE_ERROR);
					throw ge;
				}
			}

			TOCNode[] tocNodes = null;

			try {

				// This is the counter for checking how many Novus retries we
				// are making
				Integer novusTocRetryCounter = 0;
				tocRetryCount = new Integer(novusUtility.getTocRetryCount());				
				while (novusTocRetryCounter < tocRetryCount) {
					try {
						tocNodes = node.getChildren();
						break;
					} catch (final Exception exception) {
						try {
							novusTocRetryCounter = novusUtility
									.handleException(exception,
											novusTocRetryCounter, tocRetryCount);
						} catch (NovusException e) {
							LOG.error("Failed with Novus Exception in TOC getChildren()");
							GatherException ge = new GatherException(
									"TOC Novus Exception ", e,
									GatherResponse.CODE_NOVUS_ERROR);
							throw ge;
						} catch (GatherException e) {
							LOG.error("Failed with GatherException in TOC");
							throw e;
						} catch (Exception e) {
							LOG.error("Failed with Exception in TOC getChildren()");
							GatherException ge = new GatherException(
									"TOC Novus Exception ", e,
									GatherResponse.CODE_NOVUS_ERROR);
							throw ge;
						}
					}
				}
			} catch (GatherException e) {
				LOG.error("Failed with GatherException in TOC");
				throw e;
			}  catch (Exception e) {
				LOG.error("Failed with Novus Exception in TOC");
				GatherException ge = new GatherException(
						"TOC Novus Exception ", e,
						GatherResponse.CODE_NOVUS_ERROR);
				throw ge;
			}

			if (tocNodes != null) {
				printNodes(tocNodes, _tocManager, out, counter,
						docCounter, iParent, excludeDocuments, copyExcludeDocuments, renameTocEntries, copyRenameTocEntries);
				docFound = true;
			}
		}
		return docFound;
	}

	/**
	 * 
	 * Get TOC using domain and expression filter.
	 * 
	 * @throws Exception
	 */
	public GatherResponse findTableOfContents(String guid,
			String collectionName, File tocXmlFile, ArrayList<ExcludeDocument> excludeDocuments, ArrayList<RenameTocEntry> renameTocEntries,
			boolean isFinalStage, List<String> splitTocGuidList, int thresholdValue) throws GatherException {
		TOC _tocManager = null;
		Writer out = null;
		int[] counter = { 0 };
		int[] docCounter = { 0 };
		int[] retryCounter = { 0 };
		int[] iParent = { 0 };
		GatherResponse gatherResponse = new GatherResponse();

		String publishStatus = "TOC Step Completed";

		/*** for ebook builder we will always get Collection. ***/
		String type = EBConstants.COLLECTION_TYPE;

		 Novus novusObject = null;
		try {
			novusObject = novusFactory.createNovus(isFinalStage);
		} catch (NovusException e) {
			GatherException ge = new GatherException(
					"Novus error occurred while creating Novus object " + e,
					GatherResponse.CODE_NOVUS_ERROR);
			throw ge;
		}
			
		_tocManager = getTocObject(collectionName, type, novusObject);
		
		ArrayList<ExcludeDocument> copyExcludDocs = null;
		
		this.splitTocGuidList = splitTocGuidList;		
		
		tocGuidList = new ArrayList<String>();
		duplicateTocGuids = new ArrayList<String>();
		
		this.thresholdValue = thresholdValue;
		
		// Make a copy of the original excluded documents to check that all have been accounted for
		if (excludeDocuments != null) {
			copyExcludDocs = new ArrayList<ExcludeDocument>(Arrays.asList(new ExcludeDocument[excludeDocuments.size()]));
		}
		
		ArrayList<RenameTocEntry> copyRenameTocs = null;
		
		// Make a copy of the original rename TOC entries to check that all have been accounted for
		if (renameTocEntries != null) {
			copyRenameTocs = new ArrayList<RenameTocEntry>(Arrays.asList(new RenameTocEntry[renameTocEntries.size()]));
		}

		try {
			
			if (excludeDocuments != null) {
				Collections.copy(copyExcludDocs, excludeDocuments);
			}
			
			if (renameTocEntries != null) {
				Collections.copy(copyRenameTocs, renameTocEntries);
			}

			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(tocXmlFile.getPath()), "UTF8"));
			out.write(EBConstants.TOC_XML_ELEMENT);
			out.write(EBConstants.TOC_START_EBOOK_ELEMENT);

			retrieveNodes(guid, _tocManager, out, counter, docCounter,
					retryCounter, iParent, excludeDocuments, copyExcludDocs, renameTocEntries, copyRenameTocs);

			LOG.info(docCounter[0] + " documents and " + counter[0]
					+ " nodes in the TOC hierarchy for guid " + guid
					+ " collection " + collectionName);

			out.write(EBConstants.TOC_END_EBOOK_ELEMENT);
			out.flush();
			out.close();
			LOG.debug("Done with Toc.");
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage());
			GatherException ge = new GatherException(
					"TOC UTF-8 encoding error ", e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus = "TOC Step Failed UTF-8 encoding error";
			throw ge;
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage());
			GatherException ge = new GatherException("TOC File not found ", e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus = "TOC Step Failed File not found";
			throw ge;
		} catch (IOException e) {
			LOG.error(e.getMessage());
			GatherException ge = new GatherException("TOC IOException ", e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus = "TOC Step Failed IOException";
			throw ge;
		} catch (GatherException e) {
			LOG.error(e.getMessage());
			publishStatus = "TOC Step Failed GatherException";
			throw e;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			GatherException ge = new GatherException("TOC IOException ", e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus = "TOC Step Failed IOException";
			throw ge;
		}finally {
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
					publishStatus = "TOC Step Failed with Not all Excluded Docs accounted for error";
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
					publishStatus = "TOC Step Failed with Not all Rename TOCs accounted for error";
					throw ge;				
				}
				
			} catch (IOException e) {
				LOG.error(e.getMessage());
				GatherException ge = new GatherException(
						"TOC Cannot close toc.xml ", e,
						GatherResponse.CODE_FILE_ERROR);
				throw ge;
			} catch (Exception e) {
				LOG.error(e.getMessage());
				GatherException ge = new GatherException(
						"Failure in findTOC() ", e,
						GatherResponse.CODE_DATA_ERROR);
				throw ge;
			} finally {
				gatherResponse.setDocCount(docCounter[0]);
				gatherResponse.setNodeCount(counter[0]);
				gatherResponse.setRetryCount(retryCounter[0]);
				gatherResponse.setPublishStatus(publishStatus);
				gatherResponse.setFindSplitsAgain(findSplitsAgain);
				gatherResponse.setSplitTocGuidList(this.splitTocGuidList);
				gatherResponse.setDuplicateTocGuids(this.duplicateTocGuids);
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
	private TOC getTocObject(String name, String type, Novus novus) {
		TOC toc = novus.getTOC();

		if (!"".equals(type)
				&& type.equalsIgnoreCase(EBConstants.COLLECTION_SET_TYPE)) {
			toc.setCollectionSet(name);
		} else if (!"".equals(type)
				&& type.equalsIgnoreCase(EBConstants.COLLECTION_TYPE)) {
			toc.setCollection(name);
		}
		toc.setShowChildrenCount(true);
		// String dateTime = novusAPIHelper.getCurrentDateTime();
		// toc.setTOCVersion(dateTime);
		return toc;
	}

	@Required
	public void setNovusFactory(NovusFactory factory) {
		this.novusFactory = factory;
	}

	@Required
	public void setNovusUtility(NovusUtility novusUtil) {
		this.novusUtility = novusUtil;
	}

}
