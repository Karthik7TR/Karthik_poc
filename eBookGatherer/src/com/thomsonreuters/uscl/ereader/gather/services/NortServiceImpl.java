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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;
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
public class NortServiceImpl implements NortService {

	private NovusFactory novusFactory;

	private static final Logger LOG = Logger.getLogger(NortServiceImpl.class);
	private static final int DOCCOUNT = 0;
	private static final int NODECOUNT = 1;
	private static final int SKIPCOUNT = 2;
	private static final int RETRYCOUNT = 3;

	private NovusUtility novusUtility;

	private Integer nortRetryCount;

	public void retrieveNodes(NortManager _nortManager, Writer out,
			int[] counters, int[] iParent, String YYYYMMDDHHmmss)
			throws GatherException {

		NortNode[] nortNodes = null;

		try {
			// This is the counter for checking how many Novus retries we
			// are making
			Integer novusNortRetryCounter = 0;
			nortRetryCount = new Integer(novusUtility.getTocRetryCount());			
			while (novusNortRetryCounter < nortRetryCount) {
				try {
					nortNodes = _nortManager.getRootNodes();
					break;
				} catch (final Exception exception) {
					try {
						novusNortRetryCounter = novusUtility.handleException(
								exception, novusNortRetryCounter,
								nortRetryCount);
					} catch (NovusException e) {
						LOG.error("Failed with Novus Exception in NORT");
						GatherException ge = new GatherException(
								"NORT Novus Exception ", e,
								GatherResponse.CODE_NOVUS_ERROR);
						throw ge;
					} catch (Exception e) {
						LOG.error("Failed with Exception in NORT");
						GatherException ge = new GatherException(
								"NORT Exception ", e,
								GatherResponse.CODE_NOVUS_ERROR);
						throw ge;
					}
				}
			}
			if (nortNodes == null)
			{
				String emptyErr = 
				"Failed with EMPTY toc.xml for domain " + _nortManager.getDomainDescriptor() + " and filter " + _nortManager.getFilterName() ;
				LOG.error(emptyErr);
				GatherException ge = new GatherException(
						emptyErr, 
						GatherResponse.CODE_UNHANDLED_ERROR);
				throw ge;
			}
			counters[RETRYCOUNT] += novusNortRetryCounter;
			Map<String, String> tocGuidDateMap = new HashMap<String, String>();
			printNodes(nortNodes, _nortManager, out, counters, iParent,
					YYYYMMDDHHmmss, tocGuidDateMap);
		} catch (Exception e) {
			LOG.error("Failed with Exception in NORT");
			GatherException ge = new GatherException("NORT Exception ", e,
					GatherResponse.CODE_NOVUS_ERROR);
			throw ge;
		}

	}

	public boolean printNodes(NortNode[] nodes, NortManager _nortManager,
			Writer out, int[] counters, int[] iParent, String YYYYMMDDHHmmss,
			Map<String, String> tocGuidDateMap) throws GatherException,
			ParseException {
		boolean docFound = true;
		if (nodes != null) {
			try {
				for (NortNode node : nodes) {
					docFound = printNode(node, _nortManager, out, counters,
							iParent, YYYYMMDDHHmmss, tocGuidDateMap);
					// if (docFound == false)
					// {
					// LOG.debug("docFound set false for " + node.getLabel());
					// }
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
				LOG.error("Failed writing TOC in NORT");
				GatherException ge = new GatherException(
						"Failed writing TOC in NORT ", e,
						GatherResponse.CODE_NOVUS_ERROR);
				throw ge;
			} catch (NovusException e) {
				LOG.error("Failed with Novus Exception in NORT");
				GatherException ge = new GatherException(
						"NORT Novus Exception ", e,
						GatherResponse.CODE_NOVUS_ERROR);
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

	public boolean printNode(NortNode node, NortManager _nortManager,
			Writer out, int[] counters, int[] iParent, String YYYYMMDDHHmmss,
			Map<String, String> tocGuidDateMap) throws GatherException,
			NovusException, ParseException {
		boolean docFound = true;

		// skip empty node or subsection node
		if (node != null
				&& !node.getPayloadElement("/n-nortpayload/node-type")
						.equalsIgnoreCase("subsection")) {
			docFound = false;

			StringBuffer name = new StringBuffer();
			StringBuffer tocGuid = new StringBuffer();
			StringBuffer docGuid = new StringBuffer();

			counters[NODECOUNT]++;

			tocGuid.append(EBConstants.TOC_START_GUID_ELEMENT)
					.append(node.getGuid().replaceAll("\\<.*?>", ""))
					.append(counters[NODECOUNT])
					.append(EBConstants.TOC_END_GUID_ELEMENT);

			if (Long.valueOf(node
					.getPayloadElement("/n-nortpayload/n-end-date")) > Long
					.valueOf(YYYYMMDDHHmmss))
			// md.end+effective 20970101235959
			// TODO: If NOVUS api is changed add back
			// _nortManager.setNortVersion and remove above.
			{
				String endDate = node
						.getPayloadElement("/n-nortpayload/n-end-date");
				String startDate = node
						.getPayloadElement("/n-nortpayload/n-start-date");
				DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				DateFormat formatterFinal = new SimpleDateFormat("dd-MMM-yyyy");

				if (Long.valueOf(startDate) > Long.valueOf(YYYYMMDDHHmmss)) {
					Date date = formatter.parse(startDate);
					boolean bAncestorFound = false;

					String startDateFinal = formatterFinal.format(date);

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
					if (bAncestorFound == false) {
						name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
								.append(EBConstants.TOC_START_NAME_ELEMENT)
								.append(node.getLabel().replaceAll("\\<.*?>",
										"")).append(" (effective ")
								.append(startDateFinal).append(") ")
								.append(EBConstants.TOC_END_NAME_ELEMENT);
						tocGuidDateMap.put(
								tocGuid.toString().replaceAll("\\<.*?>", ""),
								startDate);
					}
				} else if (Long.valueOf(endDate) < Long
						.valueOf("20970101235959")) {

					Date date = formatter.parse(endDate);
					boolean bAncestorFound = false;

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
					if (bAncestorFound == false) {

						String endDateFinal = formatterFinal.format(date);

						name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
								.append(EBConstants.TOC_START_NAME_ELEMENT)
								.append(node.getLabel().replaceAll("\\<.*?>",
										"")).append(" (end effective ")
								.append(endDateFinal).append(") ")
								.append(EBConstants.TOC_END_NAME_ELEMENT);

						tocGuidDateMap.put(
								tocGuid.toString().replaceAll("\\<.*?>", ""),
								endDate);
					}
				} else {
					name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT)
							.append(EBConstants.TOC_START_NAME_ELEMENT)
							.append(node.getLabel().replaceAll("\\<.*?>", ""))
							.append(EBConstants.TOC_END_NAME_ELEMENT);
				}

				if (node.getPayloadElement("/n-nortpayload/n-doc-guid") != null) {
					docFound = true;
					counters[DOCCOUNT]++;
					docGuid.append(EBConstants.TOC_START_DOCUMENT_GUID_ELEMENT)
							.append(node.getPayloadElement(
									"/n-nortpayload/n-doc-guid").replaceAll(
									"\\<.*?>", ""))
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

				// LOG.debug(" document count : " + docCounter + " out of " +
				// counter + " nodes" );

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

				NortNode[] nortNodes = null;
				Integer novusNortRetryCounter = 0;
				nortRetryCount = new Integer(novusUtility.getTocRetryCount());				
				while (novusNortRetryCounter < nortRetryCount) {
					try {
						nortNodes = node.getChildren();
						break;
					} catch (final Exception exception) {
						try {
							novusNortRetryCounter = novusUtility
									.handleException(exception,
											novusNortRetryCounter,
											nortRetryCount);
						} catch (NovusException e) {
							LOG.error("Failed with Novus Exception in NORT getChildren()");
							GatherException ge = new GatherException(
									"NORT Novus Exception ", e,
									GatherResponse.CODE_NOVUS_ERROR);
							throw ge;
						} catch (Exception e) {
							LOG.error("Failed with Exception in NORT  getChildren()");
							GatherException ge = new GatherException(
									"NORT Exception ", e,
									GatherResponse.CODE_NOVUS_ERROR);
							throw ge;
						}
					}
				}

				if (nortNodes != null) {
					for (int i = 0; i < nortNodes.length; i += 10) {
						int length = (i + 10 <= nortNodes.length) ? 10
								: (nortNodes.length) - i;

						_nortManager.fillNortNodes(nortNodes, i, length);
					}

					docFound = printNodes(nortNodes, _nortManager, out,
							counters, iParent, YYYYMMDDHHmmss, tocGuidDateMap);
				}

			} else {

				// LOG.debug(" skipping old nodes " +
				// node.getLabel().replaceAll("\\<.*?>","") );
				counters[SKIPCOUNT]++;
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
	 * Get NORT using domain and expression filter.
	 * 
	 * @throws ParseException
	 * @throws Exception
	 */
	@Override
	public GatherResponse findTableOfContents(String domainName,
			String expressionFilter, File nortXmlFile, Date cutoffDate)
			throws GatherException {
		NortManager _nortManager = null;
		Writer out = null;
		int[] counters = { 0, 0, 0, 0 };
		int[] iParent = { 0 };
		String publishStatus = "TOC NORT Step Completed";
		GatherResponse gatherResponse = new GatherResponse();

		Novus novusObject = novusFactory.createNovus();

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			// TODO: fix after Test1 testing.
			String YYYYMMDDHHmmss;
			if (cutoffDate == null) {
				YYYYMMDDHHmmss = formatter.format(date);
			} else {
				YYYYMMDDHHmmss = formatter.format(cutoffDate);
			}
			// String YYYYMMDDHHmmss = "20120202111111";

			_nortManager = novusObject.getNortManager();
			_nortManager.setShowChildrenCount(true);
			_nortManager.setDomainDescriptor(domainName);
			_nortManager.setFilterName(expressionFilter, 0);
			// _nortManager.setNortVersion(YYYYMMDDHHmmss);

			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(nortXmlFile.getPath()), "UTF8"));
			out.write(EBConstants.TOC_XML_ELEMENT);
			out.write(EBConstants.TOC_START_EBOOK_ELEMENT);

			retrieveNodes(_nortManager, out, counters, iParent, YYYYMMDDHHmmss);

			LOG.info(counters[DOCCOUNT] + " documents " + counters[SKIPCOUNT]
					+ " skipped nodes  and " + counters[NODECOUNT]
					+ " nodes in the NORT hierarchy for domain " + domainName
					+ " and filter " + expressionFilter);

			out.write(EBConstants.TOC_END_EBOOK_ELEMENT);
			out.flush();
			out.close();
			LOG.debug("Done with Nort.");
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage());
			GatherException ge = new GatherException(
					"NORT UTF-8 encoding error ", e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus = "TOC NORT Step Failed UTF-8 encoding error";
			throw ge;
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage());
			GatherException ge = new GatherException("NORT File not found ", e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus = "TOC NORT Step Failed File not found";
			throw ge;
		} catch (IOException e) {
			LOG.error(e.getMessage());
			GatherException ge = new GatherException("NORT IOException ", e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus = "TOC NORT Step Failed IOException";
			throw ge;
		} catch (GatherException e) {
			LOG.error(e.getMessage());
			publishStatus = "TOC NORT Step Failed GatherException";
			throw e;
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				LOG.error(e.getMessage());
				GatherException ge = new GatherException(
						"NORT Cannot close toc.xml ", e,
						GatherResponse.CODE_FILE_ERROR);
				publishStatus = "TOC NORT Step Failed Cannot close toc.xml";
				throw ge;
			} finally {
				gatherResponse.setDocCount(counters[DOCCOUNT]);
				gatherResponse.setNodeCount(counters[NODECOUNT]);
				gatherResponse.setSkipCount(counters[SKIPCOUNT]);
				gatherResponse.setRetryCount(counters[RETRYCOUNT]);
				gatherResponse.setPublishStatus(publishStatus);

			}

			novusObject.shutdownMQ();
		}

		return gatherResponse;
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
