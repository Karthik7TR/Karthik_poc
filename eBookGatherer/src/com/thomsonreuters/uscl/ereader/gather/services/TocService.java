/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.services;

import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.domain.Toc;

public interface TocService 
{
	
	public void getNovousConnection();
 	public void getDocuments();
	public List<Toc> getTocDataFromNovus(String guid, String collectionName); 

}
