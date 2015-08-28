package com.thomsonreuters.uscl.ereader.group.step;

import java.util.List;

import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;


public interface GroupService {
	
	public List<SubGroupInfo> getSubGroupsFromProviewResponse(String groupInfoXML, String majorVersion) throws Exception;
	
	public String getGroupId(BookDefinition bookDefinition);
	
	public String getGroupName(DocumentTypeCode documentTypeCode, List<EbookName> names);
	
	public List<SubGroupInfo> getSubGroupInfo(Long jobInstance, String majorVersion);

}
