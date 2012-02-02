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
import com.westgroup.novus.productapi.TOC;
import com.westgroup.novus.productapi.TOCNode;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

/**
 * This class corresponds with novus helper and gets TOC hierarchy.
 * 
 * @author Kirsten Gunn
 * 
 */
public class TocServiceImpl implements TocService {

	private NovusFactory novusFactory;
	
	private static final Logger LOG = Logger.getLogger(TocServiceImpl.class);
	
	protected TOC _tocManager = null;
	protected Writer out = null;
	private int counter;
	private int docCounter;
	private int iParent;
	
	public void retrieveNodes(String guid) throws GatherException
	   {
	      
	      TOCNode[] tocNodes = null;
	      TOCNode tocNode = null;

	      try
	      {
//	          tocNodes = _tocManager.getRootNodes();
	          tocNode = _tocManager.getNode(guid);
	          tocNodes = new TOCNode[1];
			  tocNodes[0] =  tocNode ;
	          printNodes(tocNodes);
	      }
	      catch (NovusException e)
	      {
	    	LOG.debug("Failed with Novus Exception in TOC");
			GatherException ge = new GatherException("TOC Novus Exception ", e, GatherResponse.CODE_NOVUS_ERROR);
			throw ge;
	      }
	      
	   }

	   public void printNodes(TOCNode[] nodes) throws GatherException
	   {
	       if (nodes != null)
	       {
			try {
				for (TOCNode node : nodes) {
					printNode(node);
				}
				if (iParent > 0) {

					out.write(EBConstants.TOC_END_EBOOKTOC_ELEMENT);
					out.write("\r\n");
					out.flush();

					iParent--;

				}
			} catch (IOException e) {
				LOG.debug("Failed writing TOC in TOC");
				GatherException ge = new GatherException(
						"Failed writing TOC in TOC ", e,
						GatherResponse.CODE_NOVUS_ERROR);
				throw ge;
			} catch (NovusException e) {
				LOG.debug("Failed with Novus Exception in TOC");
				GatherException ge = new GatherException(
						"TOC Novus Exception ", e,
						GatherResponse.CODE_NOVUS_ERROR);
				throw ge;			}
	       }
	   }
	   
	   public void printNode(TOCNode node) throws GatherException, NovusException
	   {
	       if (node != null)
	       {
	          
	           StringBuffer name = new StringBuffer();
	           name.append(EBConstants.TOC_START_EBOOKTOC_ELEMENT).append(EBConstants.TOC_START_NAME_ELEMENT).append(node.getName().replaceAll("\\<.*?>","")).append(EBConstants.TOC_END_NAME_ELEMENT);
	           StringBuffer tocGuid = new StringBuffer();
	           tocGuid.append(EBConstants.TOC_START_GUID_ELEMENT).append(node.getGuid().replaceAll("\\<.*?>","")).append(EBConstants.TOC_END_GUID_ELEMENT);

	           StringBuffer guid = new StringBuffer();
	           if (node.getDocGuid() != null)
	           {
	        	   docCounter++;
	        	   guid.append(EBConstants.TOC_START_DOCUMENT_GUID_ELEMENT).append(node.getDocGuid().replaceAll("\\<.*?>","")).append(EBConstants.TOC_END_DOCUMENT_GUID_ELEMENT) ;
	        	   
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
					GatherException ge = new GatherException("Failed writing to TOC TOC ", e, GatherResponse.CODE_FILE_ERROR);
					throw ge;
					}
	           TOCNode[] tocNodes = node.getChildren();
	           
	           if (tocNodes != null)
	           {          
	               printNodes(tocNodes);
	           }
	       }
	   }
	   
	/**
	 * 
	 * Get TOC using domain and expression filter.
	 * @throws Exception 
	 */
	@Override
	public void findTableOfContents(String guid, String collectionName, File tocXmlFile) throws GatherException 
	{
		/*** for ebook builder we will always get Collection.***/
		String type = EBConstants.COLLECTION_TYPE;
		Novus novusObject = novusFactory.createNovus();
		_tocManager = getTocObject(collectionName, type, novusObject);

	      try	{

		out = new BufferedWriter(new OutputStreamWriter(
		        new FileOutputStream(tocXmlFile.getPath()), "UTF8"));
	    out.write(EBConstants.TOC_XML_ELEMENT);
	    out.write(EBConstants.TOC_START_EBOOK_ELEMENT);

	    counter = 0;
	    docCounter = 0;
	    iParent = 0;
	    
        retrieveNodes(guid);
        
        LOG.debug(docCounter + " documents and " + counter + " nodes in the TOC hierarchy" );

	    out.write(EBConstants.TOC_END_EBOOK_ELEMENT);
        out.flush();
        out.close();
        LOG.debug("Done with Toc.");
		} catch (UnsupportedEncodingException e) {
			LOG.debug(e.getMessage());
			GatherException ge = new GatherException("TOC UTF-8 encoding error ", e, GatherResponse.CODE_FILE_ERROR);
			throw ge;
		} catch (FileNotFoundException e) {
			LOG.debug(e.getMessage());
			GatherException ge = new GatherException("TOC File not found ", e, GatherResponse.CODE_FILE_ERROR);
			throw ge;
		} catch (IOException e) {
			LOG.debug(e.getMessage());
			GatherException ge = new GatherException("TOC IOException ", e, GatherResponse.CODE_FILE_ERROR);
			throw ge;
			} finally {
			novusObject.shutdownMQ();
		}

	}
	/**
	 * Sets required properties to toc object like collection/collectionSet name.
	 * and version data if needed in future. 
	 * as of now we are assuming all the TOC provided would be without version.
	 * 
	 * @param name
	 * @param type
	 * @param novus
	 * @return
	 */
	private TOC getTocObject(String name, String type, Novus novus)
	{
		TOC toc = novus.getTOC();

		if (!"".equals(type) && type.equalsIgnoreCase(EBConstants.COLLECTION_SET_TYPE))
		{
			toc.setCollectionSet(name);
		} else if (!"".equals(type) && type.equalsIgnoreCase(EBConstants.COLLECTION_TYPE))
		{
			toc.setCollection(name);
		}
		toc.setShowChildrenCount(true);
//		String dateTime = novusAPIHelper.getCurrentDateTime();
//		toc.setTOCVersion(dateTime);
		return toc;
	}
	
	@Required
	public void setNovusFactory(NovusFactory factory) {
		this.novusFactory = factory;
	}

}

