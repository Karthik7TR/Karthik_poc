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
import java.text.SimpleDateFormat;
import java.util.Date;

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

	private NovusUtility novusUtility;


	
	
	public void retrieveNodes(NortManager _nortManager, Writer out,
			int[] counter, int[] docCounter, int[] iParent,
			String YYYYMMDDHHmmss) throws GatherException {

		NortNode[] nortNodes = null;
		
		final Integer nortRetryCount = new Integer(novusUtility.getNortRetryCount());		

		try {
			// This is the counter for checking how many Novus retries we
			// are making
			Integer novusNortRetryCounter = 0;
			while (novusNortRetryCounter <= nortRetryCount) {
				try {
					nortNodes = _nortManager.getRootNodes();
					break;
				} catch (final Exception exception) {
					try {
						novusNortRetryCounter = novusUtility.handleException(
								exception, novusNortRetryCounter, nortRetryCount);
					} catch (NovusException e) {
						LOG.debug("Failed with Novus Exception in NORT");
						GatherException ge = new GatherException(
								"NORT Novus Exception ", e,
								GatherResponse.CODE_NOVUS_ERROR);
						throw ge;
					} catch (Exception e) {
						LOG.debug("Failed with Exception in NORT");
						GatherException ge = new GatherException(
								"NORT Exception ", e,
								GatherResponse.CODE_NOVUS_ERROR);
						throw ge;
					}
				}
			}

			printNodes(nortNodes, _nortManager, out, counter, docCounter,
					iParent, YYYYMMDDHHmmss);
		} catch (Exception e) {
			LOG.debug("Failed with Exception in NORT");
			GatherException ge = new GatherException("NORT Exception ", e,
					GatherResponse.CODE_NOVUS_ERROR);
			throw ge;
		}

	}

	   public void printNodes(NortNode[] nodes, NortManager _nortManager, Writer out, int[] counter, int[] docCounter, int[] iParent, String YYYYMMDDHHmmss) throws GatherException
	   {
	       if (nodes != null)
	       {
			try {
				for (NortNode node : nodes) {
					printNode(node, _nortManager, out, counter, docCounter, iParent, YYYYMMDDHHmmss);
				}
				if (iParent[0] > 0) {

					out.write(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
					out.write("\r\n");
					out.flush();

					iParent[0]--;

				}
			} catch (IOException e) {
				LOG.debug("Failed writing TOC in NORT");
				GatherException ge = new GatherException(
						"Failed writing TOC in NORT ", e,
						GatherResponse.CODE_NOVUS_ERROR);
				throw ge;
			} catch (NovusException e) {
				LOG.debug("Failed with Novus Exception in NORT");
				GatherException ge = new GatherException(
						"NORT Novus Exception ", e,
						GatherResponse.CODE_NOVUS_ERROR);
				throw ge;			}
	       }
	   }
	   
	   public void printNode(NortNode node, NortManager _nortManager, Writer out, int[] counter, int[] docCounter, int[] iParent, String YYYYMMDDHHmmss) throws GatherException, NovusException
	   {
		   // skip empty node or subsection node
	       if (node != null && !node.getPayloadElement("/n-nortpayload/node-type").equalsIgnoreCase("subsection"))
	       {
	           StringBuffer name = new StringBuffer();
	           StringBuffer tocGuid = new StringBuffer();
	           StringBuffer guid = new StringBuffer();

	           if (Long.valueOf(node.getPayloadElement("/n-nortpayload/n-end-date")) > Long.valueOf(YYYYMMDDHHmmss))
	        	   // md.end+effective 20970101235959
	        	   //TODO: If NOVUS api is changed add back _nortManager.setNortVersion and remove above.
	           {
	           name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT).append(EBConstants.TOC_START_NAME_ELEMENT).append(node.getLabel().replaceAll("\\<.*?>","")).append(EBConstants.TOC_END_NAME_ELEMENT);
	           tocGuid.append(EBConstants.TOC_START_GUID_ELEMENT).append(node.getGuid().replaceAll("\\<.*?>","")).append(EBConstants.TOC_END_GUID_ELEMENT);
	           
	             if (node.getPayloadElement("/n-nortpayload/n-doc-guid") != null)
	             {
	        	   docCounter[0]++;
	        	   guid.append(EBConstants.TOC_START_DOCUMENT_GUID_ELEMENT).append(node.getPayloadElement("/n-nortpayload/n-doc-guid").replaceAll("\\<.*?>","")).append(EBConstants.TOC_END_DOCUMENT_GUID_ELEMENT) ;   
	             }

	           if(node.getChildrenCount() == 0)
	           {
	        	   guid.append(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
	           }
	           else
	           {
	        	   iParent[0]++;
	           }

	           // Example of output:
	           // <EBookToc>
	           // <Name>Primary Source with Annotations</Name> 
	           // <DocumentGuid>I175bd1b012bb11dc8c0988fbe4566386</DocumentGuid> 

	           
	           String payloadFormatted =( name.toString() + tocGuid.toString() + guid.toString() );

	           counter[0]++;
	   
//	           LOG.debug(" document count : " + docCounter + " out of " + counter + " nodes" );

	   	    	try {
					out.write(payloadFormatted);
					out.write("\r\n");
					out.flush();
				} catch (IOException e) {
					LOG.debug(e.getMessage());
					GatherException ge = new GatherException("Failed writing to NORT TOC ", e, GatherResponse.CODE_FILE_ERROR);
					throw ge;
					}
	          
	           
	           NortNode[] nortNodes = node.getChildren();
	           
	           if (nortNodes != null)
	           {
	               for (int i = 0; i < nortNodes.length; i+=10)
	               {
	                  int length = (i + 10 <= nortNodes.length) ? 10 : (nortNodes.length) - i;
	                 
	                  _nortManager.fillNortNodes(nortNodes, i, length);
	               }
	               
	               printNodes(nortNodes, _nortManager, out, counter, docCounter, iParent, YYYYMMDDHHmmss);
	            }
	     
	           }
//	           else
//		         {
//		           LOG.debug(" skipping old nodes " + node.getLabel().replaceAll("\\<.*?>","") );
//		         }
	       } 
//       else
//         {
//           LOG.debug(" skipping subsection " );
//         }
	   }
	   
	/**
	 * 
	 * Get NORT using domain and expression filter.
	 * @throws Exception 
	 */
	@Override
	public void findTableOfContents(String domainName, String expressionFilter, File nortXmlFile) throws GatherException 
	{
		NortManager _nortManager = null;
		Writer out = null;
		int[] counter = {0};
		int[] docCounter = {0};
		int[] iParent = {0};
		
		Novus novusObject = novusFactory.createNovus();
		
		Date date = new Date();
		SimpleDateFormat formatter =
	            new SimpleDateFormat("yyyyMMddHHmmss");

	       
		try {
			
		String YYYYMMDDHHmmss;
		YYYYMMDDHHmmss = formatter.format(date);
//		String YYYYMMDDHHmmss = "20120206111111"; 
			
	    _nortManager = novusObject.getNortManager();
		_nortManager.setShowChildrenCount(true);
	    _nortManager.setDomainDescriptor(domainName);
	    _nortManager.setFilterName(expressionFilter, 0);
//	    _nortManager.setNortVersion(YYYYMMDDHHmmss);
	    

		out = new BufferedWriter(new OutputStreamWriter(
		        new FileOutputStream(nortXmlFile.getPath()), "UTF8"));
	    out.write(EBConstants.TOC_XML_ELEMENT);
	    out.write(EBConstants.TOC_START_EBOOK_ELEMENT);

//	    counter = 0;
//	    docCounter = 0;
//	    iParent = 0;
	    
        retrieveNodes( _nortManager,  out,  counter,  docCounter,  iParent, YYYYMMDDHHmmss);
        
        LOG.info(docCounter[0] + " documents and " + counter[0] + " nodes in the NORT hierarchy for domain " + domainName + " and filter " + expressionFilter );

	    out.write(EBConstants.TOC_END_EBOOK_ELEMENT);
        out.flush();
        out.close();
        LOG.debug("Done with Nort.");
		} catch (UnsupportedEncodingException e) {
			LOG.debug(e.getMessage());
			GatherException ge = new GatherException("NORT UTF-8 encoding error ", e, GatherResponse.CODE_FILE_ERROR);
			throw ge;
		} catch (FileNotFoundException e) {
			LOG.debug(e.getMessage());
			GatherException ge = new GatherException("NORT File not found ", e, GatherResponse.CODE_FILE_ERROR);
			throw ge;
		} catch (IOException e) {
			LOG.debug(e.getMessage());
			GatherException ge = new GatherException("NORT IOException ", e, GatherResponse.CODE_FILE_ERROR);
			throw ge;
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				LOG.debug(e.getMessage());
				GatherException ge = new GatherException("NORT Cannot close toc.xml ", e, GatherResponse.CODE_FILE_ERROR);
				throw ge;
			}
			
			novusObject.shutdownMQ();
		}

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

