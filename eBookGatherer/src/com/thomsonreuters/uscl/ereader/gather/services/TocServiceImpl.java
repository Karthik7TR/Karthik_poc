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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;
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

	public void retrieveNodes(String guid, TOC _tocManager, Writer out,
			int[] counter, int[] docCounter, int[] retryCounter, int[] intParent, ArrayList<ExcludeDocument> excludeDocuments)
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
					intParent, excludeDocuments);
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
			int[] counter, int[] docCounter, int[] iParent, ArrayList<ExcludeDocument> excludeDocuments)
			throws GatherException {
		boolean docFound = true;

		if (nodes != null) {
			try {
				for (TOCNode node : nodes) {
					docFound = printNode(node, _tocManager, out, counter,
							docCounter, iParent, excludeDocuments);
				}
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
			int[] counter, int[] docCounter, int[] iParent, ArrayList<ExcludeDocument> excludeDocuments)
			throws GatherException, NovusException {
		boolean docFound = true;
		boolean excludeDocumentFound = false;
		String documentGuid = null;
		
		if (node != null) {
			documentGuid = node.getDocGuid();
			if (documentGuid != null) {
				documentGuid = documentGuid.replaceAll("\\<.*?>", "");
				if ((excludeDocuments != null) &&(excludeDocuments.size() > 0)){
					for (ExcludeDocument excludeDocument : excludeDocuments) {
						if (excludeDocument.getDocumentGuid().equalsIgnoreCase(documentGuid)) {
							excludeDocumentFound = true;
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
			name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
					.append(EBConstants.TOC_START_NAME_ELEMENT)
					.append(node.getName().replaceAll("\\<.*?>", ""))
					.append(EBConstants.TOC_END_NAME_ELEMENT);
			StringBuffer tocGuid = new StringBuffer();
			tocGuid.append(EBConstants.TOC_START_GUID_ELEMENT)
					.append(node.getGuid().replaceAll("\\<.*?>", ""))
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
				docFound = printNodes(tocNodes, _tocManager, out, counter,
						docCounter, iParent, excludeDocuments);
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
	@Override
	public GatherResponse findTableOfContents(String guid,
			String collectionName, File tocXmlFile, ArrayList<ExcludeDocument> excludeDocuments) throws GatherException {
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
		Novus novusObject = novusFactory.createNovus();
		_tocManager = getTocObject(collectionName, type, novusObject);

		try {

			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(tocXmlFile.getPath()), "UTF8"));
			out.write(EBConstants.TOC_XML_ELEMENT);
			out.write(EBConstants.TOC_START_EBOOK_ELEMENT);

			retrieveNodes(guid, _tocManager, out, counter, docCounter,
					retryCounter, iParent, excludeDocuments);

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
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				LOG.error(e.getMessage());
				GatherException ge = new GatherException(
						"TOC Cannot close toc.xml ", e,
						GatherResponse.CODE_FILE_ERROR);
				throw ge;
			} catch (Exception e) {
				LOG.error(e.getMessage());
				GatherException ge = new GatherException(
						"Failure to update job stats ", e,
						GatherResponse.CODE_FILE_ERROR);
				throw ge;
			} finally {
				gatherResponse.setDocCount(docCounter[0]);
				gatherResponse.setNodeCount(counter[0]);
				gatherResponse.setRetryCount(retryCounter[0]);
				gatherResponse.setPublishStatus(publishStatus);

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
