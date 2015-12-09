package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GroupXMLHandler extends DefaultHandler{
	private static final String GROUP = "group";
	private static final String SUBGROUP = "subgroup";
	private static final String NAME = "name";
	private static final String TITLE = "title";				
	
	private String subGroupName;				
	
	private StringBuffer charBuffer = null;
	private String version;
	private Map<String,String> versionSubGroupMap = new HashMap<String,String>();
	public Map<String, String> getSubGroupVersionMap() {
		return versionSubGroupMap;
	}

	public void setSubGroupVersionMap(Map<String, String> subGroupVersionMap) {
		this.versionSubGroupMap = subGroupVersionMap;
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
				versionSubGroupMap.put(version,subGroupName);
			    
			}

			if (charBuffer != null) {
				value = StringUtils.trim(charBuffer.toString());
				if (NAME.equalsIgnoreCase(qName)){
					groupName = value;
				}
				else if (TITLE.equalsIgnoreCase(qName)){
					version = StringUtils.substringAfterLast(value, "/");
				}
			}


			charBuffer = null;
		} catch (Exception e) {
			String message = "Exception occured during Novus IMGMetadata parsing endElement. The error message is: "
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
		}
		super.startElement(uri, localName, qName, atts);
	}
}
