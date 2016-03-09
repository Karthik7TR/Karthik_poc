package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GroupXMLParser extends DefaultHandler{
	private static final String GROUP = "group";
	private static final String SUBGROUP = "subgroup";
	private static final String NAME = "name";
	private static final String TITLE = "title";	
	private static final String HEADTITLE ="headtitle";
	
	private String subGroupName;				
	
	private StringBuffer charBuffer = null;
	//private String version;
	private String headTitle;
	private List<String> titleIdList;
	private Map<String,String> subGroupVersionMap = new  HashMap<String,String>();
	private String version;

	public Map<String, String> getSubGroupVersionMap() {
		return subGroupVersionMap;
	}

	public void setSubGroupVersion(Map<String, String> subGroupVersionMap) {
		this.subGroupVersionMap = subGroupVersionMap;
	}

	public List<String> getTitleIdList() {
		return titleIdList;
	}

	public void setTitleIdList(List<String> titleIdList) {
		this.titleIdList = titleIdList;
	}

	private Map<String,List<String>> subGroupTitleList = new HashMap<String,List<String>>();
	

	public Map<String, List<String>> getSubGroupTitleList() {
		return subGroupTitleList;
	}

	public void setSubGroupTitleList(Map<String, List<String>> subGroupTitleList) {
		this.subGroupTitleList = subGroupTitleList;
	}

	public String getHeadTitle() {
		return headTitle;
	}

	public void setHeadTitle(String headTitle) {
		this.headTitle = headTitle;
	}
	
	

	public String getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	private String groupStatus;
	private String groupName;

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (charBuffer != null) {
			charBuffer.append(new String(ch, start, length));
		}
	}

	@Override
	public void endElement(String uri, String localName,
			String qName) throws SAXException {
		try {
			String value = null;
			
			if (SUBGROUP.equalsIgnoreCase(qName)) {
				if (subGroupName != null && subGroupName.length() > 0){
				 subGroupTitleList.put(subGroupName, titleIdList);
				 if (!version.isEmpty()){
					 subGroupVersionMap.put(subGroupName,version);
					 version = null;
				 }
				}			    
			}

			if (charBuffer != null) {
				value = StringUtils.trim(charBuffer.toString());
				if (NAME.equalsIgnoreCase(qName)){
					groupName = value;
				}
				else if (TITLE.equalsIgnoreCase(qName)){
					version = StringUtils.substringAfterLast(value, "/v");
					titleIdList.add(value);
				}
				else if (HEADTITLE.equalsIgnoreCase(qName)){
					this.headTitle = value;
				}
			}

			charBuffer = null;
		} catch (Exception e) {
			String message = "Exception occured while parsing Proview Response for Group. The error message is: "
					+ e.getMessage();
			throw new RuntimeException(message, e);
		}
	}
	
	@Override
	public void startElement(String uri, String localName,
			String qName, Attributes atts) throws SAXException {
		charBuffer = new StringBuffer();
		if(GROUP.equalsIgnoreCase(qName)){
			groupStatus = atts.getValue("status");
		}
		
		if(SUBGROUP.equalsIgnoreCase(qName)){
			subGroupName = atts.getValue("heading");
			titleIdList = new ArrayList<String>();
		}
		super.startElement(uri, localName, qName, atts);
	}
}

