/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.services;

import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.westgroup.novus.productapi.Document;

public interface DocService 
{
	
	public List<EBookToc> getDocFromNovus(String guid, String collectionName); 
	public void retrieveDocument(List<EBookToc> eBookTocDocumentList);
	public Document[]  getDocFromNovus(List<EBookToc> docGuidList, String collectionName, String docFileLocationPath); 


}
