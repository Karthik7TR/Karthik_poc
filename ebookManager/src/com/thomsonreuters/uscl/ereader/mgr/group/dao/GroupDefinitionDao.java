package com.thomsonreuters.uscl.ereader.mgr.group.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroup;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroupSort;

public interface GroupDefinitionDao {

	public List<EbookGroup> findAllBooksWithGroups(EbookGroup ebookGroup, EbookGroupSort sort);

	public int getEBooksWithGroupsCount(EbookGroup ebookGroup);

}
