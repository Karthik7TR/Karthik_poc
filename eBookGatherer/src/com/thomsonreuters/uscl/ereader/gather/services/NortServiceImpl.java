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
	
	protected NortManager _nortManager = null;
	protected Writer out = null;
	private int counter;
	private int docCounter;
	private int iParent;
	
	public void retrieveNodes() throws GatherException
	   {
	      
	      NortNode[] nortNodes = null;

	      try
	      {
	          nortNodes = _nortManager.getRootNodes();
	          printNodes(nortNodes);
	      }
	      catch (NovusException e)
	      {
	    	LOG.debug("Failed with Novus Exception in NORT");
			GatherException ge = new GatherException("NORT Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
			throw ge;
	      }
	      
	   }

	   public void printNodes(NortNode[] nodes) throws GatherException
	   {
	       if (nodes != null)
	       {
			try {
				for (NortNode node : nodes) {
					printNode(node);
				}
				if (iParent > 0) {

					out.write(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
					out.write("\r\n");
					out.flush();

					iParent--;

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
	   
	   public void printNode(NortNode node) throws GatherException, NovusException
	   {
	       if (node != null)
	       {
	          
	           StringBuffer name = new StringBuffer();
	           name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT).append(EBConstants.TOC_START_NAME_ELEMENT).append(node.getLabel().replaceAll("\\<.*?>","")).append(EBConstants.TOC_END_NAME_ELEMENT);
	           StringBuffer tocGuid = new StringBuffer();
	           tocGuid.append(EBConstants.TOC_START_GUID_ELEMENT).append(node.getGuid().replaceAll("\\<.*?>","")).append(EBConstants.TOC_END_GUID_ELEMENT);
	           
	           StringBuffer guid = new StringBuffer();
	           if (node.getPayloadElement("/n-nortpayload/n-doc-guid") != null)
	           {
	        	   docCounter++;
	        	   guid.append(EBConstants.TOC_START_DOCUMENT_GUID_ELEMENT).append(node.getPayloadElement("/n-nortpayload/n-doc-guid").replaceAll("\\<.*?>","")).append(EBConstants.TOC_END_DOCUMENT_GUID_ELEMENT) ;
	        	   
	           }
              
	           if(node.getChildrenCount() == 0)
	           {
	        	   guid.append(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
	           }
	           else
	           {
	        	   iParent++;
	           }

	           // Example of output:
	           // <EBookToc>
	           // <Name>Primary Source with Annotations</Name> 
	           // <DocumentGuid>I175bd1b012bb11dc8c0988fbe4566386</DocumentGuid> 

	           
	           String payloadFormatted =( name.toString() + tocGuid.toString() + guid.toString() );

	           counter++;
	   
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
	               
	               printNodes(nortNodes);
	           }
	       }
	   }
	   
	/**
	 * 
	 * Get NORT using domain and expression filter.
	 * @throws Exception 
	 */
	@Override
	public void findTableOfContents(String domainName, String expressionFilter, File nortXmlFile) throws GatherException 
	{
		Novus novusObject = novusFactory.createNovus();
		
	    _nortManager = novusObject.getNortManager();
		_nortManager.setShowChildrenCount(true);
	    _nortManager.setDomainDescriptor(domainName);
	    _nortManager.setFilterName(expressionFilter, 0);

	      try	{

		out = new BufferedWriter(new OutputStreamWriter(
		        new FileOutputStream(nortXmlFile.getPath()), "UTF8"));
	    out.write(EBConstants.TOC_XML_ELEMENT);
	    out.write(EBConstants.TOC_START_EBOOK_ELEMENT);

	    counter = 0;
	    docCounter = 0;
	    iParent = 0;
	    
        retrieveNodes();
        
        LOG.debug(docCounter + " documents and " + counter + " nodes in the NORT hierarchy" );

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
			novusObject.shutdownMQ();
		}

	}
	
	
	@Required
	public void setNovusFactory(NovusFactory factory) {
		this.novusFactory = factory;
	}

}

