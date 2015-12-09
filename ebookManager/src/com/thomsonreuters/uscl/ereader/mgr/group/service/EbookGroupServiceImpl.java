package com.thomsonreuters.uscl.ereader.mgr.group.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroup;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroupSort;
import com.thomsonreuters.uscl.ereader.mgr.group.dao.GroupDefinitionDao;

@Transactional
public class EbookGroupServiceImpl implements EbookGroupService{
	
	private GroupDefinitionDao groupDefinitionDao;

	public GroupDefinitionDao getBookDefinitionDao() {
		return groupDefinitionDao;
	}

	@Required
	public void setGroupDefinitionDao(GroupDefinitionDao groupDefinitionDao) {
		this.groupDefinitionDao = groupDefinitionDao;
	}

	public List<EbookGroup> findEbookGroups(EbookGroup ebookGroup, EbookGroupSort sort){
		
		return groupDefinitionDao.findAllBooksWithGroups(ebookGroup,sort);
		
	}
	
	public int totalEbookGroups(EbookGroup ebookGroup){
		return groupDefinitionDao.getEBooksWithGroupsCount(ebookGroup);
	}
	
}
