/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.util.NovusAPIHelper;
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

	@Autowired
	public NovusAPIHelper novusAPIHelper;

	@Override
	public void getNovousConnection() {
		// TODO Auto-generated method stub

	}

	
	/**
	 * Recursive function to retrieve all the TOC Structure with TOC metadata.
	 * 
	 * @param tocNodes
	 * @return string array of List<EBookToc>.
	 */
	private List<EBookToc> extractTocListForEbook(TOCNode[] tocNodes)
	{
		// TODO: add logging related stuff at start and end.

		List<EBookToc> ebTocList = new ArrayList<EBookToc>();
		
		for (int i = 0; i < tocNodes.length; i++)
		{
			TOCNode node = tocNodes[i];
			EBookToc eBookToc = new EBookToc();
			eBookToc.setGuid(node.getGuid());
			eBookToc.setName(node.getName());
			eBookToc.setDocGuid(node.getDocGuid());
			eBookToc.setParentGuid(node.getParentGuid());
			eBookToc.setMetadata(node.getMetadata());
			eBookToc.setChildrenCount(node.getChildrenCount());
			
			/*** process all the children if present.***/
			if(node.getChildrenCount() > 0 )
			{
				//System.out.println(" TOC Node :"+eBookToc);
				System.out.println("Name :"+eBookToc.getName());
				System.out.println("Guid :"+eBookToc.getGuid());
				System.out.println("ParentGuid :"+eBookToc.getParentGuid());
				try {
						TOCNode[] childTocList = node.getChildren();
						eBookToc.setChildren( extractTocListForEbook(childTocList));
						
					} catch (NovusException e) {
						// TODO need to find what kind of exception Novus can throw and 

						e.printStackTrace();
					}
			}else {
				if(node.getDocGuid()!= "")
				{
					/*** child count is zero and document Guid present i.e tocNode with document..****/
//					System.out.println("*************************************************************");
//					System.out.println(" Toc Nodes with child count zero :"+eBookToc);
//					System.out.println("*************************************************************");
					
				}
				
			}
			ebTocList.add(eBookToc);

		}
		return ebTocList;
	}
	

	@Override
	public void getDocuments() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 
	 * users will alwyas provide us with collection name and not collectionSet names.
	 */
	@Override
	public List<EBookToc> getTocDataFromNovus(String guid, String collectionName) 
	{
		Novus novusObject = null;
		TOCNode[] tocNodes = null;
		List<EBookToc> tocGuidList = null;
		
		/*** for ebook builder we will always get Collection.***/
		String type = EBConstants.COLLECTION_TYPE; 
		try {
			novusObject = novusAPIHelper.getNovusObject();
			TOC toc = getTocObject(collectionName,type,novusObject);
			tocNodes = toc.getRootNodes(); //getAllDocuments(guid);
			tocGuidList = extractTocListForEbook(tocNodes);
			
		} catch (NovusException e) {
			e.printStackTrace();
		} finally {
			novusAPIHelper.closeNovusConnection(novusObject);
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


}

