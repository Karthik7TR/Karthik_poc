package com.thomsonreuters.uscl.ereader.group.step;

import java.util.List;

import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;


public interface GroupService {
	
	public List<SubGroupInfo> getSubGroupsFromProviewResponse(String groupInfoXML, String majorVersion) throws Exception;
	
	public String getGroupId(BookDefinition bookDefinition);
	
	public SubGroupInfo getSubGroupInfo(Long jobInstance, String majorVersion, String subGroupHeading);

}
