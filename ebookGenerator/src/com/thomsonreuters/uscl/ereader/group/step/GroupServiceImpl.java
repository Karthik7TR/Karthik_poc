package com.thomsonreuters.uscl.ereader.group.step;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.util.XMLXpathEvaluator;

public class GroupServiceImpl implements GroupService {
	
	private static final Logger LOG = Logger.getLogger(GroupServiceImpl.class);

	private DocMetadataService docMetadataService;

	public List<SubGroupInfo> getSubGroupsFromProviewResponse(String groupInfoXML, String majorVersion) throws Exception {
		
		XMLXpathEvaluator extractor = new XMLXpathEvaluator(groupInfoXML);

		int startIndex = 0;

		// HEAD TITLE
		Node node = extractor.evaluateNode("group/headtitle");
		String headTitle = node.getTextContent();
		

		if (StringUtils.substringAfterLast(headTitle, "/").equalsIgnoreCase(majorVersion)) {
			/**
			 * Version number in the headtitle will change if the major version submitted does not exist in Proview.
			 * We will ignore the first subGroup if the versions are minor 
			 */

			startIndex = 1;
		}
		

		NodeList subGroups = extractor.evaluateNodeList("group/members/subgroup");

		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();

		if (subGroups != null && subGroups.getLength() > startIndex) {

			for (int i = startIndex; i < subGroups.getLength(); i++) {
				SubGroupInfo subGroupInfo = new SubGroupInfo();
				Node subGroup = subGroups.item(i);
				// To get heading attribute in <subgroup heading="2012">
				NamedNodeMap attributesMap = subGroup.getAttributes();
				if (attributesMap != null && attributesMap.getLength() > 0) {
					for (int j = 0; j < attributesMap.getLength(); j++) {
						Node attributeNode = attributesMap.item(j);
						String attributeName = attributeNode.getNodeName();
						if (attributeName.equalsIgnoreCase("heading")) {
							subGroupInfo.setHeading(attributeNode.getTextContent());
						}
					}
				}
				
				//Titles within subgroups
				NodeList titles = subGroup.getChildNodes();
				List<String> titleList = new ArrayList<String>();
				if (titles.getLength() > 0) {
					for (int count = 0; count < titles.getLength(); count++) {
						String title = titles.item(count).getTextContent();
						titleList.add(title);
					}
				}
				subGroupInfo.setTitles(titleList);
				subGroupInfoList.add(subGroupInfo);
				
			}
		}

		
		return subGroupInfoList;
	}
	
	/**
	 * Group ID is unique to each major version
	 * @param bookDefinition
	 * @param versionNumber
	 * @return
	 */
	public String getGroupId(BookDefinition bookDefinition){
		StringBuffer buffer = new StringBuffer();
		buffer.append(bookDefinition.getPublisherCodes().getName());
		buffer.append("/");
		buffer.append(StringUtils.substringAfterLast(bookDefinition.getFullyQualifiedTitleId(), "/"));		
		return buffer.toString();
	}
	
	public String getGroupName(DocumentTypeCode documentTypeCode, List<EbookName> names) {

		StringBuffer mainTitle = new StringBuffer();
		String series = "";
		String subTitle = "";

		for (EbookName name : names) {
			if (name.getSequenceNum() == 1) {
				mainTitle.append(name.getBookNameText());				
			}
			else if(name.getSequenceNum() == 2 && name.getBookNameText() != null){
				subTitle = " "+name.getBookNameText();
			}
			// Add series if the content type is Analytical or by default it
			// should be main title.
			else if (name.getSequenceNum() == 3 && name.getBookNameText() != null) {
				series = " "+name.getBookNameText();
			}
		}
		if (documentTypeCode == null
				|| (documentTypeCode != null && !documentTypeCode.getId().equals(DocumentTypeCode.ANALYTICAL))) {
			return mainTitle.append(subTitle).toString();
		}

		mainTitle.append(subTitle).append(series);
		LOG.debug("Group Name :" + mainTitle.toString());

		return mainTitle.toString();

	}
	
	public SubGroupInfo getSubGroupInfo(Long jobInstance, String majorVersion, String subGroupHeading){
		
		SubGroupInfo subGroupInfo = new SubGroupInfo();
		
		subGroupInfo.setHeading(subGroupHeading);
		List<String> titleList = new ArrayList<String>();
		
		
		List<String> splitTitles = docMetadataService.findDistinctSplitTitlesByJobId(jobInstance);
		
		for (String splitTitleId : splitTitles) {
			
			titleList.add(splitTitleId+"/"+majorVersion);
		}
		
		subGroupInfo.setTitles(titleList);
		
		return subGroupInfo;
	}
	
	public DocMetadataService getDocMetadataService() {
		return docMetadataService;
	}

	@Required
	public void setDocMetadataService(DocMetadataService docMetadataService) {
		this.docMetadataService = docMetadataService;
	}


}
