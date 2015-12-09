package com.thomsonreuters.uscl.ereader.mgr.group.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroup;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroupSort;

public interface EbookGroupService {
	
	/**
	 * Return all EbookAudits
	 * @return
	 */
	public List<EbookGroup> findEbookGroups(EbookGroup ebookGroup, EbookGroupSort sort);
	
	public int totalEbookGroups(EbookGroup ebookGroup);
	

}
