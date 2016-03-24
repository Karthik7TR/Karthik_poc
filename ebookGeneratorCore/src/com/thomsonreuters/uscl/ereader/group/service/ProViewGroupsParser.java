/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.group.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringUtils;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;

public class ProViewGroupsParser {
	private static final String GROUP = "group";
	private static final String GROUP_NAME = "name";
	private static final String GROUP_TYPE = "type";
	private static final String GROUP_HEAD_TITLE = "headtitle";
	private static final String GROUP_ID = "id";
	private static final String GROUP_STATUS = "status";
	private static final String GROUP_VERSION = "version";
	
	private static final String SUBGROUP = "subgroup";
	private static final String SUBGROUP_TITLE = "title";
	private static final String SUBGROUP_HEADING = "heading";
	
	private XMLInputFactory factory = null;
	
	
	
	public ProViewGroupsParser() {
		factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
	}

	public List<GroupDefinition> parse(String text) throws Exception {
		StringBuffer buffer = new StringBuffer();
		XMLEventReader r = null;
		
		List<GroupDefinition> groupDefinitions = new ArrayList<GroupDefinition>();
		GroupDefinition groupDefinition = null;
		SubGroupInfo subGroupInfo = null;
		try (InputStream input = new ByteArrayInputStream(text.getBytes("UTF-8"));) {
			r = factory.createXMLEventReader(input, "UTF-8");

			while(r.hasNext()) {
				XMLEvent event = r.nextEvent();
				
				if(event.isStartElement()) {
					StartElement element = event.asStartElement();
					if(element.getName().getLocalPart().equalsIgnoreCase(GROUP)) {
						groupDefinition = new GroupDefinition();
						Attribute idAttr = element.asStartElement().getAttributeByName(new QName(GROUP_ID));
						Attribute statusAttr= element.asStartElement().getAttributeByName(new QName(GROUP_STATUS));
						Attribute versionAttr= element.asStartElement().getAttributeByName(new QName(GROUP_VERSION));
						
						groupDefinition.setGroupId(idAttr.getValue());
						groupDefinition.setOrder(statusAttr.getValue());
						groupDefinition.setProviewGroupVersionString(versionAttr.getValue());
					} else if(element.getName().getLocalPart().equalsIgnoreCase(SUBGROUP)) {
						subGroupInfo = new SubGroupInfo();
						Attribute subheadingAttr = element.asStartElement().getAttributeByName(new QName(SUBGROUP_HEADING));
						if(subheadingAttr != null) {
							String subheading = subheadingAttr.getValue();
							if(StringUtils.isNotBlank(subheading)) {
								subGroupInfo.setHeading(subheading);
							}
						}
					}
					buffer = new StringBuffer();
				}
				
				if(event.isCharacters()) {
					Characters character = event.asCharacters();
					buffer.append(character.getData());
				}

				
				if(event.isEndElement()) {
					EndElement element = event.asEndElement();
					if(element.getName().getLocalPart().equalsIgnoreCase(GROUP)) {
						groupDefinitions.add(groupDefinition);
					} else if(element.getName().getLocalPart().equalsIgnoreCase(GROUP_NAME)){
						groupDefinition.setName(buffer.toString());
					} else if(element.getName().getLocalPart().equalsIgnoreCase(GROUP_HEAD_TITLE)) {
						groupDefinition.setHeadTitle(buffer.toString());
					} else if(element.getName().getLocalPart().equalsIgnoreCase(GROUP_TYPE)) {
						groupDefinition.setType(buffer.toString());
					} else if(element.getName().getLocalPart().equalsIgnoreCase(SUBGROUP_TITLE)) {
						subGroupInfo.addTitle(buffer.toString());
					} else if(element.getName().getLocalPart().equalsIgnoreCase(SUBGROUP)) {
						groupDefinition.addSubGroupInfo(subGroupInfo);
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Error while processing label: " + text, e);
		} finally {
			try {
				r.close();
			} catch (XMLStreamException e) {
				throw new XMLStreamException("Failed to close XMLEventReader.", e);
			}
		}
		
		return groupDefinitions;
	}
	
}
