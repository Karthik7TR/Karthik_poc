/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.services;

import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;

public interface TocService 
{
	
	public void getNovousConnection();
 	public void getDocuments();
	public List<EBookToc> getTocDataFromNovus(String guid, String collectionName); 

}
