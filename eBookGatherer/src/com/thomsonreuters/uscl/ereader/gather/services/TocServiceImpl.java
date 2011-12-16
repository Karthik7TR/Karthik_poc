/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.thomsonreuters.uscl.ereader.gather.domain.Toc;
import com.thomsonreuters.uscl.ereader.gather.util.NovusAPIHelper;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;
import com.westgroup.novus.productapi.TOC;
import com.westgroup.novus.productapi.TOCNode;

/**
 * This class corresponds with novus helper and gets documents.
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
	 * This method extract TocNode information from Array List of easel API
	 * TOCNode object.
	 * 
	 * @param tocNodes
	 * @return string array of toc guids.
	 */
	private List<Toc> extractRootTocNodes(TOCNode[] tocNodes)
	{
		// TODO: add logging related stuff at start and end.

		List<Toc> tocGuidList = new ArrayList<Toc>();
		
		for (int i = 0; i < tocNodes.length; i++)
		{
			TOCNode node = tocNodes[i];
			Toc toc = new Toc();
			toc.setGuid(node.getGuid());
			toc.setName(node.getName());
			toc.setDocGuid(node.getDocGuid());
			toc.setParentGuid(node.getParentGuid());
			toc.setMetadata(node.getMetadata());
			toc.setChildrenCount(node.getChildrenCount());
			tocGuidList.add(toc);
		}
		return tocGuidList;
	}
	

	@Override
	public void getDocuments() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Toc> getTocData(String guid, String collectionName) {
		Novus novusObject = null;
		TOCNode[] tocNodes = null;
		List<Toc> tocGuidList = null;
		
		try {
			novusObject = novusAPIHelper.getNovusObject();
			TOC toc = novusObject.getTOC();
			toc.setCollectionSet(collectionName);
			String dateTime = novusAPIHelper.getCurrentDateTime();
			toc.setTOCVersion(dateTime);
			tocNodes = toc.getRootNodes();
			tocGuidList = extractRootTocNodes(tocNodes);
			
		} catch (NovusException e) {
			e.printStackTrace();
		} finally {
			novusAPIHelper.closeNovusConnection(novusObject);
		}

		return tocGuidList;


	}

}
