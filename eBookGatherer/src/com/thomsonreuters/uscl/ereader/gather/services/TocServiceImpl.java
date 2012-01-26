/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;
import com.westgroup.novus.productapi.TOC;
import com.westgroup.novus.productapi.TOCNode;

/**
 * This class corresponds with novus helper and gets TOC documents.
 * 
 * @author U0105927
 * 
 */
public class TocServiceImpl implements TocService {

	private NovusFactory novusFactory;
	
	List<EBookToc> eBookDocumentList = new ArrayList<EBookToc>();

	
	/**
	 * Recursive function to retrieve all the TOC Structure with TOC metadata.
	 * 
	 * @param tocNodes
	 * @return string array of List<EBookToc>.
	 * @throws Exception 
	 */
	private List<EBookToc> extractTocListForEbook(TOCNode[] tocNodes) throws GatherException
	{
		// TODO: add logging related stuff at start and end.

		List<EBookToc> ebTocList = new ArrayList<EBookToc>();
		
		for (int i = 0; i < tocNodes.length; i++)
		{
			TOCNode node = tocNodes[i];
			EBookToc eBookToc = new EBookToc();
			//eBookToc.setName(novusAPIHelper.processName(node.getName()));
			eBookToc.setName(node.getName());
			eBookToc.setDocGuid(node.getDocGuid());
			eBookToc.setChildrenCount(node.getChildrenCount());
			
			
			/*** process all the children if present.***/
			if(node.getChildrenCount() > 0 )
			{
				//System.out.println(" TOC Node :"+eBookToc);

				try {
						TOCNode[] childTocList = node.getChildren();
						eBookToc.setChildren( extractTocListForEbook(childTocList));

						
					} catch (NovusException e) {
						// TODO need to find what kind of exception Novus can throw and 
						//e.printStackTrace();
						throw new GatherException("Failed to retrieve child element for Tocnode with guid = "+node.getGuid()+" tocName ="+node.getName(), e);

					}
			}else {/** Might end up removing this block **/
				if(node.getDocGuid()!= "")
				{
					/** **/
					/*** child count is zero and document Guid present i.e tocNode with document..
					need to add logic to collect this data and return or retain so that it would 
					come out handy while fetching end document to ****/
//					System.out.println("*************************************************************");
//					System.out.println(" Toc Nodes with child count zero :"+eBookToc);
//					System.out.println("*************************************************************");
					eBookDocumentList.add(eBookToc);
				}
				
			}
			ebTocList.add(eBookToc);

		}
		return ebTocList;
	}
	
	/**
	 * 
	 * users will always provide us with collection name and not collectionSet names.
	 * @throws Exception 
	 */
	@Override
	public List<EBookToc> findTableOfContents(String guid, String collectionName) throws GatherException 
	{
		Novus novusObject = null;
		TOCNode[] tocNodes = null;
		TOCNode tocNode = null;
		List<EBookToc> tocGuidList = null;
		
		/*** for ebook builder we will always get Collection.***/
		String type = EBConstants.COLLECTION_TYPE;
		novusObject = novusFactory.createNovus();
		TOC toc = getTocObject(collectionName,type,novusObject);

		try {
			tocNode = toc.getNode(guid);
			tocNodes = new TOCNode[1];
			tocNodes[0] =  tocNode ;
			tocGuidList = extractTocListForEbook(tocNodes);
		} catch (NovusException e) {
			throw new GatherException("Failed to get TOC from Novus for guid=" + guid +",collectionName=" + collectionName, e);
		} finally {
			novusObject.shutdownMQ();
		}
		return tocGuidList;
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

